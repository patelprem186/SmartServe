# Australian Validation Guide

This guide documents the comprehensive Australian validation system implemented for the EasyBook app, designed specifically for Australian users.

## Features Implemented

### 1. Australian Mobile Number Validation
- **Supports multiple formats**: +61412345678, 0412345678, 412345678
- **Validates Australian mobile patterns**: Must start with 4 (after country code)
- **Auto-formatting**: Converts to standard +61 format
- **Used in**: PhoneAuthActivity, RegisterActivity

### 2. Australian Postcode Validation
- **4-digit validation**: Exactly 4 digits required
- **Format**: 2000, 3000, 4000, etc.
- **Real-time validation**: Immediate feedback on input

### 3. Australian Address System
- **8 States/Territories**: NSW, VIC, QLD, WA, SA, TAS, ACT, NT
- **City dropdowns**: 15+ major cities per state
- **State-City validation**: Ensures city belongs to selected state
- **Formatted addresses**: Automatic address formatting

### 4. Payment Card Validation
- **CVV validation**: 3-4 digits
- **Expiry date validation**: MM/YY format with future date check
- **Card number validation**: Luhn algorithm implementation
- **Auto-formatting**: Card number spacing, expiry date formatting

## Utility Classes

### AustralianValidationUtils.java
```java
// Mobile number validation
boolean isValid = AustralianValidationUtils.isValidAustralianMobile("+61412345678");
String formatted = AustralianValidationUtils.formatAustralianMobile("0412345678");

// Postcode validation
boolean isValid = AustralianValidationUtils.isValidAustralianPostcode("2000");

// CVV validation
boolean isValid = AustralianValidationUtils.isValidCVV("123");

// Card expiry validation
boolean isValid = AustralianValidationUtils.isValidCardExpiry("12/25");

// State validation
boolean isValid = AustralianValidationUtils.isValidAustralianState("New South Wales");
String stateCode = AustralianValidationUtils.getStateCode("New South Wales");
```

### AustralianAddressUtils.java
```java
// Get cities for a state
List<String> cities = AustralianAddressUtils.getCitiesForState("NSW");

// Validate city for state
boolean isValid = AustralianAddressUtils.isValidCityForState("Sydney", "NSW");

// Get all states
List<String> states = AustralianAddressUtils.getAllStates();
```

### AustralianAddressFormHelper.java
```java
// Initialize helper
AustralianAddressFormHelper helper = new AustralianAddressFormHelper(
    context, stateDropdown, cityDropdown, postcodeField
);

// Validate entire form
boolean isValid = helper.validateAddressForm();

// Get formatted address
String address = helper.getFormattedAddress("123 Main Street");
```

### AustralianPaymentFormHelper.java
```java
// Initialize helper
AustralianPaymentFormHelper helper = new AustralianPaymentFormHelper(
    context, cardNumberField, cardHolderField, expiryField, cvvField
);

// Validate entire form
boolean isValid = helper.validatePaymentForm();

// Get masked card number
String masked = helper.getMaskedCardNumber();
```

## Layout Files

### address_form_australian.xml
- Street address input
- State dropdown (8 Australian states)
- City dropdown (populated based on state)
- Postcode input (4-digit validation)
- Country field (fixed as Australia)

### payment_form_australian.xml
- Card number input with formatting
- Card holder name
- Expiry date (MM/YY format)
- CVV (3-4 digits)
- Includes address form

## Integration Examples

### 1. Phone Number Validation
```java
// In PhoneAuthActivity or RegisterActivity
if (selectedCountryCode.getCode().equals("AU")) {
    if (AustralianValidationUtils.isValidAustralianMobile(phoneNumber)) {
        // Valid Australian mobile
        String formatted = AustralianValidationUtils.formatAustralianMobile(phoneNumber);
    } else {
        // Show error
        binding.etPhone.setError("Please enter a valid Australian mobile number");
    }
}
```

### 2. Address Form Integration
```java
// Initialize address helper
AustralianAddressFormHelper addressHelper = new AustralianAddressFormHelper(
    this, actvState, actvCity, etPostcode
);

// Validate on submit
if (addressHelper.validateAddressForm()) {
    String formattedAddress = addressHelper.getFormattedAddress(streetAddress);
    // Proceed with submission
}
```

