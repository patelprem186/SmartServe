package com.easy.easybook.ui.provider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.easy.easybook.databinding.ActivityProviderDashboardBinding;
import com.easy.easybook.models.Booking;
import com.easy.easybook.models.User;
import com.easy.easybook.data.LocalDataManager;
import com.easy.easybook.ui.provider.adapters.ProviderBookingAdapter;
import com.easy.easybook.utils.SharedPrefsManager;
import com.easy.easybook.ui.auth.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class ProviderDashboardActivity extends AppCompatActivity implements ProviderBookingAdapter.OnProviderBookingActionListener {
    
    private ActivityProviderDashboardBinding binding;
    private LocalDataManager dataManager;
    private ProviderBookingAdapter adapter;
    private List<Booking> providerRequests;
    private String providerId;
    private String providerCategory;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProviderDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Get provider info from intent or from shared prefs
        providerId = getIntent().getStringExtra("provider_id");
        providerCategory = getIntent().getStringExtra("provider_category");
        
        // If no intent data, try loading from SharedPrefs (normal login flow)
        if (providerId == null || providerCategory == null) {
            SharedPrefsManager prefsManager = SharedPrefsManager.getInstance(this);
            User currentUser = prefsManager.getUser();
            if (currentUser != null && "provider".equals(currentUser.getRole())) {
                providerId = currentUser.getId();
                providerCategory = currentUser.getServiceCategory() != null ? currentUser.getServiceCategory() : "General";
            } else {
                providerId = "provider1"; // Default for demo
                providerCategory = "General"; // Default category
            }
        }
        
        dataManager = LocalDataManager.getInstance(this);
        setupUI();
        loadProviderData();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadProviderData();
    }
    
    private void setupUI() {
        // Back button
        binding.btnBack.setOnClickListener(v -> finish());
        
        // Setup recycler view
        adapter = new ProviderBookingAdapter(this);
        binding.rvRequests.setLayoutManager(new LinearLayoutManager(this));
        binding.rvRequests.setAdapter(adapter);
        
        // Replace refresh with logout functionality
        binding.btnRefresh.setOnClickListener(v -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        // Clear shared preferences
                        SharedPrefsManager.getInstance(this).logout();
                        // Navigate to login
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
        // Set the button text to logout
        binding.btnRefresh.setText("Logout");
    }
    
    private void loadProviderData() {
        // Load all bookings and filter by category - show ALL bookings for the provider's category
        List<Booking> allBookings = dataManager.getAllBookings();
        providerRequests = new ArrayList<>();
        
        for (Booking booking : allBookings) {
            // Filter by category - show all bookings for this service category
            if (booking.getServiceCategory() != null && 
                booking.getServiceCategory().equalsIgnoreCase(providerCategory)) {
                providerRequests.add(booking);
            }
        }
        
        // Update statistics
        updateStatistics();
        
        // Update requests list
        if (providerRequests.isEmpty()) {
            showEmptyState();
        } else {
            showRequests();
            adapter.setBookings(providerRequests);
        }
    }
    
    private void updateStatistics() {
        int totalRequests = providerRequests.size();
        int pendingRequests = 0;
        int acceptedRequests = 0;
        double totalEarnings = 0.0;
        
        // Calculate from actual bookings data
        for (Booking booking : providerRequests) {
            if ("pending".equals(booking.getStatus())) {
                pendingRequests++;
            } else if ("confirmed".equals(booking.getStatus())) {
                acceptedRequests++;
                totalEarnings += booking.getTotalAmount();
            }
        }
        
        binding.tvTotalRequests.setText(String.valueOf(totalRequests));
        binding.tvPendingRequests.setText(String.valueOf(pendingRequests));
        binding.tvTotalEarnings.setText(String.format("$%.2f", totalEarnings));
        
        // Update status text
        if (pendingRequests > 0) {
            binding.tvStatus.setText("You have " + pendingRequests + " pending requests for " + providerCategory);
            binding.tvStatus.setTextColor(getColor(android.R.color.holo_orange_dark));
        } else {
            binding.tvStatus.setText("No pending requests for " + providerCategory + ". Service Category set to: " + providerCategory);
            binding.tvStatus.setTextColor(getColor(android.R.color.darker_gray));
        }
    }
    
    private void showEmptyState() {
        binding.rvRequests.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.VISIBLE);
    }
    
    private void showRequests() {
        binding.rvRequests.setVisibility(View.VISIBLE);
        binding.layoutEmpty.setVisibility(View.GONE);
    }
    
    @Override
    public void onAcceptBooking(Booking booking) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Accept Request")
                .setMessage("Are you sure you want to accept this service request?")
                .setPositiveButton("Accept", (dialog, which) -> {
                    dataManager.updateBookingStatus(booking.getId(), "confirmed");
                    Toast.makeText(this, "Request accepted", Toast.LENGTH_SHORT).show();
                    loadProviderData();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    @Override
    public void onDeclineBooking(Booking booking) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Decline Request")
                .setMessage("Are you sure you want to decline this service request?")
                .setPositiveButton("Decline", (dialog, which) -> {
                    dataManager.updateBookingStatus(booking.getId(), "declined");
                    Toast.makeText(this, "Request declined", Toast.LENGTH_SHORT).show();
                    loadProviderData();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    @Override
    public void onStartService(Booking booking) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Start Service")
                .setMessage("Are you ready to start this service?")
                .setPositiveButton("Start", (dialog, which) -> {
                    dataManager.updateBookingStatus(booking.getId(), "in_progress");
                    Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
                    loadProviderData();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    @Override
    public void onCompleteService(Booking booking) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Complete Service")
                .setMessage("Have you completed this service?")
                .setPositiveButton("Complete", (dialog, which) -> {
                    dataManager.updateBookingStatus(booking.getId(), "completed");
                    Toast.makeText(this, "Service completed", Toast.LENGTH_SHORT).show();
                    loadProviderData();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
