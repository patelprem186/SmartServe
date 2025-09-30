package com.easy.easybook.ui.customer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.easy.easybook.databinding.ActivityBookingBinding;
import com.easy.easybook.models.Service;
import com.easy.easybook.network.ApiClient;
import com.easy.easybook.network.responses.ApiResponse;
import com.easy.easybook.utils.AustralianAddressUtils;
import com.easy.easybook.utils.AustralianValidationUtils;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity {
    
    private ActivityBookingBinding binding;
    private Service selectedService;
    private Calendar selectedDateTime;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Get service data from intent
        String serviceId = getIntent().getStringExtra("service_id");
        String serviceName = getIntent().getStringExtra("service_name");
        String servicePrice = getIntent().getStringExtra("service_price");
        
        selectedDateTime = Calendar.getInstance();
        
        setupUI(serviceName, servicePrice);
        setupClickListeners();
        setupAustralianDropdowns();
    }
    
    private void setupUI(String serviceName, String servicePrice) {
        binding.tvServiceName.setText(serviceName != null ? serviceName : "Service");
        binding.tvServicePrice.setText("$" + (servicePrice != null ? servicePrice : "0"));
        
        // Set default date and time
        updateDateTimeDisplay();
    }
    
    private void setupClickListeners() {
        binding.btnSelectDate.setOnClickListener(v -> showDatePicker());
        binding.btnSelectTime.setOnClickListener(v -> showTimePicker());
        binding.btnBookService.setOnClickListener(v -> bookService());
        binding.btnBack.setOnClickListener(v -> finish());
    }
    
    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                selectedDateTime.set(Calendar.YEAR, year);
                selectedDateTime.set(Calendar.MONTH, month);
                selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateTimeDisplay();
            },
            selectedDateTime.get(Calendar.YEAR),
            selectedDateTime.get(Calendar.MONTH),
            selectedDateTime.get(Calendar.DAY_OF_MONTH)
        );
        
        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    
    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
            this,
            (view, hourOfDay, minute) -> {
                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedDateTime.set(Calendar.MINUTE, minute);
                updateDateTimeDisplay();
            },
            selectedDateTime.get(Calendar.HOUR_OF_DAY),
            selectedDateTime.get(Calendar.MINUTE),
            true
        );
        timePickerDialog.show();
    }
    
    private void updateDateTimeDisplay() {
        binding.tvSelectedDate.setText(dateFormat.format(selectedDateTime.getTime()));
        binding.tvSelectedTime.setText(timeFormat.format(selectedDateTime.getTime()));
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
        binding.actvState.setAdapter(stateAdapter);
        
        // Handle state selection
        binding.actvState.setOnItemClickListener((parent, view, position, id) -> {
            String selectedState = states.get(position);
            String stateCode = AustralianValidationUtils.getStateCode(selectedState);
            setupCityDropdown(stateCode);
            binding.actvCity.setText(""); // Clear city selection
        });
        
        // Show dropdown when clicked
        binding.actvState.setOnClickListener(v -> binding.actvState.showDropDown());
    }
    
    private void setupCityDropdown(String stateCode) {
        // Get cities for the selected state
        List<String> cities = AustralianAddressUtils.getCitiesForState(stateCode);
        
        // Create adapter for city dropdown
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_dropdown_item_1line, cities);
        binding.actvCity.setAdapter(cityAdapter);
        
        // Show dropdown when clicked
        binding.actvCity.setOnClickListener(v -> binding.actvCity.showDropDown());
    }
    
    private void bookService() {
        String address = binding.etAddress.getText().toString().trim();
        String city = binding.actvCity.getText().toString().trim();
        String state = binding.actvState.getText().toString().trim();
        String zipCode = binding.etZipCode.getText().toString().trim();
        String notes = binding.etNotes.getText().toString().trim();
        
        // Validate form
        if (TextUtils.isEmpty(address)) {
            binding.etAddress.setError("Address is required");
            return;
        }
        
        if (TextUtils.isEmpty(city)) {
            binding.actvCity.setError("Please select a city");
            return;
        }
        
        if (TextUtils.isEmpty(state)) {
            binding.actvState.setError("Please select a state");
            return;
        }
        
        if (TextUtils.isEmpty(zipCode)) {
            binding.etZipCode.setError("Zip code is required");
            return;
        }
        
        if (!AustralianValidationUtils.isValidAustralianPostcode(zipCode)) {
            binding.etZipCode.setError("Please enter a valid 4-digit Australian postcode");
            return;
        }
        
        // Show loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnBookService.setEnabled(false);
        
        // Create booking request
        JsonObject bookingRequest = new JsonObject();
        bookingRequest.addProperty("serviceId", getIntent().getStringExtra("service_id"));
        bookingRequest.addProperty("bookingDate", dateFormat.format(selectedDateTime.getTime()));
        bookingRequest.addProperty("startTime", timeFormat.format(selectedDateTime.getTime()));
        
        JsonObject location = new JsonObject();
        location.addProperty("type", "at_customer");
        JsonObject addressObj = new JsonObject();
        addressObj.addProperty("street", address);
        addressObj.addProperty("city", city);
        addressObj.addProperty("state", state);
        addressObj.addProperty("zipCode", zipCode);
        location.add("address", addressObj);
        bookingRequest.add("location", location);
        
        if (!TextUtils.isEmpty(notes)) {
            JsonObject notesObj = new JsonObject();
            notesObj.addProperty("customer", notes);
            bookingRequest.add("notes", notesObj);
        }
        
        // Make API call
        Call<ApiResponse> call = ApiClient.getInstance(this).getApiService()
                .createBooking(null, bookingRequest); // Auth header will be added by interceptor
        
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnBookService.setEnabled(true);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(BookingActivity.this, "Booking created successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(BookingActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(BookingActivity.this, "Failed to create booking", Toast.LENGTH_LONG).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnBookService.setEnabled(true);
                Toast.makeText(BookingActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
