package com.easy.easybook.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.easy.easybook.R;
import com.easy.easybook.data.LocalDataManager;
import com.easy.easybook.models.Booking;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class RatingActivity extends AppCompatActivity {

    private Booking booking;
    private LocalDataManager dataManager;
    private TextView tvServiceName, tvProviderName, tvBookingDate, tvRatingText;
    private RatingBar ratingBar;
    private TextInputEditText etComment;
    private MaterialButton btnSubmitRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        // Get booking from intent
        booking = (Booking) getIntent().getSerializableExtra("booking");
        if (booking == null) {
            Toast.makeText(this, "Booking not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dataManager = LocalDataManager.getInstance(this);
        initViews();
        setupClickListeners();
        populateData();
    }

    private void initViews() {
        tvServiceName = findViewById(R.id.tvServiceName);
        tvProviderName = findViewById(R.id.tvProviderName);
        tvBookingDate = findViewById(R.id.tvBookingDate);
        tvRatingText = findViewById(R.id.tvRatingText);
        ratingBar = findViewById(R.id.ratingBar);
        etComment = findViewById(R.id.etComment);
        btnSubmitRating = findViewById(R.id.btnSubmitRating);

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void setupClickListeners() {
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            updateRatingText((int) rating);
        });

        btnSubmitRating.setOnClickListener(v -> submitRating());
    }

    private void populateData() {
        tvServiceName.setText(booking.getServiceName());
        tvProviderName.setText("Provider: " + (booking.getProviderName() != null ? booking.getProviderName() : "Unknown"));
        
        if (booking.getBookingDate() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            tvBookingDate.setText("Date: " + dateFormat.format(booking.getBookingDate()));
        } else {
            tvBookingDate.setText("Date: Not specified");
        }
    }

    private void updateRatingText(int rating) {
        String[] ratingTexts = {"Poor", "Fair", "Good", "Very Good", "Excellent"};
        if (rating >= 1 && rating <= 5) {
            tvRatingText.setText(ratingTexts[rating - 1]);
        }
    }

    private void submitRating() {
        float rating = ratingBar.getRating();
        String comment = etComment.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save rating to booking
        dataManager.addRating(booking.getId(), rating, comment);

        Toast.makeText(this, "Thank you for your feedback!", Toast.LENGTH_LONG).show();

        // Navigate back to My Bookings
        Intent intent = new Intent(this, MyBookingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}