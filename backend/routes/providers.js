const express = require('express');
const { body, validationResult } = require('express-validator');
const User = require('../models/User');
const Service = require('../models/Service');
const Booking = require('../models/Booking');
const { protect, authorize } = require('../middleware/auth');

const router = express.Router();

// @route   GET /api/providers/profile
// @desc    Get provider profile
// @access  Private (Provider)
router.get('/profile', protect, authorize('provider'), async (req, res) => {
  try {
    const provider = await User.findById(req.user.id)
      .populate('providerInfo.services', 'name description price rating');

    res.json({
      success: true,
      data: { provider }
    });
  } catch (error) {
    console.error('Get provider profile error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while fetching provider profile'
    });
  }
});

// @route   PUT /api/providers/profile
// @desc    Update provider profile
// @access  Private (Provider)
router.put('/profile', protect, authorize('provider'), [
  body('providerInfo.businessName').optional().trim().isLength({ min: 2, max: 100 }),
  body('providerInfo.experience').optional().isInt({ min: 0 }),
  body('providerInfo.businessAddress.street').optional().trim(),
  body('providerInfo.businessAddress.city').optional().trim(),
  body('providerInfo.businessAddress.state').optional().trim(),
  body('providerInfo.businessAddress.zipCode').optional().trim(),
  body('providerInfo.isAvailable').optional().isBoolean()
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

    const provider = await User.findById(req.user.id);

    // Update provider info
    if (req.body.providerInfo) {
      Object.keys(req.body.providerInfo).forEach(key => {
        if (req.body.providerInfo[key] !== undefined) {
          provider.providerInfo[key] = req.body.providerInfo[key];
        }
      });
    }

    await provider.save();

    res.json({
      success: true,
      message: 'Provider profile updated successfully',
      data: { provider }
    });
  } catch (error) {
    console.error('Update provider profile error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while updating provider profile'
    });
  }
});

// @route   PUT /api/providers/working-hours
// @desc    Update provider working hours
// @access  Private (Provider)
router.put('/working-hours', protect, authorize('provider'), async (req, res) => {
  try {
    const { workingHours } = req.body;

    if (!workingHours || typeof workingHours !== 'object') {
      return res.status(400).json({
        success: false,
        message: 'Working hours data is required'
      });
    }

    const provider = await User.findById(req.user.id);
    provider.providerInfo.workingHours = workingHours;
    await provider.save();

    res.json({
      success: true,
      message: 'Working hours updated successfully',
      data: { workingHours: provider.providerInfo.workingHours }
    });
  } catch (error) {
    console.error('Update working hours error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while updating working hours'
    });
  }
});

// @route   GET /api/providers/my-services
// @desc    Get provider's services
// @access  Private (Provider)
router.get('/my-services', protect, authorize('provider'), async (req, res) => {
  try {
    const { page = 1, limit = 20, status = 'all' } = req.query;
    const skip = (parseInt(page) - 1) * parseInt(limit);

    let query = { provider: req.user.id };
    
    if (status !== 'all') {
      query.isActive = status === 'active';
    }

    const services = await Service.find(query)
      .populate('category', 'name icon color')
      .sort({ createdAt: -1 })
      .skip(skip)
      .limit(parseInt(limit));

    const total = await Service.countDocuments(query);

    res.json({
      success: true,
      data: {
        services,
        pagination: {
          currentPage: parseInt(page),
          totalPages: Math.ceil(total / parseInt(limit)),
          totalServices: total,
          hasNext: skip + services.length < total,
          hasPrev: parseInt(page) > 1
        }
      }
    });
  } catch (error) {
    console.error('Get provider services error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while fetching services'
    });
  }
});

