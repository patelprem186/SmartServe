package com.easy.easybook.ui.customer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easy.easybook.R;
import com.easy.easybook.models.Review;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private List<Review> reviews;

    public ReviewsAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCustomerName, tvReviewDate, tvComment, tvResponse;
        private RatingBar ratingBar;
        private View layoutResponse;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvReviewDate = itemView.findViewById(R.id.tvReviewDate);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvResponse = itemView.findViewById(R.id.tvResponse);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            layoutResponse = itemView.findViewById(R.id.layoutResponse);
        }

        public void bind(Review review) {
            tvCustomerName.setText(review.getCustomerName());
            ratingBar.setRating(review.getRating());
            tvComment.setText(review.getComment());
            
            // Format date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            tvReviewDate.setText(dateFormat.format(review.getCreatedAt()));
            
            // Show provider response if available
            if (review.getResponse() != null && !review.getResponse().isEmpty()) {
                layoutResponse.setVisibility(View.VISIBLE);
                tvResponse.setText(review.getResponse());
            } else {
                layoutResponse.setVisibility(View.GONE);
            }
        }
    }
}
