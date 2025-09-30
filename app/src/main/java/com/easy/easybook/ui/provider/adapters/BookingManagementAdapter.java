package com.easy.easybook.ui.provider.adapters;

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

public class BookingManagementAdapter extends RecyclerView.Adapter<BookingManagementAdapter.BookingViewHolder> {
    
    private List<Booking> bookings;
    private OnBookingActionListener listener;
    
    public interface OnBookingActionListener {
        void onAcceptBooking(Booking booking);
        void onRejectBooking(Booking booking);
        void onStartService(Booking booking);
        void onCompleteService(Booking booking);
        void onViewDetails(Booking booking);
    }
    
    public BookingManagementAdapter(List<Booking> bookings, OnBookingActionListener listener) {
        this.bookings = bookings;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_management, parent, false);
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
        private TextView tvCustomerName;
        private TextView tvBookingDate;
        private TextView tvBookingTime;
        private TextView tvStatus;
        private TextView tvPrice;
        private TextView btnAccept;
        private TextView btnReject;
        private TextView btnStart;
        private TextView btnComplete;
        private TextView btnViewDetails;
        
        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tv_service_name);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvBookingDate = itemView.findViewById(R.id.tv_booking_date);
            tvBookingTime = itemView.findViewById(R.id.tv_booking_time);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvPrice = itemView.findViewById(R.id.tv_price);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            btnReject = itemView.findViewById(R.id.btn_reject);
            btnStart = itemView.findViewById(R.id.btn_start);
            btnComplete = itemView.findViewById(R.id.btn_complete);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);
        }
        
        public void bind(Booking booking, OnBookingActionListener listener) {
            tvServiceName.setText(booking.getServiceName());
            tvCustomerName.setText(booking.getCustomerName());
            // Format date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            tvBookingDate.setText(dateFormat.format(booking.getBookingDate()));
            tvBookingTime.setText(booking.getTimeSlot());
            tvStatus.setText(booking.getStatus().toUpperCase());
            tvPrice.setText("$" + booking.getPrice());
            
            // Set status color
            int statusColor = getStatusColor(booking.getStatus());
            tvStatus.setTextColor(statusColor);
            
            // Show/hide action buttons based on status
            setupActionButtons(booking, listener);
        }
        
        private void setupActionButtons(Booking booking, OnBookingActionListener listener) {
            // Hide all buttons first
            btnAccept.setVisibility(View.GONE);
            btnReject.setVisibility(View.GONE);
            btnStart.setVisibility(View.GONE);
            btnComplete.setVisibility(View.GONE);
            btnViewDetails.setVisibility(View.VISIBLE);
            
            // Show relevant buttons based on status
            switch (booking.getStatus().toLowerCase()) {
                case "pending":
                    btnAccept.setVisibility(View.VISIBLE);
                    btnReject.setVisibility(View.VISIBLE);
                    break;
                case "confirmed":
                    btnStart.setVisibility(View.VISIBLE);
                    break;
                case "in_progress":
                    btnComplete.setVisibility(View.VISIBLE);
                    break;
                case "completed":
                case "cancelled":
                    // Only show view details
                    break;
            }
            
            // Set click listeners
            btnAccept.setOnClickListener(v -> listener.onAcceptBooking(booking));
            btnReject.setOnClickListener(v -> listener.onRejectBooking(booking));
            btnStart.setOnClickListener(v -> listener.onStartService(booking));
            btnComplete.setOnClickListener(v -> listener.onCompleteService(booking));
            btnViewDetails.setOnClickListener(v -> listener.onViewDetails(booking));
        }
        
        private int getStatusColor(String status) {
            switch (status.toLowerCase()) {
                case "completed":
                    return itemView.getContext().getColor(R.color.success);
                case "pending":
                    return itemView.getContext().getColor(R.color.warning);
                case "confirmed":
                    return itemView.getContext().getColor(R.color.primary);
                case "in_progress":
                    return itemView.getContext().getColor(R.color.accent);
                case "cancelled":
                    return itemView.getContext().getColor(R.color.error);
                default:
                    return itemView.getContext().getColor(R.color.text_secondary);
            }
        }
    }
}
