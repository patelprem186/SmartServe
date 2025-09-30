package com.easy.easybook.ui.provider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.easy.easybook.databinding.ActivityEditServiceBinding;
import com.easy.easybook.network.ApiClient;
import com.easy.easybook.network.responses.ApiResponse;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditServiceActivity extends AppCompatActivity {
    
    private ActivityEditServiceBinding binding;
    private String serviceId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditServiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Get service data from intent
        serviceId = getIntent().getStringExtra("service_id");
        String serviceName = getIntent().getStringExtra("service_name");
        String serviceDescription = getIntent().getStringExtra("service_description");
        double servicePrice = getIntent().getDoubleExtra("service_price", 0.0);
        String serviceDuration = getIntent().getStringExtra("service_duration");
        
        setupUI(serviceName, serviceDescription, servicePrice, serviceDuration);
    }
    
    private void setupUI(String serviceName, String serviceDescription, double servicePrice, String serviceDuration) {
        // Pre-fill form with existing data
        binding.etServiceName.setText(serviceName);
        binding.etDescription.setText(serviceDescription);
        binding.etPrice.setText(String.valueOf(servicePrice));
        
        // Extract duration number from string (e.g., "120 minutes" -> "120")
        if (serviceDuration != null && serviceDuration.contains(" ")) {
            String durationNumber = serviceDuration.split(" ")[0];
            binding.etDuration.setText(durationNumber);
        } else {
            binding.etDuration.setText(serviceDuration);
        }
        
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnUpdateService.setOnClickListener(v -> updateService());
    }
    
    private void updateService() {
        String name = binding.etServiceName.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();
        String priceStr = binding.etPrice.getText().toString().trim();
        String durationStr = binding.etDuration.getText().toString().trim();
        
        if (TextUtils.isEmpty(name)) {
            binding.etServiceName.setError("Service name is required");
            return;
        }
        
        if (TextUtils.isEmpty(description)) {
            binding.etDescription.setError("Description is required");
            return;
        }
        
        if (TextUtils.isEmpty(priceStr)) {
            binding.etPrice.setError("Price is required");
            return;
        }
        
        if (TextUtils.isEmpty(durationStr)) {
            binding.etDuration.setError("Duration is required");
            return;
        }
        
        try {
            double price = Double.parseDouble(priceStr);
            int duration = Integer.parseInt(durationStr);
            
            // Show loading
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.btnUpdateService.setEnabled(false);
            
            // Create service request
            JsonObject serviceRequest = new JsonObject();
            serviceRequest.addProperty("name", name);
            serviceRequest.addProperty("description", description);
            serviceRequest.addProperty("price", price);
            serviceRequest.addProperty("duration", duration);
            
            // Make API call
            Call<ApiResponse> call = ApiClient.getInstance(this).getApiService()
                    .updateService(null, serviceId, serviceRequest); // Auth header added by interceptor
            
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnUpdateService.setEnabled(true);
                    
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse apiResponse = response.body();
                        if (apiResponse.isSuccess()) {
                            Toast.makeText(EditServiceActivity.this, "Service updated successfully!", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(EditServiceActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(EditServiceActivity.this, "Failed to update service", Toast.LENGTH_LONG).show();
                    }
                }
                
                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnUpdateService.setEnabled(true);
                    Toast.makeText(EditServiceActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for price and duration", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
