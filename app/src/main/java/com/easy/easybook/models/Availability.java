package com.easy.easybook.models;

import java.util.List;
import java.util.Map;

public class Availability {
    private Map<String, List<TimeSlot>> availability;
    
    public Availability() {}
    
    public Map<String, List<TimeSlot>> getAvailability() {
        return availability;
    }
    
    public void setAvailability(Map<String, List<TimeSlot>> availability) {
        this.availability = availability;
    }
    
    public static class TimeSlot {
        private String _id;
        private String start;
        private String end;
        private boolean isAvailable;
        
        public TimeSlot() {}
        
        public String get_id() {
            return _id;
        }
        
        public void set_id(String _id) {
            this._id = _id;
        }
        
        public String getStart() {
            return start;
        }
        
        public void setStart(String start) {
            this.start = start;
        }
        
        public String getEnd() {
            return end;
        }
        
        public void setEnd(String end) {
            this.end = end;
        }
        
        public boolean isAvailable() {
            return isAvailable;
        }
        
        public void setAvailable(boolean available) {
            isAvailable = available;
        }
    }
}
