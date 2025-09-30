const admin = require('firebase-admin');

class FirebaseService {
    constructor() {
    // Firebase Admin SDK should already be initialized in server.js
    console.log('FirebaseService initialized');
  }

  /**
   * Test Firebase Admin SDK connection
   * @returns {Promise<Object>} Test result
   */
  async testConnection() {
    try {
      // Try to get project info
      const projectId = admin.app().options.projectId;
      console.log('Firebase project ID:', projectId);
      
      return {
        success: true,
        projectId: projectId,
        message: 'Firebase Admin SDK connection successful'
      };
    } catch (error) {
      console.error('Firebase connection test failed:', error);
      return {
        success: false,
        error: error.message,
        message: 'Firebase Admin SDK connection failed'
      };
    }
  }

  /**
   * Verify Firebase ID token
   * @param {string} idToken - Firebase ID token
   * @returns {Promise<Object>} Result object with user info
   */
  async verifyIdToken(idToken) {
    try {
      const decodedToken = await admin.auth().verifyIdToken(idToken);
      
      return {
        success: true,
        user: {
          uid: decodedToken.uid,
          email: decodedToken.email,
          emailVerified: decodedToken.email_verified,
          displayName: decodedToken.name,
          phoneNumber: decodedToken.phone_number,
          photoURL: decodedToken.picture
        }
      };
    } catch (error) {
      console.error('Error verifying ID token:', error);
      
      if (error.code === 'auth/invalid-token') {
        return {
          success: false,
          error: 'Invalid token',
          code: 'INVALID_TOKEN'
        };
      }
      
      if (error.code === 'auth/token-expired') {
        return {
          success: false,
          error: 'Token expired',
          code: 'TOKEN_EXPIRED'
        };
      }
      
      return {
        success: false,
        error: error.message || 'Failed to verify token',
        code: 'UNKNOWN_ERROR'
      };
    }
  }

  /**
   * Send password reset email using Firebase Admin SDK
   * @param {string} email - User's email address
   * @param {string} continueUrl - Optional continue URL for password reset
   * @returns {Promise<Object>} Result object with success status and message
   */
  async sendPasswordResetEmail(email, continueUrl = null) {
    try {
      // First, check if user exists in Firebase Auth
      let userRecord;
      try {
        userRecord = await admin.auth().getUserByEmail(email);
      } catch (userError) {
        if (userError.code === 'auth/user-not-found') {
          return {
            success: false,
            error: 'No user found with this email address',
            code: 'USER_NOT_FOUND'
          };
        }
        throw userError;
      }

      // Generate password reset link with Android deep linking
      const actionCodeSettings = {
        url: continueUrl || `https://smartserve-57c5e.firebaseapp.com/reset-password`,
        handleCodeInApp: true,
        iOS: {
          bundleId: 'com.easy.easybook'
        },
        android: {
          packageName: 'com.easy.easybook',
          installApp: true,
          minimumVersion: '1'
        }
      };

      const link = await admin.auth().generatePasswordResetLink(email, actionCodeSettings);
      
      console.log('Password reset link generated for:', email);
      
      return {
        success: true,
        resetLink: link,
        message: 'Password reset link generated successfully'
      };
    } catch (error) {
      console.error('Error generating password reset link:', error);
      
      // Handle specific Firebase errors
      if (error.code === 'auth/user-not-found') {
        return {
          success: false,
          error: 'No user found with this email address',
          code: 'USER_NOT_FOUND'
        };
      }
      
      if (error.code === 'auth/invalid-email') {
        return {
          success: false,
          error: 'Invalid email address',
          code: 'INVALID_EMAIL'
        };
      }

      if (error.code === 'auth/internal-error') {
        // Fallback: Try without action code settings
        try {
          const link = await admin.auth().generatePasswordResetLink(email);
          return {
            success: true,
            resetLink: link,
            message: 'Password reset link generated successfully'
          };
        } catch (fallbackError) {
          console.error('Fallback password reset link generation failed:', fallbackError);
          return {
            success: false,
            error: 'Failed to generate password reset link. Please try again.',
            code: 'GENERATION_FAILED'
          };
        }
      }
      
      return {
        success: false,
        error: error.message || 'Failed to generate password reset link',
        code: 'UNKNOWN_ERROR'
      };
    }
  }

