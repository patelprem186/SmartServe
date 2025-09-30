const express = require('express');
const { body, validationResult, query } = require('express-validator');
const User = require('../models/User');
const Booking = require('../models/Booking');
const Service = require('../models/Service');
const Review = require('../models/Review');
const { protect, authorize } = require('../middleware/auth');

const router = express.Router();

// @route   GET /api/admin/dashboard
// @desc    Get admin dashboard data
// @access  Private (Admin only)
router.get('/dashboard', protect, authorize('admin'), async (req, res) => {
  try {
    // Get overall statistics
    const totalUsers = await User.countDocuments();
    const totalProviders = await User.countDocuments({ role: 'provider' });
    const totalCustomers = await User.countDocuments({ role: 'customer' });
    const totalServices = await Service.countDocuments();
    const totalBookings = await Booking.countDocuments();
    const totalReviews = await Review.countDocuments();

    // Get recent activity
    const recentBookings = await Booking.find()
      .populate('customer', 'firstName lastName')
      .populate('provider', 'firstName lastName')
      .populate('service', 'name')
      .sort({ createdAt: -1 })
      .limit(10);

    const recentUsers = await User.find()
      .select('firstName lastName email role createdAt')
      .sort({ createdAt: -1 })
      .limit(10);

    // Get earnings for last 30 days
    const thirtyDaysAgo = new Date();
    thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);

    const earnings = await Booking.aggregate([
      {
        $match: {
          status: 'completed',
          createdAt: { $gte: thirtyDaysAgo }
        }
      },
      {
        $group: {
          _id: null,
          totalEarnings: { $sum: '$totalAmount' },
          completedBookings: { $sum: 1 }
        }
      }
    ]);

    res.json({
      success: true,
      data: {
        statistics: {
          totalUsers,
          totalProviders,
          totalCustomers,
          totalServices,
          totalBookings,
          totalReviews,
          totalEarnings: earnings[0]?.totalEarnings || 0,
          completedBookings: earnings[0]?.completedBookings || 0
        },
        recentActivity: {
          bookings: recentBookings,
          users: recentUsers
        }
      }
    });
  } catch (error) {
    console.error('Get admin dashboard error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while fetching dashboard data'
    });
  }
});

// @route   GET /api/admin/users
// @desc    Get all users with filtering
// @access  Private (Admin only)
router.get('/users', protect, authorize('admin'), [
  query('role').optional().isIn(['customer', 'provider', 'admin']),
  query('status').optional().isIn(['active', 'inactive', 'suspended']),
  query('page').optional().isInt({ min: 1 }),
  query('limit').optional().isInt({ min: 1, max: 100 })
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

    const { role, status, page = 1, limit = 20 } = req.query;
    const skip = (parseInt(page) - 1) * parseInt(limit);

    const filter = {};
    if (role) filter.role = role;
    if (status) filter.status = status;

    const users = await User.find(filter)
      .select('-password')
      .sort({ createdAt: -1 })
      .skip(skip)
      .limit(parseInt(limit));

    const total = await User.countDocuments(filter);

    res.json({
      success: true,
      data: {
        users,
        pagination: {
          currentPage: parseInt(page),
          totalPages: Math.ceil(total / parseInt(limit)),
          totalItems: total,
          itemsPerPage: parseInt(limit)
        }
      }
    });
  } catch (error) {
    console.error('Get admin users error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while fetching users'
    });
  }
});

// @route   PUT /api/admin/users/:id/status
// @desc    Update user status
// @access  Private (Admin only)
router.put('/users/:id/status', protect, authorize('admin'), [
  body('status').isIn(['active', 'inactive', 'suspended']).withMessage('Invalid status'),
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

    const { id } = req.params;
    const { status, reason } = req.body;

    const user = await User.findById(id);
    if (!user) {
      return res.status(404).json({
        success: false,
        message: 'User not found'
      });
    }

    user.status = status;
    if (reason) user.statusReason = reason;
    await user.save();

    res.json({
      success: true,
      message: 'User status updated successfully'
    });
  } catch (error) {
    console.error('Update user status error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while updating user status'
    });
  }
});

// @route   GET /api/admin/bookings
// @desc    Get all bookings with filtering
// @access  Private (Admin only)
router.get('/bookings', protect, authorize('admin'), [
  query('status').optional().isIn(['pending', 'accepted', 'declined', 'in_progress', 'completed', 'cancelled']),
  query('startDate').optional().isISO8601(),
  query('endDate').optional().isISO8601(),
  query('page').optional().isInt({ min: 1 }),
  query('limit').optional().isInt({ min: 1, max: 100 })
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

    const { status, startDate, endDate, page = 1, limit = 20 } = req.query;
    const skip = (parseInt(page) - 1) * parseInt(limit);

    const filter = {};
    if (status) filter.status = status;
    if (startDate && endDate) {
      filter.createdAt = {
        $gte: new Date(startDate),
        $lte: new Date(endDate)
      };
    }

    const bookings = await Booking.find(filter)
      .populate('customer', 'firstName lastName email')
      .populate('provider', 'firstName lastName email')
      .populate('service', 'name category')
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
    console.error('Get admin bookings error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while fetching bookings'
    });
  }
});

// @route   GET /api/admin/analytics
// @desc    Get analytics data
// @access  Private (Admin only)
router.get('/analytics', protect, authorize('admin'), [
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

    // User registration analytics
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

    res.json({
      success: true,
      data: {
        userAnalytics,
        bookingAnalytics,
        categoryAnalytics
      }
    });
  } catch (error) {
    console.error('Get admin analytics error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while fetching analytics'
    });
  }
});

module.exports = router;
