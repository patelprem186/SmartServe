package com.easy.easybook.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.easy.easybook.R;
import com.easy.easybook.databinding.ActivityPhoneAuthBinding;
import com.easy.easybook.ui.customer.CustomerMainActivity;
import com.easy.easybook.ui.provider.ProviderDashboardActivity;
import com.easy.easybook.utils.SharedPrefsManager;
import com.easy.easybook.utils.CountryCodeUtils;
import com.easy.easybook.models.User;
import com.easy.easybook.models.CountryCode;
import com.easy.easybook.utils.AustralianValidationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import java.util.List;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {
    
    private ActivityPhoneAuthBinding binding;
    private FirebaseAuth mAuth;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private boolean isRegistration = false;
    private String userType = "customer";
    private String serviceCategory = "";
    private String userName = "";
    private CountryCode selectedCountryCode;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        mAuth = FirebaseAuth.getInstance();
        
        // Get intent data
        isRegistration = getIntent().getBooleanExtra("is_registration", false);
        userType = getIntent().getStringExtra("user_type");
        serviceCategory = getIntent().getStringExtra("service_category");
        userName = getIntent().getStringExtra("user_name");
        String phoneNumber = getIntent().getStringExtra("phone_number");
        
        setupUI();
        setupCountryCodeDropdown();
        
        // Pre-fill phone number if provided
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            binding.etPhoneNumber.setText(phoneNumber);
        }
    }
    
    private void setupUI() {
        // Back button
        binding.btnBack.setOnClickListener(v -> onBackPressed());
        
        // Send OTP button
        binding.btnSendOtp.setOnClickListener(v -> {
            String phoneNumber = binding.etPhoneNumber.getText().toString().trim();
            if (validatePhoneNumber(phoneNumber)) {
                sendOTP(phoneNumber);
            }
        });
        
        // Verify OTP button
        binding.btnVerifyOtp.setOnClickListener(v -> {
            String otp = binding.etOtp.getText().toString().trim();
            if (validateOTP(otp)) {
                verifyOTP(otp);
            }
        });
        
        // Resend OTP button
        binding.btnResendOtp.setOnClickListener(v -> {
            String phoneNumber = binding.etPhoneNumber.getText().toString().trim();
            if (validatePhoneNumber(phoneNumber)) {
                resendOTP(phoneNumber);
            }
        });
        
        // Update UI based on registration or login
        if (isRegistration) {
            binding.tvTitle.setText("Verify Your Phone Number");
            binding.tvSubtitle.setText("We'll send you a verification code to complete your registration");
        } else {
            binding.tvTitle.setText("Sign In with Phone");
            binding.tvSubtitle.setText("Enter your phone number to receive a verification code");
        }
    }
    
    private void setupCountryCodeDropdown() {
        // Get country codes
        List<CountryCode> countryCodes = CountryCodeUtils.getCountryCodes();
        
        // Create adapter
        ArrayAdapter<CountryCode> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, countryCodes);
        
        // Setup AutoCompleteTextView
        AutoCompleteTextView countryCodeDropdown = binding.actvCountryCode;
        countryCodeDropdown.setAdapter(adapter);
        countryCodeDropdown.setThreshold(1);
        
        // Set default country (India)
        selectedCountryCode = CountryCodeUtils.getDefaultCountry();
        countryCodeDropdown.setText(selectedCountryCode.toString());
        
        // Handle country selection
        countryCodeDropdown.setOnItemClickListener((parent, view, position, id) -> {
            selectedCountryCode = (CountryCode) parent.getItemAtPosition(position);
            Log.d("PhoneAuth", "Selected country: " + selectedCountryCode.getName() + " " + selectedCountryCode.getDialCode());
        });
    }
    
    private boolean validatePhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            binding.etPhoneNumber.setError("Phone number is required");
            return false;
        }
        
        if (selectedCountryCode == null) {
            binding.etPhoneNumber.setError("Please select a country code");
            return false;
        }
        
        // Remove spaces and special characters except +
        String cleanNumber = phoneNumber.replaceAll("[^0-9]", "");
        
        // Validate based on country
        boolean isValid = false;
        String errorMessage = "";
        
        switch (selectedCountryCode.getCode()) {
            case "AU": // Australia
                if (AustralianValidationUtils.isValidAustralianMobile(phoneNumber)) {
                    isValid = true;
                } else {
                    errorMessage = "Please enter a valid Australian mobile number (e.g., 0412345678 or +61412345678)";
                }
                break;
            case "IN": // India
                if (cleanNumber.length() == 10) {
                    isValid = true;
                } else {
                    errorMessage = "Please enter a valid 10-digit Indian phone number";
                }
                break;
            case "US": // United States
                if (cleanNumber.length() == 10) {
                    isValid = true;
                } else {
                    errorMessage = "Please enter a valid 10-digit US phone number";
                }
                break;
            case "GB": // United Kingdom
                if (cleanNumber.length() >= 10 && cleanNumber.length() <= 11) {
                    isValid = true;
                } else {
                    errorMessage = "Please enter a valid UK phone number (10-11 digits)";
                }
                break;
            default:
                // For other countries, just check if it's not empty and has reasonable length
                if (cleanNumber.length() >= 7 && cleanNumber.length() <= 15) {
                    isValid = true;
                } else {
                    errorMessage = "Please enter a valid phone number for " + selectedCountryCode.getName();
                }
                break;
        }
        
        if (!isValid) {
            binding.etPhoneNumber.setError(errorMessage);
            return false;
        }
        
        Log.d("PhoneAuth", "Validated phone number: " + cleanNumber + " for country: " + selectedCountryCode.getName());
        
        return true;
    }
    
    private boolean validateOTP(String otp) {
        if (TextUtils.isEmpty(otp)) {
            binding.etOtp.setError("OTP is required");
            return false;
        }
        
        if (otp.length() != 6) {
            binding.etOtp.setError("Please enter a valid 6-digit OTP");
            return false;
        }
        
        return true;
    }
    
    private void sendOTP(String phoneNumber) {
        // Format phone number with country code
        String cleanNumber;
        if (selectedCountryCode != null && selectedCountryCode.getCode().equals("AU")) {
            // Use Australian formatting
            cleanNumber = AustralianValidationUtils.formatAustralianMobile(phoneNumber);
        } else {
            // Clean the phone number for other countries
            cleanNumber = phoneNumber.replaceAll("[^0-9]", "");
            
            // Add country code prefix
            if (selectedCountryCode != null) {
                cleanNumber = selectedCountryCode.getDialCode() + cleanNumber;
            } else {
                cleanNumber = "+91" + cleanNumber; // Default to India
            }
        }
        
        Log.d("PhoneAuth", "Sending OTP to: " + cleanNumber);
        Log.d("PhoneAuth", "App package: " + getPackageName());
        Log.d("PhoneAuth", "Firebase project: " + FirebaseApp.getInstance().getOptions().getProjectId());
        
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSendOtp.setEnabled(false);
        
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(cleanNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(phoneAuthCallbacks)
                .build();
        
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    
    private void resendOTP(String phoneNumber) {
        if (resendToken == null) {
            Toast.makeText(this, "Please send OTP first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Format phone number with country code
        String cleanNumber;
        if (selectedCountryCode != null && selectedCountryCode.getCode().equals("AU")) {
            // Use Australian formatting
            cleanNumber = AustralianValidationUtils.formatAustralianMobile(phoneNumber);
        } else {
            // Clean the phone number for other countries
            cleanNumber = phoneNumber.replaceAll("[^0-9]", "");
            
            // Add country code prefix
            if (selectedCountryCode != null) {
                cleanNumber = selectedCountryCode.getDialCode() + cleanNumber;
            } else {
                cleanNumber = "+91" + cleanNumber; // Default to India
            }
        }
        
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnResendOtp.setEnabled(false);
        
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(cleanNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(phoneAuthCallbacks)
                .setForceResendingToken(resendToken)
                .build();
        
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneAuthCallbacks = 
        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // Auto-verification completed
                binding.progressBar.setVisibility(View.GONE);
                binding.btnSendOtp.setEnabled(true);
                binding.btnResendOtp.setEnabled(true);
                
                // Automatically sign in
                signInWithPhoneAuthCredential(credential);
            }
            
            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // Verification failed
                binding.progressBar.setVisibility(View.GONE);
                binding.btnSendOtp.setEnabled(true);
                binding.btnResendOtp.setEnabled(true);
                
                Log.e("PhoneAuth", "Verification failed: " + e.getMessage());
                Log.e("PhoneAuth", "Full error: " + e.toString());
                
                // Handle billing error specifically
                if (e.getMessage().contains("BILLING_NOT_ENABLED")) {
                    Toast.makeText(PhoneAuthActivity.this, 
                        "Firebase billing not enabled. Please enable billing in Firebase Console for phone authentication.", 
                        Toast.LENGTH_LONG).show();
                } else if (e.getMessage().contains("Invalid app info")) {
                    Toast.makeText(PhoneAuthActivity.this, 
                        "App configuration issue. Please check SHA-1/SHA-256 fingerprints and package name in Firebase Console.", 
                        Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(PhoneAuthActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            
            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // Code sent successfully
                binding.progressBar.setVisibility(View.GONE);
                binding.btnSendOtp.setEnabled(true);
                binding.btnResendOtp.setEnabled(true);
                
                PhoneAuthActivity.this.verificationId = verificationId;
                PhoneAuthActivity.this.resendToken = token;
                
                // Show OTP input section
                binding.layoutOtp.setVisibility(View.VISIBLE);
                binding.btnSendOtp.setVisibility(View.GONE);
                
                Toast.makeText(PhoneAuthActivity.this, "OTP sent successfully!", Toast.LENGTH_SHORT).show();
            }
        };
    
    private void verifyOTP(String otp) {
        if (verificationId == null) {
            Toast.makeText(this, "Please send OTP first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnVerifyOtp.setEnabled(false);
        
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithPhoneAuthCredential(credential);
    }
    
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnVerifyOtp.setEnabled(true);
                    
                    if (task.isSuccessful()) {
                        // Sign in successful
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            handleSuccessfulAuth(user);
                        }
                    } else {
                        // Sign in failed
                        Log.e("PhoneAuth", "Sign in failed: " + task.getException().getMessage());
                        Toast.makeText(this, "Verification failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    
    private void handleSuccessfulAuth(FirebaseUser firebaseUser) {
        if (isRegistration) {
            // Create user profile for registration
            createUserProfile(firebaseUser);
        } else {
            // Load existing user for login
            loadUserProfile(firebaseUser);
        }
    }
    
    private void createUserProfile(FirebaseUser firebaseUser) {
        // Create user object
        User user = new User();
        user.setId(firebaseUser.getUid());
        user.setPhone(firebaseUser.getPhoneNumber());
        user.setFirstName(userName);
        user.setLastName("");
        user.setEmail(""); // Phone auth doesn't provide email
        user.setRole(userType);
        user.setVerified(true);
        user.setProfileImage("");
        
        if (userType.equals("provider") && !TextUtils.isEmpty(serviceCategory)) {
            user.setServiceCategory(serviceCategory);
        }
        
        // Save user to SharedPreferences
        SharedPrefsManager prefsManager = SharedPrefsManager.getInstance(this);
        prefsManager.saveUser(user);
        
        // Navigate to appropriate activity
        navigateToMainActivity();
    }
    
    private void loadUserProfile(FirebaseUser firebaseUser) {
        // For login, we'll create a basic user profile
        // In a real app, you'd fetch this from your backend
        User user = new User();
        user.setId(firebaseUser.getUid());
        user.setPhone(firebaseUser.getPhoneNumber());
        user.setFirstName("User");
        user.setLastName("");
        user.setEmail("");
        user.setRole("customer"); // Default to customer for login
        user.setVerified(true);
        user.setProfileImage("");
        
        // Save user to SharedPreferences
        SharedPrefsManager prefsManager = SharedPrefsManager.getInstance(this);
        prefsManager.saveUser(user);
        
        // Navigate to appropriate activity
        navigateToMainActivity();
    }
    
    private void navigateToMainActivity() {
        Intent intent;
        if (userType.equals("provider")) {
            intent = new Intent(this, ProviderDashboardActivity.class);
            intent.putExtra("provider_category", serviceCategory);
        } else {
            intent = new Intent(this, CustomerMainActivity.class);
        }
        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
