package com.easy.easybook.network.responses;

import com.easy.easybook.models.SimpleService;
import java.util.List;

/**
 * Simple response for services - matches simplified backend structure
 */
public class SimpleServicesResponse {
    private boolean success;
    private String message;
    private ServicesData data;
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

    public ServicesData getData() {
        return data;
    }

    public void setData(ServicesData data) {
        this.data = data;
    }

    public String[] getErrors() {
        return errors;
    }

    public void setErrors(String[] errors) {
        this.errors = errors;
    }

    // Convenience method to get services directly
    public List<SimpleService> getServices() {
        if (data != null && data.getServices() != null) {
            android.util.Log.d("SimpleServicesResponse", "Successfully parsed " + data.getServices().size() + " services");
            return data.getServices();
        }
        android.util.Log.w("SimpleServicesResponse", "No services data found");
        return new java.util.ArrayList<>();
    }

    public static class ServicesData {
        private List<SimpleService> services;

        public List<SimpleService> getServices() {
            return services;
        }

        public void setServices(List<SimpleService> services) {
            this.services = services;
        }
    }
}