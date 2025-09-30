package com.easy.easybook.models;

import java.io.Serializable;

/**
 * Simple Service model that matches the simplified backend response
 */
public class SimpleService implements Serializable {
    private String id;
    private String name;
    private String description;
    private String category;
    private double price;
    private String duration;
    private float rating;
    private int reviewCount;
    private String imageUrl;
    private String providerName;
    private String providerId;
    private boolean isAvailable;
    private boolean isFeatured;
    private String location;
    private String[] tags;
    private String createdAt;
    private String updatedAt;

    public SimpleService() {
        // Default constructor
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

    public String[] getTags() {
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

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
