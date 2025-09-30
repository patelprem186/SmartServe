const express = require('express');
const { body, validationResult, query } = require('express-validator');
const Booking = require('../models/Booking');
const Service = require('../models/Service');
const User = require('../models/User');
const { protect, authorize } = require('../middleware/auth');
const { sendNotification } = require('../services/NotificationService');

const router = express.Router();

// @route   POST /api/bookings
// @desc    Create new booking
// @access  Private (Customer only)
router.post('/', protect, authorize('customer'), [
  body('service').isMongoId().withMessage('Valid service ID is required'),
  body('bookingDate').isISO8601().withMessage('Valid booking date is required'),
  body('timeSlot').trim().notEmpty().withMessage('Time slot is required'),
  body('address.street').trim().notEmpty().withMessage('Street address is required'),
  body('address.city').trim().notEmpty().withMessage('City is required'),
  body('address.state').trim().notEmpty().withMessage('State is required'),
  body('address.zipCode').trim().notEmpty().withMessage('Zip code is required'),
  body('notes').optional().isLength({ max: 500 }).withMessage('Notes cannot exceed 500 characters'),
  body('specialInstructions').optional().isLength({ max: 500 }).withMessage('Special instructions cannot exceed 500 characters')
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

    const { service, bookingDate, timeSlot, address, notes, specialInstructions } = req.body;

    // Get service details
    const serviceData = await Service.findById(service).populate('provider');
    if (!serviceData) {
      return res.status(404).json({
        success: false,
        message: 'Service not found'
      });
    }

    // Check if service is active
    if (!serviceData.isActive) {
      return res.status(400).json({
        success: false,
        message: 'Service is not available'
      });
    }

    // Create booking
    const bookingData = {
      service,
      customer: req.user.id,
      provider: serviceData.provider._id,
      bookingDate: new Date(bookingDate),
      timeSlot,
      address,
      totalAmount: serviceData.price,
      notes,
      specialInstructions,
      customerPhone: req.user.phone,
      customerEmail: req.user.email
    };

    const booking = new Booking(bookingData);
    await booking.save();

    // Populate booking details
    await booking.populate([
      { path: 'service', select: 'name description category price duration' },
      { path: 'customer', select: 'firstName lastName email phone' },
      { path: 'provider', select: 'firstName lastName email phone' }
    ]);

    // Send notification to provider
    await sendNotification({
      userId: serviceData.provider._id,
      title: 'New Booking Request',
      body: `You have a new booking request for ${serviceData.name}`,
      data: { bookingId: booking._id, type: 'new_booking' }
    });

    res.status(201).json({
      success: true,
      message: 'Booking created successfully',
      data: { booking }
    });
  } catch (error) {
    console.error('Create booking error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while creating booking'
    });
  }
});

// @route   GET /api/bookings
// @desc    Get user's bookings
// @access  Private
router.get('/', protect, [
  query('status').optional().isIn(['pending', 'accepted', 'declined', 'in_progress', 'completed', 'cancelled']),
  query('page').optional().isInt({ min: 1 }),
  query('limit').optional().isInt({ min: 1, max: 50 })
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

    const { status, page = 1, limit = 10 } = req.query;
    const skip = (parseInt(page) - 1) * parseInt(limit);

    // Build filter based on user role
    const filter = {};
    if (req.user.role === 'customer') {
      filter.customer = req.user.id;
    } else if (req.user.role === 'provider') {
      filter.provider = req.user.id;
    }

    if (status) {
      filter.status = status;
    }

    const bookings = await Booking.find(filter)
      .populate('service', 'name description category price duration images')
      .populate('customer', 'firstName lastName email phone profileImage')
      .populate('provider', 'firstName lastName email phone profileImage')
      .sort({ createdAt: -1 })
      .skip(skip)
      .limit(parseInt(limit));

    const total = await Booking.countDocuments(filter);

    res.json({
      success: true,
      data: {
        bookings,
        pagination: {
          currentPage: parseInt(page),
          totalPages: Math.ceil(total / parseInt(limit)),
          totalItems: total,
          itemsPerPage: parseInt(limit)
        }
      }
    });
  } catch (error) {
    console.error('Get bookings error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while fetching bookings'
    });
  }
});