// @route   GET /api/providers/dashboard
// @desc    Get provider dashboard data
// @access  Private (Provider)
router.get('/dashboard', protect, authorize('provider'), async (req, res) => {
  try {
    const provider = await User.findById(req.user.id);

    // Get recent bookings
    const recentBookings = await Booking.find({ provider: req.user.id })
      .populate('customer', 'firstName lastName profileImage')
      .populate('service', 'name price')
      .sort({ createdAt: -1 })
      .limit(5);

    // Get booking statistics
    const totalBookings = await Booking.countDocuments({ provider: req.user.id });
    const pendingBookings = await Booking.countDocuments({ 
      provider: req.user.id, 
      status: 'pending' 
    });
    const confirmedBookings = await Booking.countDocuments({ 
      provider: req.user.id, 
      status: 'confirmed' 
    });
    const completedBookings = await Booking.countDocuments({ 
      provider: req.user.id, 
      status: 'completed' 
    });

    // Get total earnings
    const earningsResult = await Booking.aggregate([
      { $match: { provider: provider._id, status: 'completed' } },
      { $group: { _id: null, totalEarnings: { $sum: '$payment.amount' } } }
    ]);

    const totalEarnings = earningsResult.length > 0 ? earningsResult[0].totalEarnings : 0;

    // Get monthly earnings for the last 6 months
    const sixMonthsAgo = new Date();
    sixMonthsAgo.setMonth(sixMonthsAgo.getMonth() - 6);

    const monthlyEarnings = await Booking.aggregate([
      {
        $match: {
          provider: provider._id,
          status: 'completed',
          createdAt: { $gte: sixMonthsAgo }
        }
      },
      {
        $group: {
          _id: {
            year: { $year: '$createdAt' },
            month: { $month: '$createdAt' }
          },
          earnings: { $sum: '$payment.amount' },
          bookings: { $sum: 1 }
        }
      },
      { $sort: { '_id.year': 1, '_id.month': 1 } }
    ]);

    res.json({
      success: true,
      data: {
        provider: {
          id: provider._id,
          name: provider.fullName,
          rating: provider.providerInfo.rating,
          isAvailable: provider.providerInfo.isAvailable
        },
        statistics: {
          totalBookings,
          pendingBookings,
          confirmedBookings,
          completedBookings,
          totalEarnings
        },
        recentBookings,
        monthlyEarnings
      }
    });
  } catch (error) {
    console.error('Get provider dashboard error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while fetching dashboard data'
    });
  }
});

// @route   GET /api/providers/earnings
// @desc    Get provider earnings report
// @access  Private (Provider)
router.get('/earnings', protect, authorize('provider'), async (req, res) => {
  try {
    const { startDate, endDate, page = 1, limit = 20 } = req.query;
    const skip = (parseInt(page) - 1) * parseInt(limit);

    let query = { 
      provider: req.user.id, 
      status: 'completed' 
    };

    if (startDate && endDate) {
      query.createdAt = {
        $gte: new Date(startDate),
        $lte: new Date(endDate)
      };
    }

    const bookings = await Booking.find(query)
      .populate('customer', 'firstName lastName')
      .populate('service', 'name')
      .sort({ createdAt: -1 })
      .skip(skip)
      .limit(parseInt(limit));

    const total = await Booking.countDocuments(query);

    // Calculate total earnings for the period
    const earningsResult = await Booking.aggregate([
      { $match: query },
      { $group: { _id: null, totalEarnings: { $sum: '$payment.amount' } } }
    ]);

    const totalEarnings = earningsResult.length > 0 ? earningsResult[0].totalEarnings : 0;

    res.json({
      success: true,
      data: {
        bookings,
        totalEarnings,
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
    console.error('Get provider earnings error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while fetching earnings'
    });
  }
});

// @route   GET /api/providers/reviews
// @desc    Get provider reviews
// @access  Private (Provider)
router.get('/reviews', protect, authorize('provider'), async (req, res) => {
  try {
    const { page = 1, limit = 20 } = req.query;
    const skip = (parseInt(page) - 1) * parseInt(limit);

    const bookings = await Booking.find({
      provider: req.user.id,
      'rating.customerRating.rating': { $exists: true }
    })
    .populate('customer', 'firstName lastName profileImage')
    .populate('service', 'name')
    .sort({ 'rating.customerRating.ratedAt': -1 })
    .skip(skip)
    .limit(parseInt(limit));

    const total = await Booking.countDocuments({
      provider: req.user.id,
      'rating.customerRating.rating': { $exists: true }
    });

    res.json({
      success: true,
      data: {
        reviews: bookings,
        pagination: {
          currentPage: parseInt(page),
          totalPages: Math.ceil(total / parseInt(limit)),
          totalReviews: total,
          hasNext: skip + bookings.length < total,
          hasPrev: parseInt(page) > 1
        }
      }
    });
  } catch (error) {
    console.error('Get provider reviews error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while fetching reviews'
    });
  }
});

module.exports = router;
