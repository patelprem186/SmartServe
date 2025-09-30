const mongoose = require('mongoose');

const availabilitySchema = new mongoose.Schema({
  provider: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: [true, 'Provider is required']
  },
  date: {
    type: Date,
    required: [true, 'Date is required']
  },
  timeSlots: [{
    start: {
      type: String,
      required: true,
      match: /^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$/
    },
    end: {
      type: String,
      required: true,
      match: /^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$/
    },
    isAvailable: {
      type: Boolean,
      default: true
    },
    bookingId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Booking'
    }
  }],
  isWorkingDay: {
    type: Boolean,
    default: true
  },
  notes: {
    type: String,
    maxlength: [200, 'Notes cannot exceed 200 characters']
  }
}, {
  timestamps: true
});

// Indexes
availabilitySchema.index({ provider: 1, date: 1 }, { unique: true });
availabilitySchema.index({ date: 1, 'timeSlots.isAvailable': 1 });

module.exports = mongoose.model('ProviderAvailability', availabilitySchema);
