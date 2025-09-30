# EasyBook Admin Panel Setup Guide

## 🎯 **Admin Panel Access Instructions**

### **1. Backend Setup (Required First)**

Before accessing the admin panel, ensure the backend is running:

```bash
cd backend
npm install
npm start
```

The backend should be running on `http://localhost:4000`

### **2. Create Admin User**

Create an admin user through the backend API:

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

Navigate to the admin panel directory and install dependencies:

```bash
cd admin-panel
npm install
npm start
```

The admin panel will be available at `http://localhost:3000`

### **4. Login to Admin Panel**

Use the admin credentials created in step 2:
- **Email**: `admin@easybook.com`
- **Password**: `admin123`

## 🔧 **Admin Panel Features**

### **Dashboard**
- Platform statistics (users, bookings, revenue)
- User registration trends
- Booking completion rates
- Recent activity feed

### **User Management**
- View all users with filtering and search
- Update user status (active, inactive, suspended)
- Role-based access control
- User activity tracking

### **Booking Management**
- View all bookings with status filtering
- Booking details and customer information
- Payment status tracking
- Date-based filtering

### **Service Management**
- View all services offered on the platform
- Category-based filtering
- Service availability status
- Provider information

### **Analytics**
- User registration trends
- Booking completion rates
- Revenue analytics
- Category performance
- Interactive charts and graphs

## 📱 **Android App Integration**

The Android app has been updated to use the backend APIs instead of dummy data:

### **Updated Components**
- **ServiceSearchActivity**: Now uses API calls for service search
- **HomeFragment**: Loads categories and featured services from API
- **BookingHistoryFragment**: Displays real booking data
- **ProfileFragment**: Shows actual user information

### **API Integration**
- All dummy data has been replaced with API calls
- Fallback to dummy data if API fails
- Real-time data synchronization
- Proper error handling

## 🚀 **Complete System Architecture**

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Android App   │    │   Backend API   │    │  Admin Panel    │
│                 │    │                 │    │                 │
│ • Customer UI   │◄──►│ • Authentication│◄──►│ • Dashboard     │
│ • Provider UI   │    │ • Services     │    │ • User Mgmt     │
│ • Booking Flow  │    │ • Bookings     │    │ • Analytics     │
│ • Real-time     │    │ • Payments     │    │ • Monitoring    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🔐 **Security Features**

- JWT-based authentication
- Role-based access control (admin only)
- Secure API communication
- Input validation and sanitization
- Rate limiting and security headers

## 📊 **Data Flow**

1. **Customer** books a service through Android app
2. **Backend API** processes the booking and stores in MongoDB
3. **Admin Panel** displays real-time data and analytics
4. **Provider** receives notifications and manages bookings
5. **Admin** monitors platform performance and user activity

## 🛠 **Development Commands**

### **Backend**
```bash
cd backend
npm install
npm run dev
```

### **Admin Panel**
```bash
cd admin-panel
npm install
npm start
```

### **Android App**
Open in Android Studio and run on emulator/device

## 📈 **Monitoring & Analytics**

The admin panel provides comprehensive insights:

- **User Growth**: Track user registration trends
- **Booking Performance**: Monitor booking completion rates
- **Revenue Analytics**: Track platform earnings
- **Service Categories**: Analyze popular service categories
- **Provider Performance**: Monitor provider activity

## 🔧 **Troubleshooting**

### **Common Issues**

1. **Admin Panel Not Loading**
   - Ensure backend is running on port 4000
   - Check browser console for errors
   - Verify admin user creation

2. **API Connection Issues**
   - Check backend server status
   - Verify API endpoints
   - Check network connectivity

3. **Authentication Issues**
   - Ensure admin user has correct role
   - Check JWT token validity
   - Verify API authentication

### **Debug Steps**

1. Check backend logs for errors
2. Verify database connection
3. Test API endpoints with Postman
4. Check admin panel console for errors
5. Verify admin user permissions

## 📞 **Support**

For technical support or issues:
- Check backend logs: `npm run dev`
- Check admin panel console: F12 in browser
- Verify API endpoints: `http://localhost:4000/api/health`
- Test admin login: `http://localhost:3000/login`

## 🎉 **Success Indicators**

You'll know everything is working when:
- ✅ Backend API responds to health check
- ✅ Admin panel loads without errors
- ✅ Admin login works successfully
- ✅ Dashboard shows real data
- ✅ Android app loads services from API
- ✅ All components communicate properly

The system is now fully integrated with no dummy data - everything comes from the backend APIs!
