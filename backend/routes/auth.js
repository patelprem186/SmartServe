const express = require('express');
const { body, validationResult } = require('express-validator');
const User = require('../models/User');
const { protect, generateToken } = require('../middleware/auth');
const EmailService = require('../services/EmailService');
const FirebaseService = require('../services/FirebaseService');
const admin = require('firebase-admin');

const router = express.Router();

// @route   POST /api/auth/register
// @desc    Register a new user and send email verification
// @access  Public
router.post('/register', [
  body('firstName').trim().isLength({ min: 2, max: 50 }).withMessage('First name must be between 2 and 50 characters'),
  body('lastName').optional().trim().custom((value) => {
    if (value && value.length > 50) {
      throw new Error('Last name cannot exceed 50 characters');
    }
    return true;
  }),
  body('email').isEmail().normalizeEmail().withMessage('Please provide a valid email'),
  body('phone').matches(/^\+?[\d\s-()]+$/).withMessage('Please provide a valid phone number'),
  body('password').isLength({ min: 6 }).withMessage('Password must be at least 6 characters'),
  body('role').optional().isIn(['customer', 'provider']).withMessage('Role must be either customer or provider'),
  body('serviceCategory').optional().trim().custom((value, { req }) => {
    if (req.body.role === 'provider' && (!value || value.trim() === '')) {
      throw new Error('Service category is required for providers');
    }
    return true;
  })
], async (req, res) => {
  try {
    // Check for validation errors
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({
        success: false,
        message: 'Validation failed',
        errors: errors.array()
      });
    }

    const { firstName, lastName = '', email, phone, password, role = 'customer', serviceCategory = null } = req.body;

    // Check if user already exists
    const existingUser = await User.findOne({ email });
    if (existingUser) {
      return res.status(400).json({
        success: false,
        message: 'User already exists with this email'
      });
    }

    // Create user and save immediately without email verification
    const user = new User({
      firstName,
      lastName,
      email,
      phone,
      password,
      role,
      serviceCategory,
      isVerified: true,
      isActive: true
    });

    await user.save();

    // Generate auth token for immediate login
    const token = require('../middleware/auth').generateToken(user._id);

    res.status(201).json({
      success: true,
      message: 'Registration successful! You are now logged in.',
      data: {
        user: {
          id: user._id,
          firstName: user.firstName,
          lastName: user.lastName,
          email: user.email,
          phone: user.phone,
          role: user.role,
          serviceCategory: user.serviceCategory,
          isVerified: user.isVerified,
          profileImage: user.profileImage
        },
        token: token
      }
    });
  } catch (error) {
    console.error('Registration error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error during registration'
    });
  }
});

// @route   POST /api/auth/verify-firebase-token
// @desc    Verify Firebase ID token and return user data
// @access  Public
router.post('/verify-firebase-token', [
  body('idToken').notEmpty().withMessage('Firebase ID token is required')
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

    const { idToken } = req.body;

    // Verify Firebase ID token
    const result = await FirebaseService.verifyIdToken(idToken);
    
    if (result.success) {
      // Find or create user in our database
      let user = await User.findOne({ email: result.user.email });
      
      if (!user) {
        // Create new user in our database
        user = new User({
          firebaseUid: result.user.uid,
          firstName: result.user.displayName || 'User',
          lastName: '',
          email: result.user.email,
          phone: result.user.phoneNumber || '',
          role: 'customer', // Default role
          isVerified: result.user.emailVerified,
          isActive: true,
          profileImage: result.user.photoURL || ''
        });
        await user.save();
      }

      // Generate our own JWT token
      const token = generateToken(user._id);

      res.json({
        success: true,
        message: 'Token verified successfully',
        data: {
          user: {
            id: user._id,
            firstName: user.firstName,
            lastName: user.lastName,
            email: user.email,
            phone: user.phone,
            role: user.role,
            isVerified: user.isVerified,
            profileImage: user.profileImage
          },
          token
        }
      });
    } else {
      return res.status(401).json({
        success: false,
        message: result.error || 'Invalid token'
      });
    }
  } catch (error) {
    console.error('Token verification error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error during token verification'
    });
  }
});

// @route   GET /api/auth/me
// @desc    Get current user profile
// @access  Private
router.get('/me', protect, async (req, res) => {
  try {
    const user = await User.findById(req.user.id);
    
    res.json({
      success: true,
      data: { user }
    });
  } catch (error) {
    console.error('Get profile error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while fetching profile'
    });
  }
});

