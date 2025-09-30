package com.easy.easybook.ui.admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easy.easybook.R;
import com.easy.easybook.models.Service;

import java.util.ArrayList;
import java.util.List;

public class AdminServiceAdapter extends RecyclerView.Adapter<AdminServiceAdapter.ServiceViewHolder> {

    private List<Service> services = new ArrayList<>();
    private OnServiceActionListener listener;

    public interface OnServiceActionListener {
        void onEditService(Service service);
        void onDeleteService(Service service);
    }

    public AdminServiceAdapter(OnServiceActionListener listener) {
        this.listener = listener;
    }

    public void setServices(List<Service> services) {
        this.services = services != null ? services : new ArrayList<>();
        notifyDataSetChanged();
    }

    public List<Service> getServices() {
        return services;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = services.get(position);
        holder.bind(service);
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public class ServiceViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivServiceIcon;
        private TextView tvServiceName, tvServiceCategory, tvServicePrice, tvServiceDuration;
        private View btnEdit, btnDelete;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            ivServiceIcon = itemView.findViewById(R.id.ivServiceIcon);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvServiceCategory = itemView.findViewById(R.id.tvServiceCategory);
            tvServicePrice = itemView.findViewById(R.id.tvServicePrice);
            tvServiceDuration = itemView.findViewById(R.id.tvServiceDuration);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Service service) {
            // Set service data
            tvServiceName.setText(service.getName());
            tvServiceCategory.setText(service.getCategory());
            tvServicePrice.setText(String.format("$%.2f", service.getPrice()));
            tvServiceDuration.setText(service.getDuration());
            
            // Set click listeners
            btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEditService(service);
            });
            
            btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteService(service);
            });
        }
    }
}
