package com.example.e_commerce_mart_asssign3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.ViewHolder> {

    private List<Product> products;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onCartClick(Product product);
        void onDeleteClick(Product product, int position);
    }

    public FavouritesAdapter(List<Product> products, OnItemClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favourite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(product.getPrice());
        holder.tvModel.setText(product.getModel());
        holder.ivImage.setImageResource(product.getImageResId());

        holder.ivCart.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCartClick(product);
            }
        });

        holder.ivDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(product, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateList(List<Product> newList) {
        this.products = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvModel;
        ImageView ivImage, ivCart, ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_fav_name);
            tvPrice = itemView.findViewById(R.id.tv_fav_price);
            tvModel = itemView.findViewById(R.id.tv_fav_model);
            ivImage = itemView.findViewById(R.id.iv_fav_image);
            ivCart = itemView.findViewById(R.id.iv_fav_cart);
            ivDelete = itemView.findViewById(R.id.iv_fav_delete);
        }
    }
}