// @route   PUT /api/auth/profile
// @desc    Update user profile
// @access  Private
router.put('/profile', protect, [
  body('firstName').optional().trim().isLength({ min: 2, max: 50 }),
  body('lastName').optional().trim().isLength({ min: 2, max: 50 }),
  body('phone').optional().matches(/^\+?[\d\s-()]+$/),
  body('profileImage').optional().isURL()
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

    const allowedUpdates = ['firstName', 'lastName', 'phone', 'profileImage'];
    const updates = {};

    allowedUpdates.forEach(field => {
      if (req.body[field] !== undefined) {
        updates[field] = req.body[field];
      }
    });

    const user = await User.findByIdAndUpdate(
      req.user.id,
      updates,
      { new: true, runValidators: true }
    );

    res.json({
      success: true,
      message: 'Profile updated successfully',
      data: { user }
    });
  } catch (error) {
    console.error('Update profile error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while updating profile'
    });
  }
});

// @route   PUT /api/auth/change-password
// @desc    Change user password
// @access  Private
router.put('/change-password', protect, [
  body('currentPassword').notEmpty().withMessage('Current password is required'),
  body('newPassword').isLength({ min: 6 }).withMessage('New password must be at least 6 characters')
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

    const { currentPassword, newPassword } = req.body;

    // Get user with password
    const user = await User.findById(req.user.id).select('+password');

    // Check current password
    const isCurrentPasswordValid = await user.comparePassword(currentPassword);
    if (!isCurrentPasswordValid) {
      return res.status(400).json({
        success: false,
        message: 'Current password is incorrect'
      });
    }

    // Update password
    user.password = newPassword;
    await user.save();

    res.json({
      success: true,
      message: 'Password changed successfully'
    });
  } catch (error) {
    console.error('Change password error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while changing password'
    });
  }
});

// @route   GET /api/auth/test-firebase
// @desc    Test Firebase Admin SDK connection
// @access  Public
router.get('/test-firebase', async (req, res) => {
  try {
    const result = await FirebaseService.testConnection();
    res.json(result);
  } catch (error) {
    console.error('Firebase test error:', error);
    res.status(500).json({
      success: false,
      message: 'Firebase test failed',
      error: error.message
    });
  }
});

// @route   POST /api/auth/test-password-reset
// @desc    Test password reset email generation
// @access  Public
router.post('/test-password-reset', [
  body('email').isEmail().normalizeEmail().withMessage('Please provide a valid email')
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

    const { email } = req.body;
    
    // Test password reset email generation
    const result = await FirebaseService.sendPasswordResetEmail(email);
    
    res.json({
      success: true,
      message: 'Password reset test completed',
      result: result,
      // Include the generated link for testing
      ...(result.resetLink && { resetLink: result.resetLink })
    });
  } catch (error) {
    console.error('Password reset test error:', error);
    res.status(500).json({
      success: false,
      message: 'Password reset test failed',
      error: error.message
    });
  }
});

// @route   POST /api/auth/update-password
// @desc    Update user password via API
// @access  Public
router.post('/update-password', [
  body('email').isEmail().normalizeEmail().withMessage('Please provide a valid email'),
  body('newPassword').isLength({ min: 6 }).withMessage('Password must be at least 6 characters')
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

    const { email, newPassword } = req.body;
    
    // Find user in database
    let user = await User.findOne({ email });
    
    // If user doesn't exist in local database, check Firebase and create user
    if (!user) {
      try {
        // Get user from Firebase
        const firebaseUser = await admin.auth().getUserByEmail(email);
        
        // Create user in local database
        user = new User({
          firebaseUid: firebaseUser.uid,
          firstName: firebaseUser.displayName || 'User',
          lastName: '',
          email: firebaseUser.email,
          phone: firebaseUser.phoneNumber || '',
          role: 'customer',
          isVerified: firebaseUser.emailVerified,
          isActive: true,
          profileImage: firebaseUser.photoURL || ''
        });
        
        await user.save();
        console.log('Created user in local database:', user.email);
      } catch (firebaseError) {
        console.error('Firebase user lookup error:', firebaseError);
        return res.status(404).json({
          success: false,
          message: 'No user found with this email address'
        });
      }
    }
    
    // Update password using Firebase Admin SDK
    try {
      await admin.auth().updateUser(user.firebaseUid, {
        password: newPassword
      });
      
      // Also update password in our database
      user.password = newPassword;
      await user.save();
      
      res.json({
        success: true,
        message: 'Password updated successfully'
      });
    } catch (firebaseError) {
      console.error('Firebase password update error:', firebaseError);
      res.status(500).json({
        success: false,
        message: 'Failed to update password. Please try again.',
        error: firebaseError.message
      });
    }
  } catch (error) {
    console.error('Update password error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while updating password',
      error: error.message
    });
  }
});

