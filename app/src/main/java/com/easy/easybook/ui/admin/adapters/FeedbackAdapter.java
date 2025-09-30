package com.easy.easybook.ui.admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easy.easybook.R;
import com.easy.easybook.models.Booking;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {
    
    private List<Booking> feedbackList;
    private SimpleDateFormat dateFormat;
    
    public FeedbackAdapter() {
        this.feedbackList = new ArrayList<>();
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }
    
    public void setFeedbackList(List<Booking> feedbackList) {
        this.feedbackList = feedbackList != null ? feedbackList : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        Booking booking = feedbackList.get(position);
        holder.bind(booking);
    }
    
    @Override
    public int getItemCount() {
        return feedbackList.size();
    }
    
    public class FeedbackViewHolder extends RecyclerView.ViewHolder {
        
        private TextView tvCustomerName;
        private TextView tvServiceName;
        private RatingBar ratingBar;
        private TextView tvRating;
        private TextView tvComment;
        private TextView tvDate;
        
        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvServiceName = itemView.findViewById(R.id.tv_service_name);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvComment = itemView.findViewById(R.id.tv_comment);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
        
        public void bind(Booking booking) {
            // Set customer name (in real app, this would come from user data)
            tvCustomerName.setText(booking.getCustomerName() != null ? booking.getCustomerName() : "Anonymous Customer");
            
            // Set service name
            tvServiceName.setText(booking.getServiceName());
            
            // Set rating
            float rating = booking.getRating() > 0 ? booking.getRating() : 0f;
            ratingBar.setRating(rating);
            tvRating.setText(String.format(Locale.getDefault(), "%.1f", rating));
            
            // Set comment
            String comment = booking.getRatingComment();
            if (comment != null && !comment.isEmpty()) {
                tvComment.setText(comment);
            } else {
                tvComment.setText("No comment provided");
            }
            
            // Set date
            Date bookingDate = booking.getBookingDate();
            if (bookingDate != null) {
                tvDate.setText(dateFormat.format(bookingDate));
            } else {
                tvDate.setText("Unknown date");
            }
        }
    }
}
