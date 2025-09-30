package com.easy.easybook.network;

import com.easy.easybook.models.LoginRequest;
import com.easy.easybook.models.RegisterRequest;
import com.easy.easybook.models.ForgotPasswordRequest;
import com.easy.easybook.models.ResetPasswordRequest;
import com.easy.easybook.network.responses.ApiResponse;
import com.easy.easybook.network.responses.AuthResponse;
import com.easy.easybook.network.responses.CategoriesResponse;
import com.easy.easybook.network.responses.ServicesResponse;
import com.easy.easybook.network.responses.SimpleServicesResponse;
import com.easy.easybook.models.ServiceResponse;
import com.easy.easybook.network.responses.DashboardResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    
    // Authentication endpoints
    @POST(ApiConfig.LOGIN)
    Call<AuthResponse> login(@Body LoginRequest loginRequest);
    
    @POST(ApiConfig.REGISTER)
    Call<AuthResponse> register(@Body RegisterRequest registerRequest);
    
    @POST("auth/forgot-password")
    Call<ApiResponse> forgotPassword(@Body ForgotPasswordRequest request);
    
    @POST("auth/reset-password")
    Call<ApiResponse> resetPassword(@Body ResetPasswordRequest request);
    
    @POST("auth/verify-firebase-token")
    Call<AuthResponse> verifyFirebaseToken(@Body Object tokenRequest);
    
    @POST("auth/update-password")
    Call<ApiResponse> updatePassword(@Body Object passwordRequest);

    @POST(ApiConfig.VERIFY_EMAIL)
    Call<AuthResponse> verifyEmail(@Body Object verificationRequest);

    @POST(ApiConfig.RESEND_VERIFICATION)
    Call<ApiResponse> resendVerification(@Body Object resendRequest);

    @GET(ApiConfig.PROFILE)
    Call<AuthResponse> getProfile(@Header(ApiConfig.AUTHORIZATION) String token);
    
    @GET("api/auth/profile")
    Call<ApiResponse> getUserProfile(@Header(ApiConfig.AUTHORIZATION) String token);
    
    // Service endpoints
    @GET(ApiConfig.CATEGORIES)
    Call<CategoriesResponse> getServiceCategories();
    
    @GET(ApiConfig.FEATURED_SERVICES)
    Call<ServiceResponse> getFeaturedServices();
    
    @GET(ApiConfig.FEATURED_SERVICES)
    Call<SimpleServicesResponse> getFeaturedServicesSimple();
    
    @GET("api/services")
    Call<ApiResponse> getAllServices(
        @Header(ApiConfig.AUTHORIZATION) String token,
        @Query("sort") String sortBy,
        @Query("page") Integer page,
        @Query("limit") Integer limit
    );
    
    @GET("api/services/search")
    Call<ApiResponse> searchServices(
        @Header(ApiConfig.AUTHORIZATION) String token,
        @Query("query") String query,
        @Query("category") String category,
        @Query("sort") String sortBy,
        @Query("page") Integer page,
        @Query("limit") Integer limit
    );
    
    @GET("services/category/{category}")
    Call<ApiResponse> getServicesByCategory(
        @Header(ApiConfig.AUTHORIZATION) String token,
        @Path("category") String category,
        @Query("sort") String sortBy,
        @Query("page") Integer page,
        @Query("limit") Integer limit
    );
    
    @GET(ApiConfig.SERVICE_DETAILS + "{id}")
    Call<ApiResponse> getServiceDetails(@Path("id") String serviceId);
    
    // Customer endpoints
    @GET(ApiConfig.CUSTOMER_DASHBOARD)
    Call<DashboardResponse> getCustomerDashboard(@Header(ApiConfig.AUTHORIZATION) String token);
    
    @GET(ApiConfig.BOOKING_HISTORY)
    Call<ApiResponse> getBookingHistory(
        @Header(ApiConfig.AUTHORIZATION) String token,
        @Query("status") String status,
        @Query("page") Integer page,
        @Query("limit") Integer limit
    );
    
    @GET(ApiConfig.CUSTOMER_FAVORITES)
    Call<ApiResponse> getCustomerFavorites(@Header(ApiConfig.AUTHORIZATION) String token);
    
    // Booking endpoints
    @POST(ApiConfig.CREATE_BOOKING)
    Call<ApiResponse> createBooking(
        @Header(ApiConfig.AUTHORIZATION) String token,
        @Body Object bookingRequest
    );
    
    @GET(ApiConfig.MY_BOOKINGS)
    Call<ApiResponse> getMyBookings(
        @Header(ApiConfig.AUTHORIZATION) String token,
        @Query("status") String status,
        @Query("page") Integer page,
        @Query("limit") Integer limit
    );
    
    // Provider endpoints
    @GET(ApiConfig.PROVIDER_DASHBOARD)
    Call<DashboardResponse> getProviderDashboard(@Header(ApiConfig.AUTHORIZATION) String token);
    
    @GET(ApiConfig.PROVIDER_SERVICES)
    Call<ServicesResponse> getProviderServices(
        @Header(ApiConfig.AUTHORIZATION) String token,
        @Query("status") String status,
        @Query("page") Integer page,
        @Query("limit") Integer limit
    );
    
    // FCM Token update
    @POST(ApiConfig.UPDATE_FCM_TOKEN)
    Call<ApiResponse> updateFcmToken(
        @Header(ApiConfig.AUTHORIZATION) String token,
        @Body Object fcmTokenRequest
    );
    
    // Rating endpoints
    @POST("api/bookings/{id}/rating")
    Call<ApiResponse> addRating(
        @Header(ApiConfig.AUTHORIZATION) String token,
        @Path("id") String bookingId,
        @Body Object ratingRequest
    );
    
    // Service management endpoints (Provider)
    @POST("api/services")
    Call<ApiResponse> createService(
        @Header(ApiConfig.AUTHORIZATION) String token,
        @Body Object serviceRequest
    );
    
    @PUT("api/services/{id}")
    Call<ApiResponse> updateService(
        @Header(ApiConfig.AUTHORIZATION) String token,
        @Path("id") String serviceId,
        @Body Object serviceRequest
    );
    
    @DELETE("api/services/{id}")
    Call<ApiResponse> deleteService(
        @Header(ApiConfig.AUTHORIZATION) String token,
        @Path("id") String serviceId
    );
    
    // Booking status update
    @PUT("api/bookings/{id}/status")
    Call<ApiResponse> updateBookingStatus(
        @Header(ApiConfig.AUTHORIZATION) String token,
        @Path("id") String bookingId,
        @Body Object statusRequest
    );
}
