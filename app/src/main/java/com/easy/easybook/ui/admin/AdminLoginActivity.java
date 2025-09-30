package com.easy.easybook.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.easy.easybook.databinding.ActivityAdminLoginBinding;
import com.easy.easybook.R;

public class AdminLoginActivity extends AppCompatActivity {

    private ActivityAdminLoginBinding binding;
    
    // Hardcoded admin credentials
    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "admin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        setupClickListeners();
    }
    
    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(v -> handleLogin());
        binding.btnBack.setOnClickListener(v -> finish());
    }
    
    private void handleLogin() {
        String email = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
            Toast.makeText(this, "Admin login successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, AdminDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid admin credentials", Toast.LENGTH_SHORT).show();
        }
    }
}
