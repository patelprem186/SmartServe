const express = require('express');
const { body, validationResult } = require('express-validator');
const Booking = require('../models/Booking');
const { protect } = require('../middleware/auth');
const { processPayment, refundPayment, getPaymentStatus } = require('../services/PaymentService');

const router = express.Router();

// @route   POST /api/payments/process
// @desc    Process payment for booking
// @access  Private
router.post('/process', protect, [
  body('bookingId').notEmpty().withMessage('Booking ID is required'),
  body('amount').isFloat({ min: 0 }).withMessage('Amount must be a positive number'),
  body('paymentMethod').isIn(['credit_card', 'debit_card', 'paypal', 'stripe']).withMessage('Invalid payment method'),
  body('cardDetails').optional().isObject(),
  body('billingAddress').optional().isObject()
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

    const { bookingId, amount, paymentMethod, cardDetails, billingAddress } = req.body;

    // Get booking details
    const booking = await Booking.findById(bookingId)
      .populate('customer', 'firstName lastName email')
      .populate('provider', 'firstName lastName email')
      .populate('service', 'name category');

    if (!booking) {
      return res.status(404).json({
        success: false,
        message: 'Booking not found'
      });
    }

    // Process payment
    const paymentResult = await processPayment({
      bookingId,
      amount,
      paymentMethod,
      cardDetails,
      billingAddress,
      customer: booking.customer,
      provider: booking.provider,
      service: booking.service
    });

    if (paymentResult.success) {
      // Update booking payment status
      booking.paymentStatus = 'paid';
      booking.paymentMethod = paymentMethod;
      booking.paymentId = paymentResult.paymentId;
      await booking.save();

      res.json({
        success: true,
        message: 'Payment processed successfully',
        data: {
          paymentId: paymentResult.paymentId,
          amount: paymentResult.amount,
          status: paymentResult.status
        }
      });
    } else {
      res.status(400).json({
        success: false,
        message: 'Payment processing failed',
        error: paymentResult.error
      });
    }
  } catch (error) {
    console.error('Process payment error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while processing payment'
    });
  }
});

// @route   POST /api/payments/refund
// @desc    Process refund for booking
// @access  Private
router.post('/refund', protect, [
  body('bookingId').notEmpty().withMessage('Booking ID is required'),
  body('amount').optional().isFloat({ min: 0 }).withMessage('Amount must be a positive number'),
  body('reason').optional().isLength({ max: 200 }).withMessage('Reason cannot exceed 200 characters')
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

    const { bookingId, amount, reason } = req.body;

    // Get booking details
    const booking = await Booking.findById(bookingId);
    if (!booking) {
      return res.status(404).json({
        success: false,
        message: 'Booking not found'
      });
    }

    if (booking.paymentStatus !== 'paid') {
      return res.status(400).json({
        success: false,
        message: 'Booking is not paid, cannot process refund'
      });
    }

    // Process refund
    const refundResult = await refundPayment({
      paymentId: booking.paymentId,
      amount: amount || booking.totalAmount,
      reason
    });

    if (refundResult.success) {
      // Update booking payment status
      booking.paymentStatus = 'refunded';
      booking.refundId = refundResult.refundId;
      await booking.save();

      res.json({
        success: true,
        message: 'Refund processed successfully',
        data: {
          refundId: refundResult.refundId,
          amount: refundResult.amount,
          status: refundResult.status
        }
      });
    } else {
      res.status(400).json({
        success: false,
        message: 'Refund processing failed',
        error: refundResult.error
      });
    }
  } catch (error) {
    console.error('Process refund error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while processing refund'
    });
  }
});

// @route   GET /api/payments/status/:paymentId
// @desc    Get payment status
// @access  Private
router.get('/status/:paymentId', protect, async (req, res) => {
  try {
    const { paymentId } = req.params;

    const status = await getPaymentStatus(paymentId);

    res.json({
      success: true,
      data: status
    });
  } catch (error) {
    console.error('Get payment status error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while getting payment status'
    });
  }
});

// @route   GET /api/payments/history
// @desc    Get user's payment history
// @access  Private
router.get('/history', protect, async (req, res) => {
  try {
    const userId = req.user.id;
    const { page = 1, limit = 20 } = req.query;
    const skip = (parseInt(page) - 1) * parseInt(limit);

    const bookings = await Booking.find({ customer: userId })
      .populate('service', 'name category')
      .populate('provider', 'firstName lastName')
      .select('totalAmount paymentStatus paymentMethod createdAt')
      .sort({ createdAt: -1 })
      .skip(skip)
      .limit(parseInt(limit));

    const total = await Booking.countDocuments({ customer: userId });

    res.json({
      success: true,
      data: {
        payments: bookings,
        pagination: {
          currentPage: parseInt(page),
          totalPages: Math.ceil(total / parseInt(limit)),
          totalItems: total,
          itemsPerPage: parseInt(limit)
        }
      }
    });
  } catch (error) {
    console.error('Get payment history error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while fetching payment history'
    });
  }
});

module.exports = router;
