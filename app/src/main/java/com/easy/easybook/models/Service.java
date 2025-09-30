package com.easy.easybook.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

/**
 * Model class for Service with proper Gson annotations
 */
public class Service implements Serializable {
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("category")
    private String category;
    
    @SerializedName("price")
    private double price;
    
    @SerializedName("duration")
    private String duration;
    
    @SerializedName("rating")
    private float rating;
    
    @SerializedName("reviewCount")
    private int reviewCount;
    
    @SerializedName("imageUrl")
    private String imageUrl;
    
    @SerializedName("providerName")
    private String providerName;
    
    @SerializedName("providerId")
    private String providerId;
    
    @SerializedName("isAvailable")
    private boolean isAvailable;
    
    @SerializedName("isFeatured")
    private boolean isFeatured;
    
    @SerializedName("location")
    private String location;
    
    @SerializedName("tags")
    private List<String> tags;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("updatedAt")
    private String updatedAt;

    // Default constructor
    public Service() {
    }

    // Constructor with parameters for compatibility
    public Service(String id, String name, String description, String category, double price, float rating, String duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.rating = rating;
        this.duration = duration;
    }

    // Getters
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

    public String getDuration() {
        return duration;
    }

    public float getRating() {
        return rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getProviderId() {
        return providerId;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public String getLocation() {
        return location;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
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

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}