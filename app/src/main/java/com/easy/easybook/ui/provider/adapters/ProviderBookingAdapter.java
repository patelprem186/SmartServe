package com.easy.easybook.ui.provider.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easy.easybook.R;
import com.easy.easybook.models.Booking;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ProviderBookingAdapter extends RecyclerView.Adapter<ProviderBookingAdapter.ProviderBookingViewHolder> {
    
    private List<Booking> bookings;
    private OnProviderBookingActionListener listener;
    
    public interface OnProviderBookingActionListener {
        void onAcceptBooking(Booking booking);
        void onDeclineBooking(Booking booking);
        void onStartService(Booking booking);
        void onCompleteService(Booking booking);
    }
    
    public ProviderBookingAdapter(OnProviderBookingActionListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ProviderBookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_provider_booking, parent, false);
        return new ProviderBookingViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ProviderBookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        holder.bind(booking);
    }
    
    @Override
    public int getItemCount() {
        return bookings != null ? bookings.size() : 0;
    }
    
    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
        notifyDataSetChanged();
    }
    
    public class ProviderBookingViewHolder extends RecyclerView.ViewHolder {
        private TextView tvServiceName, tvCustomerName, tvBookingDate, tvTimeSlot, tvAddress, tvNotes, tvAmount;
        private Button btnAccept, btnDecline, btnStart, btnComplete;
        
        public ProviderBookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvBookingDate = itemView.findViewById(R.id.tvBookingDate);
            tvTimeSlot = itemView.findViewById(R.id.tvTimeSlot);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);
            btnStart = itemView.findViewById(R.id.btnStart);
            btnComplete = itemView.findViewById(R.id.btnComplete);
        }
        
        public void bind(Booking booking) {
            tvServiceName.setText(booking.getServiceName() != null ? booking.getServiceName() : "Service");
            tvCustomerName.setText("Customer: " + (booking.getCustomerName() != null ? booking.getCustomerName() : "Unknown"));
            
            if (booking.getBookingDate() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                tvBookingDate.setText("Date: " + dateFormat.format(booking.getBookingDate()));
            } else {
                tvBookingDate.setText("Date: To be scheduled");
            }
            
            tvTimeSlot.setText("Time: " + (booking.getTimeSlot() != null ? booking.getTimeSlot() : "To be scheduled"));
            tvAddress.setText("Address: " + (booking.getAddress() != null ? booking.getAddress() : "To be provided"));
            tvNotes.setText("Notes: " + (booking.getNotes() != null ? booking.getNotes() : "None"));
            tvAmount.setText("$" + String.format("%.2f", booking.getTotalAmount()));
            
            // Hide all buttons first
            btnAccept.setVisibility(View.GONE);
            btnDecline.setVisibility(View.GONE);
            btnStart.setVisibility(View.GONE);
            btnComplete.setVisibility(View.GONE);
            
            // Show relevant buttons based on status
            switch (booking.getStatus().toLowerCase()) {
                case "pending":
                    btnAccept.setVisibility(View.VISIBLE);
                    btnDecline.setVisibility(View.VISIBLE);
                    break;
                case "confirmed":
                    btnStart.setVisibility(View.VISIBLE);
                    break;
                case "in_progress":
                    btnComplete.setVisibility(View.VISIBLE);
                    break;
                case "completed":
                case "declined":
                case "cancelled":
                    // Hide all buttons - these statuses don't need actions
                    break;
            }
            
            // Set click listeners
            btnAccept.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAcceptBooking(booking);
                }
            });
            
            btnDecline.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeclineBooking(booking);
                }
            });
            
            btnStart.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onStartService(booking);
                }
            });
            
            btnComplete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCompleteService(booking);
                }
            });
        }
    }
}
