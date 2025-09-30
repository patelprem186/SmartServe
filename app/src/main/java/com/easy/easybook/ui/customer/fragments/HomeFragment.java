package com.easy.easybook.ui.customer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.easy.easybook.databinding.FragmentHomeBinding;
import com.easy.easybook.models.Service;
import com.easy.easybook.models.ServiceItem;
import com.easy.easybook.models.ServiceCategory;
import com.easy.easybook.models.User;
import com.easy.easybook.data.SeedData;
import com.easy.easybook.data.LocalDataManager;
import com.easy.easybook.data.ServiceManager;
import com.easy.easybook.utils.SharedPrefsManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.easy.easybook.ui.customer.ServiceDetailActivity;
import com.easy.easybook.ui.customer.ServiceSearchActivity;
import com.easy.easybook.ui.customer.BookingsActivity;
import com.easy.easybook.ui.customer.MyBookingsActivity;
import com.easy.easybook.ui.customer.CartActivity;
import com.easy.easybook.ui.customer.adapters.FeaturedServicesAdapter;
import com.easy.easybook.ui.customer.adapters.ServiceCategoryAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Home Fragment for customers
 * Shows service categories, featured services, and quick actions
 */
public class HomeFragment extends Fragment {
    
    private FragmentHomeBinding binding;
    private ServiceCategoryAdapter categoryAdapter;
    private FeaturedServicesAdapter featuredAdapter;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupUI();
        loadData();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Refresh user data when fragment becomes visible
        loadUserData();
    }
    
    private void setupUI() {
        // Load user data and set welcome message
        loadUserData();
        
        // Search click listener
        binding.searchView.setOnClickListener(v -> {
            // Navigate to search activity
            Intent intent = new Intent(getContext(), ServiceSearchActivity.class);
            startActivity(intent);
        });
        
        // See All Categories click listener
        binding.tvSeeAllCategories.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ServiceSearchActivity.class);
            intent.putExtra("show_categories", true);
            startActivity(intent);
        });
        
        // See All Featured Services click listener
        binding.tvSeeAllFeatured.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ServiceSearchActivity.class);
            intent.putExtra("show_featured", true);
            startActivity(intent);
        });
        
        // Quick Actions click listeners
        binding.cardMyBookings.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MyBookingsActivity.class);
            startActivity(intent);
        });
        
        binding.cardSupport.setOnClickListener(v -> {
            showSupportDialog();
        });
        
        // Cart button click listener
        binding.btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CartActivity.class);
            startActivity(intent);
        });
        
        // Categories RecyclerView
        binding.rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 2));
        categoryAdapter = new ServiceCategoryAdapter(this::onCategoryClick);
        binding.rvCategories.setAdapter(categoryAdapter);
        
        // Featured Services RecyclerView
        binding.rvFeaturedServices.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        featuredAdapter = new FeaturedServicesAdapter(this::onServiceClick);
        binding.rvFeaturedServices.setAdapter(featuredAdapter);
    }
    
    private void loadData() {
        // Load categories from API
        loadCategories();
        
        // Load featured services from API
        loadFeaturedServices();
    }
    
    private void loadCategories() {
        // Use centralized ServiceManager for consistency
        ServiceManager serviceManager = ServiceManager.getInstance(getContext());
        List<ServiceCategory> categories = serviceManager.getServiceCategories();
        
        categoryAdapter.updateCategories(categories);
        android.util.Log.d("HomeFragment", "Loaded " + categories.size() + " categories from ServiceManager");
    }
    
    
    private String getCategoryDescription(String category) {
        switch (category.toLowerCase()) {
            case "plumbing": return "Plumbing repair and installation services";
            case "cleaning": return "House and office cleaning services";
            case "electrical": return "Electrical repair and installation";
            case "hvac": return "Heating, ventilation, and air conditioning";
            case "beauty": return "Beauty and wellness services";
            case "tutoring": return "Educational and tutoring services";
            case "fitness": return "Personal training and fitness";
            default: return "Professional services";
        }
    }
    
    private void loadFeaturedServices() {
        // Use centralized ServiceManager for consistency
        ServiceManager serviceManager = ServiceManager.getInstance(getContext());
        List<Service> featuredServices = serviceManager.getFeaturedServices();
        
        List<ServiceItem> serviceItems = convertServicesToServiceItems(featuredServices);
        
        featuredAdapter.updateServices(serviceItems);
        android.util.Log.d("HomeFragment", "Loaded " + serviceItems.size() + " featured services from ServiceManager");
    }

    // Convert Service to ServiceItem for adapter compatibility
    private List<ServiceItem> convertServicesToServiceItems(List<Service> services) {
        List<ServiceItem> serviceItems = new ArrayList<>();
        for (Service service : services) {
            ServiceItem item = new ServiceItem();
            item.setId(service.getId());
            item.setName(service.getName());
            item.setDescription(service.getDescription());
            item.setCategory(service.getCategory());
            item.setPrice(service.getPrice());
            item.setDuration(service.getDuration());
            item.setRating(service.getRating());
            item.setAvailable(service.isAvailable());
            item.setFeatured(service.isFeatured());
            item.setReviewCount(service.getReviewCount());
            serviceItems.add(item);
        }
        return serviceItems;
    }

    private List<ServiceItem> createDummyServices() {
        List<ServiceItem> services = new ArrayList<>();
        
        ServiceItem service1 = new ServiceItem();
        service1.setId("1");
        service1.setName("Emergency Plumbing Repair");
        service1.setDescription("24/7 emergency plumbing services for leaks, clogs, and urgent repairs.");
        service1.setCategory("Plumbing");
        service1.setPrice(120.0);
        service1.setDuration("60");
        service1.setRating(4.5f);
        service1.setAvailable(true);
        service1.setFeatured(true);
        service1.setReviewCount(25);
        services.add(service1);
        
        ServiceItem service2 = new ServiceItem();
        service2.setId("2");
        service2.setName("Deep House Cleaning");
        service2.setDescription("Complete deep cleaning of your home including all rooms and bathrooms.");
        service2.setCategory("Cleaning");
        service2.setPrice(150.0);
        service2.setDuration("180");
        service2.setRating(4.8f);
        service2.setAvailable(true);
        service2.setFeatured(true);
        service2.setReviewCount(42);
        services.add(service2);
        
        ServiceItem service3 = new ServiceItem();
        service3.setId("3");
        service3.setName("Electrical Outlet Installation");
        service3.setDescription("Professional installation of new electrical outlets, switches, and fixtures.");
        service3.setCategory("Electrical");
        service3.setPrice(85.0);
        service3.setDuration("45");
        service3.setRating(4.3f);
        service3.setAvailable(true);
        service3.setFeatured(true);
        service3.setReviewCount(18);
        services.add(service3);
        
        return services;
    }

    
    
    private void onCategoryClick(ServiceCategory category) {
        // Navigate to search with category filter
        Intent intent = new Intent(getContext(), ServiceSearchActivity.class);
        intent.putExtra("category", category.getName());
        startActivity(intent);
    }
    
    private void onServiceClick(ServiceItem service) {
        try {
            android.util.Log.d("HomeFragment", "Service clicked: " + (service != null ? service.getName() : "null"));
            
            if (getContext() == null) {
                android.util.Log.e("HomeFragment", "Context is null, cannot start activity");
                return;
            }
            
            if (service == null) {
                android.util.Log.e("HomeFragment", "Service is null");
                return;
            }
            
            String serviceId = service.getId();
            String serviceName = service.getName();
            
            android.util.Log.d("HomeFragment", "Service ID: " + serviceId + ", Name: " + serviceName);
            
            if (serviceId == null || serviceId.isEmpty()) {
                android.util.Log.e("HomeFragment", "Service ID is null or empty");
                return;
            }
            
            Intent intent = new Intent(getContext(), ServiceDetailActivity.class);
            intent.putExtra("service_id", serviceId);
            intent.putExtra("service_name", serviceName != null ? serviceName : "Unknown Service");
            startActivity(intent);
            
            android.util.Log.d("HomeFragment", "Successfully started ServiceDetailActivity");
            
        } catch (Exception e) {
            android.util.Log.e("HomeFragment", "Error starting ServiceDetailActivity: " + e.getMessage(), e);
        }
    }
    
    // Add quick action methods for navigation
    public void navigateToBookings() {
        Intent intent = new Intent(getContext(), BookingsActivity.class);
        startActivity(intent);
    }
    
    public void navigateToMyBookings() {
        Intent intent = new Intent(getContext(), MyBookingsActivity.class);
        startActivity(intent);
    }
    
    public void navigateToCart() {
        Intent intent = new Intent(getContext(), CartActivity.class);
        startActivity(intent);
    }
    
    private void showSupportDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Customer Support");
        builder.setMessage("How can we help you today?");
        
        builder.setPositiveButton("Call Support", (dialog, which) -> {
            // TODO: Implement actual phone call functionality
            Toast.makeText(getContext(), "Calling support...", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNeutralButton("Email Support", (dialog, which) -> {
            // TODO: Implement email support functionality
            Toast.makeText(getContext(), "Opening email...", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void loadUserData() {
        // Get current Firebase user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        
        if (firebaseUser != null) {
            // Get user details from Firebase Auth
            String displayName = firebaseUser.getDisplayName();
            String email = firebaseUser.getEmail();
            String uid = firebaseUser.getUid();
            boolean emailVerified = firebaseUser.isEmailVerified();
            
            // Build welcome message with Firebase user name
            if (displayName != null && !displayName.trim().isEmpty()) {
                // Extract first name from display name
                String firstName = displayName.split(" ")[0];
                binding.tvWelcome.setText("Welcome back, " + firstName + "!");
            } else if (email != null && !email.trim().isEmpty()) {
                // Use email prefix if no display name
                String emailPrefix = email.split("@")[0];
                binding.tvWelcome.setText("Welcome back, " + emailPrefix + "!");
            } else {
                binding.tvWelcome.setText("Welcome back!");
            }
            
            // Show Firebase user-specific information
            displayFirebaseUserContent(firebaseUser);
            
            android.util.Log.d("HomeFragment", "Firebase user loaded: " + displayName + " (" + email + ")");
        } else {
            // No user logged in
            binding.tvWelcome.setText("Welcome!");
            android.util.Log.w("HomeFragment", "No Firebase user logged in");
        }
    }
    
    private void displayFirebaseUserContent(FirebaseUser firebaseUser) {
        // Display Firebase user-specific content
        String displayName = firebaseUser.getDisplayName();
        String email = firebaseUser.getEmail();
        String uid = firebaseUser.getUid();
        boolean emailVerified = firebaseUser.isEmailVerified();
        
        android.util.Log.d("HomeFragment", "Firebase user details:");
        android.util.Log.d("HomeFragment", "- Display Name: " + displayName);
        android.util.Log.d("HomeFragment", "- Email: " + email);
        android.util.Log.d("HomeFragment", "- UID: " + uid);
        android.util.Log.d("HomeFragment", "- Email Verified: " + emailVerified);
        
        // You can add Firebase user-specific content here
        // For example, show personalized recommendations based on user's booking history
        // or show their recent bookings, etc.
        
        // Show verification status
        if (!emailVerified) {
            android.util.Log.d("HomeFragment", "User email is not verified");
        }
    }
    
    private void displayUserSpecificContent(User user) {
        // You can add user-specific content here
        // For example, show personalized recommendations based on user's booking history
        // or show their recent bookings, etc.
        
        // Example: Show user's role if they're a service provider
        if ("provider".equals(user.getRole())) {
            // Could show provider-specific content
            android.util.Log.d("HomeFragment", "User is a service provider: " + user.getFirstName());
        } else {
            // Show customer-specific content
            android.util.Log.d("HomeFragment", "User is a customer: " + user.getFirstName());
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