// @route   GET /api/bookings/:id
// @desc    Get single booking
// @access  Private
router.get('/:id', protect, async (req, res) => {
  try {
    const booking = await Booking.findById(req.params.id)
      .populate('service', 'name description category price duration images')
      .populate('customer', 'firstName lastName email phone profileImage')
      .populate('provider', 'firstName lastName email phone profileImage');

    if (!booking) {
      return res.status(404).json({
        success: false,
        message: 'Booking not found'
      });
    }

    // Check if user has access to this booking
    if (booking.customer._id.toString() !== req.user.id && 
        booking.provider._id.toString() !== req.user.id) {
      return res.status(403).json({
        success: false,
        message: 'Not authorized to view this booking'
      });
    }

    res.json({
      success: true,
      data: { booking }
    });
  } catch (error) {
    console.error('Get booking error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while fetching booking'
    });
  }
});

// @route   PUT /api/bookings/:id/accept
// @desc    Accept booking (Provider only)
// @access  Private (Provider only)
router.put('/:id/accept', protect, authorize('provider'), [
  body('estimatedArrival').optional().isISO8601().withMessage('Valid estimated arrival time is required'),
  body('notes').optional().isLength({ max: 500 }).withMessage('Notes cannot exceed 500 characters')
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

    const { estimatedArrival, notes } = req.body;

    const booking = await Booking.findById(req.params.id);

    if (!booking) {
      return res.status(404).json({
        success: false,
        message: 'Booking not found'
      });
    }

    // Check if provider owns this booking
    if (booking.provider.toString() !== req.user.id) {
      return res.status(403).json({
        success: false,
        message: 'Not authorized to accept this booking'
      });
    }

    // Check if booking is still pending
    if (booking.status !== 'pending') {
      return res.status(400).json({
        success: false,
        message: 'Booking is no longer pending'
      });
    }

    // Update booking
    booking.status = 'accepted';
    booking.providerResponse = {
      status: 'accepted',
      responseTime: new Date(),
      estimatedArrival: estimatedArrival ? new Date(estimatedArrival) : null,
      notes
    };

    await booking.save();

    // Send notification to customer
    await sendNotification({
      userId: booking.customer,
      title: 'Booking Accepted',
      body: 'Your booking request has been accepted',
      data: { bookingId: booking._id, type: 'booking_accepted' }
    });

    res.json({
      success: true,
      message: 'Booking accepted successfully',
      data: { booking }
    });
  } catch (error) {
    console.error('Accept booking error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while accepting booking'
    });
  }
});

// @route   PUT /api/bookings/:id/decline
// @desc    Decline booking (Provider only)
// @access  Private (Provider only)
router.put('/:id/decline', protect, authorize('provider'), [
  body('declineReason').trim().notEmpty().withMessage('Decline reason is required'),
  body('notes').optional().isLength({ max: 500 }).withMessage('Notes cannot exceed 500 characters')
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

    const { declineReason, notes } = req.body;

    const booking = await Booking.findById(req.params.id);

    if (!booking) {
      return res.status(404).json({
        success: false,
        message: 'Booking not found'
      });
    }

    // Check if provider owns this booking
    if (booking.provider.toString() !== req.user.id) {
      return res.status(403).json({
        success: false,
        message: 'Not authorized to decline this booking'
      });
    }

    // Check if booking is still pending
    if (booking.status !== 'pending') {
      return res.status(400).json({
        success: false,
        message: 'Booking is no longer pending'
      });
    }

    // Update booking
    booking.status = 'declined';
    booking.providerResponse = {
      status: 'declined',
      responseTime: new Date(),
      declineReason,
      notes
    };

    await booking.save();

    // Send notification to customer
    await sendNotification({
      userId: booking.customer,
      title: 'Booking Declined',
      body: 'Your booking request has been declined',
      data: { bookingId: booking._id, type: 'booking_declined' }
    });

    res.json({
      success: true,
      message: 'Booking declined successfully',
      data: { booking }
    });
  } catch (error) {
    console.error('Decline booking error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while declining booking'
    });
  }
});

