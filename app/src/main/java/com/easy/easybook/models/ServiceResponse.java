package com.easy.easybook.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Model class for service response from API
 */
public class ServiceResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private ServiceData data;
    
    @SerializedName("errors")
    private String[] errors;

    // Getters and setters
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

    public ServiceData getData() {
        return data;
    }

    public void setData(ServiceData data) {
        this.data = data;
    }

    public String[] getErrors() {
        return errors;
    }

    public void setErrors(String[] errors) {
        this.errors = errors;
    }

    // Convenience method to get services directly
    public List<Service> getServices() {
        if (data != null && data.getServices() != null) {
            android.util.Log.d("ServiceResponse", "Successfully retrieved " + data.getServices().size() + " services");
            return data.getServices();
        }
        android.util.Log.w("ServiceResponse", "No services found in response");
        return new java.util.ArrayList<>();
    }

    public static class ServiceData {
        @SerializedName("services")
        private List<Service> services;
        
        @SerializedName("pagination")
        private Pagination pagination;

        public List<Service> getServices() {
            return services;
        }

        public void setServices(List<Service> services) {
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
        @SerializedName("currentPage")
        private int currentPage;
        
        @SerializedName("totalPages")
        private int totalPages;
        
        @SerializedName("totalItems")
        private int totalItems;
        
        @SerializedName("itemsPerPage")
        private int itemsPerPage;

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

        public int getTotalItems() {
            return totalItems;
        }

        public void setTotalItems(int totalItems) {
            this.totalItems = totalItems;
        }

        public int getItemsPerPage() {
            return itemsPerPage;
        }

        public void setItemsPerPage(int itemsPerPage) {
            this.itemsPerPage = itemsPerPage;
        }
    }
}
