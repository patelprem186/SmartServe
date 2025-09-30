package com.easy.easybook.ui.provider.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easy.easybook.R;
import com.easy.easybook.models.Service;

import java.util.List;

public class ServiceManagementAdapter extends RecyclerView.Adapter<ServiceManagementAdapter.ServiceViewHolder> {
    
    private List<Service> services;
    private OnServiceActionListener listener;
    
    public interface OnServiceActionListener {
        void onEditService(Service service);
        void onDeleteService(Service service);
        void onToggleStatus(Service service);
    }
    
    public ServiceManagementAdapter(List<Service> services, OnServiceActionListener listener) {
        this.services = services;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service_management, parent, false);
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
        private TextView tvServicePrice;
        private TextView tvServiceDuration;
        private TextView tvServiceRating;
        private Switch switchStatus;
        private ImageButton btnEdit;
        private ImageButton btnDelete;
        
        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tv_service_name);
            tvServiceDescription = itemView.findViewById(R.id.tv_service_description);
            tvServicePrice = itemView.findViewById(R.id.tv_service_price);
            tvServiceDuration = itemView.findViewById(R.id.tv_service_duration);
            tvServiceRating = itemView.findViewById(R.id.tv_service_rating);
            switchStatus = itemView.findViewById(R.id.switch_status);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
        
        public void bind(Service service, OnServiceActionListener listener) {
            tvServiceName.setText(service.getName());
            tvServiceDescription.setText(service.getDescription());
            tvServicePrice.setText("$" + service.getPrice());
            tvServiceDuration.setText(service.getDuration());
            tvServiceRating.setText(String.format("%.1f ⭐", service.getRating()));
            switchStatus.setChecked(service.isAvailable());
            
            // Set click listeners
            btnEdit.setOnClickListener(v -> listener.onEditService(service));
            btnDelete.setOnClickListener(v -> listener.onDeleteService(service));
            switchStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (buttonView.isPressed()) { // Only trigger on user interaction
                    listener.onToggleStatus(service);
                }
            });
        }
    }
}
