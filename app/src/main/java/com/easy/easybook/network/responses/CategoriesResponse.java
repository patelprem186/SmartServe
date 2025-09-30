package com.easy.easybook.network.responses;

import com.easy.easybook.models.ServiceCategory;
import java.util.List;

public class CategoriesResponse {
    private boolean success;
    private String message;
    private CategoriesData data;
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
    
    public CategoriesData getData() {
        return data;
    }
    
    public void setData(CategoriesData data) {
        this.data = data;
    }
    
    public String[] getErrors() {
        return errors;
    }
    
    public void setErrors(String[] errors) {
        this.errors = errors;
    }
    
    // Convenience method to get categories directly
    public List<ServiceCategory> getCategories() {
        if (data != null) {
            List<ServiceCategory> categories = data.getCategories();
            android.util.Log.d("CategoriesResponse", "Successfully parsed " + (categories != null ? categories.size() : 0) + " categories");
            return categories;
        }
        android.util.Log.w("CategoriesResponse", "No categories data found in response");
        return null;
    }
    
    public static class CategoriesData {
        private List<ServiceCategory> categories;
        
        public List<ServiceCategory> getCategories() {
            return categories;
        }
        
        public void setCategories(List<ServiceCategory> categories) {
            this.categories = categories;
        }
    }
}
