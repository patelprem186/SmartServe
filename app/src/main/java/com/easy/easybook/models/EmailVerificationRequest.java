package com.easy.easybook.models;

public class EmailVerificationRequest {
    private String email;
    private String verificationCode;
    
    public EmailVerificationRequest(String email, String verificationCode) {
        this.email = email;
        this.verificationCode = verificationCode;
    }
    
    // Getters and setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getVerificationCode() {
        return verificationCode;
    }
    
    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}

