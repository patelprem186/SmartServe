# EasyBook Admin Panel

A comprehensive admin panel for the EasyBook service booking platform, built with React and Tailwind CSS.

## Features

- **Dashboard**: Overview of platform statistics and recent activity
- **User Management**: View and manage all platform users
- **Booking Management**: Monitor and track all bookings
- **Service Management**: View and manage all services
- **Analytics**: Comprehensive analytics and reporting
- **Real-time Data**: Live data from the backend API

## Getting Started

### Prerequisites

- Node.js (v14 or higher)
- npm or yarn
- EasyBook Backend API running on localhost:4000

### Installation

1. Navigate to the admin panel directory:
   ```bash
   cd admin-panel
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm start
   ```

4. Open [http://localhost:3000](http://localhost:3000) to view the admin panel

### Admin Access

To access the admin panel, you need an admin account. Create an admin user through the backend API:

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

Then use these credentials to log in to the admin panel.

## Features Overview

### Dashboard
- Platform statistics (users, bookings, revenue)
- User registration trends
- Booking completion rates
- Recent activity feed

### User Management
- View all users with filtering and search
- Update user status (active, inactive, suspended)
- Role-based access control
- User activity tracking

### Booking Management
- View all bookings with status filtering
- Booking details and customer information
- Payment status tracking
- Date-based filtering

### Service Management
- View all services offered on the platform
- Category-based filtering
- Service availability status
- Provider information

### Analytics
- User registration trends
- Booking completion rates
- Revenue analytics
- Category performance
- Interactive charts and graphs

## API Integration

The admin panel integrates with the EasyBook backend API:

- **Base URL**: `http://localhost:4000/api`
- **Authentication**: JWT token-based authentication
- **Endpoints**: All admin endpoints from the backend API

## Technology Stack

- **Frontend**: React 18
- **Styling**: Tailwind CSS
- **Charts**: Recharts
- **HTTP Client**: Axios
- **State Management**: React Query
- **Routing**: React Router
- **Icons**: Lucide React

## Development

### Available Scripts

- `npm start`: Start development server
- `npm build`: Build for production
- `npm test`: Run tests
- `npm eject`: Eject from Create React App

### Project Structure

```
src/
├── components/          # React components
│   ├── Dashboard.js     # Dashboard component
│   ├── Users.js         # User management
│   ├── Bookings.js      # Booking management
│   ├── Services.js      # Service management
│   ├── Analytics.js     # Analytics dashboard
│   ├── Login.js         # Login component
│   └── Layout.js        # Main layout
├── hooks/               # Custom hooks
│   └── useAuth.js       # Authentication hook
├── App.js              # Main app component
├── index.js            # Entry point
└── index.css           # Global styles
```

## Security

- JWT token-based authentication
- Role-based access control (admin only)
- Secure API communication
- Input validation and sanitization

## Deployment

### Build for Production

```bash
npm run build
```

This creates a `build` folder with optimized production files.

### Deploy to Web Server

1. Build the project: `npm run build`
2. Upload the `build` folder to your web server
3. Configure your web server to serve the React app
4. Ensure the backend API is accessible from the deployed admin panel

## Environment Configuration

The admin panel connects to the backend API. Make sure the backend is running on the correct port and accessible from the admin panel.

## Support

For issues or questions about the admin panel, please check the backend API documentation or contact the development team.
