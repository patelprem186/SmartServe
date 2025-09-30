package com.easy.easybook.models;

import java.io.Serializable;

public class ForgotPasswordRequest implements Serializable {
    private String email;
    
    public ForgotPasswordRequest() {
    }
    
    public ForgotPasswordRequest(String email) {
        this.email = email;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
}
