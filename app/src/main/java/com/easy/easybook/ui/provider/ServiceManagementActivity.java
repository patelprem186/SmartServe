package com.easy.easybook.ui.provider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.easy.easybook.databinding.ActivityServiceManagementBinding;
import com.easy.easybook.models.Service;
import com.easy.easybook.network.ApiClient;
import com.easy.easybook.network.responses.ServicesResponse;
import com.easy.easybook.ui.provider.adapters.ServiceManagementAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceManagementActivity extends AppCompatActivity {
    
    private ActivityServiceManagementBinding binding;
    private ServiceManagementAdapter adapter;
    private List<Service> services = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServiceManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        setupUI();
        loadServices();
    }
    
    private void setupUI() {
        // Setup RecyclerView
        adapter = new ServiceManagementAdapter(services, new ServiceManagementAdapter.OnServiceActionListener() {
            @Override
            public void onEditService(Service service) {
                openEditService(service);
            }
            
            @Override
            public void onDeleteService(Service service) {
                deleteService(service);
            }
            
            @Override
            public void onToggleStatus(Service service) {
                toggleServiceStatus(service);
            }
        });
        
        binding.rvServices.setLayoutManager(new LinearLayoutManager(this));
        binding.rvServices.setAdapter(adapter);
        
        // Setup click listeners
        binding.btnAddService.setOnClickListener(v -> openCreateService());
        binding.btnBack.setOnClickListener(v -> finish());
    }
    
    private void loadServices() {
        // Initialize data loading
        binding.rvServices.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.GONE);
        
        Call<ServicesResponse> call = ApiClient.getInstance(this).getApiService()
                .getProviderServices(null, "all", 1, 100); // Auth header added by interceptor
        
        call.enqueue(new Callback<ServicesResponse>() {
            @Override
            public void onResponse(Call<ServicesResponse> call, Response<ServicesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ServicesResponse servicesResponse = response.body();
                    if (servicesResponse.isSuccess() && servicesResponse.getData() != null) {
                        JsonArray servicesArray = servicesResponse.getData().getAsJsonArray("services");
                        services.clear();
                        
                        for (int i = 0; i < servicesArray.size(); i++) {
                            JsonObject serviceObj = servicesArray.get(i).getAsJsonObject();
                            Service service = convertJsonToService(serviceObj);
                            services.add(service);
                        }
                        
                        adapter.notifyDataSetChanged();
                        
                        if (services.isEmpty()) {
                            binding.layoutEmpty.setVisibility(View.VISIBLE);
                            binding.rvServices.setVisibility(View.GONE);
                        } else {
                            binding.layoutEmpty.setVisibility(View.GONE);
                            binding.rvServices.setVisibility(View.VISIBLE);
                        }
                    } else {
                        showNoServices();
                    }
                } else {
                    showNoServices();
                    Toast.makeText(ServiceManagementActivity.this, "Failed to load services", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ServicesResponse> call, Throwable t) {
                showNoServices();
                Toast.makeText(ServiceManagementActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private Service convertJsonToService(JsonObject serviceObj) {
        Service service = new Service();
        service.setId(serviceObj.get("_id").getAsString());
        service.setName(serviceObj.get("name").getAsString());
        service.setDescription(serviceObj.get("description").getAsString());
        service.setPrice(serviceObj.get("price").getAsDouble());
        service.setDuration(serviceObj.get("duration").getAsInt() + " minutes");
        service.setAvailable(serviceObj.get("isActive").getAsBoolean());
        
        if (serviceObj.has("rating") && serviceObj.get("rating").isJsonObject()) {
            JsonObject rating = serviceObj.getAsJsonObject("rating");
            if (rating.has("average")) {
                service.setRating(rating.get("average").getAsFloat());
            }
        }
        
        return service;
    }
    
    private void showNoServices() {
        binding.rvServices.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.VISIBLE);
    }
    
    private void openCreateService() {
        Intent intent = new Intent(this, CreateServiceActivity.class);
        startActivityForResult(intent, 100);
    }
    
    private void openEditService(Service service) {
        Intent intent = new Intent(this, EditServiceActivity.class);
        intent.putExtra("service_id", service.getId());
        intent.putExtra("service_name", service.getName());
        intent.putExtra("service_description", service.getDescription());
        intent.putExtra("service_price", service.getPrice());
        intent.putExtra("service_duration", service.getDuration());
        startActivityForResult(intent, 101);
    }
    
    private void deleteService(Service service) {
        // TODO: Implement delete service API call
        Toast.makeText(this, "Delete service: " + service.getName(), Toast.LENGTH_SHORT).show();
    }
    
    private void toggleServiceStatus(Service service) {
        // TODO: Implement toggle service status API call
        Toast.makeText(this, "Toggle status for: " + service.getName(), Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // Refresh services list
            loadServices();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
