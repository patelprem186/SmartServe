package com.easy.easybook.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.easy.easybook.models.Booking;
import com.easy.easybook.models.Service;
import com.easy.easybook.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Local data manager using SharedPreferences for offline functionality
 */
public class LocalDataManager {
    private static final String PREFS_NAME = "EasyBookPrefs";
    private static final String KEY_BOOKINGS = "bookings";
    private static final String KEY_CART = "cart";
    private static final String KEY_USER = "user";
    private static final String KEY_PROVIDER_REQUESTS = "provider_requests";
    private static final String KEY_SERVICES = "services";
    
    private static LocalDataManager instance;
    private SharedPreferences prefs;
    private Gson gson;
    
    private LocalDataManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }
    
    public static synchronized LocalDataManager getInstance(Context context) {
        if (instance == null) {
            instance = new LocalDataManager(context.getApplicationContext());
        }
        return instance;
    }
    
    // User Management
    public void saveUser(User user) {
        String userJson = gson.toJson(user);
        prefs.edit().putString(KEY_USER, userJson).apply();
    }
    
    public User getCurrentUser() {
        String userJson = prefs.getString(KEY_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }
    
    public void logout() {
        prefs.edit().remove(KEY_USER).apply();
    }
    
    // Booking Management
    public void saveBooking(Booking booking) {
        List<Booking> bookings = getAllBookings();
        bookings.add(booking);
        saveBookings(bookings);
    }
    
    public List<Booking> getAllBookings() {
        String bookingsJson = prefs.getString(KEY_BOOKINGS, "[]");
        Type listType = new TypeToken<List<Booking>>(){}.getType();
        return gson.fromJson(bookingsJson, listType);
    }
    
    public List<Booking> getUserBookings(String userId) {
        List<Booking> allBookings = getAllBookings();
        List<Booking> userBookings = new ArrayList<>();
        
        for (Booking booking : allBookings) {
            if (booking.getCustomerId().equals(userId)) {
                userBookings.add(booking);
            }
        }
        
        return userBookings;
    }
    
    public List<Booking> getProviderRequests(String providerId) {
        List<Booking> allBookings = getAllBookings();
        List<Booking> providerRequests = new ArrayList<>();
        
        for (Booking booking : allBookings) {
            if (booking.getProviderId() != null && booking.getProviderId().equals(providerId)) {
                providerRequests.add(booking);
            }
        }
        
        return providerRequests;
    }
    
    public void updateBookingStatus(String bookingId, String status) {
        List<Booking> bookings = getAllBookings();
        for (Booking booking : bookings) {
            if (booking.getId().equals(bookingId)) {
                booking.setStatus(status);
                break;
            }
        }
        saveBookings(bookings);
    }
    
    public void saveBookings(List<Booking> bookings) {
        String bookingsJson = gson.toJson(bookings);
        prefs.edit().putString(KEY_BOOKINGS, bookingsJson).apply();
    }
    
    // Cart Management
    public void addToCart(Service service) {
        List<Service> cart = getCart();
        cart.add(service);
        saveCart(cart);
    }
    
    public List<Service> getCart() {
        String cartJson = prefs.getString(KEY_CART, "[]");
        Type listType = new TypeToken<List<Service>>(){}.getType();
        return gson.fromJson(cartJson, listType);
    }
    
    public void removeFromCart(String serviceId) {
        List<Service> cart = getCart();
        cart.removeIf(service -> service.getId().equals(serviceId));
        saveCart(cart);
    }
    
    public void clearCart() {
        prefs.edit().remove(KEY_CART).apply();
    }
    
    private void saveCart(List<Service> cart) {
        String cartJson = gson.toJson(cart);
        prefs.edit().putString(KEY_CART, cartJson).apply();
    }
    
    // Provider Request Management
    public void createProviderRequest(Booking booking) {
        // Assign a random provider for the service category
        String providerId = assignProviderToService(booking.getServiceCategory());
        booking.setProviderId(providerId);
        booking.setStatus("pending");
        saveBooking(booking);
    }
    
    public String assignProviderToService(String category) {
        // Simple provider assignment based on category
        List<User> providers = SeedData.getSeedProviders();
        int index = Math.abs(category.hashCode()) % providers.size();
        return providers.get(index).getId();
    }
    
    // Rating Management
    public void addRating(String bookingId, float rating, String comment) {
        List<Booking> bookings = getAllBookings();
        for (Booking booking : bookings) {
            if (booking.getId().equals(bookingId)) {
                booking.setRating(rating);
                booking.setRatingComment(comment);
                booking.setStatus("completed");
                break;
            }
        }
        saveBookings(bookings);
    }
    
    // Statistics
    public int getTotalBookings(String userId) {
        return getUserBookings(userId).size();
    }
    
    public int getPendingRequests(String providerId) {
        List<Booking> requests = getProviderRequests(providerId);
        int pending = 0;
        for (Booking booking : requests) {
            if ("pending".equals(booking.getStatus())) {
                pending++;
            }
        }
        return pending;
    }
    
    public double getTotalEarnings(String providerId) {
        List<Booking> requests = getProviderRequests(providerId);
        double total = 0;
        for (Booking booking : requests) {
            if ("completed".equals(booking.getStatus())) {
                total += booking.getTotalAmount();
            }
        }
        return total;
    }
    
    // Service Management
    public List<Service> getAllServices() {
        String servicesJson = prefs.getString(KEY_SERVICES, "[]");
        Type listType = new TypeToken<List<Service>>(){}.getType();
        return gson.fromJson(servicesJson, listType);
    }
    
    public boolean saveService(Service service) {
        try {
            List<Service> services = getAllServices();
            
            // Update or add
            boolean found = false;
            for (int i = 0; i < services.size(); i++) {
                if (services.get(i).getId().equals(service.getId())) {
                    services.set(i, service);
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                services.add(service);
            }
            
            saveServices(services);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean deleteService(String serviceId) {
        try {
            List<Service> services = getAllServices();
            services.removeIf(service -> service.getId().equals(serviceId));
            saveServices(services);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private void saveServices(List<Service> services) {
        String servicesJson = gson.toJson(services);
        prefs.edit().putString(KEY_SERVICES, servicesJson).apply();
    }
}
