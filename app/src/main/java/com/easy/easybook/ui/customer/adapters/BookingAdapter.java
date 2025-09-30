package com.easy.easybook.ui.customer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easy.easybook.R;
import com.easy.easybook.models.Booking;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    
    private List<Booking> bookings;
    private OnBookingClickListener listener;
    
    public interface OnBookingClickListener {
        void onBookingClick(Booking booking);
    }
    
    public BookingAdapter(List<Booking> bookings) {
        this.bookings = bookings;
    }
    
    public void setOnBookingClickListener(OnBookingClickListener listener) {
        this.listener = listener;
    }
    
    public void updateBookings(List<Booking> newBookings) {
        this.bookings = newBookings;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        holder.bind(booking, listener);
    }
    
    @Override
    public int getItemCount() {
        return bookings.size();
    }
    
    static class BookingViewHolder extends RecyclerView.ViewHolder {
        private TextView tvServiceName;
        private TextView tvProviderName;
        private TextView tvBookingDate;
        private TextView tvBookingTime;
        private TextView tvStatus;
        private TextView tvPrice;
        
        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tv_service_name);
            tvProviderName = itemView.findViewById(R.id.tv_provider_name);
            tvBookingDate = itemView.findViewById(R.id.tv_booking_date);
            tvBookingTime = itemView.findViewById(R.id.tv_booking_time);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
        
        public void bind(Booking booking, OnBookingClickListener listener) {
            tvServiceName.setText(booking.getServiceName());
            tvProviderName.setText(booking.getProviderName());
            // Format date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            tvBookingDate.setText(dateFormat.format(booking.getBookingDate()));
            tvBookingTime.setText(booking.getTimeSlot());
            tvStatus.setText(booking.getStatus());
            tvPrice.setText("$" + booking.getPrice());
            
            // Set status color
            int statusColor = getStatusColor(booking.getStatus());
            tvStatus.setTextColor(statusColor);
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBookingClick(booking);
                }
            });
        }
        
        private int getStatusColor(String status) {
            switch (status.toLowerCase()) {
                case "completed":
                    return itemView.getContext().getColor(R.color.success);
                case "pending":
                    return itemView.getContext().getColor(R.color.warning);
                case "confirmed":
                    return itemView.getContext().getColor(R.color.primary);
                case "cancelled":
                    return itemView.getContext().getColor(R.color.error);
                default:
                    return itemView.getContext().getColor(R.color.text_secondary);
            }
        }
    }
}
