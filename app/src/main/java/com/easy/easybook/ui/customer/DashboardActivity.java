package com.easy.easybook.ui.customer;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.easy.easybook.databinding.ActivityDashboardBinding;
import com.easy.easybook.data.LocalDataManager;
import com.easy.easybook.models.Booking;
import com.easy.easybook.models.User;
import com.easy.easybook.ui.customer.adapters.BookingAdapter;
import com.easy.easybook.utils.SharedPrefsManager;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    
    private ActivityDashboardBinding binding;
    private BookingAdapter bookingAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        setupUI();
        loadDashboardData();
    }
    
    private void setupUI() {
        // Set user name
        SharedPrefsManager prefsManager = SharedPrefsManager.getInstance(this);
        User currentUser = prefsManager.getUser();
        
        // Check if current user is a service provider and redirect
        if (currentUser != null && "provider".equals(currentUser.getRole())) {
            android.content.Intent intent = new android.content.Intent(this, com.easy.easybook.ui.provider.ProviderDashboardActivity.class);
            intent.putExtra("provider_id", currentUser.getId());
            intent.putExtra("provider_category", currentUser.getServiceCategory() != null ? currentUser.getServiceCategory() : "General");
            startActivity(intent);
            finish();
            return;
        }
        
        String userName = currentUser != null ? currentUser.getFullName() : "User";
        binding.tvWelcomeUser.setText("Welcome, " + userName);
        
        // Setup recent bookings RecyclerView
        bookingAdapter = new BookingAdapter(new ArrayList<>());
        binding.rvRecentBookings.setLayoutManager(new LinearLayoutManager(this));
        binding.rvRecentBookings.setAdapter(bookingAdapter);
        
        // Setup click listeners
        binding.btnViewAllBookings.setOnClickListener(v -> {
            // TODO: Navigate to all bookings activity
            Toast.makeText(this, "View all bookings", Toast.LENGTH_SHORT).show();
        });
        
        binding.btnViewCart.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, CartActivity.class);
            startActivity(intent);
        });
        
        binding.btnProviderLogin.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, com.easy.easybook.ui.provider.ProviderLoginActivity.class);
            startActivity(intent);
        });
        
        binding.btnBookNewService.setOnClickListener(v -> {
            // TODO: Navigate to services list
            Toast.makeText(this, "Book new service", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void loadDashboardData() {
        binding.progressBar.setVisibility(View.VISIBLE);
        
        // Use LocalDataManager instead of API
        LocalDataManager dataManager = LocalDataManager.getInstance(this);
        List<Booking> userBookings = dataManager.getUserBookings("current_user"); // In real app, get from login
        
        // Calculate statistics
        int totalBookings = userBookings.size();
        int pendingBookings = 0;
        int completedBookings = 0;
        double totalSpent = 0;
        
        for (Booking booking : userBookings) {
            if ("pending".equals(booking.getStatus())) {
                pendingBookings++;
            } else if ("completed".equals(booking.getStatus())) {
                completedBookings++;
            }
            totalSpent += booking.getTotalAmount();
        }
        
        // Update UI
        binding.progressBar.setVisibility(View.GONE);
        binding.tvTotalBookings.setText(String.valueOf(totalBookings));
        binding.tvPendingBookings.setText(String.valueOf(pendingBookings));
        binding.tvCompletedBookings.setText(String.valueOf(completedBookings));
        binding.tvTotalSpent.setText(String.format("$%.2f", totalSpent));
        
        // Update recent bookings (last 3)
        List<Booking> recentBookings = new ArrayList<>();
        int count = Math.min(3, userBookings.size());
        for (int i = 0; i < count; i++) {
            recentBookings.add(userBookings.get(i));
        }
        
        bookingAdapter.updateBookings(recentBookings);
    }
    
    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}

