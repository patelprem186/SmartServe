package com.easy.easybook.models;

import java.io.Serializable;

public class TwoFactorAuth implements Serializable {
    private String phoneNumber;
    private String verificationId;
    private String verificationCode;
    private boolean isVerified;
    private long timestamp;
    
    public TwoFactorAuth() {}
    
    public TwoFactorAuth(String phoneNumber, String verificationId) {
        this.phoneNumber = phoneNumber;
        this.verificationId = verificationId;
        this.isVerified = false;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and setters
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getVerificationId() {
        return verificationId;
    }
    
    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
    }
    
    public String getVerificationCode() {
        return verificationCode;
    }
    
    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
    
    public boolean isVerified() {
        return isVerified;
    }
    
    public void setVerified(boolean verified) {
        isVerified = verified;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
