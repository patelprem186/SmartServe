const express = require('express');
const { body, validationResult, query } = require('express-validator');
const User = require('../models/User');
const Booking = require('../models/Booking');
const Service = require('../models/Service');
const Review = require('../models/Review');
const ProviderAvailability = require('../models/ProviderAvailability');
const { protect, authorize } = require('../middleware/auth');

const router = express.Router();

// @route   GET /api/provider/dashboard
// @desc    Get provider dashboard data
// @access  Private (Provider only)
router.get('/dashboard', protect, authorize('provider'), async (req, res) => {
  try {
    const providerId = req.user.id;

    // Get booking statistics
    const totalBookings = await Booking.countDocuments({ provider: providerId });
    const pendingBookings = await Booking.countDocuments({ 
      provider: providerId, 
      status: 'pending' 
    });
    const acceptedBookings = await Booking.countDocuments({ 
      provider: providerId, 
      status: 'accepted' 
    });
    const completedBookings = await Booking.countDocuments({ 
      provider: providerId, 
      status: 'completed' 
    });
    const inProgressBookings = await Booking.countDocuments({ 
      provider: providerId, 
      status: 'in_progress' 
    });

    // Get recent bookings
    const recentBookings = await Booking.find({ provider: providerId })
      .populate('customer', 'firstName lastName email phone profileImage')
      .populate('service', 'name category price')
      .sort({ createdAt: -1 })
      .limit(5);

    // Get today's bookings
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);

    const todaysBookings = await Booking.find({
      provider: providerId,
      bookingDate: { $gte: today, $lt: tomorrow },
      status: { $in: ['accepted', 'in_progress'] }
    })
      .populate('customer', 'firstName lastName email phone')
      .populate('service', 'name category')
      .sort({ timeSlot: 1 });

    // Get earnings (last 30 days)
    const thirtyDaysAgo = new Date();
    thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);

    const earnings = await Booking.aggregate([
      {
        $match: {
          provider: providerId,
          status: 'completed',
          createdAt: { $gte: thirtyDaysAgo }
        }
      },
      {
        $group: {
          _id: null,
          totalEarnings: { $sum: '$totalAmount' },
          completedCount: { $sum: 1 }
        }
      }
    ]);

    // Get rating and review statistics
    const reviewStats = await Review.aggregate([
      {
        $match: { provider: providerId }
      },
      {
        $group: {
          _id: null,
          averageRating: { $avg: '$rating' },
          totalReviews: { $sum: 1 },
          ratingDistribution: {
            $push: '$rating'
          }
        }
      }
    ]);

    // Get services count
    const servicesCount = await Service.countDocuments({ 
      provider: providerId, 
      isActive: true 
    });

    res.json({
      success: true,
      data: {
        statistics: {
          totalBookings,
          pendingBookings,
          acceptedBookings,
          completedBookings,
          inProgressBookings,
          servicesCount,
          totalEarnings: earnings[0]?.totalEarnings || 0,
          completedThisMonth: earnings[0]?.completedCount || 0
        },
        rating: {
          averageRating: reviewStats[0]?.averageRating || 0,
          totalReviews: reviewStats[0]?.totalReviews || 0
        },
        recentBookings,
        todaysBookings
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

// @route   GET /api/provider/bookings
// @desc    Get provider's bookings with filtering
// @access  Private (Provider only)
router.get('/bookings', protect, authorize('provider'), [
  query('status').optional().isIn(['pending', 'accepted', 'declined', 'in_progress', 'completed', 'cancelled']),
  query('date').optional().isISO8601(),
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

    const { status, date, page = 1, limit = 10 } = req.query;
    const skip = (parseInt(page) - 1) * parseInt(limit);

    const filter = { provider: req.user.id };

    if (status) {
      filter.status = status;
    }

    if (date) {
      const startDate = new Date(date);
      startDate.setHours(0, 0, 0, 0);
      const endDate = new Date(date);
      endDate.setHours(23, 59, 59, 999);
      filter.bookingDate = { $gte: startDate, $lte: endDate };
    }

    const bookings = await Booking.find(filter)
      .populate('customer', 'firstName lastName email phone profileImage')
      .populate('service', 'name description category price duration images')
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
    console.error('Get provider bookings error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while fetching bookings'
    });
  }
});

// @route   GET /api/provider/availability
// @desc    Get provider's availability
// @access  Private (Provider only)
router.get('/availability', protect, authorize('provider'), [
  query('startDate').optional().isISO8601(),
  query('endDate').optional().isISO8601()
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

    const { startDate, endDate } = req.query;
    const filter = { provider: req.user.id };

    if (startDate && endDate) {
      filter.date = {
        $gte: new Date(startDate),
        $lte: new Date(endDate)
      };
    }

    const availability = await ProviderAvailability.find(filter)
      .sort({ date: 1 });

    res.json({
      success: true,
      data: { availability }
    });
  } catch (error) {
    console.error('Get provider availability error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while fetching availability'
    });
  }
});

