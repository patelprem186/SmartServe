package com.easy.easybook.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.easy.easybook.R;
import com.easy.easybook.databinding.ActivityCustomerMainBinding;
import com.easy.easybook.ui.customer.fragments.BookingHistoryFragment;
import com.easy.easybook.ui.customer.fragments.HomeFragment;
import com.easy.easybook.ui.customer.fragments.ProfileFragment;
import com.easy.easybook.ui.customer.fragments.SearchFragment;

/**
 * Main Activity for customers
 * Contains bottom navigation with Home, Search, Bookings, and Profile
 */
public class CustomerMainActivity extends AppCompatActivity {
    
    private ActivityCustomerMainBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        setupBottomNavigation();
        setupChatbotFAB();
        
        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }
    
    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_search) {
                selectedFragment = new SearchFragment();
            } else if (itemId == R.id.nav_bookings) {
                selectedFragment = new BookingHistoryFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }
            
            return loadFragment(selectedFragment);
        });
    }
    
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
    
    private void setupChatbotFAB() {
        binding.fabChatbot.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatBotActivity.class);
            startActivity(intent);
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
