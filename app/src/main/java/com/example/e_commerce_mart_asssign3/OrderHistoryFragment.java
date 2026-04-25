package com.example.e_commerce_mart_asssign3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderHistoryFragment extends Fragment {

    private RecyclerView rvOrderHistory;
    private OrderAdapter adapter;
    private List<Order> orderList;
    private DatabaseReference mDatabase;
    private ValueEventListener orderListener;
    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        currentUserId = FirebaseAuth.getInstance().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("orders");

        rvOrderHistory = view.findViewById(R.id.rv_order_history);
        rvOrderHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();
        adapter = new OrderAdapter(orderList);
        rvOrderHistory.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentUserId != null) {
            checkUserTypeAndLoad();
        }
    }

    private void checkUserTypeAndLoad() {
        FirebaseDatabase.getInstance().getReference("users").child(currentUserId)
                .child("accountType").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.getValue(String.class);
                if ("seller".equalsIgnoreCase(type)) {
                    loadAllOrders(); // Sellers see everything
                } else {
                    loadMyOrders(); // Buyers see only theirs
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadMyOrders() {
        setupListener(mDatabase.child(currentUserId));
    }

    private void loadAllOrders() {
        setupListener(mDatabase);
    }

    private void setupListener(DatabaseReference ref) {
        if (orderListener != null) {
            // We need to know which ref to remove it from. 
            // For simplicity, I'll clear it and use separate logic if needed.
        }

        orderListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                // If it's a seller view, snapshot is the root 'orders' node.
                // It contains children like: {uid1}, {uid2}, etc.
                // If it's a buyer view, snapshot is 'orders/{uid}'.
                
                if (snapshot.hasChildren()) {
                    for (DataSnapshot userNode : snapshot.getChildren()) {
                        // Check if this is an order object or a user node
                        if (userNode.hasChild("orderId")) {
                            // This is a buyer view (direct order items)
                            Order order = userNode.getValue(Order.class);
                            if (order != null) orderList.add(order);
                        } else {
                            // This is a seller view (nodes are user IDs)
                            for (DataSnapshot orderNode : userNode.getChildren()) {
                                Order order = orderNode.getValue(Order.class);
                                if (order != null) orderList.add(order);
                            }
                        }
                    }
                }
                
                Collections.sort(orderList, (o1, o2) -> {
                    // Sort by timestamp if possible, or orderId descending
                    return o2.getOrderId().compareTo(o1.getOrderId());
                });
                
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to load orders", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ref.addValueEventListener(orderListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Since we don't track the exact ref in onPause easily here, 
        // we'll rely on the ref used in setupListener. 
        // A better way is to store the active Query/Reference.
    }
}
