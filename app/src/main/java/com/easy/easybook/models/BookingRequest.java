package com.easy.easybook.models;

import java.io.Serializable;
import java.util.Date;

public class BookingRequest implements Serializable {
    private String serviceId;
    private String serviceName;
    private String providerId;
    private String providerName;
    private String customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private Date bookingDate;
    private String timeSlot;
    private double totalAmount;
    private String notes;
    private String paymentMethod;
    private String paymentStatus;

    // Constructors
    public BookingRequest() {}

    public BookingRequest(String serviceId, String serviceName, String providerId, 
                         String providerName, String customerId, String customerName,
                         String customerEmail, String customerPhone, String address,
                         String city, String state, String zipCode, Date bookingDate,
                         String timeSlot, double totalAmount, String notes) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.providerId = providerId;
        this.providerName = providerName;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.bookingDate = bookingDate;
        this.timeSlot = timeSlot;
        this.totalAmount = totalAmount;
        this.notes = notes;
        this.paymentStatus = "pending";
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

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public Date getBookingDate() { return bookingDate; }
    public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}
