const nodemailer = require('nodemailer');

class EmailService {
  constructor() {
    // Create transporter for email sending
    // For development, we'll use Gmail SMTP
    // In production, use a service like SendGrid, AWS SES, etc.
    this.transporter = nodemailer.createTransport({
      service: 'gmail',
      auth: {
        user: process.env.EMAIL_USER || 'your-email@gmail.com',
        pass: process.env.EMAIL_PASSWORD || 'your-app-password'
      }
    });
  }

  async sendVerificationEmail(email, verificationCode, firstName) {
    const mailOptions = {
      from: process.env.EMAIL_USER || 'noreply@smartserve.com',
      to: email,
      subject: 'SmartServe - Email Verification Code',
      html: this.getVerificationEmailTemplate(firstName, verificationCode)
    };

    try {
      const result = await this.transporter.sendMail(mailOptions);
      console.log('Verification email sent:', result.messageId);
      return { success: true, messageId: result.messageId };
    } catch (error) {
      console.error('Error sending verification email:', error);
      
      // Handle specific Gmail authentication errors
      if (error.code === 'EAUTH') {
        console.error('Gmail authentication failed. Please use an App Password instead of regular password.');
        console.error('To fix this:');
        console.error('1. Enable 2-factor authentication on your Google account');
        console.error('2. Generate an App Password for this application');
        console.error('3. Use the App Password in EMAIL_PASSWORD config');
        return { 
          success: false, 
          error: 'Email authentication failed. Please configure Gmail App Password.' 
        };
      }
      
      return { success: false, error: error.message };
    }
  }

  getVerificationEmailTemplate(firstName, verificationCode) {
    return `
      <!DOCTYPE html>
      <html>
      <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Email Verification - SmartServe</title>
        <style>
          body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            line-height: 1.6;
            color: #333;
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
            background-color: #f4f4f4;
          }
          .container {
            background-color: #ffffff;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 0 20px rgba(0,0,0,0.1);
          }
          .header {
            text-align: center;
            margin-bottom: 30px;
          }
          .logo {
            font-size: 28px;
            font-weight: bold;
            color: #1976D2;
            margin-bottom: 10px;
          }
          .verification-code {
            background-color: #f8f9fa;
            border: 2px dashed #1976D2;
            border-radius: 8px;
            padding: 20px;
            text-align: center;
            margin: 20px 0;
          }
          .code {
            font-size: 32px;
            font-weight: bold;
            color: #1976D2;
            letter-spacing: 5px;
            font-family: 'Courier New', monospace;
          }
          .footer {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #eee;
            text-align: center;
            color: #666;
            font-size: 14px;
          }
          .warning {
            background-color: #fff3cd;
            border: 1px solid #ffeaa7;
            border-radius: 5px;
            padding: 15px;
            margin: 20px 0;
            color: #856404;
          }
        </style>
      </head>
      <body>
        <div class="container">
          <div class="header">
            <div class="logo">SmartServe</div>
            <h2>Email Verification</h2>
          </div>
          
          <p>Hello ${firstName || 'User'},</p>
          
          <p>Thank you for registering with SmartServe! To complete your registration, please use the verification code below:</p>
          
          <div class="verification-code">
            <div class="code">${verificationCode}</div>
          </div>
          
          <p>Enter this code in the app to verify your email address and complete your registration.</p>
          
          <div class="warning">
            <strong>Important:</strong> This code will expire in 10 minutes. If you didn't request this verification, please ignore this email.
          </div>
          
          <p>If you have any questions, feel free to contact our support team.</p>
          
          <p>Best regards,<br>The SmartServe Team</p>
          
          <div class="footer">
            <p>This is an automated message. Please do not reply to this email.</p>
            <p>&copy; 2024 SmartServe. All rights reserved.</p>
          </div>
        </div>
      </body>
      </html>
    `;
  }

  async sendPasswordResetEmail(email, firstName, resetCode) {
    const mailOptions = {
      from: process.env.EMAIL_USER || 'noreply@smartserve.com',
      to: email,
      subject: 'SmartServe - Password Reset Code',
      html: this.getPasswordResetEmailTemplate(firstName, resetCode)
    };

    try {
      const result = await this.transporter.sendMail(mailOptions);
      console.log('Password reset email sent:', result.messageId);
      return { success: true, messageId: result.messageId };
    } catch (error) {
      console.error('Error sending password reset email:', error);
      return { success: false, error: error.message };
    }
  }

