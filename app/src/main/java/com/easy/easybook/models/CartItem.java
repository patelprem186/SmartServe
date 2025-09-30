package com.easy.easybook.models;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String serviceId;
    private String serviceName;
    private String providerId;
    private String providerName;
    private double price;
    private int quantity;
    private String description;
    private String imageUrl;

    // Constructors
    public CartItem() {}

    public CartItem(String serviceId, String serviceName, String providerId, 
                   String providerName, double price, int quantity, 
                   String description, String imageUrl) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.providerId = providerId;
        this.providerName = providerName;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // Helper methods
    public double getTotalPrice() {
        return price * quantity;
    }

    public void incrementQuantity() {
        this.quantity++;
    }

    public void decrementQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
        }
    }
}
