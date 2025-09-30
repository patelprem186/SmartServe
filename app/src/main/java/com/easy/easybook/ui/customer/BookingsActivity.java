package com.easy.easybook.ui.customer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.easy.easybook.R;
import com.easy.easybook.models.Booking;
import com.easy.easybook.models.CartItem;
import com.easy.easybook.models.Service;
import com.easy.easybook.utils.AustralianAddressUtils;
import com.easy.easybook.utils.AustralianValidationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingsActivity extends AppCompatActivity {

    private TextInputEditText etBookingDate, etTimeSlot, etAddress, etZipCode, etNotes;
    private AutoCompleteTextView etCity, etState;
    private MaterialButton btnAddToCart, btnBookNow;
    private TextView tvServiceName, tvServicePrice, tvProviderName;
    private Service selectedService;
    private Calendar selectedDate;
    private String selectedTimeSlot;
    private boolean isReschedule;
    private Booking rescheduleBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);

        // Check if this is a reschedule first
        isReschedule = getIntent().getBooleanExtra("is_reschedule", false);
        if (isReschedule) {
            rescheduleBooking = (Booking) getIntent().getSerializableExtra("reschedule_booking");
            if (rescheduleBooking != null) {
                // For reschedule, create a service object from booking data
                selectedService = new Service();
                selectedService.setId(rescheduleBooking.getServiceId() != null ? rescheduleBooking.getServiceId() : "reschedule_service");
                selectedService.setName(rescheduleBooking.getServiceName());
                selectedService.setPrice(rescheduleBooking.getTotalAmount());
                selectedService.setCategory(rescheduleBooking.getServiceCategory());
                selectedService.setDescription(rescheduleBooking.getServiceName());
                selectedService.setDuration("1 hour");
                selectedService.setRating(4.5f);
                
                prefillRescheduleData(rescheduleBooking);
            } else {
                Toast.makeText(this, "Reschedule booking not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        } else {
            // Get service data from intent for new bookings
            selectedService = (Service) getIntent().getSerializableExtra("service");
            if (selectedService == null) {
                Toast.makeText(this, "Service not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }

        initViews();
        setupClickListeners();
        populateServiceData();
    }

    private void initViews() {
        etBookingDate = findViewById(R.id.etBookingDate);
        etTimeSlot = findViewById(R.id.etTimeSlot);
        etAddress = findViewById(R.id.etAddress);
        etCity = findViewById(R.id.etCity);
        etState = findViewById(R.id.etState);
        etZipCode = findViewById(R.id.etZipCode);
        etNotes = findViewById(R.id.etNotes);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBookNow = findViewById(R.id.btnBookNow);
        tvServiceName = findViewById(R.id.tvServiceName);
        tvServicePrice = findViewById(R.id.tvServicePrice);
        tvProviderName = findViewById(R.id.tvProviderName);

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
        
        // Setup Australian dropdowns
        setupAustralianDropdowns();
    }
    
    private void setupAustralianDropdowns() {
        // Setup state dropdown
        setupStateDropdown();
    }
    
    private void setupStateDropdown() {
        // Get Australian states
        List<String> states = AustralianAddressUtils.getAllStates();
        
        // Create adapter for state dropdown
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_dropdown_item_1line, states);
        etState.setAdapter(stateAdapter);
        
        // Handle state selection
        etState.setOnItemClickListener((parent, view, position, id) -> {
            String selectedState = states.get(position);
            String stateCode = AustralianValidationUtils.getStateCode(selectedState);
            setupCityDropdown(stateCode);
            etCity.setText(""); // Clear city selection
        });
        
        // Show dropdown when clicked
        etState.setOnClickListener(v -> etState.showDropDown());
    }
    
    private void setupCityDropdown(String stateCode) {
        // Get cities for the selected state
        List<String> cities = AustralianAddressUtils.getCitiesForState(stateCode);
        
        // Create adapter for city dropdown
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_dropdown_item_1line, cities);
        etCity.setAdapter(cityAdapter);
        
        // Show dropdown when clicked
        etCity.setOnClickListener(v -> etCity.showDropDown());
    }

    private void setupClickListeners() {
        if (etBookingDate != null) {
            etBookingDate.setOnClickListener(v -> showDatePicker());
        }
        if (etTimeSlot != null) {
            etTimeSlot.setOnClickListener(v -> showTimeSlotPicker());
        }
        if (btnAddToCart != null) {
            btnAddToCart.setOnClickListener(v -> addToCart());
        }
        if (btnBookNow != null) {
            btnBookNow.setOnClickListener(v -> bookNow());
        }
    }

    private void populateServiceData() {
        // Update service name and price in the UI
        if (selectedService != null) {
            if (tvServiceName != null) {
                tvServiceName.setText(selectedService.getName());
            }
            if (tvServicePrice != null) {
                tvServicePrice.setText("$" + String.format("%.2f", selectedService.getPrice()));
            }
            if (tvProviderName != null) {
                tvProviderName.setText("by " + selectedService.getProviderName());
            }
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                        if (etBookingDate != null) {
                            etBookingDate.setText(sdf.format(selectedDate.getTime()));
                        }
                        
                        // Clear time slot when date changes
                        if (etTimeSlot != null) {
                            etTimeSlot.setText("");
                        }
                        selectedTimeSlot = null;
                    }
                }, year, month, day);
        
        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        // Set maximum date to 3 months from now
        calendar.add(Calendar.MONTH, 3);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void showTimeSlotPicker() {
        if (selectedDate == null) {
            Toast.makeText(this, "Please select a date first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Check if selected date is today
        Calendar today = Calendar.getInstance();
        boolean isToday = selectedDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                         selectedDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);
        
        // Generate available time slots based on current time
        List<String> availableSlots = generateAvailableTimeSlots(isToday);
        
        if (availableSlots.isEmpty()) {
            Toast.makeText(this, "No available time slots for this date", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String[] timeSlots = availableSlots.toArray(new String[0]);
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Select Time Slot");
        builder.setItems(timeSlots, (dialog, which) -> {
            selectedTimeSlot = timeSlots[which];
            if (etTimeSlot != null) {
                etTimeSlot.setText(selectedTimeSlot);
            }
        });
        builder.show();
    }
    
    private List<String> generateAvailableTimeSlots(boolean isToday) {
        List<String> slots = new ArrayList<>();
        Calendar now = Calendar.getInstance();
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        
        // Define all possible time slots
        String[] allSlots = {
            "09:00 AM - 10:00 AM",
            "10:00 AM - 11:00 AM", 
            "11:00 AM - 12:00 PM",
            "12:00 PM - 01:00 PM",
            "01:00 PM - 02:00 PM",
            "02:00 PM - 03:00 PM",
            "03:00 PM - 04:00 PM",
            "04:00 PM - 05:00 PM",
            "05:00 PM - 06:00 PM"
        };
        
        // If it's today, filter out past time slots
        if (isToday) {
            for (String slot : allSlots) {
                int slotHour = getSlotHour(slot);
                if (slotHour > currentHour + 1) { // Allow booking 1 hour in advance
                    slots.add(slot);
                }
            }
        } else {
            // For future dates, show all slots
            slots.addAll(Arrays.asList(allSlots));
        }
        
        return slots;
    }
    
    private int getSlotHour(String timeSlot) {
        // Extract hour from time slot (e.g., "09:00 AM - 10:00 AM" -> 9)
        String startTime = timeSlot.split(" - ")[0];
        String hourStr = startTime.split(":")[0];
        int hour = Integer.parseInt(hourStr);
        
        // Convert to 24-hour format if PM
        if (startTime.contains("PM") && hour != 12) {
            hour += 12;
        } else if (startTime.contains("AM") && hour == 12) {
            hour = 0;
        }
        
        return hour;
    }


    private void addToCart() {
        if (validateForm()) {
            // Add to cart using LocalDataManager
            com.easy.easybook.data.LocalDataManager dataManager = com.easy.easybook.data.LocalDataManager.getInstance(this);
            dataManager.addToCart(selectedService);
            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void bookNow() {
        if (validateForm()) {
            if (isReschedule && rescheduleBooking != null) {
                // Handle reschedule - update existing booking
                updateBookingReschedule();
            } else {
                // Navigate to payment activity for new booking
                Intent intent = new Intent(this, PaymentActivity.class);
                intent.putExtra("service", selectedService);
                intent.putExtra("booking_date", selectedDate.getTime());
                intent.putExtra("time_slot", selectedTimeSlot);
                intent.putExtra("address", etAddress.getText().toString().trim());
                intent.putExtra("city", etCity.getText().toString().trim());
                intent.putExtra("state", etState.getText().toString().trim());
                intent.putExtra("zip_code", etZipCode.getText().toString().trim());
                intent.putExtra("notes", etNotes.getText().toString().trim());
                intent.putExtra("amount", selectedService.getPrice());
                startActivity(intent);
            }
        }
    }
    
    private void updateBookingReschedule() {
        // Update the existing booking with new time/date details
        com.easy.easybook.data.LocalDataManager dataManager = com.easy.easybook.data.LocalDataManager.getInstance(this);
        
        // Update booking details
        rescheduleBooking.setBookingDate(selectedDate.getTime());
        rescheduleBooking.setTimeSlot(selectedTimeSlot);
        rescheduleBooking.setAddress(etAddress.getText().toString().trim());
        rescheduleBooking.setCity(etCity.getText().toString().trim());
        rescheduleBooking.setState(etState.getText().toString().trim());
        rescheduleBooking.setZipCode(etZipCode.getText().toString().trim());
        rescheduleBooking.setNotes(etNotes.getText().toString().trim());
        
        // Save the updated booking
        List<Booking> allBookings = dataManager.getAllBookings();
        for (int i = 0; i < allBookings.size(); i++) {
            if (allBookings.get(i).getId().equals(rescheduleBooking.getId())) {
                allBookings.set(i, rescheduleBooking);
                break;
            }
        }
        dataManager.saveBookings(allBookings);
        
        Toast.makeText(this, "Booking rescheduled successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean validateForm() {
        if (selectedDate == null) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (selectedTimeSlot == null || selectedTimeSlot.isEmpty()) {
            Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (etAddress != null && etAddress.getText().toString().trim().isEmpty()) {
            etAddress.setError("Please enter address");
            etAddress.requestFocus();
            return false;
        }
        
        if (etCity != null && etCity.getText().toString().trim().isEmpty()) {
            etCity.setError("Please select a city");
            etCity.requestFocus();
            return false;
        }
        
        if (etState != null && etState.getText().toString().trim().isEmpty()) {
            etState.setError("Please select a state");
            etState.requestFocus();
            return false;
        }
        
        if (etZipCode != null) {
            String zipCode = etZipCode.getText().toString().trim();
            if (zipCode.isEmpty()) {
                etZipCode.setError("Zip code is required");
                etZipCode.requestFocus();
                return false;
            }
            if (!AustralianValidationUtils.isValidAustralianPostcode(zipCode)) {
                etZipCode.setError("Please enter a valid 4-digit Australian postcode");
                etZipCode.requestFocus();
                return false;
            }
        }
        
        return true;
    }

    
    private void prefillRescheduleData(Booking booking) {
        // Pre-fill the form with existing booking data
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        if (etBookingDate != null) {
            etBookingDate.setText(dateFormat.format(booking.getBookingDate()));
        }
        if (etTimeSlot != null) {
            etTimeSlot.setText(booking.getTimeSlot());
        }
        if (etAddress != null) {
            etAddress.setText(booking.getAddress());
        }
        if (etCity != null) {
            etCity.setText(booking.getCity());
        }
        if (etState != null) {
            etState.setText(booking.getState());
        }
        if (etZipCode != null) {
            etZipCode.setText(booking.getZipCode());
        }
        if (etNotes != null) {
            etNotes.setText(booking.getNotes());
        }
        
        // Set the selected date and time slot
        selectedDate = Calendar.getInstance();
        selectedDate.setTime(booking.getBookingDate());
        selectedTimeSlot = booking.getTimeSlot();
        
        // Update button text to indicate reschedule
        if (btnBookNow != null) {
            btnBookNow.setText("Reschedule Booking");
        }
    }
}
