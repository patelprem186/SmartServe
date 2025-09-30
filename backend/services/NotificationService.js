const admin = require('firebase-admin');

class NotificationService {
  constructor() {
    this.initializeFirebase();
  }

  initializeFirebase() {
    if (!admin.apps.length) {
      try {
        const serviceAccount = require('../config/firebase-service-account.json');
        admin.initializeApp({
          credential: admin.credential.cert(serviceAccount),
          databaseURL: process.env.FIREBASE_DATABASE_URL
        });
      } catch (error) {
        console.warn('Firebase service account not found. Notifications will be disabled.');
        console.warn('To enable notifications, add firebase-service-account.json to the config folder.');
      }
    }
  }

  async sendNotification({ userId, title, body, type = 'general', data = {} }) {
    try {
      // Check if Firebase is initialized
      if (!admin.apps.length) {
        return { success: false, error: 'Firebase not initialized. Notifications disabled.' };
      }

      const user = await User.findById(userId);
      if (!user || !user.fcmToken) {
        return { success: false, error: 'User not found or FCM token not available' };
      }

      const message = {
        token: user.fcmToken,
        notification: {
          title,
          body
        },
        data: {
          type,
          ...data
        }
      };

      const response = await admin.messaging().send(message);
      return { success: true, messageId: response };
    } catch (error) {
      console.error('Send notification error:', error);
      return { success: false, error: error.message };
    }
  }

  async sendBulkNotifications(notifications) {
    const results = [];
    for (const notification of notifications) {
      const result = await this.sendNotification(notification);
      results.push(result);
    }
    return results;
  }
}

module.exports = new NotificationService();
