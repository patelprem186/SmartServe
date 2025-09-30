package com.easy.easybook;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.easy.easybook.ui.auth.LoginActivity;
import com.easy.easybook.ui.customer.CustomerMainActivity;
import com.easy.easybook.ui.provider.ProviderMainActivity;
import com.easy.easybook.utils.SharedPrefsManager;

/**
 * Main Activity - Entry point of the application
 * Redirects to login screen
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if user is already logged in
        SharedPrefsManager prefsManager = SharedPrefsManager.getInstance(this);
        
        if (prefsManager.isLoggedIn()) {
            // User is logged in, redirect based on role
            String userRole = prefsManager.getUserRole();
            Intent intent;
            
            if ("provider".equals(userRole)) {
                intent = new Intent(this, ProviderMainActivity.class);
            } else {
                intent = new Intent(this, CustomerMainActivity.class);
            }
            
            startActivity(intent);
        } else {
            // User not logged in, redirect to login
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        
        finish();
    }
}