// @route   POST /api/provider/availability
// @desc    Set provider's availability
// @access  Private (Provider only)
router.post('/availability', protect, authorize('provider'), [
  body('date').isISO8601().withMessage('Valid date is required'),
  body('timeSlots').isArray().withMessage('Time slots must be an array'),
  body('timeSlots.*.start').matches(/^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$/).withMessage('Invalid start time format'),
  body('timeSlots.*.end').matches(/^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$/).withMessage('Invalid end time format'),
  body('isWorkingDay').optional().isBoolean().withMessage('isWorkingDay must be boolean'),
  body('notes').optional().isLength({ max: 200 }).withMessage('Notes cannot exceed 200 characters')
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

    const { date, timeSlots, isWorkingDay = true, notes } = req.body;

    // Check if availability already exists for this date
    const existingAvailability = await ProviderAvailability.findOne({
      provider: req.user.id,
      date: new Date(date)
    });

    if (existingAvailability) {
      // Update existing availability
      existingAvailability.timeSlots = timeSlots;
      existingAvailability.isWorkingDay = isWorkingDay;
      existingAvailability.notes = notes;
      await existingAvailability.save();
    } else {
      // Create new availability
      const availability = new ProviderAvailability({
        provider: req.user.id,
        date: new Date(date),
        timeSlots,
        isWorkingDay,
        notes
      });
      await availability.save();
    }

    res.json({
      success: true,
      message: 'Availability updated successfully'
    });
  } catch (error) {
    console.error('Set provider availability error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while setting availability'
    });
  }
});

