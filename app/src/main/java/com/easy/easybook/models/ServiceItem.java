package com.easy.easybook.models;

import java.io.Serializable;
import java.util.List;

/**
 * Model class for ServiceItem - Updated to handle complex JSON objects
 */
public class ServiceItem implements Serializable {
    private String _id;
    private String id;
    private String name;
    private String description;
    private String category;
    private double price;
    private float rating;
    private String duration;
    private String providerId;
    private String providerName;
    private List<String> images;
    private boolean isActive;
    private boolean isFeatured;
    private boolean isAvailable;
    private Object location; // Use Object to handle both string and complex object
    private Object availability; // Use Object to handle complex availability
    private Object provider; // Use Object to handle provider object
    private Object requirements; // Use Object to handle requirements
    private List<String> serviceArea;
    private List<String> tags;
    private String cancellationPolicy;
    private String createdAt;
    private String updatedAt;
    private int reviewCount;
    private int __v;
    
    public ServiceItem() {
        // Default constructor for database
    }
    
    public ServiceItem(String id, String name, String description, String category, double price, float rating, String duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.rating = rating;
        this.duration = duration;
    }
    
    // Getters
    public String get_id() {
        return _id;
    }

    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getCategory() {
        return category;
    }
    
    public double getPrice() {
        return price;
    }
    
    public float getRating() {
        return rating;
    }
    
    public String getDuration() {
        return duration;
    }
    
    public String getProviderId() {
        return providerId;
    }
    
    public String getProviderName() {
        return providerName;
    }
    
    public List<String> getImages() {
        return images;
    }
    
    public String getImageUrl() {
        if (images != null && !images.isEmpty()) {
            return images.get(0);
        }
        return null;
    }
    
    public boolean isAvailable() {
        return isAvailable;
    }
    
    public Object getLocation() {
        return location;
    }
    
    public Object getAvailability() {
        return availability;
    }
    
    public Object getProvider() {
        return provider;
    }
    
    public Object getRequirements() {
        return requirements;
    }
    
    public List<String> getServiceArea() {
        return serviceArea;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public String getCancellationPolicy() {
        return cancellationPolicy;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public int getReviewCount() {
        return reviewCount;
    }
    
    // Setters
    public void setId(String id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public void setRating(float rating) {
        this.rating = rating;
    }
    
    public void setDuration(String duration) {
        this.duration = duration;
    }
    
    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
    
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }
    
    public void setImages(List<String> images) {
        this.images = images;
    }
    
    public void setAvailable(boolean available) {
        isAvailable = available;
    }
    
    public void setLocation(Object location) {
        this.location = location;
    }
    
    public void setAvailability(Object availability) {
        this.availability = availability;
    }
    
    public void setProvider(Object provider) {
        this.provider = provider;
    }
    
    public void setRequirements(Object requirements) {
        this.requirements = requirements;
    }
    
    public void setServiceArea(List<String> serviceArea) {
        this.serviceArea = serviceArea;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public void setCancellationPolicy(String cancellationPolicy) {
        this.cancellationPolicy = cancellationPolicy;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    // Additional getters and setters for new fields
    public void set_id(String _id) {
        this._id = _id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }
}
