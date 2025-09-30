package com.easy.easybook.ui.customer.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easy.easybook.databinding.ItemFeaturedServiceBinding;
import com.easy.easybook.models.ServiceItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying featured services in horizontal scroll
 */
public class FeaturedServicesAdapter extends RecyclerView.Adapter<FeaturedServicesAdapter.ServiceViewHolder> {
    
    private List<ServiceItem> services;
    private OnServiceClickListener listener;
    
    public interface OnServiceClickListener {
        void onServiceClick(ServiceItem service);
    }
    
    public FeaturedServicesAdapter(OnServiceClickListener listener) {
        this.services = new ArrayList<>();
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFeaturedServiceBinding binding = ItemFeaturedServiceBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ServiceViewHolder(binding);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        holder.bind(services.get(position));
    }
    
    @Override
    public int getItemCount() {
        return services.size();
    }
    
    public void updateServices(List<ServiceItem> newServices) {
        this.services.clear();
        if (newServices != null) {
            this.services.addAll(newServices);
        }
        notifyDataSetChanged();
    }
    
    class ServiceViewHolder extends RecyclerView.ViewHolder {
        private ItemFeaturedServiceBinding binding;
        
        public ServiceViewHolder(ItemFeaturedServiceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        
        public void bind(ServiceItem service) {
            binding.tvServiceName.setText(service.getName());
            binding.tvServiceDescription.setText(service.getDescription());
            binding.tvServicePrice.setText(String.format(Locale.getDefault(), "$%.0f", service.getPrice()));
            binding.tvServiceDuration.setText(service.getDuration());
            binding.ratingBar.setRating(service.getRating());
            binding.tvRating.setText(String.format(Locale.getDefault(), "%.1f", service.getRating()));
            
            // Set click listener
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onServiceClick(service);
                }
            });
        }
    }
}
