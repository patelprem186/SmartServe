# EasyBook Backend API

A comprehensive backend API for the EasyBook service booking platform, built with Node.js, Express, and MongoDB.

## Features

- **Authentication & Authorization**: JWT-based authentication with role-based access control
- **User Management**: Customer and provider registration, profile management
- **Service Management**: CRUD operations for services with categories and pricing
- **Booking System**: Complete booking lifecycle with status tracking
- **Payment Processing**: Integration with Stripe and PayPal
- **Notifications**: Firebase Cloud Messaging for real-time notifications
- **Maps Integration**: Google Maps API for directions and geocoding
- **Analytics**: Comprehensive analytics and reporting
- **Admin Panel**: Administrative functions for platform management

## API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/verify-email` - Email verification
- `POST /api/auth/resend-verification` - Resend verification email
- `GET /api/auth/profile` - Get user profile
- `PUT /api/auth/profile` - Update user profile

### Services
- `GET /api/services` - Get all services (with filtering)
- `GET /api/services/:id` - Get single service
- `POST /api/services` - Create service (Provider only)
- `PUT /api/services/:id` - Update service (Provider only)
- `DELETE /api/services/:id` - Delete service (Provider only)

### Bookings
- `GET /api/bookings` - Get user's bookings
- `GET /api/bookings/:id` - Get single booking
- `POST /api/bookings` - Create booking (Customer only)
- `PUT /api/bookings/:id` - Update booking status
- `DELETE /api/bookings/:id` - Delete booking (Admin only)

### Provider
- `GET /api/provider/dashboard` - Provider dashboard data
- `GET /api/provider/bookings` - Provider's bookings
- `GET /api/provider/availability` - Provider's availability
- `POST /api/provider/availability` - Set availability
- `GET /api/provider/earnings` - Provider's earnings
- `GET /api/provider/reviews` - Provider's reviews
- `GET /api/provider/performance` - Performance metrics

### Notifications
- `POST /api/notifications/send` - Send notification
- `POST /api/notifications/booking` - Send booking notification
- `POST /api/notifications/bulk` - Send bulk notifications
- `GET /api/notifications/templates` - Get notification templates

### Admin
- `GET /api/admin/dashboard` - Admin dashboard
- `GET /api/admin/users` - Get all users
- `PUT /api/admin/users/:id/status` - Update user status
- `GET /api/admin/bookings` - Get all bookings
- `GET /api/admin/analytics` - Get analytics data

### Maps
- `POST /api/maps/directions` - Get directions
- `POST /api/maps/distance` - Get distance
- `POST /api/maps/geocode` - Geocode address

### Payments
- `POST /api/payments/process` - Process payment
- `POST /api/payments/refund` - Process refund
- `GET /api/payments/status/:paymentId` - Get payment status
- `GET /api/payments/history` - Get payment history

### Analytics
- `GET /api/analytics/overview` - Analytics overview
- `GET /api/analytics/performance` - Performance analytics
- `GET /api/analytics/reviews` - Review analytics

## Installation

1. Clone the repository
2. Install dependencies: `npm install`
3. Set up environment variables in `config.env`
4. Start the server: `npm run dev`

## Environment Variables

```env
# Database
MONGODB_URI=mongodb://localhost:27017/easybook

# JWT
JWT_SECRET=your_jwt_secret_key_here
JWT_EXPIRE=30d

# Email Configuration
EMAIL_USER=your_email@gmail.com
EMAIL_PASSWORD=your_app_password_here
EMAIL_FROM=your_email@gmail.com

# Firebase Configuration
FIREBASE_DATABASE_URL=https://your-project.firebaseio.com

# Google Maps API
GOOGLE_MAPS_API_KEY=your_google_maps_api_key_here

# Payment Configuration
STRIPE_SECRET_KEY=your_stripe_secret_key_here
STRIPE_PUBLISHABLE_KEY=your_stripe_publishable_key_here
PAYPAL_CLIENT_ID=your_paypal_client_id_here
PAYPAL_CLIENT_SECRET=your_paypal_client_secret_here
PAYPAL_MODE=sandbox

# Server Configuration
PORT=4000
NODE_ENV=development
CLIENT_URL=http://localhost:3000
```

## Dependencies

- **express**: Web framework
- **mongoose**: MongoDB object modeling
- **bcryptjs**: Password hashing
- **jsonwebtoken**: JWT authentication
- **cors**: Cross-origin resource sharing
- **dotenv**: Environment variable management
- **express-validator**: Input validation
- **nodemailer**: Email sending
- **firebase-admin**: Firebase integration
- **stripe**: Payment processing
- **paypal-rest-sdk**: PayPal integration
- **axios**: HTTP client
- **helmet**: Security middleware
- **express-rate-limit**: Rate limiting
- **compression**: Response compression
- **morgan**: HTTP request logger

## Security Features

- JWT-based authentication
- Password hashing with bcrypt
- Rate limiting
- CORS protection
- Helmet security headers
- Input validation and sanitization
- Role-based access control

## Error Handling

The API includes comprehensive error handling with appropriate HTTP status codes and error messages.

## Rate Limiting

API endpoints are protected with rate limiting to prevent abuse and ensure fair usage.

## Monitoring

Health check endpoint available at `/health` for monitoring server status.

## License

MIT License