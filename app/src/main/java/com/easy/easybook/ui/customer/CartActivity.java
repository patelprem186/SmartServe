package com.easy.easybook.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.easy.easybook.databinding.ActivityCartBinding;
import com.easy.easybook.models.Service;
import com.easy.easybook.data.LocalDataManager;
import com.easy.easybook.data.SeedData;
import com.easy.easybook.ui.customer.adapters.CartAdapter;

import java.util.List;

public class CartActivity extends AppCompatActivity {
    
    private ActivityCartBinding binding;
    private CartAdapter cartAdapter;
    private LocalDataManager dataManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        dataManager = LocalDataManager.getInstance(this);
        
        setupToolbar();
        setupRecyclerView();
        loadCartItems();
        setupClickListeners();
    }
    
    private void setupToolbar() {
        binding.toolbar.setTitle("Shopping Cart");
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(new CartAdapter.OnCartItemClickListener() {
            @Override
            public void onRemoveItem(Service service) {
                removeFromCart(service);
            }
            
            @Override
            public void onBookNow(Service service) {
                bookService(service);
            }
        });
        
        binding.rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCartItems.setAdapter(cartAdapter);
    }
    
    private void loadCartItems() {
        List<Service> cartItems = dataManager.getCart();
        
        if (cartItems.isEmpty()) {
            showEmptyCart();
        } else {
            showCartItems(cartItems);
        }
    }
    
    private void showEmptyCart() {
        binding.rvCartItems.setVisibility(View.GONE);
        binding.layoutEmptyCart.setVisibility(View.VISIBLE);
        binding.layoutCartSummary.setVisibility(View.GONE);
    }
    
    private void showCartItems(List<Service> cartItems) {
        binding.rvCartItems.setVisibility(View.VISIBLE);
        binding.layoutEmptyCart.setVisibility(View.GONE);
        binding.layoutCartSummary.setVisibility(View.VISIBLE);
        
        cartAdapter.updateCartItems(cartItems);
        updateCartSummary(cartItems);
    }
    
    private void updateCartSummary(List<Service> cartItems) {
        double total = 0;
        for (Service service : cartItems) {
            total += service.getPrice();
        }
        
        binding.tvCartTotal.setText(String.format("$%.2f", total));
        binding.tvItemCount.setText(cartItems.size() + " items");
    }
    
    private void setupClickListeners() {
        binding.btnCheckout.setOnClickListener(v -> {
            List<Service> cartItems = dataManager.getCart();
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Navigate to payment for cart checkout
            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra("is_cart_checkout", true);
            intent.putExtra("cart_items", cartItems.toArray(new Service[0]));
            intent.putExtra("total_amount", calculateTotal(cartItems));
            startActivity(intent);
        });
        
        binding.btnContinueShopping.setOnClickListener(v -> {
            finish();
        });
    }
    
    private void removeFromCart(Service service) {
        dataManager.removeFromCart(service.getId());
        loadCartItems();
        Toast.makeText(this, "Item removed from cart", Toast.LENGTH_SHORT).show();
    }
    
    private void bookService(Service service) {
        // Navigate to payment page for single service booking
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("is_cart_checkout", false);
        intent.putExtra("service", service);
        intent.putExtra("amount", service.getPrice());
        
        // Provide default values for booking details
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.DAY_OF_MONTH, 1); // Tomorrow
        intent.putExtra("booking_date", calendar.getTime());
        intent.putExtra("time_slot", "10:00 AM");
        intent.putExtra("address", "123 Main St, City, State");
        intent.putExtra("notes", "Please contact before arrival");
        
        startActivity(intent);
    }
    
    private double calculateTotal(List<Service> cartItems) {
        double total = 0;
        for (Service service : cartItems) {
            total += service.getPrice();
        }
        return total;
    }
    
    private void createBooking(Service service) {
        // Create a booking using LocalDataManager
        com.easy.easybook.models.Booking booking = new com.easy.easybook.models.Booking();
        booking.setId("booking_" + System.currentTimeMillis());
        booking.setServiceId(service.getId());
        booking.setServiceName(service.getName());
        booking.setServiceCategory(service.getCategory());
        booking.setCustomerId("current_user"); // In real app, get from login
        booking.setTotalAmount(service.getPrice());
        booking.setStatus("pending");
        booking.setBookingDate(new java.util.Date());
        booking.setTimeSlot("10:00 AM");
        booking.setAddress("123 Main St, City, State");
        booking.setNotes("Please contact before arrival");
        
        // Create provider request
        dataManager.createProviderRequest(booking);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();
    }
}