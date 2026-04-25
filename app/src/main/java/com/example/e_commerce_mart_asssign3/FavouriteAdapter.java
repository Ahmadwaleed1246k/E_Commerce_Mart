package com.example.e_commerce_mart_asssign3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavViewHolder> {

    private Context context;
    private List<Product> products;
    private DatabaseHelper dbHelper;
    private OnItemRemovedListener removedListener;

    public interface OnItemRemovedListener {
        void onItemRemoved();
    }

    public FavouriteAdapter(Context context, List<Product> products, OnItemRemovedListener listener) {
        this.context = context;
        this.products = products;
        this.dbHelper = new DatabaseHelper(context);
        this.removedListener = listener;
    }

    @NonNull
    @Override
    public FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favourite, parent, false);
        return new FavViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavViewHolder holder, int position) {
        Product product = products.get(position);
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(product.getPrice());
        holder.tvModel.setText(product.getModel() != null ? product.getModel() : product.getType());

        if (product.hasImageUrl()) {
            Glide.with(context).load(product.getImageUrl()).placeholder(R.drawable.camera).into(holder.ivImage);
        } else if (product.getImageResId() != 0) {
            holder.ivImage.setImageResource(product.getImageResId());
        } else {
            holder.ivImage.setImageResource(R.drawable.camera);
        }

        holder.ivCart.setOnClickListener(v -> {
            dbHelper.addToCart(product, 1);
            Toast.makeText(context, product.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
        });

        holder.ivMore.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Favourite")
                    .setMessage("Do you want to delete this product from favourites?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dbHelper.removeFavourite(product.getId());
                        products.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, products.size());
                        if (removedListener != null) removedListener.onItemRemoved();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class FavViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage, ivCart, ivMore;
        TextView tvName, tvPrice, tvModel;

        public FavViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_fav_image);
            ivCart = itemView.findViewById(R.id.iv_fav_cart);
            ivMore = itemView.findViewById(R.id.iv_fav_more);
            tvName = itemView.findViewById(R.id.tv_fav_name);
            tvPrice = itemView.findViewById(R.id.tv_fav_price);
            tvModel = itemView.findViewById(R.id.tv_fav_model);
        }
    }
}
