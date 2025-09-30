package com.easy.easybook.data;

import android.content.Context;
import com.easy.easybook.models.Service;
import com.easy.easybook.models.ServiceCategory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Centralized service management to ensure consistency across all app pages
 */
public class ServiceManager {
    
    private static ServiceManager instance;
    private LocalDataManager dataManager;
    
    private ServiceManager(Context context) {
        dataManager = LocalDataManager.getInstance(context);
    }
    
    public static synchronized ServiceManager getInstance(Context context) {
        if (instance == null) {
            instance = new ServiceManager(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * Get all services (seed data + admin-added services)
     * This ensures consistency across customer and provider pages
     */
    public List<Service> getAllServices() {
        List<Service> allServices = new ArrayList<>();
        
        // Add seed services
        allServices.addAll(SeedData.getSeedServices());
        
        // Add admin-added services
        allServices.addAll(dataManager.getAllServices());
        
        // Remove duplicates based on service ID
        return removeDuplicates(allServices);
    }
    
    /**
     * Get services by category
     */
    public List<Service> getServicesByCategory(String category) {
        List<Service> allServices = getAllServices();
        List<Service> categoryServices = new ArrayList<>();
        
        for (Service service : allServices) {
            if (service.getCategory().equalsIgnoreCase(category)) {
                categoryServices.add(service);
            }
        }
        
        return categoryServices;
    }
    
    /**
     * Get featured services (high-rated services)
     */
    public List<Service> getFeaturedServices() {
        List<Service> allServices = getAllServices();
        List<Service> featured = new ArrayList<>();
        
        // Get top-rated services (rating >= 4.5)
        for (Service service : allServices) {
            if (service.getRating() >= 4.5f) {
                featured.add(service);
            }
        }
        
        return featured;
    }
    
    /**
     * Get all available categories from services
     */
    public List<String> getAllCategories() {
        List<Service> allServices = getAllServices();
        Set<String> categorySet = new HashSet<>();
        
        for (Service service : allServices) {
            categorySet.add(service.getCategory());
        }
        
        return new ArrayList<>(categorySet);
    }
    
    /**
     * Get ServiceCategory objects for UI display
     */
    public List<ServiceCategory> getServiceCategories() {
        List<String> categoryNames = getAllCategories();
        List<ServiceCategory> categories = new ArrayList<>();
        
        for (String name : categoryNames) {
            ServiceCategory category = new ServiceCategory();
            category.setName(name);
            category.setDescription(getCategoryDescription(name));
            categories.add(category);
        }
        
        return categories;
    }
    
    /**
     * Search services by query and category
     */
    public List<Service> searchServices(String query, String category) {
        List<Service> allServices = getAllServices();
        List<Service> results = new ArrayList<>();
        
        String lowerQuery = query.toLowerCase();
        
        for (Service service : allServices) {
            boolean matchesQuery = query.isEmpty() || 
                service.getName().toLowerCase().contains(lowerQuery) ||
                service.getDescription().toLowerCase().contains(lowerQuery) ||
                service.getCategory().toLowerCase().contains(lowerQuery);
                
            boolean matchesCategory = category.isEmpty() || 
                service.getCategory().equalsIgnoreCase(category);
                
            if (matchesQuery && matchesCategory) {
                results.add(service);
            }
        }
        
        return results;
    }
    
    /**
     * Remove duplicate services based on ID
     */
    private List<Service> removeDuplicates(List<Service> services) {
        List<Service> uniqueServices = new ArrayList<>();
        Set<String> seenIds = new HashSet<>();
        
        for (Service service : services) {
            if (!seenIds.contains(service.getId())) {
                uniqueServices.add(service);
                seenIds.add(service.getId());
            }
        }
        
        return uniqueServices;
    }
    
    /**
     * Get category icon resource name
     */
    
    /**
     * Get category description
     */
    private String getCategoryDescription(String category) {
        switch (category.toLowerCase()) {
            case "plumbing": return "Plumbing and water system services";
            case "cleaning": return "House and office cleaning services";
            case "electrical": return "Electrical installation and repair";
            case "hvac": return "Heating, ventilation, and air conditioning";
            case "beauty": return "Beauty and personal care services";
            case "tutoring": return "Educational and tutoring services";
            case "fitness": return "Personal training and fitness";
            default: return "Professional services";
        }
    }
}
