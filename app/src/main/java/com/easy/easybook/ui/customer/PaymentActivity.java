package com.easy.easybook.ui.customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.easy.easybook.R;
import com.easy.easybook.data.LocalDataManager;
import com.easy.easybook.models.Booking;
import com.easy.easybook.models.Service;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {

    private Service selectedService;
    private Service[] cartItems;
    private boolean isCartCheckout;
    private Date bookingDate;
    private String timeSlot;
    private String address;
    private String notes;
    private double amount;
    
    private TextView tvServiceName, tvServiceDate, tvServiceTime, tvServicePrice, tvTotalAmount;
    private RadioGroup rgPaymentMethod;
    private RadioButton rbCreditCard, rbPayPal, rbCash;
    private MaterialCardView cardPaymentDetails;
    private TextInputEditText etCardNumber, etExpiryDate, etCVV;
    private MaterialButton btnPayNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Get data from intent
        isCartCheckout = getIntent().getBooleanExtra("is_cart_checkout", false);
        if (isCartCheckout) {
            cartItems = (Service[]) getIntent().getSerializableExtra("cart_items");
            amount = getIntent().getDoubleExtra("total_amount", 0.0);
        } else {
            selectedService = (Service) getIntent().getSerializableExtra("service");
            bookingDate = (Date) getIntent().getSerializableExtra("booking_date");
            timeSlot = getIntent().getStringExtra("time_slot");
            address = getIntent().getStringExtra("address");
            notes = getIntent().getStringExtra("notes");
            amount = getIntent().getDoubleExtra("amount", 0.0);
        }

        initViews();
        setupClickListeners();
        populateData();
    }

    private void initViews() {
        tvServiceName = findViewById(R.id.tvServiceName);
        tvServiceDate = findViewById(R.id.tvServiceDate);
        tvServiceTime = findViewById(R.id.tvServiceTime);
        tvServicePrice = findViewById(R.id.tvServicePrice);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        rbCreditCard = findViewById(R.id.rbCreditCard);
        rbPayPal = findViewById(R.id.rbPayPal);
        rbCash = findViewById(R.id.rbCash);
        
        cardPaymentDetails = findViewById(R.id.cardPaymentDetails);
        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiryDate = findViewById(R.id.etExpiryDate);
        etCVV = findViewById(R.id.etCVV);
        
        btnPayNow = findViewById(R.id.btnPayNow);

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void setupClickListeners() {
        rgPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbCreditCard) {
                cardPaymentDetails.setVisibility(View.VISIBLE);
            } else {
                cardPaymentDetails.setVisibility(View.GONE);
            }
        });

        btnPayNow.setOnClickListener(v -> processPayment());
    }

    private void populateData() {
        if (isCartCheckout) {
            // Handle cart checkout
            tvServiceName.setText("Cart Items (" + cartItems.length + " items)");
            tvServiceDate.setText("Date: To be scheduled");
            tvServiceTime.setText("Time: To be scheduled");
            tvServicePrice.setText("$" + String.format("%.2f", amount));
        } else {
            // Handle single service booking
            if (selectedService != null) {
                tvServiceName.setText(selectedService.getName());
                tvServicePrice.setText("$" + String.format("%.2f", selectedService.getPrice()));
            }
            
            if (bookingDate != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                tvServiceDate.setText("Date: " + dateFormat.format(bookingDate));
            }
            
            if (timeSlot != null && !timeSlot.isEmpty()) {
                tvServiceTime.setText("Time: " + timeSlot);
            }
        }
        
        tvTotalAmount.setText("$" + String.format("%.2f", amount));
    }

    private void processPayment() {
        if (validatePaymentForm()) {
            // Show loading dialog
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Processing payment...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // Simulate payment processing
            new Handler().postDelayed(() -> {
                progressDialog.dismiss();
                
                // Create booking
                createBooking();
                
                // Show success message
                Toast.makeText(this, "Payment successful! Booking confirmed.", Toast.LENGTH_LONG).show();
                
                // Navigate to My Bookings to show the completed bookings
                Intent intent = new Intent(this, MyBookingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }, 2000); // 2 second delay to simulate payment processing
        }
    }

    private boolean validatePaymentForm() {
        if (rbCreditCard.isChecked()) {
            if (etCardNumber.getText().toString().trim().isEmpty()) {
                etCardNumber.setError("Please enter card number");
                etCardNumber.requestFocus();
                return false;
            }
            
            if (etExpiryDate.getText().toString().trim().isEmpty()) {
                etExpiryDate.setError("Please enter expiry date");
                etExpiryDate.requestFocus();
                return false;
            }
            
            if (etCVV.getText().toString().trim().isEmpty()) {
                etCVV.setError("Please enter CVV");
                etCVV.requestFocus();
                return false;
            }
        }
        
        return true;
    }

    private void createBooking() {
        LocalDataManager dataManager = LocalDataManager.getInstance(this);
        
        if (isCartCheckout) {
            // Create bookings for all cart items
            for (Service service : cartItems) {
                Booking booking = new Booking();
                booking.setId("booking_" + System.currentTimeMillis() + "_" + service.getId());
                booking.setServiceId(service.getId());
                booking.setServiceName(service.getName());
                booking.setServiceCategory(service.getCategory());
                booking.setCustomerId("current_user");
                booking.setTotalAmount(service.getPrice());
                booking.setBookingDate(new Date()); // Current date for cart items
                booking.setTimeSlot("To be scheduled");
                booking.setAddress("To be provided");
                booking.setCity("");
                booking.setState("");
                booking.setZipCode("");
                booking.setNotes("Cart checkout - " + service.getName());
                booking.setStatus("pending");
                
                // Assign provider and save (start with pending status)
                String providerId = dataManager.assignProviderToService(booking.getServiceCategory());
                booking.setProviderId(providerId);
                dataManager.saveBooking(booking);
            }
            
            // Clear cart after successful payment
            dataManager.clearCart();
        } else {
            // Single service booking
            Booking booking = new Booking();
            booking.setId("booking_" + System.currentTimeMillis());
            booking.setServiceId(selectedService.getId());
            booking.setServiceName(selectedService.getName());
            booking.setServiceCategory(selectedService.getCategory());
            booking.setCustomerId("current_user");
            booking.setTotalAmount(amount);
            booking.setBookingDate(bookingDate);
            booking.setTimeSlot(timeSlot);
            booking.setAddress(address);
            booking.setCity("");
            booking.setState("");
            booking.setZipCode("");
            booking.setNotes(notes);
            booking.setStatus("pending");
            
            // Assign provider and save (start with pending status)
            String providerId = dataManager.assignProviderToService(booking.getServiceCategory());
            booking.setProviderId(providerId);
            dataManager.saveBooking(booking);
        }
    }
}