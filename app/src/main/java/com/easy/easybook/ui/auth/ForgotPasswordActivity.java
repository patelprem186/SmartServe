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
import com.easy.easybook.databinding.ActivityForgotPasswordBinding;
import com.easy.easybook.network.ApiClient;
import com.easy.easybook.ui.customer.CustomerMainActivity;
import com.easy.easybook.ui.provider.ProviderDashboardActivity;
import com.easy.easybook.utils.SharedPrefsManager;
import com.easy.easybook.models.User;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.TimeUnit;

public class ForgotPasswordActivity extends AppCompatActivity {
    
    private ActivityForgotPasswordBinding binding;
    private FirebaseAuth mAuth;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private String phoneNumber;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        mAuth = FirebaseAuth.getInstance();
        setupUI();
    }
    
    private void setupUI() {
        // Back button
        binding.btnBack.setOnClickListener(v -> onBackPressed());
        
        // Send OTP button
        binding.btnSendOtp.setOnClickListener(v -> {
            String input = binding.etPhoneNumber.getText().toString().trim();
            if (isEmail(input)) {
                // Email password reset
                handleEmailPasswordReset(input);
            } else if (validatePhoneNumber(input)) {
                // Phone OTP reset
                phoneNumber = input;
                sendOTP(input);
            } else {
                binding.etPhoneNumber.setError("Please enter a valid email or 10-digit phone number");
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
            if (phoneNumber != null) {
                resendOTP(phoneNumber);
            }
        });
    }
    
    private boolean isEmail(String input) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches();
    }
    
    private boolean validatePhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            binding.etPhoneNumber.setError("Phone number is required");
            return false;
        }
        
        // Remove any non-digit characters
        String cleanNumber = phoneNumber.replaceAll("[^0-9]", "");
        
        // Check if it's a valid 10-digit number
        if (cleanNumber.length() != 10) {
            binding.etPhoneNumber.setError("Please enter a valid 10-digit phone number");
            return false;
        }
        
        return true;
    }
    
    private void handleEmailPasswordReset(String email) {
        // Call backend API to send password reset email
        sendPasswordResetEmailViaAPI(email);
    }
    
    private void sendPasswordResetEmailViaAPI(String email) {
        // Show loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSendOtp.setEnabled(false);

        // Get Firebase Auth instance
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Send password reset email using Firebase client-side with native template
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btnSendOtp.setEnabled(true);

                        if (task.isSuccessful()) {
                            showPasswordResetDialog(email, "Password reset email sent successfully! Check your email and click the reset link to change your password.");
                        } else {
                            // Handle Firebase errors
                            String errorMessage = "Failed to send password reset email";
                            if (task.getException() != null) {
                                String errorCode = task.getException().getMessage();
                                if (errorCode.contains("user-not-found")) {
                                    errorMessage = "No account found with this email address";
                                } else if (errorCode.contains("invalid-email")) {
                                    errorMessage = "Invalid email address";
                                } else if (errorCode.contains("too-many-requests")) {
                                    errorMessage = "Too many requests. Please try again later";
                                } else {
                                    errorMessage = "Error: " + task.getException().getMessage();
                                }
                            }
                            Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    
    private void showPasswordResetDialog(String email, String message) {
        // Show the password reset form directly
        showResetPasswordForm(email);
    }
    
    private void showResetPasswordForm(String email) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Password Reset Email Sent");
        builder.setMessage("We've sent a password reset link to " + email + ". Please check your email and click the link to reset your password.\n\nIf you don't see the email, check your spam folder.");
        
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Navigate back to login
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        
        builder.setNegativeButton("Send Again", (dialog, which) -> {
            // Send another reset email
            sendPasswordResetEmailViaAPI(email);
        });
        
        builder.show();
    }
    
    
    private void resetPassword(String token, String newPassword) {
        // Show loading
        binding.progressBar.setVisibility(View.VISIBLE);
        
        // Create reset password request
        com.easy.easybook.models.ResetPasswordRequest request = new com.easy.easybook.models.ResetPasswordRequest(token, newPassword);
        
        // Make API call
        ApiClient.getInstance(this).getApiService().resetPassword(request).enqueue(new retrofit2.Callback<com.easy.easybook.network.responses.ApiResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.easy.easybook.network.responses.ApiResponse> call, retrofit2.Response<com.easy.easybook.network.responses.ApiResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    com.easy.easybook.network.responses.ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Password reset successfully!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Failed to reset password. Please try again.", Toast.LENGTH_LONG).show();
                }
            }
            
            @Override
            public void onFailure(retrofit2.Call<com.easy.easybook.network.responses.ApiResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(ForgotPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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
        // Clean the phone number
        String cleanNumber = phoneNumber.replaceAll("[^0-9]", "");
        if (!cleanNumber.startsWith("+")) {
            cleanNumber = "+91" + cleanNumber;
        }
        
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
        
        String cleanNumber = phoneNumber.replaceAll("[^0-9]", "");
        if (!cleanNumber.startsWith("+")) {
            cleanNumber = "+91" + cleanNumber;
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
                Toast.makeText(ForgotPasswordActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            
            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // Code sent successfully
                binding.progressBar.setVisibility(View.GONE);
                binding.btnSendOtp.setEnabled(true);
                binding.btnResendOtp.setEnabled(true);
                
                ForgotPasswordActivity.this.verificationId = verificationId;
                ForgotPasswordActivity.this.resendToken = token;
                
                // Show OTP input section
                binding.layoutOtp.setVisibility(View.VISIBLE);
                binding.btnSendOtp.setVisibility(View.GONE);
                
                Toast.makeText(ForgotPasswordActivity.this, "OTP sent successfully!", Toast.LENGTH_SHORT).show();
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
                        // Sign in successful - this means the phone number is verified
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            handleSuccessfulVerification(user);
                        }
                    } else {
                        // Sign in failed
                        Log.e("PhoneAuth", "Sign in failed: " + task.getException().getMessage());
                        Toast.makeText(this, "Verification failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    
    private void handleSuccessfulVerification(FirebaseUser firebaseUser) {
        // For forgot password, we just need to verify the phone number
        // and then allow the user to set a new password or continue with phone auth
        
        // Create a basic user profile for the verified phone number
        User user = new User();
        user.setId(firebaseUser.getUid());
        user.setPhone(firebaseUser.getPhoneNumber());
        user.setFirstName("User");
        user.setLastName("");
        user.setEmail("");
        user.setRole("customer"); // Default to customer
        user.setVerified(true);
        user.setProfileImage("");
        
        // Save user to SharedPreferences
        SharedPrefsManager prefsManager = SharedPrefsManager.getInstance(this);
        prefsManager.saveUser(user);
        
        // Show success message and navigate
        Toast.makeText(this, "Phone number verified successfully! You can now continue.", Toast.LENGTH_LONG).show();
        
        // Navigate to customer dashboard
        Intent intent = new Intent(this, CustomerMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
