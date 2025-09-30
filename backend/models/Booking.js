const mongoose = require('mongoose');

const bookingSchema = new mongoose.Schema({
  bookingId: {
    type: String,
    required: true,
    unique: true
  },
  service: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Service',
    required: [true, 'Service is required']
  },
  customer: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: [true, 'Customer is required']
  },
  provider: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: [true, 'Provider is required']
  },
  bookingDate: {
    type: Date,
    required: [true, 'Booking date is required']
  },
  timeSlot: {
    type: String,
    required: [true, 'Time slot is required']
  },
  address: {
    street: { type: String, required: true },
    city: { type: String, required: true },
    state: { type: String, required: true },
    zipCode: { type: String, required: true },
    coordinates: {
      lat: Number,
      lng: Number
    }
  },
  status: {
    type: String,
    enum: ['pending', 'accepted', 'declined', 'in_progress', 'completed', 'cancelled'],
    default: 'pending'
  },
  totalAmount: {
    type: Number,
    required: [true, 'Total amount is required'],
    min: [0, 'Amount cannot be negative']
  },
  notes: {
    type: String,
    maxlength: [500, 'Notes cannot exceed 500 characters']
  },
  specialInstructions: {
    type: String,
    maxlength: [500, 'Special instructions cannot exceed 500 characters']
  },
  customerPhone: {
    type: String,
    required: [true, 'Customer phone is required']
  },
  customerEmail: {
    type: String,
    required: [true, 'Customer email is required']
  },
  // Provider response
  providerResponse: {
    status: {
      type: String,
      enum: ['pending', 'accepted', 'declined'],
      default: 'pending'
    },
    responseTime: Date,
    declineReason: String,
    estimatedArrival: Date,
    notes: String
  },
  // Completion details
  completionDetails: {
    completedAt: Date,
    completionNotes: String,
    beforePhotos: [String],
    afterPhotos: [String],
    customerSignature: String,
    providerSignature: String
  },
  // Payment details
  payment: {
    status: {
      type: String,
      enum: ['pending', 'paid', 'refunded', 'failed'],
      default: 'pending'
    },
    method: String,
    transactionId: String,
    paidAt: Date,
    refundedAt: Date
  },
  // Rating and review
  rating: {
    type: Number,
    min: 1,
    max: 5
  },
  review: {
    type: String,
    maxlength: [500, 'Review cannot exceed 500 characters']
  },
  // Cancellation
  cancellation: {
    cancelledBy: {
      type: String,
      enum: ['customer', 'provider', 'admin']
    },
    cancelledAt: Date,
    reason: String,
    refundAmount: Number
  }
}, {
  timestamps: true
});

// Indexes for better performance
bookingSchema.index({ bookingId: 1 });
bookingSchema.index({ customer: 1, status: 1 });
bookingSchema.index({ provider: 1, status: 1 });
bookingSchema.index({ bookingDate: 1, status: 1 });
bookingSchema.index({ status: 1, createdAt: -1 });

// Generate booking ID before saving
bookingSchema.pre('save', async function(next) {
  if (!this.bookingId) {
    const count = await this.constructor.countDocuments();
    this.bookingId = `BK-${Date.now()}-${String(count + 1).padStart(4, '0')}`;
  }
  next();
});

module.exports = mongoose.model('Booking', bookingSchema);