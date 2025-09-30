package com.easy.easybook.network.responses;

import com.google.gson.JsonObject;

public class ApiResponse {
    private boolean success;
    private String message;
    private JsonObject data;
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
    
    public JsonObject getData() {
        return data;
    }
    
    public void setData(JsonObject data) {
        this.data = data;
    }
    
    public String[] getErrors() {
        return errors;
    }
    
    public void setErrors(String[] errors) {
        this.errors = errors;
    }
}
