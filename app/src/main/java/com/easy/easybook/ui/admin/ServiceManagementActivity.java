package com.easy.easybook.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.easy.easybook.R;
import com.easy.easybook.databinding.ActivityServiceManagementBinding;
import com.easy.easybook.models.Service;
import com.easy.easybook.data.LocalDataManager;
import com.easy.easybook.data.ServiceManager;
import com.easy.easybook.ui.admin.adapters.AdminServiceAdapter;

public class ServiceManagementActivity extends AppCompatActivity implements AdminServiceAdapter.OnServiceActionListener {

    private ActivityServiceManagementBinding binding;
    private AdminServiceAdapter adapter;
    private LocalDataManager dataManager;
    private Service editingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServiceManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataManager = LocalDataManager.getInstance(this);

        // Check if editing a service passed in the extras
        if (getIntent().getBooleanExtra("is_edit_mode", false)) {
            editingService = (Service) getIntent().getSerializableExtra("service_to_edit");
            if (editingService != null) {
                populateEditForm(editingService);
            }
        }

        setupUI();
        setupRecyclerView();
        loadServices();
    }

    private void setupUI() {
        // Initialize filter actions
        binding.btnAll.setOnClickListener(v -> filterServices("all"));
        binding.btnPlumbing.setOnClickListener(v -> filterServices("Plumbing"));
        binding.btnElectrical.setOnClickListener(v -> filterServices("Electrical"));
        binding.btnHVAC.setOnClickListener(v -> filterServices("HVAC"));
        binding.btnCleaning.setOnClickListener(v -> filterServices("Cleaning"));
        binding.btnLandscaping.setOnClickListener(v -> filterServices("Landscaping"));

        // Manage service buttons
        binding.btnAddService.setOnClickListener(v -> showAddServiceDialog());
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new AdminServiceAdapter(this);
        binding.rvServices.setLayoutManager(new LinearLayoutManager(this));
        binding.rvServices.setAdapter(adapter);
    }

    private void loadServices() {
        // Use centralized ServiceManager for consistency
        ServiceManager serviceManager = ServiceManager.getInstance(this);
        List<Service> services = serviceManager.getAllServices();
        adapter.setServices(services);

        if (services.isEmpty()) {
            binding.layoutEmpty.setVisibility(android.view.View.VISIBLE);
            binding.rvServices.setVisibility(android.view.View.GONE);
        } else {
            binding.layoutEmpty.setVisibility(android.view.View.GONE);
            binding.rvServices.setVisibility(android.view.View.VISIBLE);
        }
    }

    private void filterServices(String category) {
        // Use centralized ServiceManager for consistency
        ServiceManager serviceManager = ServiceManager.getInstance(this);
        List<Service> allServices = serviceManager.getAllServices();
        
        if (category.equals("all")) {
            adapter.setServices(allServices);
            return;
        }
        
        List<Service> filteredServices = new ArrayList<>();
        for (Service service : allServices) {
            if (service.getCategory().equals(category)) {
                filteredServices.add(service);
            }
        }
        adapter.setServices(filteredServices);
    }

    private void showAddServiceDialog() {
        showServiceDialog(null);
    }

    private void populateEditForm(Service service) {
        showServiceDialog(service);
    }

    private void showServiceDialog(Service service) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        
        android.view.View dialogView = android.view.LayoutInflater.from(this).
            inflate(R.layout.dialog_service_edit, null);
        
        EditText etName = dialogView.findViewById(R.id.etServiceName);
        EditText etCategory = dialogView.findViewById(R.id.etServiceCategory);
        EditText etDescription = dialogView.findViewById(R.id.etServiceDescription);
        EditText etPrice = dialogView.findViewById(R.id.etServicePrice);
        EditText etDuration = dialogView.findViewById(R.id.etServiceDuration);

        if (service != null) {
            etName.setText(service.getName());
            etCategory.setText(service.getCategory());
            etDescription.setText(service.getDescription());
            etPrice.setText(String.format("%.2f", service.getPrice()));
            etDuration.setText(service.getDuration());
        }

        builder.setTitle(service != null ? "Edit Service" : "Add Service")
               .setView(dialogView)
               .setPositiveButton(service != null ? "Update" : "Add", (dialog, which) -> {
                   handleSaveService(etName, etCategory, etDescription, etPrice, etDuration, service);
               })
               .setNegativeButton("Cancel", null)
               .show();
    }

    private void handleSaveService(
            EditText etName, 
            EditText etCategory, 
            EditText etDescription, 
            EditText etPrice, 
            EditText etDuration, 
            Service existingService) {
            
        String name = etName.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String duration = etDuration.getText().toString().trim();
        
        if (name.isEmpty() || category.isEmpty() || priceStr.isEmpty() || duration.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            double price = Double.parseDouble(priceStr);
            Service service = existingService != null ? existingService : new Service();
            
            service.setName(name);
            service.setCategory(category);
            service.setDescription(description);
            service.setPrice(price);
            service.setDuration(duration);
            service.setRating(5.0f); // Default for new services
            
            if (dataManager.saveService(service)) {
                Toast.makeText(this, 
                    existingService != null ? "Service updated" : "Service added successfully", 
                    Toast.LENGTH_SHORT).show();
                loadServices();
            } else {
                Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException | IllegalStateException e) {
            Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException | SecurityException e) {
            Toast.makeText(this, "Error saving service", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEditService(Service service) {
        showServiceDialog(service);
    }

    @Override
    public void onDeleteService(Service service) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Delete Service")
                .setMessage("Are you sure you want to delete this service? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (dataManager.deleteService(service.getId())) {
                        Toast.makeText(this, "Service deleted successfully", Toast.LENGTH_SHORT).show();
                        loadServices();
                    } else {
                        Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
