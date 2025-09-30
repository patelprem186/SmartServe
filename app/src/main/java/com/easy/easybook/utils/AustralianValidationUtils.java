package com.easy.easybook.utils;

import java.util.regex.Pattern;

public class AustralianValidationUtils {
    
    // Australian mobile number patterns
    private static final Pattern AUS_MOBILE_PATTERN = Pattern.compile("^(\\+61|0)[2-9]\\d{8}$");
    private static final Pattern AUS_MOBILE_CLEAN_PATTERN = Pattern.compile("^[2-9]\\d{8}$");
    
    // Australian postcode pattern (4 digits)
    private static final Pattern AUS_POSTCODE_PATTERN = Pattern.compile("^\\d{4}$");
    
    // CVV pattern (3-4 digits)
    private static final Pattern CVV_PATTERN = Pattern.compile("^\\d{3,4}$");
    
    // Card expiry date pattern (MM/YY)
    private static final Pattern CARD_EXPIRY_PATTERN = Pattern.compile("^(0[1-9]|1[0-2])/(\\d{2})$");
    
    // Australian states and territories
    public static final String[] AUSTRALIAN_STATES = {
        "Australian Capital Territory",
        "New South Wales", 
        "Northern Territory",
        "Queensland",
        "South Australia",
        "Tasmania",
        "Victoria",
        "Western Australia"
    };
    
    public static final String[] AUSTRALIAN_STATE_CODES = {
        "ACT", "NSW", "NT", "QLD", "SA", "TAS", "VIC", "WA"
    };
    
    /**
     * Validates Australian mobile number
     * Accepts formats: +61412345678, 0412345678, 412345678
     */
    public static boolean isValidAustralianMobile(String mobile) {
        if (mobile == null || mobile.trim().isEmpty()) {
            return false;
        }
        
        String cleanMobile = mobile.trim().replaceAll("\\s+", "");
        
        // Check if it starts with +61
        if (cleanMobile.startsWith("+61")) {
            return AUS_MOBILE_PATTERN.matcher(cleanMobile).matches();
        }
        
        // Check if it starts with 0 (Australian format)
        if (cleanMobile.startsWith("0")) {
            return AUS_MOBILE_PATTERN.matcher(cleanMobile).matches();
        }
        
        // Check if it's just the 9 digits after country code
        if (cleanMobile.length() == 9) {
            return AUS_MOBILE_CLEAN_PATTERN.matcher(cleanMobile).matches();
        }
        
        return false;
    }
    
    /**
     * Formats Australian mobile number to standard format
     */
    public static String formatAustralianMobile(String mobile) {
        if (mobile == null || mobile.trim().isEmpty()) {
            return "";
        }
        
        String cleanMobile = mobile.trim().replaceAll("\\s+", "");
        
        // If it starts with +61, return as is
        if (cleanMobile.startsWith("+61")) {
            return cleanMobile;
        }
        
        // If it starts with 0, convert to +61 format
        if (cleanMobile.startsWith("0")) {
            return "+61" + cleanMobile.substring(1);
        }
        
        // If it's 9 digits, add +61
        if (cleanMobile.length() == 9 && AUS_MOBILE_CLEAN_PATTERN.matcher(cleanMobile).matches()) {
            return "+61" + cleanMobile;
        }
        
        return cleanMobile;
    }
    
    /**
     * Validates Australian postcode (4 digits)
     */
    public static boolean isValidAustralianPostcode(String postcode) {
        if (postcode == null || postcode.trim().isEmpty()) {
            return false;
        }
        
        String cleanPostcode = postcode.trim();
        return AUS_POSTCODE_PATTERN.matcher(cleanPostcode).matches();
    }
    
    /**
     * Validates CVV (3-4 digits)
     */
    public static boolean isValidCVV(String cvv) {
        if (cvv == null || cvv.trim().isEmpty()) {
            return false;
        }
        
        String cleanCVV = cvv.trim();
        return CVV_PATTERN.matcher(cleanCVV).matches();
    }
    
    /**
     * Validates card expiry date (MM/YY format)
     */
    public static boolean isValidCardExpiry(String expiry) {
        if (expiry == null || expiry.trim().isEmpty()) {
            return false;
        }
        
        String cleanExpiry = expiry.trim();
        if (!CARD_EXPIRY_PATTERN.matcher(cleanExpiry).matches()) {
            return false;
        }
        
        // Check if expiry date is not in the past
        try {
            String[] parts = cleanExpiry.split("/");
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]) + 2000; // Convert YY to YYYY
            
            java.util.Calendar now = java.util.Calendar.getInstance();
            int currentYear = now.get(java.util.Calendar.YEAR);
            int currentMonth = now.get(java.util.Calendar.MONTH) + 1;
            
            if (year < currentYear || (year == currentYear && month < currentMonth)) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Validates Australian state
     */
    public static boolean isValidAustralianState(String state) {
        if (state == null || state.trim().isEmpty()) {
            return false;
        }
        
        String cleanState = state.trim();
        for (String validState : AUSTRALIAN_STATES) {
            if (validState.equalsIgnoreCase(cleanState)) {
                return true;
            }
        }
        
        for (String stateCode : AUSTRALIAN_STATE_CODES) {
            if (stateCode.equalsIgnoreCase(cleanState)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Gets state code from full state name
     */
    public static String getStateCode(String stateName) {
        if (stateName == null || stateName.trim().isEmpty()) {
            return "";
        }
        
        String cleanState = stateName.trim();
        for (int i = 0; i < AUSTRALIAN_STATES.length; i++) {
            if (AUSTRALIAN_STATES[i].equalsIgnoreCase(cleanState)) {
                return AUSTRALIAN_STATE_CODES[i];
            }
        }
        
        return cleanState.toUpperCase();
    }
    
    /**
     * Gets full state name from state code
     */
    public static String getFullStateName(String stateCode) {
        if (stateCode == null || stateCode.trim().isEmpty()) {
            return "";
        }
        
        String cleanCode = stateCode.trim().toUpperCase();
        for (int i = 0; i < AUSTRALIAN_STATE_CODES.length; i++) {
            if (AUSTRALIAN_STATE_CODES[i].equals(cleanCode)) {
                return AUSTRALIAN_STATES[i];
            }
        }
        
        return cleanCode;
    }
}
