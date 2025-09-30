package com.easy.easybook.ui.customer;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.easy.easybook.R;
import com.easy.easybook.databinding.ActivityProviderRatingsBinding;
import com.easy.easybook.models.Review;
import com.easy.easybook.ui.customer.adapters.ReviewsAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProviderRatingsActivity extends AppCompatActivity {

    private ActivityProviderRatingsBinding binding;
    private ReviewsAdapter reviewsAdapter;
    private List<Review> reviews;
    private String providerId;
    private String providerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProviderRatingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentData();
        setupUI();
        loadProviderRatings();
    }

    private void getIntentData() {
        providerId = getIntent().getStringExtra("provider_id");
        providerName = getIntent().getStringExtra("provider_name");
    }

    private void setupUI() {
        // Back button
        binding.btnBack.setOnClickListener(v -> finish());

        // Set provider name
        binding.tvProviderName.setText(providerName != null ? providerName : "Provider");

        // Setup reviews RecyclerView
        reviews = new ArrayList<>();
        reviewsAdapter = new ReviewsAdapter(reviews);
        binding.rvReviews.setLayoutManager(new LinearLayoutManager(this));
        binding.rvReviews.setAdapter(reviewsAdapter);
    }

    private void loadProviderRatings() {
        // Load real reviews from API
        reviews.clear();
        
        // TODO: Implement real API call to load provider reviews
        android.util.Log.d("ProviderRatingsActivity", "Loading real provider reviews from API");
        
        updateRatingDisplay();
    }

    private void updateRatingDisplay() {
        // Calculate overall rating
        double overallRating = calculateOverallRating();
        int totalReviews = reviews.size();
        
        // Update overall rating display
        binding.ratingBar.setRating((float) overallRating);
        binding.tvOverallRating.setText(String.format("%.1f", overallRating));
        binding.tvTotalReviews.setText(String.format("(%d reviews)", totalReviews));
        
        // Update rating breakdown
        updateRatingBreakdown();
    }

    private double calculateOverallRating() {
        if (reviews.isEmpty()) return 0.0;
        
        int totalRating = 0;
        for (Review review : reviews) {
            totalRating += review.getRating();
        }
        
        return (double) totalRating / reviews.size();
    }

    private void updateRatingBreakdown() {
        int[] ratingCounts = new int[5]; // Index 0 = 1 star, Index 4 = 5 stars
        
        // Count ratings
        for (Review review : reviews) {
            ratingCounts[review.getRating() - 1]++;
        }
        
        int totalReviews = reviews.size();
        
        // Update progress bars and counts
        updateProgressBar(binding.progress5, binding.tvCount5, ratingCounts[4], totalReviews);
        updateProgressBar(binding.progress4, binding.tvCount4, ratingCounts[3], totalReviews);
        updateProgressBar(binding.progress3, binding.tvCount3, ratingCounts[2], totalReviews);
        updateProgressBar(binding.progress2, binding.tvCount2, ratingCounts[1], totalReviews);
        updateProgressBar(binding.progress1, binding.tvCount1, ratingCounts[0], totalReviews);
    }

    private void updateProgressBar(android.widget.ProgressBar progressBar, TextView countText, 
                                 int count, int total) {
        countText.setText(String.valueOf(count));
        
        if (total > 0) {
            int percentage = (count * 100) / total;
            progressBar.setProgress(percentage);
        } else {
            progressBar.setProgress(0);
        }
    }
}
