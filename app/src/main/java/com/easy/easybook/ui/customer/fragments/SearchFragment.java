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

import com.easy.easybook.R;
import com.easy.easybook.databinding.FragmentSearchBinding;
import com.easy.easybook.ui.customer.ServiceSearchActivity;

/**
 * Search Fragment for customers
 * Allows searching and filtering services
 */
public class SearchFragment extends Fragment {
    
    private FragmentSearchBinding binding;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupUI();
    }
    
    private void setupUI() {
        // Setup click listeners for search functionality
        setupClickListeners();
    }
    
    private void setupClickListeners() {
        // Search bar click listener
        binding.getRoot().findViewById(R.id.search_bar).setOnClickListener(v -> {
            // Navigate to ServiceSearchActivity
            Intent intent = new Intent(getContext(), ServiceSearchActivity.class);
            startActivity(intent);
        });
        
        // Category click listeners
        setupCategoryClickListeners();
    }
    
    private void setupCategoryClickListeners() {
        // Cleaning category
        binding.getRoot().findViewById(R.id.category_cleaning).setOnClickListener(v -> {
            navigateToSearchWithCategory("Cleaning");
        });
        
        // Plumbing category
        binding.getRoot().findViewById(R.id.category_plumbing).setOnClickListener(v -> {
            navigateToSearchWithCategory("Plumbing");
        });
        
        // HVAC category (replacing Electrical)
        binding.getRoot().findViewById(R.id.category_hvac).setOnClickListener(v -> {
            navigateToSearchWithCategory("HVAC");
        });
        
        // Beauty category
        binding.getRoot().findViewById(R.id.category_beauty).setOnClickListener(v -> {
            navigateToSearchWithCategory("Beauty");
        });
    }
    
    private void navigateToSearchWithCategory(String category) {
        Intent intent = new Intent(getContext(), ServiceSearchActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
