package com.easy.easybook.ui.customer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.BookingViewHolder> {

    private List<Booking> bookings;
    private OnBookingActionListener listener;

    public interface OnBookingActionListener {
        void onCancelBooking(Booking booking);
        void onRescheduleBooking(Booking booking);
        void onRateBooking(Booking booking);
    }

    public BookingsAdapter(OnBookingActionListener listener) {
        this.bookings = new ArrayList<>();
        this.listener = listener;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
        notifyDataSetChanged();
    }

    public void addBooking(Booking booking) {
        this.bookings.add(0, booking);
        notifyItemInserted(0);
    }

    public void updateBooking(Booking booking) {
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).getId().equals(booking.getId())) {
                bookings.set(i, booking);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void removeBooking(String bookingId) {
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).getId().equals(bookingId)) {
                bookings.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
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
        holder.bind(booking);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        private TextView tvServiceName, tvStatus, tvProviderName, tvBookingDateTime, tvAddress, tvAmount;
        private View btnCancel, btnReschedule, btnRate;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvProviderName = itemView.findViewById(R.id.tvProviderName);
            tvBookingDateTime = itemView.findViewById(R.id.tvBookingDateTime);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnReschedule = itemView.findViewById(R.id.btnReschedule);
            btnRate = itemView.findViewById(R.id.btnRate);
        }

        public void bind(Booking booking) {
            tvServiceName.setText(booking.getServiceName());
            tvProviderName.setText(booking.getProviderName());
            tvAmount.setText(String.format("$%.2f", booking.getTotalAmount()));
            
            // Format address
            String address = booking.getAddress() + ", " + booking.getCity() + ", " + booking.getState();
            tvAddress.setText(address);
            
            // Format date and time
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String dateTime = dateFormat.format(booking.getBookingDate()) + " • " + booking.getTimeSlot();
            tvBookingDateTime.setText(dateTime);
            
            // Set status with appropriate styling
            tvStatus.setText(booking.getStatus().toUpperCase());
            setStatusStyle(booking.getStatus());
            
            // Set button visibility based on status
            setButtonVisibility(booking.getStatus());
            
            // Set click listeners
            btnCancel.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCancelBooking(booking);
                }
            });
            
            btnReschedule.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRescheduleBooking(booking);
                }
            });
            
            btnRate.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRateBooking(booking);
                }
            });
        }

        private void setStatusStyle(String status) {
            switch (status.toLowerCase()) {
                case "pending":
                    tvStatus.setBackgroundResource(R.drawable.circle_background_light);
                    tvStatus.setTextColor(itemView.getContext().getColor(R.color.orange));
                    break;
                case "confirmed":
                    tvStatus.setBackgroundResource(R.drawable.circle_background_light);
                    tvStatus.setTextColor(itemView.getContext().getColor(R.color.blue));
                    break;
                case "in_progress":
                    tvStatus.setBackgroundResource(R.drawable.circle_background_light);
                    tvStatus.setTextColor(itemView.getContext().getColor(R.color.primary));
                    break;
                case "completed":
                    tvStatus.setBackgroundResource(R.drawable.circle_background);
                    tvStatus.setTextColor(itemView.getContext().getColor(R.color.white));
                    break;
                case "cancelled":
                    tvStatus.setBackgroundResource(R.drawable.circle_background_light);
                    tvStatus.setTextColor(itemView.getContext().getColor(R.color.red));
                    break;
                default:
                    tvStatus.setBackgroundResource(R.drawable.circle_background_light);
                    tvStatus.setTextColor(itemView.getContext().getColor(R.color.text_secondary));
                    break;
            }
        }

        private void setButtonVisibility(String status) {
            switch (status.toLowerCase()) {
                case "pending":
                    btnCancel.setVisibility(View.VISIBLE);
                    btnReschedule.setVisibility(View.VISIBLE);
                    btnRate.setVisibility(View.GONE);
                    break;
                case "confirmed":
                    btnCancel.setVisibility(View.GONE);  // No cancel for confirmed
                    btnReschedule.setVisibility(View.GONE);  // No reschedule for confirmed
                    btnRate.setVisibility(View.GONE);
                    break;
                case "in_progress":
                    btnCancel.setVisibility(View.GONE);
                    btnReschedule.setVisibility(View.GONE);
                    btnRate.setVisibility(View.GONE);
                    break;
                case "completed":
                    btnCancel.setVisibility(View.GONE);
                    btnReschedule.setVisibility(View.GONE);
                    btnRate.setVisibility(View.VISIBLE);  // Only rate for completed
                    break;
                case "cancelled":
                    btnCancel.setVisibility(View.GONE);
                    btnReschedule.setVisibility(View.GONE);
                    btnRate.setVisibility(View.GONE);
                    break;
                default:
                    btnCancel.setVisibility(View.GONE);
                    btnReschedule.setVisibility(View.GONE);
                    btnRate.setVisibility(View.GONE);
                    break;
            }
        }
    }
}
