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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.easy.easybook.R;
import com.easy.easybook.databinding.FragmentBookingHistoryBinding;
import com.easy.easybook.models.Booking;
import com.easy.easybook.models.Service;
import com.easy.easybook.ui.customer.MyBookingsActivity;
import com.easy.easybook.ui.customer.adapters.BookingsAdapter;
import com.easy.easybook.data.LocalDataManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Booking History Fragment for customers
 * Shows past and current bookings
 */
public class BookingHistoryFragment extends Fragment {
    
    private FragmentBookingHistoryBinding binding;
    private BookingsAdapter adapter;
    private List<Booking> recentBookings;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBookingHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupUI();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when fragment becomes visible
        loadBookingData();
        if (adapter != null) {
            adapter.setBookings(recentBookings);
        }
    }
    
    private void setupUI() {
        // Load booking data
        loadBookingData();
        
        // Setup click listeners
        setupClickListeners();
        
        // Setup RecyclerView
        setupRecyclerView();
    }
    
    private void loadBookingData() {
        // Load bookings from LocalDataManager
        LocalDataManager dataManager = LocalDataManager.getInstance(getContext());
        List<Booking> allBookings = dataManager.getUserBookings("current_user"); // In real app, get from login
        
        // Get recent bookings (last 3)
        recentBookings = new ArrayList<>();
        int count = Math.min(3, allBookings.size());
        for (int i = 0; i < count; i++) {
            recentBookings.add(allBookings.get(i));
        }
        
        // Update stats
        updateStats(allBookings);
    }
    
    private void updateStats(List<Booking> allBookings) {
        int pendingCount = 0;
        int confirmedCount = 0;
        int totalCount = allBookings.size();
        
        for (Booking booking : allBookings) {
            if ("pending".equals(booking.getStatus())) {
                pendingCount++;
            } else if ("confirmed".equals(booking.getStatus())) {
                confirmedCount++;
            }
        }
        
        binding.tvPendingCount.setText(String.valueOf(pendingCount));
        binding.tvConfirmedCount.setText(String.valueOf(confirmedCount));
        
        // Update total bookings if there's a total count display
        // You can add this to the layout if needed
    }
    
    private void setupClickListeners() {
        // View All button
        binding.btnViewAll.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MyBookingsActivity.class);
            startActivity(intent);
        });
    }
    
    private void setupRecyclerView() {
        adapter = new BookingsAdapter(new BookingsAdapter.OnBookingActionListener() {
            @Override
            public void onCancelBooking(Booking booking) {
                // Handle cancel booking
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                builder.setTitle("Cancel Booking")
                        .setMessage("Are you sure you want to cancel this booking?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            com.easy.easybook.data.LocalDataManager dataManager = com.easy.easybook.data.LocalDataManager.getInstance(getContext());
                            dataManager.updateBookingStatus(booking.getId(), "cancelled");
                            loadBookings();
                            Toast.makeText(getContext(), "Booking cancelled", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
            
            @Override
            public void onRescheduleBooking(Booking booking) {
                // Navigate to reschedule activity with booking data
                android.content.Intent intent = new android.content.Intent(getContext(), com.easy.easybook.ui.customer.RescheduleActivity.class);
                intent.putExtra("reschedule_booking", booking);
                startActivity(intent);
            }
            
            @Override
            public void onRateBooking(Booking booking) {
                // Navigate to rating activity
                android.content.Intent intent = new android.content.Intent(getContext(), com.easy.easybook.ui.customer.RatingActivity.class);
                intent.putExtra("booking", booking);
                startActivity(intent);
            }
        });
        binding.rvRecentBookings.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvRecentBookings.setAdapter(adapter);
        
        // Set bookings
        adapter.setBookings(recentBookings);
    }
    
    private void loadBookings() {
        loadBookingData();
        if (adapter != null) {
            adapter.setBookings(recentBookings);
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
