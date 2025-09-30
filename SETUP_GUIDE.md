# EasyBook Complete Setup Guide

## 🚀 **Quick Start**

### **1. Backend Setup**

```bash
cd backend
npm install
npm start
```

The backend will run on `http://localhost:4000`

### **2. Create Admin User**

```bash
curl -X POST http://localhost:4000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@easybook.com",
    "firstName": "Admin",
    "lastName": "User",
    "password": "admin123",
    "role": "admin"
  }'
```

### **3. Admin Panel Setup**

```bash
cd admin-panel
npm install
npm start
```

The admin panel will run on `http://localhost:3000`

### **4. Android App**

Open the project in Android Studio and run on emulator/device.

## 🔧 **Configuration**

### **Backend Environment Variables**

Create `backend/config.env` with:

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

# Firebase Configuration (Optional)
FIREBASE_DATABASE_URL=https://your-project.firebaseio.com

# Google Maps API (Optional)
GOOGLE_MAPS_API_KEY=your_google_maps_api_key_here

# Payment Configuration (Optional)
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

## 📱 **Access Points**

### **Admin Panel**
- **URL**: `http://localhost:3000`
- **Login**: `admin@easybook.com` / `admin123`

### **Backend API**
- **Health Check**: `http://localhost:4000/health`
- **API Base**: `http://localhost:4000/api`

### **Android App**
- Run in Android Studio
- Uses `http://10.0.2.2:4000` for API calls (Android emulator)

## 🎯 **Features**

### **Admin Panel**
- ✅ Dashboard with platform statistics
- ✅ User management (view, filter, update status)
- ✅ Booking management (monitor all bookings)
- ✅ Service management (view all services)
- ✅ Analytics (charts and performance metrics)

### **Android App**
- ✅ Real API integration (no dummy data)
- ✅ User authentication and registration
- ✅ Service search and booking
- ✅ Booking history and management
- ✅ Profile management

### **Backend API**
- ✅ Complete REST API
- ✅ JWT authentication
- ✅ Role-based access control
- ✅ MongoDB integration
- ✅ Email notifications
- ✅ Payment processing
- ✅ Analytics and reporting

## 🔐 **Security**

- JWT-based authentication
- Role-based access control
- Input validation and sanitization
- Rate limiting
- Secure API communication

## 📊 **System Architecture**

```
Android App ←→ Backend API ←→ Admin Panel
     ↓              ↓              ↓
  Real Data    MongoDB DB    Web Dashboard
```

## 🛠 **Troubleshooting**

### **Backend Issues**
1. Check MongoDB is running
2. Verify environment variables
3. Check port 4000 is available
4. Review server logs

### **Admin Panel Issues**
1. Ensure backend is running
2. Check browser console for errors
3. Verify admin user creation
4. Check API connectivity

### **Android App Issues**
1. Check API base URL
2. Verify network permissions
3. Check authentication tokens
4. Review API responses

## 🎉 **Success Indicators**

You'll know everything is working when:
- ✅ Backend responds to health check
- ✅ Admin panel loads without errors
- ✅ Admin login works successfully
- ✅ Dashboard shows real data
- ✅ Android app loads services from API
- ✅ All components communicate properly

## 📞 **Support**

For issues:
1. Check server logs
2. Verify API endpoints
3. Test with Postman
4. Check browser console
5. Review Android logs

The system is now fully integrated with **no dummy data** - everything comes from the backend APIs!
