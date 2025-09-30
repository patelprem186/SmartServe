const express = require('express');
const { body, validationResult } = require('express-validator');
const User = require('../models/User');
const Booking = require('../models/Booking');
const { protect, authorize } = require('../middleware/auth');

const router = express.Router();

// @route   GET /api/customers/profile
// @desc    Get customer profile
// @access  Private (Customer)
router.get('/profile', protect, authorize('customer'), async (req, res) => {
  try {
    const customer = await User.findById(req.user.id);

    res.json({
      success: true,
      data: { customer }
    });
  } catch (error) {
    console.error('Get customer profile error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while fetching customer profile'
    });
  }
});

// @route   PUT /api/customers/profile
// @desc    Update customer profile
// @access  Private (Customer)
router.put('/profile', protect, authorize('customer'), [
  body('customerInfo.address.street').optional().trim(),
  body('customerInfo.address.city').optional().trim(),
  body('customerInfo.address.state').optional().trim(),
  body('customerInfo.address.zipCode').optional().trim(),
  body('customerInfo.preferences.notifications').optional().isBoolean(),
  body('customerInfo.preferences.emailUpdates').optional().isBoolean()
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

    const customer = await User.findById(req.user.id);

    // Update customer info
    if (req.body.customerInfo) {
      Object.keys(req.body.customerInfo).forEach(key => {
        if (req.body.customerInfo[key] !== undefined) {
          customer.customerInfo[key] = req.body.customerInfo[key];
        }
      });
    }

    await customer.save();

    res.json({
      success: true,
      message: 'Customer profile updated successfully',
      data: { customer }
    });
  } catch (error) {
    console.error('Update customer profile error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while updating customer profile'
    });
  }
});

// @route   GET /api/customers/dashboard
// @desc    Get customer dashboard data
// @access  Private (Customer)
router.get('/dashboard', protect, authorize('customer'), async (req, res) => {
  try {
    const customer = await User.findById(req.user.id);

    // Get recent bookings
    const recentBookings = await Booking.find({ customer: req.user.id })
      .populate('provider', 'firstName lastName profileImage providerInfo.rating')
      .populate('service', 'name price')
      .sort({ createdAt: -1 })
      .limit(5);

    // Get booking statistics
    const totalBookings = await Booking.countDocuments({ customer: req.user.id });
    const pendingBookings = await Booking.countDocuments({ 
      customer: req.user.id, 
      status: 'pending' 
    });
    const confirmedBookings = await Booking.countDocuments({ 
      customer: req.user.id, 
      status: 'confirmed' 
    });
    const completedBookings = await Booking.countDocuments({ 
      customer: req.user.id, 
      status: 'completed' 
    });

    // Get total spent
    const spendingResult = await Booking.aggregate([
      { $match: { customer: customer._id, status: 'completed' } },
      { $group: { _id: null, totalSpent: { $sum: '$payment.amount' } } }
    ]);

    const totalSpent = spendingResult.length > 0 ? spendingResult[0].totalSpent : 0;

    // Get favorite categories (most booked services by category)
    const favoriteCategories = await Booking.aggregate([
      { $match: { customer: customer._id, status: 'completed' } },
      { $lookup: { from: 'services', localField: 'service', foreignField: '_id', as: 'serviceData' } },
      { $unwind: '$serviceData' },
      { $lookup: { from: 'servicecategories', localField: 'serviceData.category', foreignField: '_id', as: 'categoryData' } },
      { $unwind: '$categoryData' },
      { $group: { _id: '$categoryData._id', categoryName: { $first: '$categoryData.name' }, count: { $sum: 1 } } },
      { $sort: { count: -1 } },
      { $limit: 3 }
    ]);

    res.json({
      success: true,
      data: {
        customer: {
          id: customer._id,
          name: customer.fullName,
          email: customer.email
        },
        statistics: {
          totalBookings,
          pendingBookings,
          confirmedBookings,
          completedBookings,
          totalSpent
        },
        recentBookings,
        favoriteCategories
      }
    });
  } catch (error) {
    console.error('Get customer dashboard error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while fetching dashboard data'
    });
  }
});

// @route   GET /api/customers/booking-history
// @desc    Get customer booking history
// @access  Private (Customer)
router.get('/booking-history', protect, authorize('customer'), async (req, res) => {
  try {
    const { status, page = 1, limit = 20 } = req.query;
    const skip = (parseInt(page) - 1) * parseInt(limit);

    let query = { customer: req.user.id };
    
    if (status) {
      query.status = status;
    }

    const bookings = await Booking.find(query)
      .populate('provider', 'firstName lastName profileImage providerInfo.rating providerInfo.businessName')
      .populate('service', 'name description price duration')
      .sort({ bookingDate: -1, createdAt: -1 })
      .skip(skip)
      .limit(parseInt(limit));

    const total = await Booking.countDocuments(query);

    res.json({
      success: true,
      data: {
        bookings,
        pagination: {
          currentPage: parseInt(page),
          totalPages: Math.ceil(total / parseInt(limit)),
          totalBookings: total,
          hasNext: skip + bookings.length < total,
          hasPrev: parseInt(page) > 1
        }
      }
    });
  } catch (error) {
    console.error('Get customer booking history error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while fetching booking history'
    });
  }
});

// @route   GET /api/customers/favorites
// @desc    Get customer's favorite services/providers
// @access  Private (Customer)
router.get('/favorites', protect, authorize('customer'), async (req, res) => {
  try {
    // Get most booked services
    const favoriteServices = await Booking.aggregate([
      { $match: { customer: req.user.id, status: 'completed' } },
      { $lookup: { from: 'services', localField: 'service', foreignField: '_id', as: 'serviceData' } },
      { $unwind: '$serviceData' },
      { $lookup: { from: 'users', localField: 'serviceData.provider', foreignField: '_id', as: 'providerData' } },
      { $unwind: '$providerData' },
      { $group: { 
        _id: '$serviceData._id', 
        service: { $first: '$serviceData' },
        provider: { $first: '$providerData' },
        bookingCount: { $sum: 1 },
        lastBooked: { $max: '$createdAt' }
      }},
      { $sort: { bookingCount: -1, lastBooked: -1 } },
      { $limit: 10 }
    ]);

    // Get most booked providers
    const favoriteProviders = await Booking.aggregate([
      { $match: { customer: req.user.id, status: 'completed' } },
      { $lookup: { from: 'users', localField: 'provider', foreignField: '_id', as: 'providerData' } },
      { $unwind: '$providerData' },
      { $group: { 
        _id: '$providerData._id', 
        provider: { $first: '$providerData' },
        bookingCount: { $sum: 1 },
        totalSpent: { $sum: '$payment.amount' },
        lastBooked: { $max: '$createdAt' }
      }},
      { $sort: { bookingCount: -1, lastBooked: -1 } },
      { $limit: 5 }
    ]);

    res.json({
      success: true,
      data: {
        favoriteServices,
        favoriteProviders
      }
    });
  } catch (error) {
    console.error('Get customer favorites error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while fetching favorites'
    });
  }
});

// @route   POST /api/customers/fcm-token
// @desc    Update customer FCM token for notifications
// @access  Private (Customer)
router.post('/fcm-token', protect, authorize('customer'), [
  body('fcmToken').notEmpty().withMessage('FCM token is required')
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

    const { fcmToken } = req.body;

    await User.findByIdAndUpdate(req.user.id, { fcmToken });

    res.json({
      success: true,
      message: 'FCM token updated successfully'
    });
  } catch (error) {
    console.error('Update FCM token error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while updating FCM token'
    });
  }
});

module.exports = router;
