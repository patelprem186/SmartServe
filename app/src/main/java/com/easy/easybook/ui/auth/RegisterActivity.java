package com.easy.easybook.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.easy.easybook.R;
import com.easy.easybook.databinding.ActivityRegisterBinding;
import com.easy.easybook.network.ApiClient;
import com.easy.easybook.network.responses.AuthResponse;
import com.easy.easybook.utils.SharedPrefsManager;
import com.easy.easybook.utils.CountryCodeUtils;
import com.easy.easybook.data.ServiceManager;
import com.easy.easybook.models.User;
import com.easy.easybook.models.CountryCode;
import com.easy.easybook.utils.AustralianValidationUtils;
import com.easy.easybook.utils.AustralianAddressUtils;
import com.easy.easybook.utils.AustralianAddressFormHelper;
import java.util.List;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.auth.GetTokenResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Registration Activity for new users
 * Allows users to create accounts as customers or service providers
 */
public class RegisterActivity extends AppCompatActivity {
    
    private ActivityRegisterBinding binding;
    private boolean isServiceProvider = false;
    private FirebaseAuth mAuth;
    // Address form fields
    private AutoCompleteTextView actvState;
    private AutoCompleteTextView actvCity;
    private TextInputEditText etPostcode;
    private TextInputEditText etStreetAddress;
    
