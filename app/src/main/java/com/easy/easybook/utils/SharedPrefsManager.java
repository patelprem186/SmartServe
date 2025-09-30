package com.easy.easybook.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.easy.easybook.models.User;
import com.easy.easybook.network.ApiConfig;

public class SharedPrefsManager {
    private static SharedPrefsManager instance;
    private SharedPreferences sharedPreferences;
    
    private SharedPrefsManager(Context context) {
        sharedPreferences = context.getSharedPreferences(ApiConfig.PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public static synchronized SharedPrefsManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefsManager(context.getApplicationContext());
        }
        return instance;
    }
    
    // Authentication methods
    public void saveAuthToken(String token) {
        sharedPreferences.edit().putString(ApiConfig.KEY_TOKEN, token).apply();
    }
    
    public String getAuthToken() {
        return sharedPreferences.getString(ApiConfig.KEY_TOKEN, null);
    }
    
    // Convenience method for getToken()
    public String getToken() {
        return getAuthToken();
    }
    
    public void saveUser(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ApiConfig.KEY_USER_ID, user.getId());
        editor.putString(ApiConfig.KEY_USER_NAME, user.getFullName());
        editor.putString(ApiConfig.KEY_USER_EMAIL, user.getEmail());
        editor.putString(ApiConfig.KEY_USER_ROLE, user.getRole());
        editor.putBoolean(ApiConfig.KEY_IS_LOGGED_IN, true);
        editor.apply();
    }
    
    public User getUser() {
        if (!isLoggedIn()) {
            return null;
        }
        
        User user = new User();
        user.setId(sharedPreferences.getString(ApiConfig.KEY_USER_ID, ""));
        user.setEmail(sharedPreferences.getString(ApiConfig.KEY_USER_EMAIL, ""));
        user.setRole(sharedPreferences.getString(ApiConfig.KEY_USER_ROLE, ""));
        
        String fullName = sharedPreferences.getString(ApiConfig.KEY_USER_NAME, "");
        if (!fullName.isEmpty()) {
            String[] names = fullName.split(" ", 2);
            user.setFirstName(names[0]);
            if (names.length > 1) {
                user.setLastName(names[1]);
            }
        }
        
        return user;
    }
    
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(ApiConfig.KEY_IS_LOGGED_IN, false) 
               && getAuthToken() != null;
    }
    
    public String getUserRole() {
        return sharedPreferences.getString(ApiConfig.KEY_USER_ROLE, "");
    }
    
    public String getUserId() {
        return sharedPreferences.getString(ApiConfig.KEY_USER_ID, "");
    }
    
    // FCM Token methods
    public void saveFcmToken(String fcmToken) {
        sharedPreferences.edit().putString(ApiConfig.KEY_FCM_TOKEN, fcmToken).apply();
    }
    
    public String getFcmToken() {
        return sharedPreferences.getString(ApiConfig.KEY_FCM_TOKEN, null);
    }
    
    // Logout
    public void logout() {
        sharedPreferences.edit().clear().apply();
    }
    
    // Clear user data (alias for logout)
    public void clearUserData() {
        sharedPreferences.edit().clear().apply();
    }
    
    // Helper method to get Authorization header
    public String getAuthHeader() {
        String token = getAuthToken();
        if (token != null) {
            return ApiConfig.BEARER + token;
        }
        return null;
    }
}
