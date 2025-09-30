package com.easy.easybook.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.easy.easybook.R;
import com.easy.easybook.databinding.ActivityAdminDashboardBinding;
import com.easy.easybook.models.Booking;
import com.easy.easybook.models.Service;
import com.easy.easybook.models.User;
import com.easy.easybook.data.LocalDataManager;
import com.easy.easybook.data.SeedData;
import com.easy.easybook.data.ServiceManager;
import com.easy.easybook.ui.auth.LoginActivity;
import com.easy.easybook.ui.admin.adapters.AdminServiceAdapter;
import com.easy.easybook.ui.admin.adapters.FeedbackAdapter;
import com.easy.easybook.ui.admin.adapters.ProviderAdapter;
import com.easy.easybook.ui.admin.ServiceManagementActivity;
import com.easy.easybook.utils.SharedPrefsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminDashboardActivity extends AppCompatActivity implements AdminServiceAdapter.OnServiceActionListener, ProviderAdapter.OnProviderActionListener {

    private ActivityAdminDashboardBinding binding;
    private AdminServiceAdapter serviceAdapter;
    private FeedbackAdapter feedbackAdapter;
    private ProviderAdapter providerAdapter;
    private LocalDataManager dataManager;
    private List<User> allUsers;
    private List<User> serviceProviders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        dataManager = LocalDataManager.getInstance(this);
        setupUI();
        loadAnalytics();
        setupClickListeners();
        setupRecyclerView();
    }

    private void setupUI() {
        // Nothing special needed here, all UI setup in layout
    }

    private void setupClickListeners() {
        // Refresh button
        binding.btnRefresh.setOnClickListener(v -> loadAnalytics());
        
        // Of course, "Logout" button
        binding.btnLogout.setOnClickListener(v -> performLogout());
        
        // Manage Services button
        binding.btnManageServices.setOnClickListener(v -> {
            Intent intent = new Intent(this, ServiceManagementActivity.class);
            startActivity(intent);
        });
        
        // Manage Providers button
        binding.btnManageProviders.setOnClickListener(v -> {
            // TODO: Implement provider management activity
            Toast.makeText(this, "Provider management coming soon", Toast.LENGTH_SHORT).show();
        });
        
        // View Feedback button
        binding.btnViewFeedback.setOnClickListener(v -> {
            // TODO: Implement feedback viewing activity
            Toast.makeText(this, "Feedback viewer coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupRecyclerView() {
        // Services RecyclerView
        serviceAdapter = new AdminServiceAdapter(this);
        binding.rvServices.setLayoutManager(new LinearLayoutManager(this));
        binding.rvServices.setAdapter(serviceAdapter);
        
        // Feedback RecyclerView
        feedbackAdapter = new FeedbackAdapter();
        binding.rvFeedback.setLayoutManager(new LinearLayoutManager(this));
        binding.rvFeedback.setAdapter(feedbackAdapter);
        
        // Providers RecyclerView
        providerAdapter = new ProviderAdapter(this);
        binding.rvProviders.setLayoutManager(new LinearLayoutManager(this));
        binding.rvProviders.setAdapter(providerAdapter);
        
        loadServices();
        loadFeedback();
        loadProviders();
    }

    private void loadAnalytics() {
        List<Booking> allBookings = dataManager.getAllBookings();
        
        // Load users and providers
        allUsers = new ArrayList<>();
        serviceProviders = new ArrayList<>();
        
        // Get seed users (customers and providers)
        List<User> seedProviders = SeedData.getSeedProviders();
        serviceProviders.addAll(seedProviders);
        
        // Simulate some customer users (in real app, this would come from database)
        allUsers.add(new User("customer1", "Alice", "Johnson", "alice@email.com", "555-1001", "customer", true, ""));
        allUsers.add(new User("customer2", "Bob", "Smith", "bob@email.com", "555-1002", "customer", true, ""));
        allUsers.add(new User("customer3", "Carol", "Davis", "carol@email.com", "555-1003", "customer", true, ""));
        allUsers.add(new User("customer4", "David", "Wilson", "david@email.com", "555-1004", "customer", true, ""));
        allUsers.add(new User("customer5", "Emma", "Brown", "emma@email.com", "555-1005", "customer", true, ""));
        
        allUsers.addAll(serviceProviders);
        
        // Calculate analytics
        int totalBookings = allBookings.size();
        int completedBookings = 0;
        int pendingBookings = 0;
        int confirmedBookings = 0;
        int inProgressBookings = 0;
        double totalEarnings = 0.0;
        
        for (Booking booking : allBookings) {
            String status = booking.getStatus().toLowerCase();
            switch (status) {
                case "completed":
                    completedBookings++;
                    totalEarnings += booking.getTotalAmount();
                    break;
                case "pending":
                    pendingBookings++;
                    break;
                case "confirmed":
                    confirmedBookings++;
                    break;
                case "in_progress":
                    inProgressBookings++;
                    totalEarnings += booking.getTotalAmount() * 0.5; // 50% paid on acceptance
                    break;
            }
        }
        
        // Load services
        List<Service> services = dataManager.getAllServices();
        int totalServices = services.size();
        
        // Update analytics displays
        binding.tvTotalBookings.setText(String.valueOf(totalBookings));
        binding.tvCompletedBookings.setText(String.valueOf(completedBookings));
        binding.tvPendingBookings.setText(String.valueOf(pendingBookings));
        binding.tvTotalEarnings.setText(String.format(Locale.getDefault(), "$%.2f", totalEarnings));
        binding.tvTotalServices.setText(String.valueOf(totalServices));
        binding.tvTotalUsers.setText(String.valueOf(allUsers.size()));
        binding.tvTotalProviders.setText(String.valueOf(serviceProviders.size()));
        
        // Update status distribution
        binding.tvBookingsStatus.setText(String.format("Bookings: %d Completed | %d Pending | %d Confirmed | %d In Progress", 
            completedBookings, pendingBookings, confirmedBookings, inProgressBookings));
        
        // Show/hide empty state
        if (totalBookings == 0) {
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            binding.layoutStats.setVisibility(View.GONE);
        } else {
            binding.layoutEmpty.setVisibility(View.GONE);
            binding.layoutStats.setVisibility(View.VISIBLE);
        }
    }

    private void loadServices() {
        // Use centralized ServiceManager for consistency
        ServiceManager serviceManager = ServiceManager.getInstance(this);
        List<Service> services = serviceManager.getAllServices();
        serviceAdapter.setServices(services);
    }
    
    private void loadFeedback() {
        // Get bookings with ratings (feedback)
        List<Booking> allBookings = dataManager.getAllBookings();
        List<Booking> feedbackBookings = new ArrayList<>();
        
        for (Booking booking : allBookings) {
            if (booking.getRating() > 0) { // Only bookings with ratings
                feedbackBookings.add(booking);
            }
        }
        
        feedbackAdapter.setFeedbackList(feedbackBookings);
    }
    
    private void loadProviders() {
        // Load service providers
        List<User> providers = SeedData.getSeedProviders();
        providerAdapter.setProviderList(providers);
    }

    private void performLogout() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Logout")
                .setMessage("Are you sure you want to logout from the admin?" )
                .setPositiveButton("Logout", (dialog, which) -> {
                    // Navigate directly way back to login
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reload data when returning to this screen
        loadAnalytics();
        loadServices();
    }

    @Override
    public void onEditService(Service service) {
        Intent intent = new Intent(this, ServiceManagementActivity.class);
        intent.putExtra("service_to_edit", service);
        intent.putExtra("is_edit_mode", true);
        startActivity(intent);
    }

    @Override
    public void onDeleteService(Service service) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Delete Service")
                .setMessage("Are you sure you want to delete this service? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // In a real app, implement service deletion in DataManager
                    // For demo purposes, update via button
                    List<Service> services = serviceAdapter.getServices();
                    services.remove(service);
                    serviceAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "Service" + service.getName() + "deleted successfully", Toast.LENGTH_SHORT).show(); // simplified for readability
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    // Provider action methods
    @Override
    public void onDeleteProvider(User provider) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Delete Provider")
                .setMessage("Are you sure you want to delete this service provider? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Remove from list and update adapter
                    serviceProviders.remove(provider);
                    providerAdapter.setProviderList(serviceProviders);
                    Toast.makeText(this, "Provider " + provider.getFirstName() + " " + provider.getLastName() + " deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    @Override
    public void onViewProviderDetails(User provider) {
        // TODO: Implement provider details view
        Toast.makeText(this, "Provider details for " + provider.getFirstName() + " " + provider.getLastName(), Toast.LENGTH_SHORT).show();
    }
}
