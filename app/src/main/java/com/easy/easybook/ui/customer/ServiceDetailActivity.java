package com.easy.easybook.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.easy.easybook.databinding.ActivityServiceDetailBinding;
import com.easy.easybook.models.Service;
import com.easy.easybook.data.LocalDataManager;
import com.easy.easybook.data.SeedData;

/**
 * Service Detail Activity
 * Shows detailed information about a service and allows booking
 */
public class ServiceDetailActivity extends AppCompatActivity {
    
    private ActivityServiceDetailBinding binding;
    private Service service;
    private LocalDataManager dataManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServiceDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        dataManager = LocalDataManager.getInstance(this);
        getIntentData();
        setupUI();
    }
    
    private void getIntentData() {
        Intent intent = getIntent();
        service = (Service) intent.getSerializableExtra("service");
        
        // If service not passed, try to get from seed data
        if (service == null) {
            String serviceId = intent.getStringExtra("service_id");
            if (serviceId != null) {
                service = findServiceById(serviceId);
            }
        }
    }
    
    private Service findServiceById(String serviceId) {
        // Search through seed data to find service
        for (Service s : SeedData.getSeedServices()) {
            if (s.getId().equals(serviceId)) {
                return s;
            }
        }
        return null;
    }
    
    private void setupUI() {
        if (service == null) {
            Toast.makeText(this, "Service not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Back button
        binding.btnBack.setOnClickListener(v -> onBackPressed());
        
        // Cart button
        binding.btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        });
        
        // Set service information
        binding.tvServiceName.setText(service.getName());
        binding.tvServiceTitle.setText(service.getName());
        binding.tvServiceDescription.setText(service.getDescription());
        binding.tvServicePrice.setText(String.format("$%.2f", service.getPrice()));
        binding.tvServiceCategory.setText("Category: " + service.getCategory());
        binding.tvServiceDuration.setText("Duration: " + service.getDuration() + " minutes");
        
        // Set rating
        binding.ratingService.setRating(service.getRating());
        binding.tvRatingText.setText(String.format("%.1f (25 reviews)", service.getRating()));
        
        // Book now button
        binding.btnBookNow.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingsActivity.class);
            intent.putExtra("service", service);
            startActivity(intent);
        });
        
        // Add to cart button
        binding.btnAddToCart.setOnClickListener(v -> {
            dataManager.addToCart(service);
            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
