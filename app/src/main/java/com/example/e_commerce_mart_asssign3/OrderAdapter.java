package com.example.e_commerce_mart_asssign3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orders;

    public OrderAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.tvOrderId.setText("#" + order.getOrderId());
        holder.tvOrderDate.setText(order.getDate());
        holder.tvOrderStatus.setText(order.getStatus().toUpperCase());
        holder.tvOrderTotal.setText(String.format("$%.2f", order.getTotalAmount()));

        StringBuilder itemsBuilder = new StringBuilder();
        for (CartItem item : order.getItems()) {
            itemsBuilder.append(item.getProduct().getName())
                    .append(" x")
                    .append(item.getQuantity())
                    .append(" ($")
                    .append(String.format("%.2f", item.getTotalPrice()))
                    .append(")\n");
        }
        holder.tvOrderItems.setText(itemsBuilder.toString().trim());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderStatus, tvOrderItems, tvOrderTotal;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvOrderItems = itemView.findViewById(R.id.tv_order_items);
            tvOrderTotal = itemView.findViewById(R.id.tv_order_total);
        }
    }
}
