package com.easy.easybook.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

public class AustralianPaymentFormHelper {
    
    private Context context;
    private TextView cardNumberField;
    private TextView cardHolderNameField;
    private TextView expiryDateField;
    private TextView cvvField;
    
    public AustralianPaymentFormHelper(Context context, TextView cardNumberField, 
                                     TextView cardHolderNameField, TextView expiryDateField, 
                                     TextView cvvField) {
        this.context = context;
        this.cardNumberField = cardNumberField;
        this.cardHolderNameField = cardHolderNameField;
        this.expiryDateField = expiryDateField;
        this.cvvField = cvvField;
    }
    
    /**
     * Validates the entire payment form
     */
    public boolean validatePaymentForm() {
        boolean isValid = true;
        
        // Validate card number
        String cardNumber = cardNumberField.getText().toString().trim();
        if (TextUtils.isEmpty(cardNumber)) {
            cardNumberField.setError("Card number is required");
            isValid = false;
        } else if (!isValidCardNumber(cardNumber)) {
            cardNumberField.setError("Please enter a valid card number");
            isValid = false;
        } else {
            cardNumberField.setError(null);
        }
        
        // Validate card holder name
        String cardHolderName = cardHolderNameField.getText().toString().trim();
        if (TextUtils.isEmpty(cardHolderName)) {
            cardHolderNameField.setError("Card holder name is required");
            isValid = false;
        } else if (cardHolderName.length() < 2) {
            cardHolderNameField.setError("Card holder name must be at least 2 characters");
            isValid = false;
        } else {
            cardHolderNameField.setError(null);
        }
        
        // Validate expiry date
        String expiryDate = expiryDateField.getText().toString().trim();
        if (TextUtils.isEmpty(expiryDate)) {
            expiryDateField.setError("Expiry date is required");
            isValid = false;
        } else if (!AustralianValidationUtils.isValidCardExpiry(expiryDate)) {
            expiryDateField.setError("Please enter a valid expiry date (MM/YY)");
            isValid = false;
        } else {
            expiryDateField.setError(null);
        }
        
        // Validate CVV
        String cvv = cvvField.getText().toString().trim();
        if (TextUtils.isEmpty(cvv)) {
            cvvField.setError("CVV is required");
            isValid = false;
        } else if (!AustralianValidationUtils.isValidCVV(cvv)) {
            cvvField.setError("Please enter a valid CVV (3-4 digits)");
            isValid = false;
        } else {
            cvvField.setError(null);
        }
        
        return isValid;
    }
    
    /**
     * Validates card number using Luhn algorithm
     */
    private boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            return false;
        }
        
        // Remove spaces and non-digits
        String cleanCardNumber = cardNumber.replaceAll("[^0-9]", "");
        
        // Check if it's a reasonable length (13-19 digits)
        if (cleanCardNumber.length() < 13 || cleanCardNumber.length() > 19) {
            return false;
        }
        
        // Apply Luhn algorithm
        return luhnCheck(cleanCardNumber);
    }
    
    /**
     * Luhn algorithm implementation
     */
    private boolean luhnCheck(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        
        // Process digits from right to left
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));
            
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }
            
            sum += digit;
            alternate = !alternate;
        }
        
        return (sum % 10) == 0;
    }
    
    /**
     * Formats card number with spaces
     */
    public void formatCardNumber() {
        String cardNumber = cardNumberField.getText().toString().trim();
        String cleanNumber = cardNumber.replaceAll("[^0-9]", "");
        
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < cleanNumber.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append(" ");
            }
            formatted.append(cleanNumber.charAt(i));
        }
        
        cardNumberField.setText(formatted.toString());
    }
    
    /**
     * Formats expiry date as MM/YY
     */
    public void formatExpiryDate() {
        String expiryDate = expiryDateField.getText().toString().trim();
        String cleanDate = expiryDate.replaceAll("[^0-9]", "");
        
        if (cleanDate.length() >= 2) {
            String month = cleanDate.substring(0, 2);
            String year = cleanDate.length() >= 4 ? cleanDate.substring(2, 4) : cleanDate.substring(2);
            
            // Validate month
            int monthInt = Integer.parseInt(month);
            if (monthInt < 1 || monthInt > 12) {
                expiryDateField.setError("Invalid month");
                return;
            }
            
            expiryDateField.setText(month + "/" + year);
        }
    }
    
    /**
     * Gets the formatted card number
     */
    public String getFormattedCardNumber() {
        return cardNumberField.getText().toString().trim();
    }
    
    /**
     * Gets the card holder name
     */
    public String getCardHolderName() {
        return cardHolderNameField.getText().toString().trim();
    }
    
    /**
     * Gets the expiry date
     */
    public String getExpiryDate() {
        return expiryDateField.getText().toString().trim();
    }
    
    /**
     * Gets the CVV
     */
    public String getCVV() {
        return cvvField.getText().toString().trim();
    }
    
    /**
     * Clears all form fields
     */
    public void clearForm() {
        cardNumberField.setText("");
        cardHolderNameField.setText("");
        expiryDateField.setText("");
        cvvField.setText("");
    }
    
    /**
     * Gets masked card number for display
     */
    public String getMaskedCardNumber() {
        String cardNumber = getFormattedCardNumber().replaceAll("[^0-9]", "");
        if (cardNumber.length() < 8) {
            return "****";
        }
        
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