// @route   PUT /api/bookings/:id/start
// @desc    Start service (Provider only)
// @access  Private (Provider only)
router.put('/:id/start', protect, authorize('provider'), async (req, res) => {
  try {
    const booking = await Booking.findById(req.params.id);

    if (!booking) {
      return res.status(404).json({
        success: false,
        message: 'Booking not found'
      });
    }

    // Check if provider owns this booking
    if (booking.provider.toString() !== req.user.id) {
      return res.status(403).json({
        success: false,
        message: 'Not authorized to start this booking'
      });
    }

    // Check if booking is accepted
    if (booking.status !== 'accepted') {
      return res.status(400).json({
        success: false,
        message: 'Booking must be accepted before starting'
      });
    }

    // Update booking
    booking.status = 'in_progress';
    await booking.save();

    // Send notification to customer
    await sendNotification({
      userId: booking.customer,
      title: 'Service Started',
      body: 'Your service has started',
      data: { bookingId: booking._id, type: 'service_started' }
    });

    res.json({
      success: true,
      message: 'Service started successfully',
      data: { booking }
    });
  } catch (error) {
    console.error('Start service error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while starting service'
    });
  }
});

// @route   PUT /api/bookings/:id/complete
// @desc    Complete service (Provider only)
// @access  Private (Provider only)
router.put('/:id/complete', protect, authorize('provider'), [
  body('completionNotes').optional().isLength({ max: 500 }).withMessage('Completion notes cannot exceed 500 characters'),
  body('beforePhotos').optional().isArray().withMessage('Before photos must be an array'),
  body('afterPhotos').optional().isArray().withMessage('After photos must be an array')
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

    const { completionNotes, beforePhotos, afterPhotos } = req.body;

    const booking = await Booking.findById(req.params.id);

    if (!booking) {
      return res.status(404).json({
        success: false,
        message: 'Booking not found'
      });
    }

    // Check if provider owns this booking
    if (booking.provider.toString() !== req.user.id) {
      return res.status(403).json({
        success: false,
        message: 'Not authorized to complete this booking'
      });
    }

    // Check if booking is in progress
    if (booking.status !== 'in_progress') {
      return res.status(400).json({
        success: false,
        message: 'Service must be in progress to complete'
      });
    }

    // Update booking
    booking.status = 'completed';
    booking.completionDetails = {
      completedAt: new Date(),
      completionNotes,
      beforePhotos,
      afterPhotos
    };

    await booking.save();

    // Send notification to customer
    await sendNotification({
      userId: booking.customer,
      title: 'Service Completed',
      body: 'Your service has been completed',
      data: { bookingId: booking._id, type: 'service_completed' }
    });

    res.json({
      success: true,
      message: 'Service completed successfully',
      data: { booking }
    });
  } catch (error) {
    console.error('Complete service error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while completing service'
    });
  }
});

// @route   PUT /api/bookings/:id/cancel
// @desc    Cancel booking
// @access  Private
router.put('/:id/cancel', protect, [
  body('reason').trim().notEmpty().withMessage('Cancellation reason is required')
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

    const { reason } = req.body;

    const booking = await Booking.findById(req.params.id);

    if (!booking) {
      return res.status(404).json({
        success: false,
        message: 'Booking not found'
      });
    }

    // Check if user has access to this booking
    if (booking.customer.toString() !== req.user.id && 
        booking.provider.toString() !== req.user.id) {
      return res.status(403).json({
        success: false,
        message: 'Not authorized to cancel this booking'
      });
    }

    // Check if booking can be cancelled
    if (['completed', 'cancelled'].includes(booking.status)) {
      return res.status(400).json({
        success: false,
        message: 'Booking cannot be cancelled'
      });
    }

    // Update booking
    booking.status = 'cancelled';
    booking.cancellation = {
      cancelledBy: booking.customer.toString() === req.user.id ? 'customer' : 'provider',
      cancelledAt: new Date(),
      reason
    };

    await booking.save();

    // Send notification to the other party
    const notificationUserId = booking.customer.toString() === req.user.id ? 
      booking.provider : booking.customer;

    await sendNotification({
      userId: notificationUserId,
      title: 'Booking Cancelled',
      body: 'A booking has been cancelled',
      data: { bookingId: booking._id, type: 'booking_cancelled' }
    });

    res.json({
      success: true,
      message: 'Booking cancelled successfully',
      data: { booking }
    });
  } catch (error) {
    console.error('Cancel booking error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while cancelling booking'
    });
  }
});

module.exports = router;