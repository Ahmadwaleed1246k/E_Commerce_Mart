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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private DatabaseHelper dbHelper;
    private OnCartUpdateListener updateListener;

    public interface OnCartUpdateListener {
        void onCartUpdated();
    }

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartUpdateListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.dbHelper = new DatabaseHelper(context);
        this.updateListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        Product product = item.getProduct();

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(product.getPrice());
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvTotal.setText(String.format("$%.2f", item.getTotalPrice()));

        if (product.hasImageUrl()) {
            Glide.with(context).load(product.getImageUrl()).placeholder(R.drawable.camera).into(holder.ivImage);
        } else if (product.getImageResId() != 0) {
            holder.ivImage.setImageResource(product.getImageResId());
        }

        holder.btnPlus.setOnClickListener(v -> {
            int newQty = item.getQuantity() + 1;
            dbHelper.updateCartQuantity(product.getId(), newQty);
            item.setQuantity(newQty);
            notifyItemChanged(position);
            if (updateListener != null) updateListener.onCartUpdated();
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                int newQty = item.getQuantity() - 1;
                dbHelper.updateCartQuantity(product.getId(), newQty);
                item.setQuantity(newQty);
                notifyItemChanged(position);
                if (updateListener != null) updateListener.onCartUpdated();
            }
        });

        holder.ivMore.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Remove Item")
                    .setMessage("Do you want to remove this item from your cart?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dbHelper.removeFromCart(product.getId());
                        cartItems.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, cartItems.size());
                        if (updateListener != null) updateListener.onCartUpdated();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateList(List<CartItem> newList) {
        this.cartItems = newList;
        notifyDataSetChanged();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage, btnPlus, btnMinus, ivMore;
        TextView tvName, tvPrice, tvQuantity, tvTotal;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_cart_image);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            ivMore = itemView.findViewById(R.id.iv_cart_more);
            tvName = itemView.findViewById(R.id.tv_cart_name);
            tvPrice = itemView.findViewById(R.id.tv_cart_price);
            tvQuantity = itemView.findViewById(R.id.tv_cart_quantity);
            tvTotal = itemView.findViewById(R.id.tv_cart_total);
        }
    }
}
