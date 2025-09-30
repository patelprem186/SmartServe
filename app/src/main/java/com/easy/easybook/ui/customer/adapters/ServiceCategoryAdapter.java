package com.easy.easybook.ui.customer.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easy.easybook.databinding.ItemServiceCategoryBinding;
import com.easy.easybook.models.ServiceCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying service categories in a grid layout
 */
public class ServiceCategoryAdapter extends RecyclerView.Adapter<ServiceCategoryAdapter.CategoryViewHolder> {
    
    private List<ServiceCategory> categories;
    private OnCategoryClickListener listener;
    
    public interface OnCategoryClickListener {
        void onCategoryClick(ServiceCategory category);
    }
    
    public ServiceCategoryAdapter(OnCategoryClickListener listener) {
        this.categories = new ArrayList<>();
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemServiceCategoryBinding binding = ItemServiceCategoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(binding);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(categories.get(position));
    }
    
    @Override
    public int getItemCount() {
        return categories.size();
    }
    
    public void updateCategories(List<ServiceCategory> newCategories) {
        this.categories.clear();
        if (newCategories != null) {
            this.categories.addAll(newCategories);
        }
        notifyDataSetChanged();
    }
    
    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private ItemServiceCategoryBinding binding;
        
        public CategoryViewHolder(ItemServiceCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        
        public void bind(ServiceCategory category) {
            binding.tvCategoryName.setText(category.getName());
            
            // Set appropriate emoji based on category
            String emoji = getCategoryEmoji(category.getName());
            binding.tvCategoryIcon.setText(emoji);
            
            // Set click listener
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(category);
                }
            });
        }
        
        private String getCategoryEmoji(String categoryName) {
            switch (categoryName.toLowerCase()) {
                case "plumbing":
                    return "🔧";
                case "cleaning":
                    return "🧹";
                case "electrical":
                    return "⚡";
                case "hvac":
                    return "🌡️";
                case "beauty":
                    return "💄";
                case "tutoring":
                    return "📚";
                case "fitness":
                    return "💪";
                case "gardening":
                    return "🌱";
                case "automotive":
                    return "🚗";
                case "pet care":
                    return "🐕";
                case "home repair":
                    return "🔨";
                case "cooking":
                    return "👨‍🍳";
                case "massage":
                    return "💆";
                case "photography":
                    return "📸";
                case "music":
                    return "🎵";
                default:
                    return "🔧";
            }
        }
    }
}
