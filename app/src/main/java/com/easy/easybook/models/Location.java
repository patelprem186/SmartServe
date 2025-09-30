package com.easy.easybook.models;

import java.util.List;

public class Location {
    private String type;
    private ServiceArea serviceArea;
    
    public Location() {}
    
    public Location(String type, ServiceArea serviceArea) {
        this.type = type;
        this.serviceArea = serviceArea;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public ServiceArea getServiceArea() {
        return serviceArea;
    }
    
    public void setServiceArea(ServiceArea serviceArea) {
        this.serviceArea = serviceArea;
    }
    
    public static class ServiceArea {
        private int radius;
        private List<String> cities;
        
        public ServiceArea() {}
        
        public ServiceArea(int radius, List<String> cities) {
            this.radius = radius;
            this.cities = cities;
        }
        
        public int getRadius() {
            return radius;
        }
        
        public void setRadius(int radius) {
            this.radius = radius;
        }
        
        public List<String> getCities() {
            return cities;
        }
        
        public void setCities(List<String> cities) {
            this.cities = cities;
        }
    }
}
