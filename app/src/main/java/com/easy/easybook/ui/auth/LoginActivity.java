package com.easy.easybook.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.easy.easybook.databinding.ActivityLoginBinding;
import com.easy.easybook.models.LoginRequest;
import com.easy.easybook.network.ApiClient;
import com.easy.easybook.network.responses.AuthResponse;
import com.easy.easybook.ui.customer.CustomerMainActivity;
import com.easy.easybook.ui.provider.ProviderMainActivity;
import com.easy.easybook.ui.provider.ProviderLoginActivity;
import com.easy.easybook.ui.admin.AdminLoginActivity;
import com.easy.easybook.utils.SharedPrefsManager;
import com.easy.easybook.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Login Activity for user authentication
 * Handles login for both customers and service providers
 */
public class LoginActivity extends AppCompatActivity {
    
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        mAuth = FirebaseAuth.getInstance();
        setupUI();
    }
    
    
    private void setupUI() {
        // Login button click listener
        binding.btnLogin.setOnClickListener(v -> {
            handleEmailLogin();
        });
        
        // Register redirect
        binding.btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
        
        // Forgot password click listener
        binding.tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
        
        // Service Provider login
        binding.btnProviderLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProviderLoginActivity.class);
            startActivity(intent);
        });
        
        // Admin login
        binding.btnAdminLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminLoginActivity.class);
            startActivity(intent);
        });
        
        // Direct phone login
        binding.btnDirectPhoneLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, PhoneAuthActivity.class);
            intent.putExtra("is_registration", false);
            intent.putExtra("user_type", "customer");
            startActivity(intent);
        });
        
    }
    
    private void handleEmailLogin() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        
        if (email.isEmpty()) {
            binding.etEmail.setError("Email is required");
            return;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError("Please enter a valid email");
            return;
        }
        
        if (password.isEmpty()) {
            binding.etPassword.setError("Password is required");
            return;
        }
        
        authenticateUser(email, password);
    }
    
    
    
    private void authenticateUser(String email, String password) {
        // Show loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnLogin.setEnabled(false);
        
        // Use Firebase Auth to sign in
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                    @Override
                    public void onComplete(Task<com.google.firebase.auth.AuthResult> task) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btnLogin.setEnabled(true);
                        
                        if (task.isSuccessful()) {
                            // Firebase Auth successful, now sync with backend
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                syncUserWithBackend(firebaseUser);
                            }
                        } else {
                            // Handle Firebase Auth errors
                            String errorMessage = "Login failed";
                            if (task.getException() != null) {
                                String errorCode = task.getException().getMessage();
                                if (errorCode.contains("user-not-found")) {
                                    errorMessage = "No account found with this email address";
                                } else if (errorCode.contains("wrong-password")) {
                                    errorMessage = "Invalid password";
                                } else if (errorCode.contains("invalid-email")) {
                                    errorMessage = "Invalid email address";
                                } else if (errorCode.contains("user-disabled")) {
                                    errorMessage = "Account is deactivated";
                                } else if (errorCode.contains("too-many-requests")) {
                                    errorMessage = "Too many failed attempts. Please try again later";
                                } else {
                                    errorMessage = "Error: " + task.getException().getMessage();
                                }
                            }
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    
    private void syncUserWithBackend(FirebaseUser firebaseUser) {
        // Get Firebase ID token
        firebaseUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    String idToken = task.getResult().getToken();
                    // Call backend to verify token and get user data
                    verifyTokenWithBackend(idToken);
                } else {
                    Toast.makeText(LoginActivity.this, "Failed to get authentication token", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    
    private void verifyTokenWithBackend(String idToken) {
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
                        SharedPrefsManager prefsManager = SharedPrefsManager.getInstance(LoginActivity.this);
                        prefsManager.saveAuthToken(authResponse.getData().getToken());
                        prefsManager.saveUser(authResponse.getData().getUser());
                        
                        // Navigate based on user role
                        navigateToMainActivity(authResponse.getData().getUser().getRole());
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, authResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Failed to verify token with backend", Toast.LENGTH_LONG).show();
                }
            }
            
            @Override
            public void onFailure(retrofit2.Call<com.easy.easybook.network.responses.AuthResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void navigateToMainActivity(String userRole) {
        Intent intent;
        
        if ("provider".equals(userRole)) {
            intent = new Intent(this, ProviderMainActivity.class);
        } else {
            intent = new Intent(this, CustomerMainActivity.class);
        }
        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
