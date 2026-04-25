package com.example.e_commerce_mart_asssign3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartItem> cartItems;
    private OnCartItemListener listener;

    public interface OnCartItemListener {
        void onQuantityChanged();
        void onRemoveClick(int position);
    }

    public CartAdapter(List<CartItem> cartItems, OnCartItemListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        Product product = cartItem.getProduct();

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(product.getPrice());
        holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
        holder.tvTotal.setText(String.format("$%.2f", cartItem.getTotalPrice()));
        holder.ivImage.setImageResource(product.getImageResId());

        holder.btnPlus.setOnClickListener(v -> {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
            holder.tvTotal.setText(String.format("$%.2f", cartItem.getTotalPrice()));
            if (listener != null) {
                listener.onQuantityChanged();
            }
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (cartItem.getQuantity() > 1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
                holder.tvTotal.setText(String.format("$%.2f", cartItem.getTotalPrice()));
                if (listener != null) {
                    listener.onQuantityChanged();
                }
            }
        });

        holder.ivDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveClick(position);
            }
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity, tvTotal;
        ImageView ivImage, ivDelete;
        ImageView btnPlus, btnMinus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_cart_name);
            tvPrice = itemView.findViewById(R.id.tv_cart_price);
            tvQuantity = itemView.findViewById(R.id.tv_cart_quantity);
            tvTotal = itemView.findViewById(R.id.tv_cart_total);
            ivImage = itemView.findViewById(R.id.iv_cart_image);
            ivDelete = itemView.findViewById(R.id.iv_cart_delete);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnMinus = itemView.findViewById(R.id.btn_minus);
        }
    }
}
