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
import com.easy.easybook.databinding.ActivityPasswordResetBinding;
import com.easy.easybook.network.ApiClient;
import com.easy.easybook.ui.customer.CustomerMainActivity;
import com.easy.easybook.ui.provider.ProviderDashboardActivity;
import com.easy.easybook.utils.SharedPrefsManager;
import com.easy.easybook.models.User;
import com.easy.easybook.models.ResetPasswordRequest;

public class PasswordResetActivity extends AppCompatActivity {
    
    private ActivityPasswordResetBinding binding;
    private String oobCode;
    private String email;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordResetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Get the reset parameters from the intent
        Intent intent = getIntent();
        if (intent != null) {
            oobCode = intent.getStringExtra("oobCode");
            email = intent.getStringExtra("email");
            
            // Handle Firebase reset URLs
            String data = intent.getDataString();
            if (data != null) {
                Log.d("PasswordReset", "Received URL: " + data);
                
                // Handle custom URL scheme (smartserve://reset?mode=resetPassword&oobCode=...&email=...)
                if (data.startsWith("smartserve://")) {
                    if (data.contains("oobCode=")) {
                        oobCode = data.substring(data.indexOf("oobCode=") + 8);
                        if (oobCode.contains("&")) {
                            oobCode = oobCode.substring(0, oobCode.indexOf("&"));
                        }
                    }
                    if (data.contains("email=")) {
                        email = data.substring(data.indexOf("email=") + 6);
                        if (email.contains("&")) {
                            email = email.substring(0, email.indexOf("&"));
                        }
                        // URL decode the email
                        try {
                            email = java.net.URLDecoder.decode(email, "UTF-8");
                        } catch (Exception e) {
                            Log.e("PasswordReset", "Error decoding email", e);
                        }
                    }
                }
                // Handle Firebase HTTPS URLs
                else if (data.contains("smartserve-57c5e.firebaseapp.com")) {
                    // Extract oobCode from Firebase URL
                    if (data.contains("oobCode=")) {
                        oobCode = data.substring(data.indexOf("oobCode=") + 8);
                        if (oobCode.contains("&")) {
                            oobCode = oobCode.substring(0, oobCode.indexOf("&"));
                        }
                    }
                    
                    // Extract email from Firebase URL
                    if (data.contains("email=")) {
                        email = data.substring(data.indexOf("email=") + 6);
                        if (email.contains("&")) {
                            email = email.substring(0, email.indexOf("&"));
                        }
                        // URL decode the email
                        try {
                            email = java.net.URLDecoder.decode(email, "UTF-8");
                        } catch (Exception e) {
                            Log.e("PasswordReset", "Error decoding email", e);
                        }
                    }
                    
                    // Handle Firebase action URLs (format: /__/auth/action?mode=resetPassword&oobCode=...&apiKey=...)
                    if (data.contains("/__/auth/action")) {
                        if (data.contains("mode=resetPassword")) {
                            // Extract oobCode from Firebase action URL
                            if (data.contains("oobCode=")) {
                                oobCode = data.substring(data.indexOf("oobCode=") + 8);
                                if (oobCode.contains("&")) {
                                    oobCode = oobCode.substring(0, oobCode.indexOf("&"));
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Log extracted parameters for debugging
        Log.d("PasswordReset", "Extracted oobCode: " + oobCode);
        Log.d("PasswordReset", "Extracted email: " + email);
        
        setupUI();
    }
    
    private void setupUI() {
        // Back button
        binding.btnBack.setOnClickListener(v -> onBackPressed());
        
        // Reset password button
        binding.btnResetPassword.setOnClickListener(v -> {
            String password = binding.etNewPassword.getText().toString().trim();
            String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
            
            if (validateInputs(password, confirmPassword)) {
                resetPassword(password);
            }
        });
        
        // Show email if available
        if (!TextUtils.isEmpty(email)) {
            binding.tvEmail.setText("Reset password for: " + email);
        }
        
        // Login click listener
        binding.tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(PasswordResetActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
    
    private boolean validateInputs(String password, String confirmPassword) {
        if (TextUtils.isEmpty(password)) {
            binding.etNewPassword.setError("Password is required");
            return false;
        }
        
        if (password.length() < 6) {
            binding.etNewPassword.setError("Password must be at least 6 characters");
            return false;
        }
        
        if (TextUtils.isEmpty(confirmPassword)) {
            binding.etConfirmPassword.setError("Please confirm your password");
            return false;
        }
        
        if (!password.equals(confirmPassword)) {
            binding.etConfirmPassword.setError("Passwords do not match");
            return false;
        }
        
        return true;
    }
    
    private void resetPassword(String newPassword) {
        if (TextUtils.isEmpty(oobCode) && TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Invalid reset link. Please request a new password reset.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        // Show loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnResetPassword.setEnabled(false);
        
        // Create reset password request
        ResetPasswordRequest request;
        if (!TextUtils.isEmpty(oobCode)) {
            // Use oobCode for Firebase reset
            request = new ResetPasswordRequest(oobCode, newPassword, true);
        } else {
            // Use email for direct reset
            request = ResetPasswordRequest.forEmail(email, newPassword);
        }
        
        // Make API call to backend
        ApiClient.getInstance(this).getApiService().resetPassword(request).enqueue(new retrofit2.Callback<com.easy.easybook.network.responses.ApiResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.easy.easybook.network.responses.ApiResponse> call, retrofit2.Response<com.easy.easybook.network.responses.ApiResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnResetPassword.setEnabled(true);
                
                if (response.isSuccessful() && response.body() != null) {
                    com.easy.easybook.network.responses.ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(PasswordResetActivity.this, "Password reset successfully!", Toast.LENGTH_LONG).show();
                        
                        // Navigate to login
                        Intent intent = new Intent(PasswordResetActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(PasswordResetActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    String errorMessage = "Failed to reset password";
                    if (response.code() == 400) {
                        errorMessage = "Invalid or expired reset link. Please request a new password reset.";
                    } else if (response.code() == 404) {
                        errorMessage = "No account found with this email address";
                    } else if (response.code() == 500) {
                        errorMessage = "Server error. Please try again later";
                    }
                    Toast.makeText(PasswordResetActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }
            
            @Override
            public void onFailure(retrofit2.Call<com.easy.easybook.network.responses.ApiResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnResetPassword.setEnabled(true);
                Toast.makeText(PasswordResetActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
