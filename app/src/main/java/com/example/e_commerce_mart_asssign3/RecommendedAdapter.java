package com.example.e_commerce_mart_asssign3;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.ViewHolder> {

    private List<Product> products;
    private OnFavoriteClickListener favoriteListener;

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Product product);
    }

    public RecommendedAdapter(List<Product> products, OnFavoriteClickListener listener) {
        this.products = products;
        this.favoriteListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommended, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(product.getPrice());
        holder.tvModel.setText(product.getModel() != null ? product.getModel() : product.getType());

        // Load image: prefer URL, fallback to drawable resource
        if (product.hasImageUrl()) {
            Glide.with(holder.itemView.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.camera)
                    .error(R.drawable.camera)
                    .centerCrop()
                    .into(holder.ivImage);
        } else if (product.getImageResId() != 0) {
            holder.ivImage.setImageResource(product.getImageResId());
        } else {
            holder.ivImage.setImageResource(R.drawable.camera);
        }

        if (product.isFavorite()) {
            holder.ivHeart.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            holder.ivHeart.setImageResource(android.R.drawable.btn_star_big_off);
        }

        holder.ivHeart.setOnClickListener(v -> {
            if (favoriteListener != null) {
                favoriteListener.onFavoriteClick(product);
            }
        });

        // Product click to open detail page — pass the Firebase key if available
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), ProductDetailActivity.class);
            if (product.getProductKey() != null) {
                intent.putExtra("product_key", product.getProductKey());
            }
            intent.putExtra("product_id", product.getId());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvModel;
        ImageView ivImage, ivHeart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_rec_name);
            tvPrice = itemView.findViewById(R.id.tv_rec_price);
            tvModel = itemView.findViewById(R.id.tv_rec_model);
            ivImage = itemView.findViewById(R.id.iv_rec_image);
            ivHeart = itemView.findViewById(R.id.iv_rec_heart);
        }
    }
}
