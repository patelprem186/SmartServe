package com.easy.easybook.ui.customer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easy.easybook.R;
import com.easy.easybook.models.Service;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    
    private List<Service> cartItems;
    private OnCartItemClickListener listener;
    
    public interface OnCartItemClickListener {
        void onRemoveItem(Service service);
        void onBookNow(Service service);
    }
    
    public CartAdapter(OnCartItemClickListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Service service = cartItems.get(position);
        holder.bind(service);
    }
    
    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }
    
    public void updateCartItems(List<Service> cartItems) {
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }
    
    class CartViewHolder extends RecyclerView.ViewHolder {
        private TextView tvServiceName;
        private TextView tvServiceDescription;
        private TextView tvServicePrice;
        private TextView tvServiceCategory;
        private ImageView ivServiceImage;
        private TextView btnRemove;
        private TextView btnBookNow;
        
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tv_service_name);
            tvServiceDescription = itemView.findViewById(R.id.tv_service_description);
            tvServicePrice = itemView.findViewById(R.id.tv_service_price);
            tvServiceCategory = itemView.findViewById(R.id.tv_service_category);
            ivServiceImage = itemView.findViewById(R.id.iv_service_image);
            btnRemove = itemView.findViewById(R.id.btn_remove);
            btnBookNow = itemView.findViewById(R.id.btn_book_now);
        }
        
        public void bind(Service service) {
            tvServiceName.setText(service.getName());
            tvServiceDescription.setText(service.getDescription());
            tvServicePrice.setText(String.format("$%.2f", service.getPrice()));
            tvServiceCategory.setText(service.getCategory());
            
            // Set default image
            ivServiceImage.setImageResource(R.drawable.placeholder_service);
            
            btnRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveItem(service);
                }
            });
            
            btnBookNow.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBookNow(service);
                }
            });
        }
    }
}