  /**
   * Verify password reset code and get user info
   * @param {string} oobCode - The out-of-band code from the reset link
   * @returns {Promise<Object>} Result object with user info or error
   */
  async verifyPasswordResetCode(oobCode) {
    try {
      const userInfo = await admin.auth().verifyPasswordResetCode(oobCode);
      
      return {
        success: true,
        user: {
          uid: userInfo.uid,
          email: userInfo.email
        }
      };
    } catch (error) {
      console.error('Error verifying password reset code:', error);
      
      if (error.code === 'auth/invalid-action-code') {
        return {
          success: false,
          error: 'Invalid or expired reset code',
          code: 'INVALID_CODE'
        };
      }
      
      if (error.code === 'auth/expired-action-code') {
        return {
          success: false,
          error: 'Reset code has expired',
          code: 'EXPIRED_CODE'
        };
      }
      
      return {
        success: false,
        error: error.message || 'Failed to verify reset code',
        code: 'UNKNOWN_ERROR'
      };
    }
  }

  /**
   * Confirm password reset using the oob code
   * @param {string} oobCode - The out-of-band code from the reset link
   * @param {string} newPassword - The new password
   * @returns {Promise<Object>} Result object with success status
   */
  async confirmPasswordReset(oobCode, newPassword) {
    try {
      await admin.auth().confirmPasswordReset(oobCode, newPassword);
      
      return {
        success: true,
        message: 'Password reset successfully'
      };
    } catch (error) {
      console.error('Error confirming password reset:', error);
      
      if (error.code === 'auth/invalid-action-code') {
        return {
          success: false,
          error: 'Invalid or expired reset code',
          code: 'INVALID_CODE'
        };
      }
      
      if (error.code === 'auth/expired-action-code') {
        return {
          success: false,
          error: 'Reset code has expired',
          code: 'EXPIRED_CODE'
        };
      }
      
      if (error.code === 'auth/weak-password') {
        return {
          success: false,
          error: 'Password is too weak',
          code: 'WEAK_PASSWORD'
        };
      }
      
      return {
        success: false,
        error: error.message || 'Failed to reset password',
        code: 'UNKNOWN_ERROR'
      };
    }
  }

  /**
   * Get user by email
   * @param {string} email - User's email address
   * @returns {Promise<Object>} Result object with user info or error
   */
  async getUserByEmail(email) {
    try {
      const userRecord = await admin.auth().getUserByEmail(email);
      
      return {
        success: true,
        user: {
          uid: userRecord.uid,
          email: userRecord.email,
          displayName: userRecord.displayName,
          phoneNumber: userRecord.phoneNumber,
          emailVerified: userRecord.emailVerified,
          disabled: userRecord.disabled
        }
      };
    } catch (error) {
      console.error('Error getting user by email:', error);
      
      if (error.code === 'auth/user-not-found') {
        return {
          success: false,
          error: 'No user found with this email address',
          code: 'USER_NOT_FOUND'
        };
      }
      
      return {
        success: false,
        error: error.message || 'Failed to get user',
        code: 'UNKNOWN_ERROR'
      };
    }
  }

  /**
   * Update user password using Firebase Admin SDK
   * @param {string} uid - User's Firebase UID
   * @param {string} newPassword - New password
   * @returns {Promise<Object>} Result object with success status
   */
  async updateUserPassword(uid, newPassword) {
    try {
      await admin.auth().updateUser(uid, {
        password: newPassword
      });
      
      return {
        success: true,
        message: 'Password updated successfully'
      };
    } catch (error) {
      console.error('Error updating user password:', error);
      
      if (error.code === 'auth/user-not-found') {
        return {
          success: false,
          error: 'User not found',
          code: 'USER_NOT_FOUND'
        };
      }
      
      if (error.code === 'auth/weak-password') {
        return {
          success: false,
          error: 'Password is too weak',
          code: 'WEAK_PASSWORD'
        };
      }
      
      return {
        success: false,
        error: error.message || 'Failed to update password',
        code: 'UNKNOWN_ERROR'
      };
    }
    }
}

module.exports = new FirebaseService();