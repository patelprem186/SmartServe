package com.easy.easybook.data;

import com.easy.easybook.models.Service;
import com.easy.easybook.models.User;
import com.easy.easybook.models.Booking;
import java.util.ArrayList;
import java.util.List;

/**
 * Seed data for the application - provides default data when no API is available
 */
public class SeedData {
    
    // Seed Services
    public static List<Service> getSeedServices() {
        List<Service> services = new ArrayList<>();
        
        // Plumbing Services
        services.add(new Service("1", "Emergency Plumbing Repair", 
            "24/7 emergency plumbing services for leaks, clogs, and urgent repairs.", 
            "Plumbing", 120.0, 4.5f, "60"));
        
        services.add(new Service("2", "Pipe Installation", 
            "Professional pipe installation and repair services.", 
            "Plumbing", 200.0, 4.8f, "120"));
        
        // Cleaning Services
        services.add(new Service("3", "Deep House Cleaning", 
            "Complete deep cleaning of your home including all rooms and bathrooms.", 
            "Cleaning", 150.0, 4.7f, "180"));
        
        services.add(new Service("4", "Office Cleaning", 
            "Professional office cleaning services for businesses.", 
            "Cleaning", 100.0, 4.6f, "120"));
        
        // Electrical Services
        services.add(new Service("5", "Electrical Outlet Installation", 
            "Professional installation of new electrical outlets and switches.", 
            "Electrical", 85.0, 4.3f, "45"));
        
        services.add(new Service("6", "Light Fixture Installation", 
            "Installation of new light fixtures and electrical components.", 
            "Electrical", 120.0, 4.4f, "60"));
        
        // HVAC Services
        services.add(new Service("7", "HVAC System Repair", 
            "Professional heating, ventilation, and air conditioning system repair.", 
            "HVAC", 200.0, 4.6f, "120"));
        
        services.add(new Service("8", "Air Conditioning Installation", 
            "Complete air conditioning system installation with warranty.", 
            "HVAC", 800.0, 4.9f, "240"));
        
        // Beauty Services
        services.add(new Service("9", "Hair Styling", 
            "Professional hair styling and cutting services.", 
            "Beauty", 50.0, 4.5f, "60"));
        
        services.add(new Service("10", "Facial Treatment", 
            "Relaxing facial treatment and skincare services.", 
            "Beauty", 80.0, 4.7f, "90"));
        
        // Tutoring Services
        services.add(new Service("11", "Math Tutoring", 
            "One-on-one math tutoring for all grade levels.", 
            "Tutoring", 50.0, 4.7f, "60"));
        
        services.add(new Service("12", "English Tutoring", 
            "Professional English language tutoring and writing assistance.", 
            "Tutoring", 45.0, 4.6f, "60"));
        
        // Fitness Services
        services.add(new Service("13", "Personal Training", 
            "One-on-one personal training sessions with certified trainers.", 
            "Fitness", 75.0, 4.8f, "60"));
        
        services.add(new Service("14", "Yoga Classes", 
            "Group and private yoga classes for all skill levels.", 
            "Fitness", 40.0, 4.5f, "60"));
        
        return services;
    }
    
    // Seed Service Categories
    public static List<String> getSeedCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("Plumbing");
        categories.add("Cleaning");
        categories.add("Electrical");
        categories.add("HVAC");
        categories.add("Beauty");
        categories.add("Tutoring");
        categories.add("Fitness");
        return categories;
    }
    
    // Seed Providers
    public static List<User> getSeedProviders() {
        List<User> providers = new ArrayList<>();
        
        providers.add(new User("provider1", "John", "Smith", "john.smith@email.com", "555-0101", "provider", true, ""));
        providers.add(new User("provider2", "Sarah", "Johnson", "sarah.johnson@email.com", "555-0102", "provider", true, ""));
        providers.add(new User("provider3", "Mike", "Wilson", "mike.wilson@email.com", "555-0103", "provider", true, ""));
        providers.add(new User("provider4", "Lisa", "Brown", "lisa.brown@email.com", "555-0104", "provider", true, ""));
        providers.add(new User("provider5", "David", "Davis", "david.davis@email.com", "555-0105", "provider", true, ""));
        
        return providers;
    }
    
    // Get services by category
    public static List<Service> getServicesByCategory(String category) {
        List<Service> allServices = getSeedServices();
        List<Service> categoryServices = new ArrayList<>();
        
        for (Service service : allServices) {
            if (service.getCategory().equalsIgnoreCase(category)) {
                categoryServices.add(service);
            }
        }
        
        return categoryServices;
    }
    
    // Get featured services
    public static List<Service> getFeaturedServices() {
        List<Service> allServices = getSeedServices();
        List<Service> featured = new ArrayList<>();
        
        // Get top-rated services
        for (Service service : allServices) {
            if (service.getRating() >= 4.5f) {
                featured.add(service);
            }
        }
        
        return featured;
    }
    
    // Search services
    public static List<Service> searchServices(String query) {
        List<Service> allServices = getSeedServices();
        List<Service> results = new ArrayList<>();
        
        String lowerQuery = query.toLowerCase();
        
        for (Service service : allServices) {
            if (service.getName().toLowerCase().contains(lowerQuery) ||
                service.getDescription().toLowerCase().contains(lowerQuery) ||
                service.getCategory().toLowerCase().contains(lowerQuery)) {
                results.add(service);
            }
        }
        
        return results;
    }
}
