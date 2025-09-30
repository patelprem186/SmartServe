package com.easy.easybook.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.easy.easybook.models.Booking;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager class to handle booking storage and retrieval
 */
public class BookingManager {
    private static final String PREFS_NAME = "booking_prefs";
    private static final String KEY_BOOKINGS = "bookings";
    
    private SharedPreferences prefs;
    private Gson gson;
    
    public BookingManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }
    
    /**
     * Save a new booking
     */
    public void saveBooking(Booking booking) {
        List<Booking> bookings = getAllBookings();
        bookings.add(booking);
        saveBookings(bookings);
    }
    
    /**
     * Get all bookings
     */
    public List<Booking> getAllBookings() {
        String bookingsJson = prefs.getString(KEY_BOOKINGS, "[]");
        Type listType = new TypeToken<List<Booking>>(){}.getType();
        List<Booking> bookings = gson.fromJson(bookingsJson, listType);
        return bookings != null ? bookings : new ArrayList<>();
    }
    
    /**
     * Save bookings list
     */
    private void saveBookings(List<Booking> bookings) {
        String bookingsJson = gson.toJson(bookings);
        prefs.edit().putString(KEY_BOOKINGS, bookingsJson).apply();
    }
    
    /**
     * Update a booking
     */
    public void updateBooking(Booking updatedBooking) {
        List<Booking> bookings = getAllBookings();
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).getId().equals(updatedBooking.getId())) {
                bookings.set(i, updatedBooking);
                break;
            }
        }
        saveBookings(bookings);
    }
    
    /**
     * Delete a booking
     */
    public void deleteBooking(String bookingId) {
        List<Booking> bookings = getAllBookings();
        bookings.removeIf(booking -> booking.getId().equals(bookingId));
        saveBookings(bookings);
    }
    
    /**
     * Get bookings by status
     */
    public List<Booking> getBookingsByStatus(String status) {
        List<Booking> allBookings = getAllBookings();
        List<Booking> filteredBookings = new ArrayList<>();
        
        for (Booking booking : allBookings) {
            if (booking.getStatus().equalsIgnoreCase(status)) {
                filteredBookings.add(booking);
            }
        }
        
        return filteredBookings;
    }
}
