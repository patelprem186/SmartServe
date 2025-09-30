const express = require('express');
const { body, validationResult } = require('express-validator');
const User = require('../models/User');
const Booking = require('../models/Booking');
const Service = require('../models/Service');
const { protect, authorize } = require('../middleware/auth');
const { sendNotification } = require('../services/NotificationService');

const router = express.Router();

// @route   POST /api/notifications/send
// @desc    Send notification to user
// @access  Private (Admin/Provider only)
router.post('/send', protect, authorize(['admin', 'provider']), [
  body('userId').notEmpty().withMessage('User ID is required'),
  body('title').notEmpty().withMessage('Title is required'),
  body('body').notEmpty().withMessage('Body is required'),
  body('type').optional().isIn(['booking', 'payment', 'review', 'general']),
  body('data').optional().isObject()
], async (req, res) => {
  try {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({
        success: false,
        message: 'Validation failed',
        errors: errors.array()
      });
    }

    const { userId, title, body, type = 'general', data = {} } = req.body;

    // Check if user exists
    const user = await User.findById(userId);
    if (!user) {
      return res.status(404).json({
        success: false,
        message: 'User not found'
      });
    }

    // Send notification
    const result = await sendNotification({
      userId,
      title,
      body,
      type,
      data
    });

    if (result.success) {
      res.json({
        success: true,
        message: 'Notification sent successfully',
        data: result
      });
    } else {
      res.status(400).json({
        success: false,
        message: 'Failed to send notification',
        error: result.error
      });
    }
  } catch (error) {
    console.error('Send notification error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while sending notification'
    });
  }
});

// @route   POST /api/notifications/booking
// @desc    Send booking-related notification
// @access  Private
router.post('/booking', protect, [
  body('bookingId').notEmpty().withMessage('Booking ID is required'),
  body('action').isIn(['created', 'accepted', 'declined', 'in_progress', 'completed', 'cancelled']).withMessage('Invalid action'),
  body('message').optional().isLength({ max: 200 })
], async (req, res) => {
  try {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({
        success: false,
        message: 'Validation failed',
        errors: errors.array()
      });
    }

    const { bookingId, action, message } = req.body;

    // Get booking details
    const booking = await Booking.findById(bookingId)
      .populate('customer', 'firstName lastName fcmToken')
      .populate('provider', 'firstName lastName fcmToken')
      .populate('service', 'name category');

    if (!booking) {
      return res.status(404).json({
        success: false,
        message: 'Booking not found'
      });
    }

    let title, body, targetUser;

    switch (action) {
      case 'created':
        title = 'New Booking Request';
        body = `${booking.customer.firstName} ${booking.customer.lastName} has requested your ${booking.service.name} service`;
        targetUser = booking.provider;
        break;
      case 'accepted':
        title = 'Booking Accepted';
        body = `${booking.provider.firstName} ${booking.provider.lastName} has accepted your ${booking.service.name} booking`;
        targetUser = booking.customer;
        break;
      case 'declined':
        title = 'Booking Declined';
        body = `${booking.provider.firstName} ${booking.provider.lastName} has declined your ${booking.service.name} booking`;
        targetUser = booking.customer;
        break;
      case 'in_progress':
        title = 'Service Started';
        body = `${booking.provider.firstName} ${booking.provider.lastName} has started your ${booking.service.name} service`;
        targetUser = booking.customer;
        break;
      case 'completed':
        title = 'Service Completed';
        body = `${booking.provider.firstName} ${booking.provider.lastName} has completed your ${booking.service.name} service`;
        targetUser = booking.customer;
        break;
      case 'cancelled':
        title = 'Booking Cancelled';
        body = `Your ${booking.service.name} booking has been cancelled`;
        targetUser = booking.customer;
        break;
    }

    if (message) {
      body = message;
    }

    // Send notification
    const result = await sendNotification({
      userId: targetUser._id,
      title,
      body,
      type: 'booking',
      data: {
        bookingId: booking._id,
        action,
        serviceName: booking.service.name,
        bookingDate: booking.bookingDate,
        timeSlot: booking.timeSlot
      }
    });

    if (result.success) {
      res.json({
        success: true,
        message: 'Booking notification sent successfully'
      });
    } else {
      res.status(400).json({
        success: false,
        message: 'Failed to send booking notification',
        error: result.error
      });
    }
  } catch (error) {
    console.error('Send booking notification error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while sending booking notification'
    });
  }
});

// @route   POST /api/notifications/bulk
// @desc    Send bulk notifications
// @access  Private (Admin only)
router.post('/bulk', protect, authorize('admin'), [
  body('userIds').isArray().withMessage('User IDs must be an array'),
  body('userIds.*').isMongoId().withMessage('Invalid user ID'),
  body('title').notEmpty().withMessage('Title is required'),
  body('body').notEmpty().withMessage('Body is required'),
  body('type').optional().isIn(['booking', 'payment', 'review', 'general']),
  body('data').optional().isObject()
], async (req, res) => {
  try {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({
        success: false,
        message: 'Validation failed',
        errors: errors.array()
      });
    }

    const { userIds, title, body, type = 'general', data = {} } = req.body;

    const results = [];
    const errorList = [];

    for (const userId of userIds) {
      try {
        const result = await sendNotification({
          userId,
          title,
          body,
          type,
          data
        });
        results.push({ userId, success: result.success });
      } catch (error) {
        errorList.push({ userId, error: error.message });
      }
    }

    res.json({
      success: true,
      message: 'Bulk notification process completed',
      data: {
        successful: results.filter(r => r.success).length,
        failed: errorList.length,
        results,
        errors: errorList
      }
    });
  } catch (error) {
    console.error('Send bulk notification error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while sending bulk notifications'
    });
  }
});

// @route   GET /api/notifications/templates
// @desc    Get notification templates
// @access  Private
router.get('/templates', protect, async (req, res) => {
  try {
    const templates = {
      booking: {
        created: {
          title: 'New Booking Request',
          body: '{customerName} has requested your {serviceName} service'
        },
        accepted: {
          title: 'Booking Accepted',
          body: '{providerName} has accepted your {serviceName} booking'
        },
        declined: {
          title: 'Booking Declined',
          body: '{providerName} has declined your {serviceName} booking'
        },
        in_progress: {
          title: 'Service Started',
          body: '{providerName} has started your {serviceName} service'
        },
        completed: {
          title: 'Service Completed',
          body: '{providerName} has completed your {serviceName} service'
        },
        cancelled: {
          title: 'Booking Cancelled',
          body: 'Your {serviceName} booking has been cancelled'
        }
      },
      payment: {
        success: {
          title: 'Payment Successful',
          body: 'Your payment of ${amount} has been processed successfully'
        },
        failed: {
          title: 'Payment Failed',
          body: 'Your payment of ${amount} could not be processed'
        },
        refund: {
          title: 'Refund Processed',
          body: 'Your refund of ${amount} has been processed'
        }
      },
      review: {
        received: {
          title: 'New Review',
          body: '{customerName} has left a {rating}-star review for your {serviceName} service'
        }
      }
    };

    res.json({
      success: true,
      data: { templates }
    });
  } catch (error) {
    console.error('Get notification templates error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while fetching templates'
    });
  }
});

module.exports = router;