// @route   POST /api/auth/forgot-password
// @desc    Send password reset email using Firebase Admin SDK
// @access  Public
router.post('/forgot-password', [
  body('email').isEmail().normalizeEmail().withMessage('Please provide a valid email')
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

    const { email } = req.body;

    // Use Firebase native password reset email template
    try {
      // Check if user exists in our database
      let user = await User.findOne({ email });
      
      // If user doesn't exist in local database, check Firebase and create user
      if (!user) {
        try {
          // Get user from Firebase
          const firebaseUser = await admin.auth().getUserByEmail(email);
          
          // Create user in local database
          user = new User({
            firebaseUid: firebaseUser.uid,
            firstName: firebaseUser.displayName || 'User',
            lastName: '',
            email: firebaseUser.email,
            phone: firebaseUser.phoneNumber || '',
            role: 'customer',
            isVerified: firebaseUser.emailVerified,
            isActive: true,
            profileImage: firebaseUser.photoURL || ''
          });
          
          await user.save();
          console.log('Created user in local database:', user.email);
        } catch (firebaseError) {
          console.error('Firebase user lookup error:', firebaseError);
          return res.status(404).json({
            success: false,
            message: 'No user found with this email address'
          });
        }
      }

      // Use Firebase Admin SDK to send password reset email with native template
      const result = await FirebaseService.sendPasswordResetEmail(email);
      
      if (result.success) {
        console.log('Firebase password reset email sent successfully');
      } else {
        console.error('Firebase password reset email failed:', result.error);
      }
    } catch (error) {
      console.error('Password reset email error:', error);
      result = { success: false, error: 'Failed to send reset email. Please try again.', code: 'EMAIL_FAILED' };
    }
    
    if (result.success) {
      res.json({
        success: true,
        message: result.message || 'Password reset email sent successfully. Please check your email for the reset link.',
        // Don't expose the reset link in production
        ...(process.env.NODE_ENV === 'development' && { resetLink: result.resetLink }),
        // Include reset token for custom email flow
        ...(result.resetToken && { resetToken: result.resetToken })
      });
    } else {
      // Handle Firebase errors
      if (result.code === 'USER_NOT_FOUND') {
        return res.status(404).json({
          success: false,
          message: 'No user found with this email address'
        });
      }
      
      if (result.code === 'INVALID_EMAIL') {
        return res.status(400).json({
          success: false,
          message: 'Invalid email address'
        });
      }
      
      return res.status(500).json({
        success: false,
        message: result.error || 'Failed to send password reset email'
      });
    }

  } catch (error) {
    console.error('Forgot password error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while processing forgot password request'
    });
  }
});

// @route   POST /api/auth/reset-password
// @desc    Reset password using reset code verification
// @access  Public
router.post('/reset-password', [
  body('email').isEmail().normalizeEmail().withMessage('Please provide a valid email'),
  body('resetCode').notEmpty().withMessage('Reset code is required'),
  body('newPassword').isLength({ min: 6 }).withMessage('New password must be at least 6 characters')
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

    const { email, resetCode, newPassword } = req.body;

    // Find user by email
    const user = await User.findOne({ email });
    if (!user) {
      return res.status(404).json({
        success: false,
        message: 'No user found with this email address'
      });
    }

    // Check if reset token exists and is not expired
    if (!user.resetPasswordToken || !user.resetPasswordExpires) {
      return res.status(400).json({
        success: false,
        message: 'No active password reset request found'
      });
    }

    if (Date.now() > user.resetPasswordExpires) {
      return res.status(400).json({
        success: false,
        message: 'Reset code has expired. Please request a new one.'
      });
    }

    // Verify reset code
    if (!resetCode || resetCode.length !== 6 || !/^\d+$/.test(resetCode)) {
      return res.status(400).json({
        success: false,
        message: 'Invalid reset code format'
      });
    }

    // Check if the provided reset code matches the stored one
    if (user.resetPasswordCode !== resetCode) {
      return res.status(400).json({
        success: false,
        message: 'Invalid reset code'
      });
    }

    // Update password using Firebase Admin SDK if user has firebaseUid
    if (user.firebaseUid) {
      try {
        await admin.auth().updateUser(user.firebaseUid, {
          password: newPassword
        });
        console.log('Password updated in Firebase for user:', user.email);
      } catch (firebaseError) {
        console.error('Firebase password update error:', firebaseError);
        return res.status(500).json({
          success: false,
          message: 'Failed to update password in Firebase. Please try again.',
          error: firebaseError.message
        });
      }
    }

    // Update password in our database
    user.password = newPassword;
    user.resetPasswordToken = undefined;
    user.resetPasswordCode = undefined;
    user.resetPasswordExpires = undefined;
    await user.save();

    res.json({
      success: true,
      message: 'Password reset successfully'
    });

  } catch (error) {
    console.error('Reset password error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while resetting password'
    });
  }
});

