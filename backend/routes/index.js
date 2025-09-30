const express = require('express');
const auth = require('./auth');
const services = require('./services');
const bookings = require('./bookings');
const provider = require('./provider');
const notifications = require('./notifications');
const admin = require('./admin');
const maps = require('./maps');
const payments = require('./payments');
const analytics = require('./analytics');

const router = express.Router();

// Mount routes
router.use('/auth', auth);
router.use('/services', services);
router.use('/bookings', bookings);
router.use('/provider', provider);
router.use('/notifications', notifications);
router.use('/admin', admin);
router.use('/maps', maps);
router.use('/payments', payments);
router.use('/analytics', analytics);

module.exports = router;
