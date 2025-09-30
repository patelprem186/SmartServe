# SmartServe - EasyBook2

A comprehensive service booking platform with Android app, web admin panel, and Node.js backend.

## 🏗️ Project Structure

```
SmartServe/
├── app/                          # Android Application
├── backend/                      # Node.js Backend API
├── admin-panel/                  # React Admin Panel
├── SETUP_GUIDE.md               # Complete setup instructions
├── ADMIN_PANEL_SETUP.md         # Admin panel specific setup
└── AI_CHATBOT_SETUP.md          # AI chatbot setup guide
```

## 🚀 Quick Start

### Prerequisites
- Node.js (v14 or higher)
- Android Studio
- MongoDB
- Firebase account

### 1. Backend Setup
```bash
cd backend
npm install
# Copy config.env.example to config.env and fill in your secrets
cp config.env.example config.env
npm start
```

### 2. Admin Panel Setup
```bash
cd admin-panel
npm install
npm start
```

### 3. Android App Setup
1. Open `app/` folder in Android Studio
2. Add your `google-services.json` file to `app/` directory
3. Build and run the app

## 🔐 Required Secrets & Environment Variables

### Backend Configuration (`backend/config.env`)
Create this file with the following variables:

```env
# Database
MONGODB_URI=mongodb://localhost:27017/smartserve
# or MongoDB Atlas: mongodb+srv://username:password@cluster.mongodb.net/smartserve

# JWT
JWT_SECRET=your_jwt_secret_key_here
JWT_EXPIRES_IN=7d

# Email Service (SendGrid)
SENDGRID_API_KEY=your_sendgrid_api_key_here
FROM_EMAIL=noreply@smartserve.com

# Firebase (for notifications)
FIREBASE_PROJECT_ID=your_firebase_project_id
FIREBASE_PRIVATE_KEY_ID=your_private_key_id
FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\nYour_Private_Key_Here\n-----END PRIVATE KEY-----\n"
FIREBASE_CLIENT_EMAIL=firebase-adminsdk-xxxxx@your-project.iam.gserviceaccount.com
FIREBASE_CLIENT_ID=your_client_id
FIREBASE_AUTH_URI=https://accounts.google.com/o/oauth2/auth
FIREBASE_TOKEN_URI=https://oauth2.googleapis.com/token

# Payment Gateway (Stripe)
STRIPE_SECRET_KEY=sk_test_your_stripe_secret_key
STRIPE_PUBLISHABLE_KEY=pk_test_your_stripe_publishable_key

# Maps API
GOOGLE_MAPS_API_KEY=your_google_maps_api_key

# Server
PORT=4000
NODE_ENV=development
```

### Android App Configuration
1. **Firebase Setup**: Add your `google-services.json` file to `app/` directory
2. **API Configuration**: Update `app/src/main/java/com/easy/easybook/config/ApiConfig.java` with your backend URL

### Admin Panel Configuration
The admin panel uses the backend API, so ensure the backend is running on `http://localhost:4000`

## 📱 Features

### Android App
- User authentication (email/password, phone verification)
- Service browsing and search
- Booking management
- Real-time chat with AI assistant
- Payment integration
- Push notifications

### Admin Panel
- User management
- Service management
- Booking oversight
- Analytics dashboard
- Provider management

### Backend API
- RESTful API with Express.js
- MongoDB database
- JWT authentication
- Email notifications
- Firebase push notifications
- Payment processing
- File upload handling

## 🛠️ Development

### Backend Development
```bash
cd backend
npm run dev  # For development with nodemon
```

### Admin Panel Development
```bash
cd admin-panel
npm start  # Starts development server
```

### Android Development
1. Open project in Android Studio
2. Sync Gradle files
3. Run on emulator or device

## 📊 Database Schema

The application uses MongoDB with the following main collections:
- `users` - User accounts (customers, providers, admins)
- `services` - Service listings
- `bookings` - Booking records
- `reviews` - User reviews and ratings
- `categories` - Service categories

## 🔧 API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/forgot-password` - Password reset
- `POST /api/auth/verify-email` - Email verification

### Services
- `GET /api/services` - Get all services
- `GET /api/services/categories` - Get service categories
- `POST /api/services` - Create service (provider only)

### Bookings
- `GET /api/bookings` - Get user bookings
- `POST /api/bookings` - Create booking
- `PUT /api/bookings/:id` - Update booking

## 🚀 Deployment

### Backend Deployment
1. Set up MongoDB Atlas or local MongoDB
2. Configure environment variables
3. Deploy to Heroku, AWS, or your preferred platform

### Android App Deployment
1. Generate signed APK
2. Upload to Google Play Store

### Admin Panel Deployment
1. Build the React app: `npm run build`
2. Deploy to Netlify, Vercel, or your preferred platform

## 📝 License

This project is licensed under the MIT License.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📞 Support

For support and questions, please contact the development team or create an issue in the repository.

---

**Note**: Make sure to keep all sensitive information (API keys, database credentials, etc.) in environment variables and never commit them to version control.
