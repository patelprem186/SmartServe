package com.easy.easybook.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.easy.easybook.R;
import com.easy.easybook.models.Booking;
import com.easy.easybook.ui.customer.adapters.BookingsAdapter;
import com.easy.easybook.data.LocalDataManager;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyBookingsActivity extends AppCompatActivity implements BookingsAdapter.OnBookingActionListener {

    private RecyclerView rvBookings;
    private LinearLayout layoutEmpty;
    private MaterialButton btnAll, btnPending, btnConfirmed, btnInProgress, btnCompleted;
    private BookingsAdapter adapter;
    private List<Booking> allBookings;
    private String currentFilter = "all";
    private LocalDataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        dataManager = LocalDataManager.getInstance(this);
        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadBookings();
        filterBookings("all");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh bookings when returning to this activity
        loadBookings();
        filterBookings(currentFilter);
    }

    private void initViews() {
        rvBookings = findViewById(R.id.rvBookings);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        btnAll = findViewById(R.id.btnAll);
        btnPending = findViewById(R.id.btnPending);
        btnConfirmed = findViewById(R.id.btnConfirmed);
        btnInProgress = findViewById(R.id.btnInProgress);
        btnCompleted = findViewById(R.id.btnCompleted);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnFilter).setOnClickListener(v -> showFilterDialog());
    }

    private void setupRecyclerView() {
        adapter = new BookingsAdapter(this);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        rvBookings.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnAll.setOnClickListener(v -> filterBookings("all"));
        btnPending.setOnClickListener(v -> filterBookings("pending"));
        btnConfirmed.setOnClickListener(v -> filterBookings("confirmed"));
        btnInProgress.setOnClickListener(v -> filterBookings("in_progress"));
        btnCompleted.setOnClickListener(v -> filterBookings("completed"));
        
        // Handle back button - redirect to home instead of following activity stack
        findViewById(R.id.btnBack).setOnClickListener(v -> navigateToHome());
    }
    
    private void navigateToHome() {
        Intent intent = new Intent(this, CustomerMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void loadBookings() {
        // Load real bookings from LocalDataManager
        allBookings = dataManager.getUserBookings("current_user"); // In real app, get from login
        
        if (allBookings.isEmpty()) {
            android.util.Log.d("MyBookingsActivity", "No bookings found");
        } else {
            android.util.Log.d("MyBookingsActivity", "Loaded " + allBookings.size() + " bookings from local storage");
        }
        
        // Update adapter with new data
        adapter.setBookings(allBookings);
    }

    private void filterBookings(String filter) {
        currentFilter = filter;
        updateFilterButtons();
        
        List<Booking> filteredBookings = new ArrayList<>();
        
        if (filter.equals("all")) {
            filteredBookings.addAll(allBookings);
        } else {
            for (Booking booking : allBookings) {
                if (booking.getStatus().equalsIgnoreCase(filter)) {
                    filteredBookings.add(booking);
                }
            }
        }
        
        adapter.setBookings(filteredBookings);
        
        // Show/hide empty state
        if (filteredBookings.isEmpty()) {
            rvBookings.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvBookings.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void updateFilterButtons() {
        // Reset all buttons
        btnAll.setBackgroundTintList(getColorStateList(R.color.background_light));
        btnAll.setTextColor(getColor(R.color.text_secondary));
        btnAll.setStrokeColor(getColorStateList(R.color.primary));
        btnAll.setStrokeWidth(1);
        
        btnPending.setBackgroundTintList(getColorStateList(R.color.background_light));
        btnPending.setTextColor(getColor(R.color.text_secondary));
        btnPending.setStrokeColor(getColorStateList(R.color.primary));
        btnPending.setStrokeWidth(1);
        
        btnConfirmed.setBackgroundTintList(getColorStateList(R.color.background_light));
        btnConfirmed.setTextColor(getColor(R.color.text_secondary));
        btnConfirmed.setStrokeColor(getColorStateList(R.color.primary));
        btnConfirmed.setStrokeWidth(1);
        
        btnInProgress.setBackgroundTintList(getColorStateList(R.color.background_light));
        btnInProgress.setTextColor(getColor(R.color.text_secondary));
        btnInProgress.setStrokeColor(getColorStateList(R.color.primary));
        btnInProgress.setStrokeWidth(1);
        
        btnCompleted.setBackgroundTintList(getColorStateList(R.color.background_light));
        btnCompleted.setTextColor(getColor(R.color.text_secondary));
        btnCompleted.setStrokeColor(getColorStateList(R.color.primary));
        btnCompleted.setStrokeWidth(1);
        
        // Highlight selected button
        switch (currentFilter) {
            case "all":
                btnAll.setBackgroundTintList(getColorStateList(R.color.primary));
                btnAll.setTextColor(getColor(R.color.white));
                btnAll.setStrokeWidth(0);
                break;
            case "pending":
                btnPending.setBackgroundTintList(getColorStateList(R.color.primary));
                btnPending.setTextColor(getColor(R.color.white));
                btnPending.setStrokeWidth(0);
                break;
            case "confirmed":
                btnConfirmed.setBackgroundTintList(getColorStateList(R.color.primary));
                btnConfirmed.setTextColor(getColor(R.color.white));
                btnConfirmed.setStrokeWidth(0);
                break;
            case "in_progress":
                btnInProgress.setBackgroundTintList(getColorStateList(R.color.primary));
                btnInProgress.setTextColor(getColor(R.color.white));
                btnInProgress.setStrokeWidth(0);
                break;
            case "completed":
                btnCompleted.setBackgroundTintList(getColorStateList(R.color.primary));
                btnCompleted.setTextColor(getColor(R.color.white));
                btnCompleted.setStrokeWidth(0);
                break;
        }
    }

    private void showFilterDialog() {
        // Create search dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Search Bookings");
        
        // Create input field
        final android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Search by service name, provider, or status...");
        builder.setView(input);
        
        builder.setPositiveButton("Search", (dialog, which) -> {
            String searchQuery = input.getText().toString().trim();
            if (!searchQuery.isEmpty()) {
                searchBookings(searchQuery);
            } else {
                // Show all bookings if search is empty
                filterBookings(currentFilter);
            }
        });
        
        builder.setNegativeButton("Clear", (dialog, which) -> {
            // Show all bookings
            filterBookings(currentFilter);
        });
        
        builder.setNeutralButton("Cancel", null);
        builder.show();
    }
    
    private void searchBookings(String query) {
        List<Booking> filteredBookings = new ArrayList<>();
        
        for (Booking booking : allBookings) {
            boolean matchesSearch = 
                booking.getServiceName().toLowerCase().contains(query.toLowerCase()) ||
                booking.getProviderName().toLowerCase().contains(query.toLowerCase()) ||
                booking.getStatus().toLowerCase().contains(query.toLowerCase()) ||
                booking.getAddress().toLowerCase().contains(query.toLowerCase());
            
            if (matchesSearch) {
                filteredBookings.add(booking);
            }
        }
        
        adapter.setBookings(filteredBookings);
        
        // Show/hide empty state
        if (filteredBookings.isEmpty()) {
            rvBookings.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvBookings.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCancelBooking(Booking booking) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Cancel Booking")
                .setMessage("Are you sure you want to cancel this booking?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Update booking status to "cancelled"
                    dataManager.updateBookingStatus(booking.getId(), "cancelled");
                    
                    // Refresh the bookings list
                    loadBookings();
                    
                    Toast.makeText(this, "Booking cancelled", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onRescheduleBooking(Booking booking) {
        // Navigate to reschedule activity with booking data
        Intent intent = new Intent(this, RescheduleActivity.class);
        intent.putExtra("reschedule_booking", booking);
        startActivity(intent);
    }
    
    @Override
    public void onRateBooking(Booking booking) {
        Intent intent = new Intent(this, RatingActivity.class);
        intent.putExtra("booking", booking);
        startActivity(intent);
    }
}