  async sendWelcomeEmail(email, firstName) {
    const mailOptions = {
      from: process.env.EMAIL_USER || 'noreply@smartserve.com',
      to: email,
      subject: 'Welcome to SmartServe!',
      html: this.getWelcomeEmailTemplate(firstName)
    };

    try {
      const result = await this.transporter.sendMail(mailOptions);
      console.log('Welcome email sent:', result.messageId);
      return { success: true, messageId: result.messageId };
    } catch (error) {
      console.error('Error sending welcome email:', error);
      return { success: false, error: error.message };
    }
  }

  getPasswordResetEmailTemplate(firstName, resetCode) {
    return `
      <!DOCTYPE html>
      <html>
      <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Password Reset - SmartServe</title>
        <style>
          body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            line-height: 1.6;
            color: #333;
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
            background-color: #f4f4f4;
          }
          .container {
            background-color: #ffffff;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 0 20px rgba(0,0,0,0.1);
          }
          .header {
            text-align: center;
            margin-bottom: 30px;
          }
          .logo {
            font-size: 28px;
            font-weight: bold;
            color: #1976D2;
            margin-bottom: 10px;
          }
          .reset-code {
            background-color: #f8f9fa;
            border: 2px dashed #1976D2;
            border-radius: 8px;
            padding: 20px;
            text-align: center;
            margin: 20px 0;
          }
          .code {
            font-size: 32px;
            font-weight: bold;
            color: #1976D2;
            letter-spacing: 5px;
            font-family: 'Courier New', monospace;
          }
          .footer {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #eee;
            text-align: center;
            color: #666;
            font-size: 14px;
          }
          .warning {
            background-color: #fff3cd;
            border: 1px solid #ffeaa7;
            border-radius: 5px;
            padding: 15px;
            margin: 20px 0;
            color: #856404;
          }
        </style>
      </head>
      <body>
        <div class="container">
          <div class="header">
            <div class="logo">SmartServe</div>
            <h2>Password Reset</h2>
          </div>
          
          <p>Hello ${firstName || 'User'},</p>
          
          <p>You requested to reset your password for your SmartServe account. Use the code below to reset your password:</p>
          
          <div class="reset-code">
            <div class="code">${resetCode}</div>
          </div>
          
          <p>Enter this code in the app to reset your password.</p>
          
          <div class="warning">
            <strong>Important:</strong> This code will expire in 1 hour. If you didn't request this password reset, please ignore this email and your password will remain unchanged.
          </div>
          
          <p>If you have any questions, feel free to contact our support team.</p>
          
          <p>Best regards,<br>The SmartServe Team</p>
          
          <div class="footer">
            <p>This is an automated message. Please do not reply to this email.</p>
            <p>&copy; 2024 SmartServe. All rights reserved.</p>
          </div>
        </div>
      </body>
      </html>
    `;
  }

  getWelcomeEmailTemplate(firstName) {
    return `
      <!DOCTYPE html>
      <html>
      <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Welcome to SmartServe</title>
        <style>
          body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            line-height: 1.6;
            color: #333;
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
            background-color: #f4f4f4;
          }
          .container {
            background-color: #ffffff;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 0 20px rgba(0,0,0,0.1);
          }
          .header {
            text-align: center;
            margin-bottom: 30px;
          }
          .logo {
            font-size: 28px;
            font-weight: bold;
            color: #1976D2;
            margin-bottom: 10px;
          }
          .cta-button {
            display: inline-block;
            background-color: #1976D2;
            color: white;
            padding: 12px 30px;
            text-decoration: none;
            border-radius: 5px;
            margin: 20px 0;
          }
        </style>
      </head>
      <body>
        <div class="container">
          <div class="header">
            <div class="logo">SmartServe</div>
            <h2>Welcome to SmartServe!</h2>
          </div>
          
          <p>Hello ${firstName || 'User'},</p>
          
          <p>Congratulations! Your account has been successfully verified and you're now part of the SmartServe community.</p>
          
          <p>With SmartServe, you can:</p>
          <ul>
            <li>Book services from verified providers</li>
            <li>Manage your bookings easily</li>
            <li>Rate and review services</li>
            <li>Get real-time notifications</li>
          </ul>
          
          <p>Ready to get started? Download our mobile app and start booking services today!</p>
          
          <p>If you have any questions or need assistance, our support team is here to help.</p>
          
          <p>Best regards,<br>The SmartServe Team</p>
        </div>
      </body>
      </html>
    `;
  }
}

module.exports = new EmailService();
