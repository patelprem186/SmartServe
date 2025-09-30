package com.easy.easybook.models;

import java.io.Serializable;

public class RegisterRequest implements Serializable {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private String role;
    private String serviceCategory;
    private String experience;
    private String description;
    
    // No-argument constructor
    public RegisterRequest() {
    }
    
    public RegisterRequest(String firstName, String lastName, String email, 
                          String phone, String password, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.serviceCategory = null;
    }
    
    public RegisterRequest(String firstName, String lastName, String email, 
                          String phone, String password, String role, String serviceCategory) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.serviceCategory = serviceCategory;
    }
    
    // Getters and setters
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getServiceCategory() {
        return serviceCategory;
    }
    
    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }
    
    public String getExperience() {
        return experience;
    }
    
    public void setExperience(String experience) {
        this.experience = experience;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
