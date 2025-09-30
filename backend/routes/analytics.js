const express = require('express');
const { query, validationResult } = require('express-validator');
const User = require('../models/User');
const Booking = require('../models/Booking');
const Service = require('../models/Service');
const Review = require('../models/Review');
const { protect, authorize } = require('../middleware/auth');

const router = express.Router();

// @route   GET /api/analytics/overview
// @desc    Get analytics overview
// @access  Private
router.get('/overview', protect, [
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

    let dateFilter = {};
    if (startDate && endDate) {
      dateFilter = {
        $gte: new Date(startDate),
        $lte: new Date(endDate)
      };
    } else {
      // Default to last 30 days
      const thirtyDaysAgo = new Date();
      thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);
      dateFilter = { $gte: thirtyDaysAgo };
    }

    // User analytics
    const userAnalytics = await User.aggregate([
      { $match: { createdAt: dateFilter } },
      {
        $group: {
          _id: {
            $dateToString: {
              format: period === 'daily' ? '%Y-%m-%d' : 
                     period === 'weekly' ? '%Y-%U' : '%Y-%m',
              date: '$createdAt'
            }
          },
          totalUsers: { $sum: 1 },
          providers: { $sum: { $cond: [{ $eq: ['$role', 'provider'] }, 1, 0] } },
          customers: { $sum: { $cond: [{ $eq: ['$role', 'customer'] }, 1, 0] } }
        }
      },
      { $sort: { _id: 1 } }
    ]);

    // Booking analytics
    const bookingAnalytics = await Booking.aggregate([
      { $match: { createdAt: dateFilter } },
      {
        $group: {
          _id: {
            $dateToString: {
              format: period === 'daily' ? '%Y-%m-%d' : 
                     period === 'weekly' ? '%Y-%U' : '%Y-%m',
              date: '$createdAt'
            }
          },
          totalBookings: { $sum: 1 },
          completedBookings: { $sum: { $cond: [{ $eq: ['$status', 'completed'] }, 1, 0] } },
          cancelledBookings: { $sum: { $cond: [{ $eq: ['$status', 'cancelled'] }, 1, 0] } },
          totalRevenue: { $sum: { $cond: [{ $eq: ['$status', 'completed'] }, '$totalAmount', 0] } }
        }
      },
      { $sort: { _id: 1 } }
    ]);

    // Service category analytics
    const categoryAnalytics = await Service.aggregate([
      {
        $group: {
          _id: '$category',
          totalServices: { $sum: 1 },
          averagePrice: { $avg: '$price' },
          averageRating: { $avg: '$rating' }
        }
      },
      { $sort: { totalServices: -1 } }
    ]);

    // Revenue analytics
    const revenueAnalytics = await Booking.aggregate([
      { $match: { status: 'completed', createdAt: dateFilter } },
      {
        $group: {
          _id: {
            $dateToString: {
              format: period === 'daily' ? '%Y-%m-%d' : 
                     period === 'weekly' ? '%Y-%U' : '%Y-%m',
              date: '$createdAt'
            }
          },
          totalRevenue: { $sum: '$totalAmount' },
          averageOrderValue: { $avg: '$totalAmount' },
          completedBookings: { $sum: 1 }
        }
      },
      { $sort: { _id: 1 } }
    ]);

    res.json({
      success: true,
      data: {
        userAnalytics,
        bookingAnalytics,
        categoryAnalytics,
        revenueAnalytics
      }
    });
  } catch (error) {
    console.error('Get analytics overview error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while fetching analytics overview'
    });
  }
});

