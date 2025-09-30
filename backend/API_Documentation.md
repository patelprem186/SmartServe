# EasyBook API Documentation

## 🚀 Quick Start

**Base URL:** `http://localhost:4000/api`

## 📋 Test Credentials

| Role | Email | Password |
|------|-------|----------|
| Customer | `john.doe@email.com` | `password123` |
| Provider | `robert.wilson@email.com` | `password123` |
| Admin | `admin@easybook.com` | `admin123` |

## 🔧 Postman Setup

1. **Import Collection:** Import `EasyBook_API_Collection.postman_collection.json`
2. **Import Environment:** Import `EasyBook_Environment.postman_environment.json`
3. **Set Environment:** Select "EasyBook Development Environment"

## 📚 API Endpoints

### 1. Health Check
```http
GET /api/health
```
**Response:**
```json
{
  "status": "OK",
  "message": "EasyBook API is running",
  "timestamp": "2024-01-04T10:30:00.000Z"
}
```

### 2. Authentication

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "firstName": "Test",
  "lastName": "User",
  "email": "test@example.com",
  "phone": "+1234567890",
  "password": "password123",
  "role": "customer"
}
```

#### Login User
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john.doe@email.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "user": {
      "id": "60f7b3b3b3b3b3b3b3b3b3b3",
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@email.com",
      "role": "customer"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### 3. Services

#### Get Service Categories
```http
GET /api/services/categories
```

#### Get Featured Services
```http
GET /api/services/featured
```

#### Search Services
```http
GET /api/services/search?query=cleaning&minPrice=50&maxPrice=200&sortBy=rating
```

#### Get Service Details
```http
GET /api/services/{serviceId}
```

### 4. Bookings

#### Create Booking
```http
POST /api/bookings
Authorization: Bearer {token}
Content-Type: application/json

{
  "serviceId": "60f7b3b3b3b3b3b3b3b3b3b3",
  "bookingDate": "2024-01-25",
  "startTime": "10:00",
  "location": {
    "type": "at_customer",
    "address": {
      "street": "123 Test St",
      "city": "New York",
      "state": "NY",
      "zipCode": "10001"
    }
  }
}
```

#### Get My Bookings
```http
GET /api/bookings/my-bookings
Authorization: Bearer {token}
```

#### Update Booking Status
```http
PUT /api/bookings/{bookingId}/status
Authorization: Bearer {token}
Content-Type: application/json

{
  "status": "confirmed",
  "notes": "Booking confirmed"
}
```

### 5. Customer Endpoints

#### Get Customer Dashboard
```http
GET /api/customers/dashboard
Authorization: Bearer {customer_token}
```

#### Get Booking History
```http
GET /api/customers/booking-history
Authorization: Bearer {customer_token}
```

#### Get Favorites
```http
GET /api/customers/favorites
Authorization: Bearer {customer_token}
```

### 6. Provider Endpoints

#### Get Provider Dashboard
```http
GET /api/providers/dashboard
Authorization: Bearer {provider_token}
```

#### Get Provider Services
```http
GET /api/providers/my-services
Authorization: Bearer {provider_token}
```

#### Get Earnings Report
```http
GET /api/providers/earnings?startDate=2024-01-01&endDate=2024-12-31
Authorization: Bearer {provider_token}
```

## 🧪 Testing Workflow

### Step 1: Health Check
```bash
curl -X GET http://localhost:4000/api/health
```

### Step 2: Get Service Categories
```bash
curl -X GET http://localhost:4000/api/services/categories
```

### Step 3: Login as Customer
```bash
curl -X POST http://localhost:4000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "john.doe@email.com", "password": "password123"}'
```

### Step 4: Get Customer Dashboard
```bash
curl -X GET http://localhost:4000/api/customers/dashboard \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Step 5: Login as Provider
```bash
curl -X POST http://localhost:4000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "robert.wilson@email.com", "password": "password123"}'
```

### Step 6: Get Provider Dashboard
```bash
curl -X GET http://localhost:4000/api/providers/dashboard \
  -H "Authorization: Bearer PROVIDER_TOKEN_HERE"
```

## 📊 Sample Data Available

### Service Categories (8 categories)
- Home Cleaning
- Plumbing
- Electrical
- HVAC
- Landscaping
- Painting
- Carpentry
- Appliance Repair

### Sample Services (6 services)
- Deep House Cleaning ($150, 3 hours)
- Kitchen Deep Clean ($80, 1.5 hours)
- Emergency Plumbing Repair ($120, 1 hour)
- Drain Cleaning Service ($95, 1.25 hours)
- Electrical Outlet Installation ($85, 45 minutes)
- AC Repair & Maintenance ($110, 1.5 hours)

### Sample Users
- **3 Customer Users** with addresses and preferences
- **4 Provider Users** with business profiles and working hours
- **1 Admin User** for system management

### Sample Bookings (3 bookings)
- Completed bookings with ratings and reviews
- Various statuses (pending, confirmed, completed)

## 🔐 Authentication

All protected endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer {your_jwt_token}
```

## 📱 Mobile App Integration

### Base URL for Android App
```java
public static final String BASE_URL = "http://localhost:4000/api/";
```

### Example API Call in Android
```java
// Login request
JSONObject loginData = new JSONObject();
loginData.put("email", "john.doe@email.com");
loginData.put("password", "password123");

// Make HTTP request to /api/auth/login
```

## 🚨 Error Handling

All API responses follow this format:

**Success Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

**Error Response:**
```json
{
  "success": false,
  "message": "Error description",
  "errors": [ ... ]
}
```

## 📸 Screenshots to Capture

1. **Health Check Response** - API status
2. **Service Categories** - 8 categories with colors
3. **Featured Services** - Sample services with ratings
4. **Login Response** - JWT token generation
5. **Customer Dashboard** - Booking statistics
6. **Provider Dashboard** - Earnings and performance
7. **Booking Creation** - Successful booking flow
8. **Search Results** - Filtered services

## 🔄 Status Codes

- `200` - Success
- `201` - Created
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `500` - Internal Server Error

## 📝 Notes

- All timestamps are in UTC
- Prices are in USD (whole numbers)
- Time slots are in 30-minute intervals
- JWT tokens expire in 7 days
- All endpoints support pagination with `page` and `limit` parameters
