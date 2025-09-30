package com.easy.easybook.ui.provider;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.easy.easybook.databinding.ActivityCreateServiceBinding;
import com.easy.easybook.network.ApiClient;
import com.easy.easybook.network.responses.ApiResponse;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateServiceActivity extends AppCompatActivity {
    
    private ActivityCreateServiceBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateServiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        setupUI();
    }
    
    private void setupUI() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnCreateService.setOnClickListener(v -> createService());
    }
    
    private void createService() {
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
            binding.btnCreateService.setEnabled(false);
            
            // Create service request
            JsonObject serviceRequest = new JsonObject();
            serviceRequest.addProperty("name", name);
            serviceRequest.addProperty("description", description);
            serviceRequest.addProperty("price", price);
            serviceRequest.addProperty("duration", duration);
            serviceRequest.addProperty("category", "64f8a1b2c1d2e3f4a5b6c7d8"); // Default category ID
            serviceRequest.addProperty("isActive", true);
            
            // Make API call
            Call<ApiResponse> call = ApiClient.getInstance(this).getApiService()
                    .createService(null, serviceRequest); // Auth header added by interceptor
            
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnCreateService.setEnabled(true);
                    
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse apiResponse = response.body();
                        if (apiResponse.isSuccess()) {
                            Toast.makeText(CreateServiceActivity.this, "Service created successfully!", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(CreateServiceActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(CreateServiceActivity.this, "Failed to create service", Toast.LENGTH_LONG).show();
                    }
                }
                
                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnCreateService.setEnabled(true);
                    Toast.makeText(CreateServiceActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
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
