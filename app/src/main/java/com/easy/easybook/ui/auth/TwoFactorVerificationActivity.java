package com.easy.easybook.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.easy.easybook.databinding.ActivityTwoFactorVerificationBinding;
import com.easy.easybook.models.EmailVerificationRequest;
import com.easy.easybook.network.ApiClient;
import com.easy.easybook.network.responses.ApiResponse;
import com.easy.easybook.network.responses.AuthResponse;
import com.easy.easybook.utils.SharedPrefsManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Email Verification Activity
 * Handles email verification for user registration
 */
public class TwoFactorVerificationActivity extends AppCompatActivity {
    
    private ActivityTwoFactorVerificationBinding binding;
    private String email;
    private boolean isEmailVerification = false;
    private CountDownTimer countDownTimer;
    private boolean canResend = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTwoFactorVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Get data from intent
        if (getIntent().hasExtra("email") && getIntent().hasExtra("isEmailVerification")) {
            email = getIntent().getStringExtra("email");
            isEmailVerification = getIntent().getBooleanExtra("isEmailVerification", false);
        } else {
            Toast.makeText(this, "Invalid verification data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        setupUI();
        startResendTimer();
    }
    
    private void setupUI() {
        // Set email display
        binding.tvPhoneNumber.setText("Verification code sent to " + email);
        
        // Back button
        binding.btnBack.setOnClickListener(v -> onBackPressed());
        
        // Auto-focus and format OTP input
        setupOtpInput();
        
        // Verify button
        binding.btnVerify.setOnClickListener(v -> verifyEmailCode());
        
        // Resend button
        binding.btnResend.setOnClickListener(v -> {
            if (canResend) {
                resendVerificationCode();
            }
        });
    }
    
    private void setupOtpInput() {
        // Add text watcher for automatic verification when 6 digits are entered
        binding.etOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 6) {
                    binding.btnVerify.setEnabled(true);
                } else {
                    binding.btnVerify.setEnabled(false);
                }
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void verifyEmailCode() {
        String otp = binding.etOtp.getText().toString().trim();
        
        if (otp.length() != 6) {
            binding.etOtp.setError("Please enter 6-digit verification code");
            return;
        }
        
        // Show loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnVerify.setEnabled(false);
        
        // Create email verification request
        EmailVerificationRequest verificationRequest = new EmailVerificationRequest(email, otp);
        
        // Call backend email verification API
        Call<AuthResponse> call = ApiClient.getInstance(this).getApiService().verifyEmail(verificationRequest);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnVerify.setEnabled(true);
                
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    
                    if (authResponse.isSuccess() && authResponse.getData() != null) {
                        // Save user data and token
                        SharedPrefsManager prefsManager = SharedPrefsManager.getInstance(TwoFactorVerificationActivity.this);
                        prefsManager.saveAuthToken(authResponse.getData().getToken());
                        prefsManager.saveUser(authResponse.getData().getUser());
                        
                        Toast.makeText(TwoFactorVerificationActivity.this, 
                            "Email verification successful! Welcome to SmartServe.", Toast.LENGTH_SHORT).show();
                        
                        // Navigate to appropriate main activity
                        navigateToMainActivity(authResponse.getData().getUser().getRole());
                        
                    } else {
                        Toast.makeText(TwoFactorVerificationActivity.this, 
                            authResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(TwoFactorVerificationActivity.this, 
                        "Invalid verification code", Toast.LENGTH_LONG).show();
                }
            }
            
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnVerify.setEnabled(true);
                Toast.makeText(TwoFactorVerificationActivity.this, 
                    "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    
    
    private void navigateToMainActivity(String userRole) {
        Intent intent;
        
        if ("provider".equals(userRole)) {
            intent = new Intent(this, com.easy.easybook.ui.provider.ProviderMainActivity.class);
        } else {
            intent = new Intent(this, com.easy.easybook.ui.customer.CustomerMainActivity.class);
        }
        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void resendVerificationCode() {
        if (!canResend) return;
        
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnResend.setEnabled(false);
        
        // Create resend request
        EmailVerificationRequest resendRequest = new EmailVerificationRequest(email, "");
        
        // Call backend resend verification API
        Call<ApiResponse> call = ApiClient.getInstance(this).getApiService().resendVerification(resendRequest);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(TwoFactorVerificationActivity.this, 
                        "Verification code resent to your email", Toast.LENGTH_SHORT).show();
                    startResendTimer();
                } else {
                    Toast.makeText(TwoFactorVerificationActivity.this, 
                        "Failed to resend verification code", Toast.LENGTH_LONG).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(TwoFactorVerificationActivity.this, 
                    "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void startResendTimer() {
        canResend = false;
        binding.btnResend.setEnabled(false);
        
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.btnResend.setText("Resend in " + (millisUntilFinished / 1000) + "s");
            }
            
            @Override
            public void onFinish() {
                canResend = true;
                binding.btnResend.setEnabled(true);
                binding.btnResend.setText("Resend Code");
            }
        }.start();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        binding = null;
    }
}
