package com.easy.easybook.ui.customer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.easy.easybook.databinding.ActivityRescheduleBinding;
import com.easy.easybook.models.Booking;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RescheduleActivity extends AppCompatActivity {

    private ActivityRescheduleBinding binding;
    private Booking booking;
    private Calendar selectedDate;
    private String selectedTimeSlot;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRescheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Get booking to reschedule
        booking = (Booking) getIntent().getSerializableExtra("reschedule_booking");
        if (booking == null) {
            Toast.makeText(this, "Booking not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        setupUI();
        setupClickListeners();
    }
    
    private void setupUI() {
        // Set booking info
        binding.tvServiceName.setText(booking.getServiceName());
        binding.tvCurrentDetails.setText(formatBookingDetails());
        
        // Set current date/time
        selectedDate = Calendar.getInstance();
        selectedDate.setTime(booking.getBookingDate());
        selectedTimeSlot = booking.getTimeSlot();
        updateDateTimeDisplay();
        
        // Set current data values in form
        binding.etBookingDate.setText(formatDate(selectedDate.getTime()));
        binding.etTimeSlot.setText(selectedTimeSlot);
    }
    
    private void setupClickListeners() {
        // Back button
        binding.btnBack.setOnClickListener(v -> finish());
        
        // Date picker
        binding.etBookingDate.setOnClickListener(v -> showDatePickerDialog());
        binding.etBookingDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) showDatePickerDialog();
        });
        
        // Time slot picker
        binding.etTimeSlot.setOnClickListener(v -> showTimePickerDialog());
        binding.etTimeSlot.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) showTimePickerDialog();
        });
        
        // Reschedule button
        binding.btnReschedule.setOnClickListener(v -> rescheduleBooking());
    }
    
    private void showDatePickerDialog() {
        Calendar calendar = selectedDate != null ? (Calendar) selectedDate.clone() : Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
            (view, year, month, dayOfMonth) -> {
                selectedDate.set(year, month, dayOfMonth);
                updateDateTimeDisplay();
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        // Don't allow past dates
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.show();
    }
    
    private void showTimePickerDialog() {
        String[] timeSlots = {
            "8:00 AM - 9:00 AM",
            "9:00 AM - 10:00 AM", 
            "10:00 AM - 11:00 AM",
            "11:00 AM - 12:00 PM",
            "12:00 PM - 1:00 PM",
            "1:00 PM - 2:00 PM",
            "2:00 PM - 3:00 PM",
            "3:00 PM - 4:00 PM",
            "4:00 PM - 5:00 PM",
            "5:00 PM - 6:00 PM",
            "6:00 PM - 7:00 PM",
            "7:00 PM - 8:00 PM"
        };
        
        new android.app.AlertDialog.Builder(this)
            .setTitle("Select New Time")
            .setItems(timeSlots, (dialog, which) -> {
                selectedTimeSlot = timeSlots[which];
                updateDateTimeDisplay();
            })
            .show();
    }
    
    private void updateDateTimeDisplay() {
        if (selectedDate != null) {
            binding.etBookingDate.setText(formatDate(selectedDate.getTime()));
        }
        if (selectedTimeSlot != null) {
            binding.etTimeSlot.setText(selectedTimeSlot);
        }
    }
    
    private void rescheduleBooking() {
        if (validateForm()) {
            // Update booking with new date/time
            booking.setBookingDate(selectedDate.getTime());
            booking.setTimeSlot(selectedTimeSlot);
            
            // Save to local data manager
            com.easy.easybook.data.LocalDataManager dataManager = 
                com.easy.easybook.data.LocalDataManager.getInstance(this);
            
            List<Booking> allBookings = dataManager.getAllBookings();
            for (int i = 0; i < allBookings.size(); i++) {
                if (allBookings.get(i).getId().equals(booking.getId())) {
                    allBookings.set(i, booking);
                    break;
                }
            }
            dataManager.saveBookings(allBookings);
            
            Toast.makeText(this, "Booking rescheduled successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    private boolean validateForm() {
        if (selectedDate == null) {
            binding.etBookingDate.setError("Please select a date");
            return false;
        }
        
        if (selectedTimeSlot == null || selectedTimeSlot.isEmpty()) {
            binding.etTimeSlot.setError("Please select a time slot");
            return false;
        }
        
        // Check if new date/time is different from original
        if (booking.getBookingDate().getTime() == selectedDate.getTimeInMillis() 
            && booking.getTimeSlot().equals(selectedTimeSlot)) {
            Toast.makeText(this, "No changes made to the booking", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }
    
    private String formatDate(java.util.Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return formatter.format(date);
    }
    
    private String formatBookingDetails() {
        String date = formatDate(booking.getBookingDate());
        return String.format("Current: %s • %s", date, booking.getTimeSlot());
    }
    
    // Method to handle onClick="finish" from XML
    public void finish(android.view.View view) {
        finish();
    }
}
