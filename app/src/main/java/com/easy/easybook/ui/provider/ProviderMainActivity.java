package com.easy.easybook.ui.provider;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.easy.easybook.databinding.ActivityProviderMainBinding;

/**
 * Main Activity for service providers
 * Contains dashboard, job requests, schedule, and profile
 */
public class ProviderMainActivity extends AppCompatActivity {
    
    private ActivityProviderMainBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProviderMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        setupUI();
    }
    
    private void setupUI() {
        binding.tvWelcome.setText("Welcome, Service Provider!");
        binding.tvDescription.setText("Manage your services and bookings");
        
        // Dashboard functionality - navigate to provider dashboard (booking management)
        binding.btnDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProviderDashboardActivity.class);
            
            // Get current user info for provider context
            com.easy.easybook.utils.SharedPrefsManager prefsManager = com.easy.easybook.utils.SharedPrefsManager.getInstance(this);
            com.easy.easybook.models.User currentUser = prefsManager.getUser();
            
            if (currentUser != null) {
                intent.putExtra("provider_id", currentUser.getId());
                intent.putExtra("provider_category", currentUser.getServiceCategory() != null ? currentUser.getServiceCategory() : "General");
            }
            
            startActivity(intent);
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
