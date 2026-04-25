package com.example.e_commerce_mart_asssign3;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DealsAdapter extends RecyclerView.Adapter<DealsAdapter.DealViewHolder> {

    private List<Product> deals;
    private OnFavoriteClickListener favoriteListener;

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Product product);
    }

    public DealsAdapter(List<Product> deals, OnFavoriteClickListener listener) {
        this.deals = deals;
        this.favoriteListener = listener;
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_deal, parent, false);
        return new DealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
        Product product = deals.get(position);

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(product.getPrice());
        holder.tvOriginalPrice.setText(product.getOriginalPrice());
        holder.tvDescription.setText(product.getDescription());
        holder.ivImage.setImageResource(product.getImageResId());

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

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    static class DealViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvOriginalPrice, tvDescription;
        ImageView ivHeart, ivImage;

        DealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName        = itemView.findViewById(R.id.tv_deal_name);
            tvPrice       = itemView.findViewById(R.id.tv_deal_price);
            tvOriginalPrice = itemView.findViewById(R.id.tv_deal_original_price);
            tvDescription = itemView.findViewById(R.id.tv_deal_description);
            ivHeart       = itemView.findViewById(R.id.iv_deal_heart);
            ivImage       = itemView.findViewById(R.id.iv_deal_image);
        }
    }
}
