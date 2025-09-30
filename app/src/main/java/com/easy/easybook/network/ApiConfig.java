package com.easy.easybook.network;

public class ApiConfig {
    
    // Base URL for SmartServe API
    public static final String BASE_URL = "http://10.0.2.2:4000/api/"; // For Android Emulator
    // Use "http://YOUR_COMPUTER_IP:4000/api/" for physical device
    
    // API Endpoints
    public static final String LOGIN = "auth/login";
    public static final String REGISTER = "auth/register";
    public static final String VERIFY_EMAIL = "auth/verify-email";
    public static final String RESEND_VERIFICATION = "auth/resend-verification";
    public static final String PROFILE = "auth/me";
    public static final String UPDATE_PROFILE = "auth/profile";
    public static final String CHANGE_PASSWORD = "auth/change-password";
    
    // Services
    public static final String CATEGORIES = "services/categories";
    public static final String FEATURED_SERVICES = "services/featured";
    public static final String SEARCH_SERVICES = "services/search";
    public static final String SERVICE_DETAILS = "services/";
    
    // Bookings
    public static final String CREATE_BOOKING = "bookings";
    public static final String MY_BOOKINGS = "bookings/my-bookings";
    public static final String BOOKING_DETAILS = "bookings/";
    public static final String UPDATE_BOOKING_STATUS = "bookings/{id}/status";
    public static final String ADD_RATING = "bookings/{id}/rating";
    
    // Customer
    public static final String CUSTOMER_DASHBOARD = "customers/dashboard";
    public static final String BOOKING_HISTORY = "customers/booking-history";
    public static final String CUSTOMER_FAVORITES = "customers/favorites";
    public static final String UPDATE_FCM_TOKEN = "customers/fcm-token";
    
    // Provider
    public static final String PROVIDER_DASHBOARD = "providers/dashboard";
    public static final String PROVIDER_SERVICES = "providers/my-services";
    public static final String PROVIDER_EARNINGS = "providers/earnings";
    public static final String PROVIDER_REVIEWS = "providers/reviews";
    
    // Headers
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    
    // SharedPreferences Keys
    public static final String PREF_NAME = "SmartServePrefs";
    public static final String KEY_TOKEN = "jwt_token";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_ROLE = "user_role";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    public static final String KEY_FCM_TOKEN = "fcm_token";
}
