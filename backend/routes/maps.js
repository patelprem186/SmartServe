const express = require('express');
const { body, validationResult } = require('express-validator');
const { protect } = require('../middleware/auth');
const { getDirections, getDistance, getGeocode } = require('../services/MapsService');

const router = express.Router();

// @route   POST /api/maps/directions
// @desc    Get directions between two points
// @access  Private
router.post('/directions', protect, [
  body('origin').notEmpty().withMessage('Origin is required'),
  body('destination').notEmpty().withMessage('Destination is required'),
  body('mode').optional().isIn(['driving', 'walking', 'bicycling', 'transit']),
  body('avoid').optional().isArray()
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

    const { origin, destination, mode = 'driving', avoid = [] } = req.body;

    const directions = await getDirections({
      origin,
      destination,
      mode,
      avoid
    });

    res.json({
      success: true,
      data: directions
    });
  } catch (error) {
    console.error('Get directions error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while getting directions'
    });
  }
});

// @route   POST /api/maps/distance
// @desc    Get distance between two points
// @access  Private
router.post('/distance', protect, [
  body('origin').notEmpty().withMessage('Origin is required'),
  body('destination').notEmpty().withMessage('Destination is required'),
  body('mode').optional().isIn(['driving', 'walking', 'bicycling', 'transit'])
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

    const { origin, destination, mode = 'driving' } = req.body;

    const distance = await getDistance({
      origin,
      destination,
      mode
    });

    res.json({
      success: true,
      data: distance
    });
  } catch (error) {
    console.error('Get distance error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while getting distance'
    });
  }
});

// @route   POST /api/maps/geocode
// @desc    Get coordinates from address
// @access  Private
router.post('/geocode', protect, [
  body('address').notEmpty().withMessage('Address is required')
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

    const { address } = req.body;

    const geocode = await getGeocode(address);

    res.json({
      success: true,
      data: geocode
    });
  } catch (error) {
    console.error('Get geocode error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while geocoding address'
    });
  }
});

module.exports = router;
