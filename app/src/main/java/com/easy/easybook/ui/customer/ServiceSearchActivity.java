package com.easy.easybook.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.easy.easybook.databinding.ActivityServiceSearchBinding;
import com.easy.easybook.models.Service;
import com.easy.easybook.ui.customer.adapters.ServiceSearchAdapter;
import com.easy.easybook.data.SeedData;
import com.easy.easybook.data.LocalDataManager;
import com.easy.easybook.data.ServiceManager;

import java.util.ArrayList;
import java.util.List;

public class ServiceSearchActivity extends AppCompatActivity {
    
    private ActivityServiceSearchBinding binding;
    private ServiceSearchAdapter adapter;
    private List<Service> services = new ArrayList<>();
    private String currentQuery = "";
    private String currentCategory = "";
    private String currentSortBy = "rating";
    private int currentPage = 1;
    private boolean isLoading = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServiceSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        initializeApi();
        setupUI();
        loadServices();
    }
    
    private void initializeApi() {
        // No longer using API - using seed data instead
    }
    
    private void setupUI() {
        // Get category filter from intent
        Intent intent = getIntent();
        if (intent.hasExtra("category")) {
            currentCategory = intent.getStringExtra("category");
            binding.etSearch.setText(currentCategory);
        }
        
        // Setup search functionality
        binding.btnSearch.setOnClickListener(v -> performSearch());
        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            performSearch();
            return true;
        });
        
        // Setup filter options
        binding.btnFilter.setOnClickListener(v -> showFilterDialog());
        binding.btnSort.setOnClickListener(v -> showSortDialog());
        
        // Setup RecyclerView
        adapter = new ServiceSearchAdapter(services, new ServiceSearchAdapter.OnServiceClickListener() {
            @Override
            public void onServiceClick(Service service) {
                openServiceDetails(service);
            }
            
            @Override
            public void onBookService(Service service) {
                openBooking(service);
            }
        });
        
        binding.rvServices.setLayoutManager(new LinearLayoutManager(this));
        binding.rvServices.setAdapter(adapter);
        
        // Setup click listeners
        binding.btnBack.setOnClickListener(v -> finish());
    }
    
    private void performSearch() {
        currentQuery = binding.etSearch.getText().toString().trim();
        currentPage = 1;
        services.clear();
        adapter.notifyDataSetChanged();
        loadServices();
    }
    
    private void loadServices() {
        if (isLoading) return;
        
        isLoading = true;
        binding.progressBar.setVisibility(View.VISIBLE);
        
        if (currentPage == 1) {
            binding.rvServices.setVisibility(View.GONE);
            binding.tvNoServices.setVisibility(View.GONE);
        }
        
        // Use centralized ServiceManager for consistency
        ServiceManager serviceManager = ServiceManager.getInstance(this);
        List<Service> allServices = serviceManager.getAllServices();
        List<Service> filteredServices = new ArrayList<>();
        
        // Apply filters
        for (Service service : allServices) {
            boolean matchesQuery = currentQuery.isEmpty() || 
                service.getName().toLowerCase().contains(currentQuery.toLowerCase()) ||
                service.getDescription().toLowerCase().contains(currentQuery.toLowerCase()) ||
                service.getCategory().toLowerCase().contains(currentQuery.toLowerCase());
                
            boolean matchesCategory = currentCategory.isEmpty() || 
                service.getCategory().equalsIgnoreCase(currentCategory);
                
            if (matchesQuery && matchesCategory) {
                filteredServices.add(service);
            }
        }
        
        // Apply sorting
        if (currentSortBy.equals("rating")) {
            filteredServices.sort((a, b) -> Float.compare(b.getRating(), a.getRating()));
        } else if (currentSortBy.equals("price")) {
            filteredServices.sort((a, b) -> Double.compare(a.getPrice(), b.getPrice()));
        } else if (currentSortBy.equals("name")) {
            filteredServices.sort((a, b) -> a.getName().compareTo(b.getName()));
        }
        
        // Update UI
        isLoading = false;
        binding.progressBar.setVisibility(View.GONE);
        
        if (currentPage == 1) {
            services.clear();
        }
        
        services.addAll(filteredServices);
        adapter.notifyDataSetChanged();
        
        if (services.isEmpty() && currentPage == 1) {
            showNoServices();
        } else {
            binding.rvServices.setVisibility(View.VISIBLE);
        }
    }
    
    
    private void showNoServices() {
        binding.rvServices.setVisibility(View.GONE);
        binding.tvNoServices.setVisibility(View.VISIBLE);
    }
    
    private void showFilterDialog() {
        String[] filterOptions = {"All Categories", "Cleaning", "Plumbing", "HVAC", "Beauty", "Tutoring", "Fitness"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Filter by Category");
        builder.setItems(filterOptions, (dialog, which) -> {
            if (which == 0) {
                currentCategory = "";
            } else {
                currentCategory = filterOptions[which];
            }
            performSearch();
        });
        builder.show();
    }
    
    private void showSortDialog() {
        String[] sortOptions = {"Rating (High to Low)", "Price (Low to High)", "Price (High to Low)", "Name (A to Z)"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Sort by");
        builder.setItems(sortOptions, (dialog, which) -> {
            switch (which) {
                case 0: currentSortBy = "rating"; break;
                case 1: currentSortBy = "price_asc"; break;
                case 2: currentSortBy = "price_desc"; break;
                case 3: currentSortBy = "name"; break;
            }
            performSearch();
        });
        builder.show();
    }
    
    private void openServiceDetails(Service service) {
        Intent intent = new Intent(this, ServiceDetailActivity.class);
        intent.putExtra("service_id", service.getId());
        intent.putExtra("service_name", service.getName());
        intent.putExtra("service_price", service.getPrice());
        startActivity(intent);
    }
    
    private void openBooking(Service service) {
        Intent intent = new Intent(this, BookingsActivity.class);
        intent.putExtra("service", service);
        startActivity(intent);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}