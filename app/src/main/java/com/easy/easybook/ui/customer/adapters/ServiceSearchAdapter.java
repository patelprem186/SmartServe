package com.easy.easybook.ui.customer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easy.easybook.R;
import com.easy.easybook.models.Service;

import java.util.List;

public class ServiceSearchAdapter extends RecyclerView.Adapter<ServiceSearchAdapter.ServiceViewHolder> {
    
    private List<Service> services;
    private OnServiceClickListener listener;
    
    public interface OnServiceClickListener {
        void onServiceClick(Service service);
        void onBookService(Service service);
    }
    
    public ServiceSearchAdapter(List<Service> services, OnServiceClickListener listener) {
        this.services = services;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service_search, parent, false);
        return new ServiceViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = services.get(position);
        holder.bind(service, listener);
    }
    
    @Override
    public int getItemCount() {
        return services.size();
    }
    
    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        private TextView tvServiceName;
        private TextView tvServiceDescription;
        private TextView tvProviderName;
        private TextView tvServicePrice;
        private TextView tvServiceRating;
        private TextView tvServiceDuration;
        private TextView btnBookNow;
        
        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tv_service_name);
            tvServiceDescription = itemView.findViewById(R.id.tv_service_description);
            tvProviderName = itemView.findViewById(R.id.tv_provider_name);
            tvServicePrice = itemView.findViewById(R.id.tv_service_price);
            tvServiceRating = itemView.findViewById(R.id.tv_service_rating);
            tvServiceDuration = itemView.findViewById(R.id.tv_service_duration);
            btnBookNow = itemView.findViewById(R.id.btn_book_now);
        }
        
        public void bind(Service service, OnServiceClickListener listener) {
            tvServiceName.setText(service.getName());
            tvServiceDescription.setText(service.getDescription());
            tvProviderName.setText(service.getProviderName());
            tvServicePrice.setText("$" + service.getPrice());
            tvServiceRating.setText(String.format("%.1f ⭐", service.getRating()));
            tvServiceDuration.setText(service.getDuration());
            
            // Set click listeners
            itemView.setOnClickListener(v -> listener.onServiceClick(service));
            btnBookNow.setOnClickListener(v -> listener.onBookService(service));
        }
    }
}
