package com.easy.easybook.utils;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class AustralianAddressFormHelper {
    
    private Context context;
    private AutoCompleteTextView stateDropdown;
    private AutoCompleteTextView cityDropdown;
    private TextView postcodeField;
    private String selectedState = "";
    
    public AustralianAddressFormHelper(Context context, AutoCompleteTextView stateDropdown, 
                                     AutoCompleteTextView cityDropdown, TextView postcodeField) {
        this.context = context;
        this.stateDropdown = stateDropdown;
        this.cityDropdown = cityDropdown;
        this.postcodeField = postcodeField;
        
        setupStateDropdown();
        setupCityDropdown();
    }
    
    private void setupStateDropdown() {
        List<String> states = AustralianAddressUtils.getAllStates();
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(context, 
            android.R.layout.simple_dropdown_item_1line, states);
        stateDropdown.setAdapter(stateAdapter);
        
        stateDropdown.setOnItemClickListener((parent, view, position, id) -> {
            selectedState = states.get(position);
            String stateCode = AustralianValidationUtils.getStateCode(selectedState);
            updateCityDropdown(stateCode);
            cityDropdown.setText(""); // Clear city selection
        });
    }
    
    private void setupCityDropdown() {
        // Initially empty, will be populated when state is selected
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(context, 
            android.R.layout.simple_dropdown_item_1line, new String[]{});
        cityDropdown.setAdapter(cityAdapter);
        
        cityDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCity = (String) parent.getItemAtPosition(position);
            // You can add any city-specific logic here
        });
    }
    
    private void updateCityDropdown(String stateCode) {
        List<String> cities = AustralianAddressUtils.getCitiesForState(stateCode);
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(context, 
            android.R.layout.simple_dropdown_item_1line, cities);
        cityDropdown.setAdapter(cityAdapter);
    }
    
    /**
     * Validates the entire address form
     */
    public boolean validateAddressForm() {
        boolean isValid = true;
        
        // Validate state
        if (selectedState.isEmpty()) {
            stateDropdown.setError("Please select a state");
            isValid = false;
        } else {
            stateDropdown.setError(null);
        }
        
        // Validate city
        String selectedCity = cityDropdown.getText().toString().trim();
        if (selectedCity.isEmpty()) {
            cityDropdown.setError("Please select a city");
            isValid = false;
        } else if (!selectedState.isEmpty() && !AustralianAddressUtils.isValidCityForState(selectedCity, 
                AustralianValidationUtils.getStateCode(selectedState))) {
            cityDropdown.setError("Please select a valid city for " + selectedState);
            isValid = false;
        } else {
            cityDropdown.setError(null);
        }
        
        // Validate postcode
        String postcode = postcodeField.getText().toString().trim();
        if (postcode.isEmpty()) {
            postcodeField.setError("Postcode is required");
            isValid = false;
        } else if (!AustralianValidationUtils.isValidAustralianPostcode(postcode)) {
            postcodeField.setError("Please enter a valid 4-digit Australian postcode");
            isValid = false;
        } else {
            postcodeField.setError(null);
        }
        
        return isValid;
    }
    
    /**
     * Gets the formatted address string
     */
    public String getFormattedAddress(String streetAddress) {
        if (streetAddress == null || streetAddress.trim().isEmpty()) {
            return "";
        }
        
        String city = cityDropdown.getText().toString().trim();
        String postcode = postcodeField.getText().toString().trim();
        
        StringBuilder address = new StringBuilder();
        address.append(streetAddress.trim());
        
        if (!city.isEmpty()) {
            address.append(", ").append(city);
        }
        
        if (!selectedState.isEmpty()) {
            address.append(", ").append(selectedState);
        }
        
        if (!postcode.isEmpty()) {
            address.append(" ").append(postcode);
        }
        
        address.append(", Australia");
        
        return address.toString();
    }
    
    /**
     * Gets the selected state
     */
    public String getSelectedState() {
        return selectedState;
    }
    
    /**
     * Gets the selected city
     */
    public String getSelectedCity() {
        return cityDropdown.getText().toString().trim();
    }
    
    /**
     * Gets the postcode
     */
    public String getPostcode() {
        return postcodeField.getText().toString().trim();
    }
    
    /**
     * Clears all form fields
     */
    public void clearForm() {
        stateDropdown.setText("");
        cityDropdown.setText("");
        postcodeField.setText("");
        selectedState = "";
    }
}