// @route   GET /api/provider/earnings
// @desc    Get provider's earnings
// @access  Private (Provider only)
router.get('/earnings', protect, authorize('provider'), [
  query('startDate').optional().isISO8601(),
  query('endDate').optional().isISO8601(),
  query('period').optional().isIn(['daily', 'weekly', 'monthly'])
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

    const { startDate, endDate, period = 'monthly' } = req.query;
    const filter = { 
      provider: req.user.id, 
      status: 'completed' 
    };

    if (startDate && endDate) {
      filter.createdAt = {
        $gte: new Date(startDate),
        $lte: new Date(endDate)
      };
    } else {
      // Default to last 30 days
      const thirtyDaysAgo = new Date();
      thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);
      filter.createdAt = { $gte: thirtyDaysAgo };
    }

    const earnings = await Booking.aggregate([
      { $match: filter },
      {
        $group: {
          _id: {
            $dateToString: {
              format: period === 'daily' ? '%Y-%m-%d' : 
                     period === 'weekly' ? '%Y-%U' : '%Y-%m',
              date: '$createdAt'
            }
          },
          totalEarnings: { $sum: '$totalAmount' },
          completedBookings: { $sum: 1 }
        }
      },
      { $sort: { _id: 1 } }
    ]);

    const totalEarnings = earnings.reduce((sum, item) => sum + item.totalEarnings, 0);
    const totalBookings = earnings.reduce((sum, item) => sum + item.completedBookings, 0);

    res.json({
      success: true,
      data: {
        earnings,
        summary: {
          totalEarnings,
          totalBookings,
          averageEarning: totalBookings > 0 ? totalEarnings / totalBookings : 0
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

// @route   GET /api/provider/reviews
// @desc    Get provider's reviews
// @access  Private (Provider only)
router.get('/reviews', protect, authorize('provider'), [
  query('page').optional().isInt({ min: 1 }),
  query('limit').optional().isInt({ min: 1, max: 50 }),
  query('rating').optional().isInt({ min: 1, max: 5 })
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

    const { page = 1, limit = 10, rating } = req.query;
    const skip = (parseInt(page) - 1) * parseInt(limit);

    const filter = { provider: req.user.id };
    if (rating) {
      filter.rating = parseInt(rating);
    }

    const reviews = await Review.find(filter)
      .populate('customer', 'firstName lastName profileImage')
      .populate('service', 'name category')
      .populate('booking', 'bookingDate timeSlot')
      .sort({ createdAt: -1 })
      .skip(skip)
      .limit(parseInt(limit));

    const total = await Review.countDocuments(filter);

    // Get rating statistics
    const ratingStats = await Review.aggregate([
      { $match: { provider: req.user.id } },
      {
        $group: {
          _id: '$rating',
          count: { $sum: 1 }
        }
      },
      { $sort: { _id: 1 } }
    ]);

    res.json({
      success: true,
      data: {
        reviews,
        ratingStats,
        pagination: {
          currentPage: parseInt(page),
          totalPages: Math.ceil(total / parseInt(limit)),
          totalItems: total,
          itemsPerPage: parseInt(limit)
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

// @route   GET /api/provider/performance
// @desc    Get provider's performance metrics
// @access  Private (Provider only)
router.get('/performance', protect, authorize('provider'), async (req, res) => {
  try {
    const providerId = req.user.id;

    // Get performance metrics for the last 30 days
    const thirtyDaysAgo = new Date();
    thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);

    const performance = await Booking.aggregate([
      {
        $match: {
          provider: providerId,
          createdAt: { $gte: thirtyDaysAgo }
        }
      },
      {
        $group: {
          _id: null,
          totalBookings: { $sum: 1 },
          completedBookings: {
            $sum: { $cond: [{ $eq: ['$status', 'completed'] }, 1, 0] }
          },
          cancelledBookings: {
            $sum: { $cond: [{ $eq: ['$status', 'cancelled'] }, 1, 0] }
          },
          totalEarnings: {
            $sum: { $cond: [{ $eq: ['$status', 'completed'] }, '$totalAmount', 0] }
          },
          averageResponseTime: {
            $avg: {
              $cond: [
                { $ne: ['$providerResponse.responseTime', null] },
                {
                  $divide: [
                    { $subtract: ['$providerResponse.responseTime', '$createdAt'] },
                    1000 * 60 // Convert to minutes
                  ]
                },
                null
              ]
            }
          }
        }
      }
    ]);

    // Get rating statistics
    const ratingStats = await Review.aggregate([
      { $match: { provider: providerId } },
      {
        $group: {
          _id: null,
          averageRating: { $avg: '$rating' },
          totalReviews: { $sum: 1 },
          ratingDistribution: {
            $push: '$rating'
          }
        }
      }
    ]);

    const metrics = performance[0] || {
      totalBookings: 0,
      completedBookings: 0,
      cancelledBookings: 0,
      totalEarnings: 0,
      averageResponseTime: 0
    };

    const completionRate = metrics.totalBookings > 0 ? 
      (metrics.completedBookings / metrics.totalBookings) * 100 : 0;

    const cancellationRate = metrics.totalBookings > 0 ? 
      (metrics.cancelledBookings / metrics.totalBookings) * 100 : 0;

    res.json({
      success: true,
      data: {
        metrics: {
          ...metrics,
          completionRate: Math.round(completionRate * 100) / 100,
          cancellationRate: Math.round(cancellationRate * 100) / 100
        },
        rating: ratingStats[0] || {
          averageRating: 0,
          totalReviews: 0
        }
      }
    });
  } catch (error) {
    console.error('Get provider performance error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while fetching performance data'
    });
  }
});

module.exports = router;