### 3. Payment Form Integration
```java
// Initialize payment helper
AustralianPaymentFormHelper paymentHelper = new AustralianPaymentFormHelper(
    this, etCardNumber, etCardHolder, etExpiry, etCVV
);

// Validate on submit
if (paymentHelper.validatePaymentForm()) {
    String maskedCard = paymentHelper.getMaskedCardNumber();
    // Proceed with payment
}
```

## Australian States and Cities

### New South Wales (NSW)
Sydney, Newcastle, Wollongong, Wagga Wagga, Albury, Maitland, Tamworth, Orange, Dubbo, Nowra, Bathurst, Lismore, Port Macquarie, Coffs Harbour, Broken Hill

### Victoria (VIC)
Melbourne, Geelong, Ballarat, Bendigo, Shepparton, Warrnambool, Mildura, Sale, Traralgon, Hamilton, Colac, Echuca, Swan Hill, Wodonga, Frankston

### Queensland (QLD)
Brisbane, Gold Coast, Townsville, Cairns, Toowoomba, Rockhampton, Mackay, Bundaberg, Hervey Bay, Gladstone, Mount Isa, Maryborough, Gympie, Warwick, Emerald

### Western Australia (WA)
Perth, Fremantle, Rockingham, Mandurah, Bunbury, Geraldton, Albany, Broome, Kalgoorlie, Port Hedland, Busselton, Karratha, Esperance, Carnarvon, Kununurra

### South Australia (SA)
Adelaide, Mount Gambier, Whyalla, Murray Bridge, Port Augusta, Port Pirie, Port Lincoln, Gawler, Millicent, Kadina, Berri, Naracoorte, Roxby Downs, Victor Harbor, Wallaroo

### Tasmania (TAS)
Hobart, Launceston, Devonport, Burnie, Ulverstone, George Town, Queenstown, Scottsdale, Smithton, New Norfolk, Sorell, Kingston, Currie, Strahan, Zeehan

### Australian Capital Territory (ACT)
Canberra, Gungahlin, Tuggeranong, Weston Creek, Woden Valley

### Northern Territory (NT)
Darwin, Alice Springs, Katherine, Nhulunbuy, Tennant Creek, Palmerston

## Validation Rules

### Mobile Numbers
- **Format**: +61412345678, 0412345678, 412345678
- **Length**: 9 digits after country code
- **Pattern**: Must start with 4 (Australian mobile prefix)
- **Auto-format**: Converts to +61 format

### Postcodes
- **Format**: Exactly 4 digits
- **Range**: 1000-9999
- **Examples**: 2000 (Sydney), 3000 (Melbourne), 4000 (Brisbane)

### CVV
- **Length**: 3-4 digits
- **Format**: Numeric only
- **Examples**: 123, 1234

### Card Expiry
- **Format**: MM/YY
- **Validation**: Must be future date
- **Examples**: 12/25, 01/26

## Error Messages

### Mobile Number Errors
- "Please enter a valid Australian mobile number (e.g., 0412345678 or +61412345678)"

### Postcode Errors
- "Please enter a valid 4-digit Australian postcode"

### CVV Errors
- "Please enter a valid CVV (3-4 digits)"

### Card Expiry Errors
- "Please enter a valid expiry date (MM/YY)"

### Address Errors
- "Please select a state"
- "Please select a city"
- "Please select a valid city for [State]"

## Testing

Use the `AustralianFormExampleActivity` to test all validation features:

1. **Address Form**: Test state/city selection and postcode validation
2. **Payment Form**: Test card number, expiry, and CVV validation
3. **Mobile Validation**: Test various Australian mobile number formats
4. **Form Integration**: Test complete form submission with all validations

## Future Enhancements

1. **Postcode to State Mapping**: Automatically detect state from postcode
2. **Suburb Validation**: Add suburb/city validation within states
3. **Address Autocomplete**: Integration with Google Places API
4. **Payment Gateway**: Integration with Australian payment providers
5. **Tax Validation**: ABN validation for business users

## Support

For questions or issues with Australian validation:
- Check the example activity for implementation patterns
- Use the utility classes for consistent validation
- Test with the provided example layouts
- Refer to this documentation for validation rules
