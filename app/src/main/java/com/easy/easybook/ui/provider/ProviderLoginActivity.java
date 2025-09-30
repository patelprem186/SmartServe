package com.easy.easybook.ui.provider;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.easy.easybook.R;
import com.easy.easybook.data.LocalDataManager;
import com.easy.easybook.data.SeedData;
import com.easy.easybook.data.ServiceManager;
import com.easy.easybook.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class ProviderLoginActivity extends AppCompatActivity {

    private AutoCompleteTextView etServiceCategory;
    private TextInputEditText etProviderName, etEmail, etPhone;
    private MaterialButton btnLogin;
    private LocalDataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_login);

        dataManager = LocalDataManager.getInstance(this);
        initViews();
        setupClickListeners();
        setupCategoryDropdown();
        
        // Check if we got a preselected category from registration
        String preselectedCategory = getIntent().getStringExtra("preselected_category");
        if (preselectedCategory != null && !preselectedCategory.trim().isEmpty()) {
            etServiceCategory.setText(preselectedCategory);
        }
    }

    private void initViews() {
        etServiceCategory = findViewById(R.id.etServiceCategory);
        etProviderName = findViewById(R.id.etProviderName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnLogin = findViewById(R.id.btnLogin);

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());
    }

    private void setupCategoryDropdown() {
        // Use centralized ServiceManager to get all available categories
        ServiceManager serviceManager = ServiceManager.getInstance(this);
        List<String> categories = serviceManager.getAllCategories();
        
        // Sort categories alphabetically for better UX
        categories.sort(String::compareToIgnoreCase);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        etServiceCategory.setAdapter(adapter);

        etServiceCategory.setOnClickListener(v -> etServiceCategory.showDropDown());
    }

    private void handleLogin() {
        String category = etServiceCategory.getText().toString().trim();
        String name = etProviderName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (category.isEmpty() || name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create provider user
        User provider = new User();
        provider.setId("provider_" + System.currentTimeMillis());
        provider.setFirstName(name);
        provider.setLastName("");
        provider.setEmail(email);
        provider.setPhone(phone);
        provider.setRole("provider");
        provider.setVerified(true);
        provider.setProfileImage("");
        provider.setServiceCategory(category);

        // Save provider
        dataManager.saveUser(provider);
        
        // Also save to SharedPrefs for normal authentication flow
        com.easy.easybook.utils.SharedPrefsManager prefsManager = com.easy.easybook.utils.SharedPrefsManager.getInstance(this);
        prefsManager.saveUser(provider);

        // Navigate to provider dashboard
        Intent intent = new Intent(this, ProviderDashboardActivity.class);
        intent.putExtra("provider_category", category);
        intent.putExtra("provider_id", provider.getId());
        startActivity(intent);
        finish();
    }

}