    // Address form helper
    private AustralianAddressFormHelper addressHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        mAuth = FirebaseAuth.getInstance();
        setupUI();
    }
    
    private void setupUI() {
        // Back button
        binding.btnBack.setOnClickListener(v -> onBackPressed());
        
        // User type toggle
        binding.chipCustomer.setOnClickListener(v -> {
            isServiceProvider = false;
            binding.chipCustomer.setChecked(true);
            binding.chipProvider.setChecked(false);
            toggleProviderFields(false);
            setupAddressForm();
        });
        
        binding.chipProvider.setOnClickListener(v -> {
            isServiceProvider = true;
            binding.chipProvider.setChecked(true);
            binding.chipCustomer.setChecked(false);
            toggleProviderFields(true);
            setupServiceCategoryDropdown();
            setupAddressForm();
        });
        
        // Register button
        binding.btnRegister.setOnClickListener(v -> {
            if (validateInputs()) {
                showRegistrationOptions();
            }
        });
        
        // Login redirect
        binding.tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
        
        // Setup Australian dropdowns (same as BookingsActivity)
        setupAustralianDropdowns();
    }
    
    private void toggleProviderFields(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        binding.tilServiceCategory.setVisibility(visibility);
        binding.tilExperience.setVisibility(visibility);
        binding.tilDescription.setVisibility(visibility);
    }
    
    private void setupServiceCategoryDropdown() {
        AutoCompleteTextView categoryDropdown = binding.etServiceCategory;
        
        // Use centralized ServiceManager to get all available categories
        ServiceManager serviceManager = ServiceManager.getInstance(this);
        java.util.List<String> categoriesList = serviceManager.getAllCategories();
        
        // Sort categories alphabetically for better UX
        categoriesList.sort(String::compareToIgnoreCase);
        
        String[] categories = categoriesList.toArray(new String[0]);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        categoryDropdown.setAdapter(adapter);
        
        categoryDropdown.setOnClickListener(v -> categoryDropdown.showDropDown());
    }
    
    private void setupAddressForm() {
        // Initialize address form fields using findViewById since they're not in binding
        actvState = findViewById(R.id.actv_state);
        actvCity = findViewById(R.id.actv_city);
        etPostcode = findViewById(R.id.et_postcode);
        etStreetAddress = findViewById(R.id.et_street_address);
        
        Log.d("RegisterActivity", "Address form fields initialized");
        
        // Initialize address form helper (this will setup the dropdowns)
        addressHelper = new AustralianAddressFormHelper(
            this, actvState, actvCity, etPostcode
        );
        
        Log.d("RegisterActivity", "Address form helper initialized");
    }
    
    private void setupAustralianDropdowns() {
        // Initialize address form fields first
        actvState = findViewById(R.id.actv_state);
        actvCity = findViewById(R.id.actv_city);
        etPostcode = findViewById(R.id.et_postcode);
        etStreetAddress = findViewById(R.id.et_street_address);
        
        Log.d("RegisterActivity", "Address form fields initialized for dropdowns");
        
        // Setup state dropdown (same as BookingsActivity)
        setupStateDropdown();
    }
    
    private void setupStateDropdown() {
        // Get Australian states
        List<String> states = AustralianAddressUtils.getAllStates();
        Log.d("RegisterActivity", "Setting up state dropdown with " + states.size() + " states");
        
        // Create adapter for state dropdown
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, states);
        actvState.setAdapter(stateAdapter);
        
        // Handle state selection
        actvState.setOnItemClickListener((parent, view, position, id) -> {
            String selectedState = states.get(position);
            Log.d("RegisterActivity", "State selected: " + selectedState);
            String stateCode = AustralianValidationUtils.getStateCode(selectedState);
            Log.d("RegisterActivity", "State code: " + stateCode);
            setupCityDropdown(stateCode);
            actvCity.setText(""); // Clear city selection
        });
        
        // Show dropdown when clicked
        actvState.setOnClickListener(v -> {
            Log.d("RegisterActivity", "State dropdown clicked");
            actvState.showDropDown();
        });
    }
    
    private void setupCityDropdown(String stateCode) {
        // Get cities for the selected state
        List<String> cities = AustralianAddressUtils.getCitiesForState(stateCode);
        Log.d("RegisterActivity", "Setting up city dropdown for " + stateCode + " with " + cities.size() + " cities");
        
        // Create adapter for city dropdown
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, cities);
        actvCity.setAdapter(cityAdapter);
        
        // Show dropdown when clicked
        actvCity.setOnClickListener(v -> {
            Log.d("RegisterActivity", "City dropdown clicked");
            actvCity.showDropDown();
        });
    }
    
    private void handleRegister() {
        String fullName = binding.etFullName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
        
        // Basic validation
        if (TextUtils.isEmpty(fullName)) {
            binding.etFullName.setError("Full name is required");
            return;
        }
        
        if (TextUtils.isEmpty(email)) {
            binding.etEmail.setError("Email is required");
            return;
        }
        
        if (TextUtils.isEmpty(phone)) {
            binding.etPhone.setError("Phone number is required");
            return;
        }
        
        // Validate phone number with country code
        if (!validatePhoneNumber(phone)) {
            return;
        }
        
        // Validate address form
        if (!addressHelper.validateAddressForm()) {
            Toast.makeText(this, "Please fill in all address fields correctly", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (TextUtils.isEmpty(password)) {
            binding.etPassword.setError("Password is required");
            return;
        }
        
        if (password.length() < 6) {
            binding.etPassword.setError("Password must be at least 6 characters");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            binding.etConfirmPassword.setError("Passwords do not match");
            return;
        }
        
        // Provider-specific validation
        if (isServiceProvider) {
            String category = binding.etServiceCategory.getText().toString().trim();
            String experience = binding.etExperience.getText().toString().trim();
            
            if (TextUtils.isEmpty(category)) {
                binding.etServiceCategory.setError("Service category is required");
                return;
            }
            
            if (TextUtils.isEmpty(experience)) {
                binding.etExperience.setError("Experience is required");
                return;
            }
        }
        
        // Show loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnRegister.setEnabled(false);
        
        // Start registration process with email verification
        startRegistrationWithEmailVerification(fullName, email, phone, password);
    }
    
    private boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            binding.etPhone.setError("Phone number is required");
            return false;
        }
        
        // Validate Australian mobile number
        if (AustralianValidationUtils.isValidAustralianMobile(phoneNumber)) {
            binding.etPhone.setError(null);
            return true;
        } else {
            binding.etPhone.setError("Please enter a valid Australian mobile number (e.g., 0412345678)");
            return false;
        }
    }
    
    private String formatPhoneNumberWithCountryCode(String phoneNumber) {
        // Format as Australian mobile number
        return AustralianValidationUtils.formatAustralianMobile(phoneNumber);
    }
    
    private void startRegistrationWithEmailVerification(String fullName, String email, String phone, String password) {
        // Split full name into first and last name
        String[] names = fullName.trim().split("\\s+", 2);
        String firstName = names[0];
        String lastName = names.length > 1 ? names[1] : "";
        
        // Use Firebase Auth to create user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                    @Override
                    public void onComplete(Task<com.google.firebase.auth.AuthResult> task) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btnRegister.setEnabled(true);
                        
                        if (task.isSuccessful()) {
                            // Firebase user created successfully
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                // Update user profile
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(fullName)
                                        .build();
                                
                                firebaseUser.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // Send email verification
                                                    firebaseUser.sendEmailVerification()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        // Create user object and save locally
                                                                        createAndSaveUser(firebaseUser, firstName, lastName, phone);
                                                                        
                                                                        Toast.makeText(RegisterActivity.this, 
                                                                            "Registration successful! Please check your email for verification.", 
                                                                            Toast.LENGTH_LONG).show();
                                                                    } else {
                                                                        // Still create user even if email verification fails
                                                                        createAndSaveUser(firebaseUser, firstName, lastName, phone);
                                                                        Toast.makeText(RegisterActivity.this, 
                                                                            "Registration successful! Email verification failed to send.", 
                                                                            Toast.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    // Still proceed with registration
                                                    createAndSaveUser(firebaseUser, firstName, lastName, phone);
                                                    Toast.makeText(RegisterActivity.this, 
                                                        "Registration successful!", 
                                                        Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            // Handle Firebase Auth errors
                            String errorMessage = "Registration failed";
                            if (task.getException() != null) {
                                String errorCode = task.getException().getMessage();
                                if (errorCode.contains("email-already-in-use")) {
                                    errorMessage = "An account with this email already exists";
                                } else if (errorCode.contains("invalid-email")) {
                                    errorMessage = "Invalid email address";
                                } else if (errorCode.contains("weak-password")) {
                                    errorMessage = "Password is too weak";
                                } else if (errorCode.contains("too-many-requests")) {
                                    errorMessage = "Too many requests. Please try again later";
                                } else {
                                    errorMessage = "Error: " + task.getException().getMessage();
                                }
                            }
                            Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    
    private void createAndSaveUser(FirebaseUser firebaseUser, String firstName, String lastName, String phone) {
        // Format phone number with country code
        String formattedPhone = formatPhoneNumberWithCountryCode(phone);
        
        // Get address information
        String streetAddress = etStreetAddress.getText().toString().trim();
        String formattedAddress = addressHelper.getFormattedAddress(streetAddress);
        
        // Get Firebase ID token
        firebaseUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    String idToken = task.getResult().getToken();
                    // Call backend to verify token and create user
                    verifyTokenWithBackend(idToken, formattedPhone, formattedAddress);
                } else {
                    Toast.makeText(RegisterActivity.this, "Failed to get authentication token", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    
    private void verifyTokenWithBackend(String idToken, String formattedPhone, String formattedAddress) {
        // Create request body
        java.util.Map<String, String> requestBody = new java.util.HashMap<>();
        requestBody.put("idToken", idToken);
        
        // Call backend to verify Firebase token
        retrofit2.Call<com.easy.easybook.network.responses.AuthResponse> call = 
            ApiClient.getInstance(this).getApiService().verifyFirebaseToken(requestBody);
        
        call.enqueue(new retrofit2.Callback<com.easy.easybook.network.responses.AuthResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.easy.easybook.network.responses.AuthResponse> call, 
                                 retrofit2.Response<com.easy.easybook.network.responses.AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.easy.easybook.network.responses.AuthResponse authResponse = response.body();
                    
                    if (authResponse.isSuccess() && authResponse.getData() != null) {
                        // Save user data and token
                        SharedPrefsManager prefsManager = SharedPrefsManager.getInstance(RegisterActivity.this);
                        prefsManager.saveAuthToken(authResponse.getData().getToken());
                        prefsManager.saveUser(authResponse.getData().getUser());
                        
                        // Navigate based on user role
                        navigateToMainActivity(authResponse.getData().getUser().getRole());
                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, authResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Failed to verify token with backend", Toast.LENGTH_LONG).show();
                }
            }
            
            @Override
            public void onFailure(retrofit2.Call<com.easy.easybook.network.responses.AuthResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void navigateToMainActivity(String userRole) {
        Intent intent;
        
        SharedPrefsManager prefsManager = SharedPrefsManager.getInstance(this);
        
        if ("provider".equals(userRole)) {
            // Pass service category to ProviderLoginActivity for completion
            Intent providerIntent = new Intent(this, com.easy.easybook.ui.provider.ProviderLoginActivity.class);
            String category = binding.etServiceCategory.getText().toString().trim();
            providerIntent.putExtra("preselected_category", category);
            intent = providerIntent;
        } else {
            intent = new Intent(this, com.easy.easybook.ui.customer.CustomerMainActivity.class);
        }
        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private boolean validateInputs() {
        boolean isValid = true;
        
        // Validate full name
        if (TextUtils.isEmpty(binding.etFullName.getText().toString().trim())) {
            binding.etFullName.setError("Full name is required");
            isValid = false;
        }
        
        // Validate email
        String email = binding.etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            binding.etEmail.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError("Please enter a valid email address");
            isValid = false;
        }
        
        // Validate phone number (10 digits)
        String phone = binding.etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            binding.etPhone.setError("Phone number is required");
            isValid = false;
        } else if (phone.length() != 10 || !phone.matches("\\d{10}")) {
            binding.etPhone.setError("Please enter a valid 10-digit phone number");
            isValid = false;
        }
        
        // Validate password
        String password = binding.etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            binding.etPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            binding.etPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }
        
        // Validate confirm password
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
        if (TextUtils.isEmpty(confirmPassword)) {
            binding.etConfirmPassword.setError("Please confirm your password");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            binding.etConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }
        
        // Validate service provider fields
        if (isServiceProvider) {
            if (TextUtils.isEmpty(binding.etServiceCategory.getText().toString().trim())) {
                binding.etServiceCategory.setError("Service category is required");
                isValid = false;
            }
        }
        
        return isValid;
    }
    
    private void showRegistrationOptions() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Choose Registration Method");
        builder.setMessage("How would you like to register?");
        
        builder.setPositiveButton("Email & Password", (dialog, which) -> {
            // Use Firebase Auth registration
            String fullName = binding.etFullName.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String phone = binding.etPhone.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            
            if (validateInputs()) {
                startRegistrationWithEmailVerification(fullName, email, phone, password);
            }
        });
        
        builder.setNeutralButton("Phone Number (OTP)", (dialog, which) -> {
            navigateToPhoneAuth();
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    
    private void navigateToPhoneAuth() {
        Intent intent = new Intent(this, PhoneAuthActivity.class);
        intent.putExtra("is_registration", true);
        intent.putExtra("user_type", isServiceProvider ? "provider" : "customer");
        intent.putExtra("user_name", binding.etFullName.getText().toString().trim());
        
        if (isServiceProvider) {
            intent.putExtra("service_category", binding.etServiceCategory.getText().toString().trim());
        }
        
        startActivity(intent);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
