package com.easy.easybook.models;

import java.io.Serializable;
import java.util.Date;

public class Review implements Serializable {
    private String id;
    private String bookingId;
    private String serviceId;
    private String providerId;
    private String customerId;
    private String customerName;
    private String customerEmail;
    private int rating;
    private String comment;
    private String response; // Provider's response to the review
    private Date createdAt;
    private Date updatedAt;
    private boolean isVerified;

    // Constructors
    public Review() {}

    public Review(String id, String bookingId, String serviceId, String providerId,
                  String customerId, String customerName, String customerEmail,
                  int rating, String comment) {
        this.id = id;
        this.bookingId = bookingId;
        this.serviceId = serviceId;
        this.providerId = providerId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.isVerified = true;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
}
