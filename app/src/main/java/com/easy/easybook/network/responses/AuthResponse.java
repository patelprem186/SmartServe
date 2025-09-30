package com.easy.easybook.network.responses;

import com.easy.easybook.models.User;

public class AuthResponse {
    private boolean success;
    private String message;
    private AuthData data;
    private String[] errors;
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public AuthData getData() {
        return data;
    }
    
    public void setData(AuthData data) {
        this.data = data;
    }
    
    public String[] getErrors() {
        return errors;
    }
    
    public void setErrors(String[] errors) {
        this.errors = errors;
    }
    
    public static class AuthData {
        private User user;
        private String token;
        
        public User getUser() {
            return user;
        }
        
        public void setUser(User user) {
            this.user = user;
        }
        
        public String getToken() {
            return token;
        }
        
        public void setToken(String token) {
            this.token = token;
        }
    }
}
