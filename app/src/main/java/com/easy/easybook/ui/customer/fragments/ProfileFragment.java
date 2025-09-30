package com.easy.easybook.ui.customer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.easy.easybook.databinding.FragmentProfileBinding;
import com.easy.easybook.models.User;
import com.easy.easybook.data.LocalDataManager;
import com.easy.easybook.ui.auth.LoginActivity;
import com.easy.easybook.utils.SharedPrefsManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Profile Fragment for customers
 * Shows user profile and settings
 */
public class ProfileFragment extends Fragment {
    
    private FragmentProfileBinding binding;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupUI();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Refresh user data when fragment becomes visible
        loadUserData();
    }
    
    private void setupUI() {
        // Load user data
        loadUserData();
        
        // Setup click listeners
        setupClickListeners();
    }
    
    private void loadUserData() {
        // Get current Firebase user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        
        if (firebaseUser != null) {
            // Display Firebase user data immediately
            displayFirebaseUserData(firebaseUser);
        } else {
            // No user logged in
            binding.tvUserName.setText("No user logged in");
            binding.tvUserEmail.setText("Please login");
            binding.tvMemberSince.setText("Not available");
            binding.tvTotalBookings.setText("0");
            binding.tvRating.setText("0.0");
            
            android.util.Log.w("ProfileFragment", "No Firebase user logged in");
        }
    }
    
    private void displayFirebaseUserData(FirebaseUser firebaseUser) {
        // Get Firebase user details
        String displayName = firebaseUser.getDisplayName();
        String email = firebaseUser.getEmail();
        String uid = firebaseUser.getUid();
        boolean emailVerified = firebaseUser.isEmailVerified();
        
        // Display user name
        if (displayName != null && !displayName.trim().isEmpty()) {
            binding.tvUserName.setText(displayName);
        } else if (email != null && !email.trim().isEmpty()) {
            // Use email prefix if no display name
            String emailPrefix = email.split("@")[0];
            binding.tvUserName.setText(capitalizeFirst(emailPrefix));
        } else {
            binding.tvUserName.setText("Firebase User");
        }
        
        // Display email
        if (email != null && !email.trim().isEmpty()) {
            binding.tvUserEmail.setText(email);
        } else {
            binding.tvUserEmail.setText("No email available");
        }
        
        // Set member since date (you can implement actual date logic)
        String memberSince = "Member since " + getFirebaseMemberSinceDate(firebaseUser);
        binding.tvMemberSince.setText(memberSince);
        
        // Set Firebase user-specific stats
        displayFirebaseUserStats(firebaseUser);
        
        // Log Firebase user details
        android.util.Log.d("ProfileFragment", "Firebase user details:");
        android.util.Log.d("ProfileFragment", "- Display Name: " + displayName);
        android.util.Log.d("ProfileFragment", "- Email: " + email);
        android.util.Log.d("ProfileFragment", "- UID: " + uid);
        android.util.Log.d("ProfileFragment", "- Email Verified: " + emailVerified);
    }
    
    private void displayUserData(User user) {
        // Build full name properly
        String fullName = user.getFirstName();
        if (user.getLastName() != null && !user.getLastName().trim().isEmpty()) {
            fullName += " " + user.getLastName();
        }
        
        binding.tvUserName.setText(fullName);
        binding.tvUserEmail.setText(user.getEmail());
        
        // Set member since date based on user data
        String memberSince = "Member since " + getMemberSinceDate(user);
        binding.tvMemberSince.setText(memberSince);
        
        // Set user-specific stats
        displayUserStats(user);
        
        // Show user role if available
        if (user.getRole() != null && !user.getRole().isEmpty()) {
            String roleText = "Role: " + capitalizeFirst(user.getRole());
            // You could add this to the layout if needed
            android.util.Log.d("ProfileFragment", "User role: " + user.getRole());
        }
    }
    
    private String getFirebaseMemberSinceDate(FirebaseUser firebaseUser) {
        // You can implement actual date logic here using Firebase user metadata
        // For now, return a default date
        return "Dec 2024";
    }
    
    private String getMemberSinceDate(User user) {
        // You can implement actual date logic here
        // For now, return a default date
        return "Dec 2024";
    }
    
    private void displayFirebaseUserStats(FirebaseUser firebaseUser) {
        // Set stats based on Firebase user data
        // You can implement actual booking count and rating calculation here
        
        // For now, show default values
        binding.tvTotalBookings.setText("0"); // Could calculate from actual bookings
        binding.tvRating.setText("5.0"); // Could calculate from actual reviews
        
        // Show Firebase user-specific information
        String phoneNumber = firebaseUser.getPhoneNumber();
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            android.util.Log.d("ProfileFragment", "Firebase user phone: " + phoneNumber);
        }
        
        boolean emailVerified = firebaseUser.isEmailVerified();
        if (emailVerified) {
            android.util.Log.d("ProfileFragment", "Firebase user email is verified");
        } else {
            android.util.Log.d("ProfileFragment", "Firebase user email is not verified");
        }
        
        // Show user UID
        String uid = firebaseUser.getUid();
        android.util.Log.d("ProfileFragment", "Firebase user UID: " + uid);
    }
    
    private void displayUserStats(User user) {
        // Set stats based on user data
        // You can implement actual booking count and rating calculation here
        
        // For now, show default values
        binding.tvTotalBookings.setText("0"); // Could calculate from actual bookings
        binding.tvRating.setText("5.0"); // Could calculate from actual reviews
        
        // Show user-specific information
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            android.util.Log.d("ProfileFragment", "User phone: " + user.getPhone());
        }
        
        if (user.isVerified()) {
            android.util.Log.d("ProfileFragment", "User is verified");
        }
    }
    
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    
    private void setupClickListeners() {
        
        
        // Help & Support
        binding.cardHelpSupport.setOnClickListener(v -> {
            showSupportDialog();
        });
        
        // Logout
        binding.cardLogout.setOnClickListener(v -> {
            showLogoutDialog();
        });
    }
    
    private void showSupportDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Customer Support");
        builder.setMessage("How can we help you today?");
        
        builder.setPositiveButton("Call Support", (dialog, which) -> {
            // TODO: Implement actual call support functionality
        });
        
        builder.setNeutralButton("Email Support", (dialog, which) -> {
            // TODO: Implement actual email support functionality
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void showLogoutDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        
        builder.setPositiveButton("Logout", (dialog, which) -> {
            // Clear user data
            SharedPrefsManager prefsManager = SharedPrefsManager.getInstance(getContext());
            prefsManager.clearUserData();
            
            // Navigate to login
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
