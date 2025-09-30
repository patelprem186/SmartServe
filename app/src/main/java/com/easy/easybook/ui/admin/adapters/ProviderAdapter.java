package com.easy.easybook.ui.admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easy.easybook.R;
import com.easy.easybook.models.User;

import java.util.ArrayList;
import java.util.List;

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ProviderViewHolder> {
    
    private List<User> providerList;
    private OnProviderActionListener listener;
    
    public interface OnProviderActionListener {
        void onDeleteProvider(User provider);
        void onViewProviderDetails(User provider);
    }
    
    public ProviderAdapter(OnProviderActionListener listener) {
        this.providerList = new ArrayList<>();
        this.listener = listener;
    }
    
    public void setProviderList(List<User> providerList) {
        this.providerList = providerList != null ? providerList : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_provider, parent, false);
        return new ProviderViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ProviderViewHolder holder, int position) {
        User provider = providerList.get(position);
        holder.bind(provider);
    }
    
    @Override
    public int getItemCount() {
        return providerList.size();
    }
    
    public class ProviderViewHolder extends RecyclerView.ViewHolder {
        
        private TextView tvProviderName;
        private TextView tvProviderEmail;
        private TextView tvProviderPhone;
        private TextView tvServiceCategory;
        private Button btnDelete;
        private Button btnViewDetails;
        
        public ProviderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProviderName = itemView.findViewById(R.id.tv_provider_name);
            tvProviderEmail = itemView.findViewById(R.id.tv_provider_email);
            tvProviderPhone = itemView.findViewById(R.id.tv_provider_phone);
            tvServiceCategory = itemView.findViewById(R.id.tv_service_category);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);
        }
        
        public void bind(User provider) {
            // Set provider name
            String fullName = provider.getFirstName() + " " + provider.getLastName();
            tvProviderName.setText(fullName);
            
            // Set email
            tvProviderEmail.setText(provider.getEmail());
            
            // Set phone
            tvProviderPhone.setText(provider.getPhone());
            
            // Set service category
            String category = provider.getServiceCategory();
            if (category != null && !category.isEmpty()) {
                tvServiceCategory.setText("Category: " + category);
            } else {
                tvServiceCategory.setText("Category: Not specified");
            }
            
            // Set click listeners
            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteProvider(provider);
                }
            });
            
            btnViewDetails.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewProviderDetails(provider);
                }
            });
        }
    }
}