// @route   GET /api/analytics/performance
// @desc    Get performance analytics
// @access  Private
router.get('/performance', protect, [
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

    let dateFilter = {};
    if (startDate && endDate) {
      dateFilter = {
        $gte: new Date(startDate),
        $lte: new Date(endDate)
      };
    } else {
      // Default to last 30 days
      const thirtyDaysAgo = new Date();
      thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);
      dateFilter = { $gte: thirtyDaysAgo };
    }

    // Provider performance
    const providerPerformance = await Booking.aggregate([
      { $match: { createdAt: dateFilter } },
      {
        $group: {
          _id: '$provider',
          totalBookings: { $sum: 1 },
          completedBookings: { $sum: { $cond: [{ $eq: ['$status', 'completed'] }, 1, 0] } },
          cancelledBookings: { $sum: { $cond: [{ $eq: ['$status', 'cancelled'] }, 1, 0] } },
          totalRevenue: { $sum: { $cond: [{ $eq: ['$status', 'completed'] }, '$totalAmount', 0] } },
          averageRating: { $avg: '$rating' }
        }
      },
      {
        $lookup: {
          from: 'users',
          localField: '_id',
          foreignField: '_id',
          as: 'provider'
        }
      },
      { $unwind: '$provider' },
      {
        $project: {
          providerName: { $concat: ['$provider.firstName', ' ', '$provider.lastName'] },
          totalBookings: 1,
          completedBookings: 1,
          cancelledBookings: 1,
          totalRevenue: 1,
          averageRating: 1,
          completionRate: {
            $multiply: [
              { $divide: ['$completedBookings', '$totalBookings'] },
              100
            ]
          }
        }
      },
      { $sort: { totalRevenue: -1 } }
    ]);

    // Service performance
    const servicePerformance = await Booking.aggregate([
      { $match: { createdAt: dateFilter } },
      {
        $group: {
          _id: '$service',
          totalBookings: { $sum: 1 },
          completedBookings: { $sum: { $cond: [{ $eq: ['$status', 'completed'] }, 1, 0] } },
          totalRevenue: { $sum: { $cond: [{ $eq: ['$status', 'completed'] }, '$totalAmount', 0] } },
          averageRating: { $avg: '$rating' }
        }
      },
      {
        $lookup: {
          from: 'services',
          localField: '_id',
          foreignField: '_id',
          as: 'service'
        }
      },
      { $unwind: '$service' },
      {
        $project: {
          serviceName: '$service.name',
          category: '$service.category',
          totalBookings: 1,
          completedBookings: 1,
          totalRevenue: 1,
          averageRating: 1,
          completionRate: {
            $multiply: [
              { $divide: ['$completedBookings', '$totalBookings'] },
              100
            ]
          }
        }
      },
      { $sort: { totalRevenue: -1 } }
    ]);

    res.json({
      success: true,
      data: {
        providerPerformance,
        servicePerformance
      }
    });
  } catch (error) {
    console.error('Get performance analytics error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while fetching performance analytics'
    });
  }
});

// @route   GET /api/analytics/reviews
// @desc    Get review analytics
// @access  Private
router.get('/reviews', protect, [
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

    let dateFilter = {};
    if (startDate && endDate) {
      dateFilter = {
        $gte: new Date(startDate),
        $lte: new Date(endDate)
      };
    } else {
      // Default to last 30 days
      const thirtyDaysAgo = new Date();
      thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);
      dateFilter = { $gte: thirtyDaysAgo };
    }

    // Review statistics
    const reviewStats = await Review.aggregate([
      { $match: { createdAt: dateFilter } },
      {
        $group: {
          _id: null,
          totalReviews: { $sum: 1 },
          averageRating: { $avg: '$rating' },
          ratingDistribution: {
            $push: '$rating'
          }
        }
      }
    ]);

    // Rating distribution
    const ratingDistribution = await Review.aggregate([
      { $match: { createdAt: dateFilter } },
      {
        $group: {
          _id: '$rating',
          count: { $sum: 1 }
        }
      },
      { $sort: { _id: 1 } }
    ]);

    // Top rated services
    const topRatedServices = await Review.aggregate([
      { $match: { createdAt: dateFilter } },
      {
        $group: {
          _id: '$service',
          averageRating: { $avg: '$rating' },
          totalReviews: { $sum: 1 }
        }
      },
      {
        $lookup: {
          from: 'services',
          localField: '_id',
          foreignField: '_id',
          as: 'service'
        }
      },
      { $unwind: '$service' },
      {
        $project: {
          serviceName: '$service.name',
          category: '$service.category',
          averageRating: 1,
          totalReviews: 1
        }
      },
      { $sort: { averageRating: -1, totalReviews: -1 } },
      { $limit: 10 }
    ]);

    res.json({
      success: true,
      data: {
        reviewStats: reviewStats[0] || {
          totalReviews: 0,
          averageRating: 0,
          ratingDistribution: []
        },
        ratingDistribution,
        topRatedServices
      }
    });
  } catch (error) {
    console.error('Get review analytics error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while fetching review analytics'
    });
  }
});

module.exports = router;
