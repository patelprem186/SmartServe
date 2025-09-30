package com.easy.easybook.models;

import java.io.Serializable;
import java.util.Map;

/**
 * Simple ServiceItem model that can handle any JSON structure
 * Uses Map<String, Object> to store all fields dynamically
 */
public class ServiceItemSimple implements Serializable {
    private Map<String, Object> data;

    public ServiceItemSimple() {
        // Default constructor
    }

    public ServiceItemSimple(Map<String, Object> data) {
        this.data = data;
    }

    // Get any field as Object
    public Object get(String key) {
        return data != null ? data.get(key) : null;
    }

    // Get string field with fallback
    public String getString(String key) {
        Object value = get(key);
        return value != null ? value.toString() : "";
    }

    // Get string field with default
    public String getString(String key, String defaultValue) {
        Object value = get(key);
        return value != null ? value.toString() : defaultValue;
    }

    // Get double field with fallback
    public double getDouble(String key) {
        Object value = get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value != null ? value.toString() : "0");
        } catch (Exception e) {
            return 0.0;
        }
    }

    // Get int field with fallback
    public int getInt(String key) {
        Object value = get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value != null ? value.toString() : "0");
        } catch (Exception e) {
            return 0;
        }
    }

    // Get boolean field with fallback
    public boolean getBoolean(String key) {
        Object value = get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return "true".equalsIgnoreCase(value != null ? value.toString() : "false");
    }

    // Convenience methods for common fields
    public String getId() {
        return getString("_id", getString("id", ""));
    }

    public String getName() {
        return getString("name", "Unknown Service");
    }

    public String getDescription() {
        return getString("description", "");
    }

    public String getCategory() {
        return getString("category", "");
    }

    public double getPrice() {
        return getDouble("price");
    }

    public String getDuration() {
        return getString("duration", "");
    }

    public String getImageUrl() {
        Object images = get("images");
        if (images instanceof java.util.List) {
            java.util.List<?> imageList = (java.util.List<?>) images;
            if (!imageList.isEmpty()) {
                return imageList.get(0).toString();
            }
        }
        return "";
    }

    public boolean isAvailable() {
        return getBoolean("isActive") || getBoolean("isAvailable");
    }

    public boolean isFeatured() {
        return getBoolean("isFeatured");
    }

    public int getReviewCount() {
        return getInt("reviewCount");
    }

    // Set any field
    public void set(String key, Object value) {
        if (data != null) {
            data.put(key, value);
        }
    }

    // Get all data
    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
