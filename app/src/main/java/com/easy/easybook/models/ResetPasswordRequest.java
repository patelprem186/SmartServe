package com.easy.easybook.models;

import java.io.Serializable;

public class ResetPasswordRequest implements Serializable {
    private String token;
    private String oobCode;
    private String email;
    private String newPassword;
    
    public ResetPasswordRequest() {
    }
    
    public ResetPasswordRequest(String token, String newPassword) {
        this.token = token;
        this.newPassword = newPassword;
    }
    
    // Constructor for Firebase oobCode reset
    public ResetPasswordRequest(String oobCode, String newPassword, boolean isOobCode) {
        if (isOobCode) {
            this.oobCode = oobCode;
        } else {
            this.token = oobCode;
        }
        this.newPassword = newPassword;
    }
    
    // Factory method for email-based reset
    public static ResetPasswordRequest forEmail(String email, String newPassword) {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.email = email;
        request.newPassword = newPassword;
        return request;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getOobCode() {
        return oobCode;
    }
    
    public void setOobCode(String oobCode) {
        this.oobCode = oobCode;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
