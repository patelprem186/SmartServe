package com.easy.easybook.network.responses;

import com.easy.easybook.models.ServiceItem;
import com.google.gson.JsonObject;
import java.util.List;

public class ServicesResponse {
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
    
    // Convenience method to get services directly - simplified approach
    public List<ServiceItem> getServices() {
        android.util.Log.d("ServicesResponse", "Getting services from response...");
        
        if (data != null && data.has("services")) {
            try {
                android.util.Log.d("ServicesResponse", "Found services in data, parsing...");
                
                // Get the services array from JSON
                com.google.gson.JsonArray servicesArray = data.getAsJsonArray("services");
                android.util.Log.d("ServicesResponse", "Services array size: " + servicesArray.size());
                
                List<ServiceItem> services = new java.util.ArrayList<>();
                
                // Parse each service individually to avoid bulk parsing errors
                for (int i = 0; i < servicesArray.size(); i++) {
                    try {
                        com.google.gson.JsonObject serviceObj = servicesArray.get(i).getAsJsonObject();
                        ServiceItem service = new ServiceItem();
                        
                        // Parse each field individually with fallbacks
                        service.setId(getStringValue(serviceObj, "id", ""));
                        service.setName(getStringValue(serviceObj, "name", "Unknown Service"));
                        service.setDescription(getStringValue(serviceObj, "description", ""));
                        service.setCategory(getStringValue(serviceObj, "category", "Other"));
                        service.setPrice(getDoubleValue(serviceObj, "price", 0.0));
                        service.setDuration(getStringValue(serviceObj, "duration", "60"));
                        service.setRating(getFloatValue(serviceObj, "rating", 0.0f));
                        service.setReviewCount(getIntValue(serviceObj, "reviewCount", 0));
                        service.setAvailable(getBooleanValue(serviceObj, "isAvailable", true));
                        service.setFeatured(getBooleanValue(serviceObj, "isFeatured", false));
                        
                        services.add(service);
                        android.util.Log.d("ServicesResponse", "Parsed service: " + service.getName());
                        
                    } catch (Exception e) {
                        android.util.Log.e("ServicesResponse", "Error parsing individual service " + i + ": " + e.getMessage());
                    }
                }
                
                android.util.Log.d("ServicesResponse", "Successfully parsed " + services.size() + " services");
                return services;
                
            } catch (Exception e) {
                android.util.Log.e("ServicesResponse", "Error parsing services array: " + e.getMessage(), e);
                return new java.util.ArrayList<>();
            }
        }
        
        android.util.Log.w("ServicesResponse", "No services data found in response");
        return new java.util.ArrayList<>();
    }
    
    // Helper methods for safe JSON parsing
    private String getStringValue(com.google.gson.JsonObject obj, String key, String defaultValue) {
        try {
            if (obj.has(key) && !obj.get(key).isJsonNull()) {
                return obj.get(key).getAsString();
            }
        } catch (Exception e) {
            android.util.Log.w("ServicesResponse", "Error getting string value for " + key + ": " + e.getMessage());
        }
        return defaultValue;
    }
    
    private double getDoubleValue(com.google.gson.JsonObject obj, String key, double defaultValue) {
        try {
            if (obj.has(key) && !obj.get(key).isJsonNull()) {
                return obj.get(key).getAsDouble();
            }
        } catch (Exception e) {
            android.util.Log.w("ServicesResponse", "Error getting double value for " + key + ": " + e.getMessage());
        }
        return defaultValue;
    }
    
    private float getFloatValue(com.google.gson.JsonObject obj, String key, float defaultValue) {
        try {
            if (obj.has(key) && !obj.get(key).isJsonNull()) {
                return obj.get(key).getAsFloat();
            }
        } catch (Exception e) {
            android.util.Log.w("ServicesResponse", "Error getting float value for " + key + ": " + e.getMessage());
        }
        return defaultValue;
    }
    
    private int getIntValue(com.google.gson.JsonObject obj, String key, int defaultValue) {
        try {
            if (obj.has(key) && !obj.get(key).isJsonNull()) {
                return obj.get(key).getAsInt();
            }
        } catch (Exception e) {
            android.util.Log.w("ServicesResponse", "Error getting int value for " + key + ": " + e.getMessage());
        }
        return defaultValue;
    }
    
    private boolean getBooleanValue(com.google.gson.JsonObject obj, String key, boolean defaultValue) {
        try {
            if (obj.has(key) && !obj.get(key).isJsonNull()) {
                return obj.get(key).getAsBoolean();
            }
        } catch (Exception e) {
            android.util.Log.w("ServicesResponse", "Error getting boolean value for " + key + ": " + e.getMessage());
        }
        return defaultValue;
    }
    
    public static class ServicesData {
        private List<ServiceItem> services;
        private Pagination pagination;
        
        public List<ServiceItem> getServices() {
            return services;
        }
        
        public void setServices(List<ServiceItem> services) {
            this.services = services;
        }
        
        public Pagination getPagination() {
            return pagination;
        }
        
        public void setPagination(Pagination pagination) {
            this.pagination = pagination;
        }
    }
    
    public static class Pagination {
        private int currentPage;
        private int totalPages;
        private int totalServices;
        private boolean hasNext;
        private boolean hasPrev;
        
        public int getCurrentPage() {
            return currentPage;
        }
        
        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }
        
        public int getTotalPages() {
            return totalPages;
        }
        
        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }
        
        public int getTotalServices() {
            return totalServices;
        }
        
        public void setTotalServices(int totalServices) {
            this.totalServices = totalServices;
        }
        
        public boolean isHasNext() {
            return hasNext;
        }
        
        public void setHasNext(boolean hasNext) {
            this.hasNext = hasNext;
        }
        
        public boolean isHasPrev() {
            return hasPrev;
        }
        
        public void setHasPrev(boolean hasPrev) {
            this.hasPrev = hasPrev;
        }
    }
}
