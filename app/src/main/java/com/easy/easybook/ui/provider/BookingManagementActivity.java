package com.easy.easybook.ui.provider;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.easy.easybook.databinding.ActivityBookingManagementBinding;
import com.easy.easybook.models.Booking;
import com.easy.easybook.network.ApiClient;
import com.easy.easybook.network.responses.ApiResponse;
import com.easy.easybook.ui.provider.adapters.BookingManagementAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingManagementActivity extends AppCompatActivity {
    
    private ActivityBookingManagementBinding binding;
    private BookingManagementAdapter adapter;
    private List<Booking> bookings = new ArrayList<>();
    private String currentStatus = "all";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        setupUI();
        loadBookings();
    }
    
    private void setupUI() {
        // Setup RecyclerView
        adapter = new BookingManagementAdapter(bookings, new BookingManagementAdapter.OnBookingActionListener() {
            @Override
            public void onAcceptBooking(Booking booking) {
                updateBookingStatus(booking.getId(), "confirmed");
            }
            
            @Override
            public void onRejectBooking(Booking booking) {
                updateBookingStatus(booking.getId(), "cancelled");
            }
            
            @Override
            public void onStartService(Booking booking) {
                updateBookingStatus(booking.getId(), "in_progress");
            }
            
            @Override
            public void onCompleteService(Booking booking) {
                updateBookingStatus(booking.getId(), "completed");
            }
            
            @Override
            public void onViewDetails(Booking booking) {
                // TODO: Open booking details
                Toast.makeText(BookingManagementActivity.this, "View details: " + booking.getServiceName(), Toast.LENGTH_SHORT).show();
            }
        });
        
        binding.rvBookings.setLayoutManager(new LinearLayoutManager(this));
        binding.rvBookings.setAdapter(adapter);
        
        // Setup click listeners
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnAll.setOnClickListener(v -> filterBookings("all"));
        binding.btnPending.setOnClickListener(v -> filterBookings("pending"));
        binding.btnConfirmed.setOnClickListener(v -> filterBookings("confirmed"));
        binding.btnInProgress.setOnClickListener(v -> filterBookings("in_progress"));
        binding.btnCompleted.setOnClickListener(v -> filterBookings("completed"));
    }
    
    private void loadBookings() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.rvBookings.setVisibility(View.GONE);
        binding.tvNoBookings.setVisibility(View.GONE);
        
        Call<ApiResponse> call = ApiClient.getInstance(this).getApiService()
                .getMyBookings(null, currentStatus, 1, 100); // Auth header added by interceptor
        
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        JsonObject data = apiResponse.getData().getAsJsonObject();
                        JsonArray bookingsArray = data.getAsJsonArray("bookings");
                        bookings.clear();
                        
                        for (int i = 0; i < bookingsArray.size(); i++) {
                            JsonObject bookingObj = bookingsArray.get(i).getAsJsonObject();
                            Booking booking = convertJsonToBooking(bookingObj);
                            bookings.add(booking);
                        }
                        
                        adapter.notifyDataSetChanged();
                        
                        if (bookings.isEmpty()) {
                            binding.tvNoBookings.setVisibility(View.VISIBLE);
                        } else {
                            binding.rvBookings.setVisibility(View.VISIBLE);
                        }
                    } else {
                        showNoBookings();
                    }
                } else {
                    showNoBookings();
                    Toast.makeText(BookingManagementActivity.this, "Failed to load bookings", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                showNoBookings();
                Toast.makeText(BookingManagementActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private Booking convertJsonToBooking(JsonObject bookingObj) {
        Booking booking = new Booking();
        booking.setId(bookingObj.get("_id").getAsString());
        booking.setStatus(bookingObj.get("status").getAsString());
        booking.setPrice(bookingObj.get("payment").getAsJsonObject().get("amount").getAsDouble());
        
        // Parse service info
        if (bookingObj.has("service") && bookingObj.get("service").isJsonObject()) {
            JsonObject service = bookingObj.getAsJsonObject("service");
            booking.setServiceName(service.get("name").getAsString());
        }
        
        // Parse customer info
        if (bookingObj.has("customer") && bookingObj.get("customer").isJsonObject()) {
            JsonObject customer = bookingObj.getAsJsonObject("customer");
            booking.setCustomerName(customer.get("firstName").getAsString() + " " + customer.get("lastName").getAsString());
        }
        
        // Parse booking date and time
        if (bookingObj.has("bookingDate")) {
            try {
                String dateString = bookingObj.get("bookingDate").getAsString().split("T")[0];
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date bookingDate = sdf.parse(dateString);
                booking.setBookingDate(bookingDate);
            } catch (Exception e) {
                // Handle parsing error - set current date as fallback
                booking.setBookingDate(new Date());
            }
        }
        if (bookingObj.has("startTime")) {
            booking.setTimeSlot(bookingObj.get("startTime").getAsString());
        }
        
        return booking;
    }
    
    private void showNoBookings() {
        binding.rvBookings.setVisibility(View.GONE);
        binding.tvNoBookings.setVisibility(View.VISIBLE);
    }
    
    private void filterBookings(String status) {
        currentStatus = status;
        updateFilterButtons();
        loadBookings();
    }
    
    private void updateFilterButtons() {
        // Reset all buttons
        binding.btnAll.setSelected(false);
        binding.btnPending.setSelected(false);
        binding.btnConfirmed.setSelected(false);
        binding.btnInProgress.setSelected(false);
        binding.btnCompleted.setSelected(false);
        
        // Select current button
        switch (currentStatus) {
            case "all":
                binding.btnAll.setSelected(true);
                break;
            case "pending":
                binding.btnPending.setSelected(true);
                break;
            case "confirmed":
                binding.btnConfirmed.setSelected(true);
                break;
            case "in_progress":
                binding.btnInProgress.setSelected(true);
                break;
            case "completed":
                binding.btnCompleted.setSelected(true);
                break;
        }
    }
    
    private void updateBookingStatus(String bookingId, String newStatus) {
        // TODO: Implement update booking status API call
        Toast.makeText(this, "Update booking " + bookingId + " to " + newStatus, Toast.LENGTH_SHORT).show();
        // For now, just reload the bookings
        loadBookings();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
