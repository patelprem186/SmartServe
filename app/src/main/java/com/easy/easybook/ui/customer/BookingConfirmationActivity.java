package com.easy.easybook.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.easy.easybook.R;
import com.easy.easybook.databinding.ActivityBookingConfirmationBinding;
import com.easy.easybook.models.Booking;
import com.easy.easybook.models.BookingRequest;
import com.easy.easybook.data.LocalDataManager;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class BookingConfirmationActivity extends AppCompatActivity {

    private ActivityBookingConfirmationBinding binding;
    private BookingRequest bookingRequest;
    private String bookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingConfirmationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentData();
        setupUI();
        displayBookingDetails();
        saveBookingToDatabase();
        sendConfirmationNotification();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        bookingRequest = (BookingRequest) intent.getSerializableExtra("booking_request");
        bookingId = generateBookingId();
    }

    private void setupUI() {
        // Back button
        binding.btnBack.setOnClickListener(v -> finish());

        // Action buttons
        binding.btnViewBookings.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyBookingsActivity.class);
            startActivity(intent);
            finish();
        });

        binding.btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomerMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void displayBookingDetails() {
        if (bookingRequest == null) {
            Toast.makeText(this, "Booking details not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Display booking details
        binding.tvServiceName.setText(bookingRequest.getServiceName());
        binding.tvProviderName.setText(bookingRequest.getProviderName());
        
        // Format date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        binding.tvBookingDate.setText(dateFormat.format(bookingRequest.getBookingDate()));
        
        binding.tvBookingTime.setText(bookingRequest.getTimeSlot());
        
        // Format address
        String address = bookingRequest.getAddress() + ", " + 
                        bookingRequest.getCity() + ", " + 
                        bookingRequest.getState() + " " + 
                        bookingRequest.getZipCode();
        binding.tvAddress.setText(address);
        
        // Format amount
        binding.tvTotalAmount.setText(String.format("$%.2f", bookingRequest.getTotalAmount()));
        
        // Display booking ID
        binding.tvBookingId.setText(bookingId);
    }

    private String generateBookingId() {
        // Generate a unique booking ID
        Random random = new Random();
        int randomNumber = random.nextInt(999999);
        return String.format("BK-%d-%06d", 
            new Date().getYear() + 1900, randomNumber);
    }

    private void sendConfirmationNotification() {
        // Send confirmation notification to customer
        sendNotificationToCustomer();
        
        // Send notification to provider
        sendNotificationToProvider();
    }

    private void sendNotificationToCustomer() {
        // This would integrate with Firebase Cloud Messaging
        // For now, we'll just show a toast
        Toast.makeText(this, "Confirmation sent to your email", Toast.LENGTH_SHORT).show();
        
        // TODO: Implement actual FCM notification
        // FirebaseMessaging.getInstance().send(notification);
    }

    private void sendNotificationToProvider() {
        // This would send a notification to the service provider
        // about the new booking
        
        // TODO: Implement provider notification
        // This could be done through:
        // 1. Firebase Cloud Messaging
        // 2. Push notification service
        // 3. Email notification
    }

    private void saveBookingToDatabase() {
        // Create a Booking object from BookingRequest
        Booking booking = new Booking(
            bookingId,
            bookingRequest.getServiceId(),
            bookingRequest.getServiceName(),
            "General", // serviceCategory - you might want to get this from the service
            bookingRequest.getProviderId(),
            bookingRequest.getProviderName(),
            bookingRequest.getCustomerId(),
            bookingRequest.getCustomerName(),
            bookingRequest.getCustomerEmail(),
            bookingRequest.getCustomerPhone(),
            bookingRequest.getAddress(),
            bookingRequest.getCity(),
            bookingRequest.getState(),
            bookingRequest.getZipCode(),
            bookingRequest.getBookingDate(),
            bookingRequest.getTimeSlot(),
            "confirmed",
            bookingRequest.getTotalAmount(),
            bookingRequest.getNotes()
        );

        // Save booking using LocalDataManager
        LocalDataManager dataManager = LocalDataManager.getInstance(this);
        dataManager.saveBooking(booking);
        
        Toast.makeText(this, "Booking saved successfully", Toast.LENGTH_SHORT).show();
    }
}