// @route   POST /api/auth/verify-reset-code
// @desc    Verify password reset code
// @access  Public
router.post('/verify-reset-code', [
  body('oobCode').notEmpty().withMessage('Reset code is required')
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

    const { oobCode } = req.body;

    const result = await FirebaseService.verifyPasswordResetCode(oobCode);
    
    if (result.success) {
      res.json({
        success: true,
        message: 'Reset code is valid',
        user: result.user
      });
    } else {
      if (result.code === 'INVALID_CODE' || result.code === 'EXPIRED_CODE') {
        return res.status(400).json({
          success: false,
          message: 'Invalid or expired reset code'
        });
      }
      
      return res.status(500).json({
        success: false,
        message: result.error || 'Failed to verify reset code'
      });
    }

  } catch (error) {
    console.error('Verify reset code error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while verifying reset code'
    });
  }
});

// @route   POST /api/auth/verify-email
// @desc    Verify email with verification code
// @access  Public
router.post('/verify-email', [
  body('email').isEmail().normalizeEmail().withMessage('Please provide a valid email'),
  body('verificationCode').isLength({ min: 6, max: 6 }).withMessage('Verification code must be 6 digits')
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

    const { email, verificationCode } = req.body;

    // Find user by email
    const user = await User.findOne({ email });
    if (!user) {
      return res.status(404).json({
        success: false,
        message: 'User not found with this email'
      });
    }

    // Check if user is already verified
    if (user.isVerified) {
      return res.status(400).json({
        success: false,
        message: 'Email is already verified'
      });
    }

    // Check if verification code exists and is not expired
    if (!user.emailVerificationCode || !user.emailVerificationExpires) {
      return res.status(400).json({
        success: false,
        message: 'No verification code found. Please register again.'
      });
    }

    if (new Date() > user.emailVerificationExpires) {
      return res.status(400).json({
        success: false,
        message: 'Verification code has expired. Please register again.'
      });
    }

    // Verify the code
    if (user.emailVerificationCode !== verificationCode) {
      return res.status(400).json({
        success: false,
        message: 'Invalid verification code'
      });
    }

    // Mark user as verified and clear verification data
    user.isVerified = true;
    user.emailVerificationCode = undefined;
    user.emailVerificationExpires = undefined;
    await user.save();

    // Generate token for immediate login
    const token = generateToken(user._id);

    // Send welcome email
    await EmailService.sendWelcomeEmail(email, user.firstName);

    res.json({
      success: true,
      message: 'Email verification successful! Welcome to SmartServe.',
      data: {
        user: {
          id: user._id,
          firstName: user.firstName,
          lastName: user.lastName,
          email: user.email,
          phone: user.phone,
          role: user.role,
          isVerified: user.isVerified
        },
        token
      }
    });
  } catch (error) {
    console.error('Email verification error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error during email verification'
    });
  }
});

// @route   POST /api/auth/resend-verification
// @desc    Resend email verification code
// @access  Public
router.post('/resend-verification', [
  body('email').isEmail().normalizeEmail().withMessage('Please provide a valid email')
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

    const { email } = req.body;

    // Find user by email
    const user = await User.findOne({ email });
    if (!user) {
      return res.status(404).json({
        success: false,
        message: 'User not found with this email'
      });
    }

    // Check if user is already verified
    if (user.isVerified) {
      return res.status(400).json({
        success: false,
        message: 'Email is already verified'
      });
    }

    // Generate new verification code
    const verificationCode = Math.floor(100000 + Math.random() * 900000).toString();
    
    // Update user with new verification code
    user.emailVerificationCode = verificationCode;
    user.emailVerificationExpires = new Date(Date.now() + 10 * 60 * 1000); // 10 minutes
    await user.save();

    // Send verification email
    const emailResult = await EmailService.sendVerificationEmail(email, verificationCode, user.firstName);
    
    if (!emailResult.success) {
      return res.status(500).json({
        success: false,
        message: 'Failed to send verification email. Please try again.'
      });
    }

    res.json({
      success: true,
      message: 'Verification code sent to your email'
    });
  } catch (error) {
    console.error('Resend verification error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error while resending verification code'
    });
  }
});

module.exports = router;
