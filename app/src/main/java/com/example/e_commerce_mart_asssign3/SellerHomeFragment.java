package com.example.e_commerce_mart_asssign3;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SellerHomeFragment extends Fragment {

    private TextView tvWelcome;
    private RecyclerView rvProducts;
    private FloatingActionButton fabAdd;
    private RecommendedAdapter adapter;
    private List<Product> productList;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            currentUserId = user.getUid();
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();

        initViews(view);
        loadSellerProducts();
        loadSellerName();

        return view;
    }

    private void initViews(View view) {
        tvWelcome = view.findViewById(R.id.tv_seller_welcome);
        rvProducts = view.findViewById(R.id.rv_seller_products);
        fabAdd = view.findViewById(R.id.fab_add_product);

        productList = new ArrayList<>();
        adapter = new RecommendedAdapter(productList, product -> {
            Intent intent = new Intent(getContext(), ProductDetailActivity.class);
            if (product.getProductKey() != null) {
                intent.putExtra("product_key", product.getProductKey());
            }
            intent.putExtra("product_id", product.getId());
            startActivity(intent);
        });

        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvProducts.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AddProductActivity.class));
        });
    }

    private void loadSellerName() {
        mDatabase.child("users").child(currentUserId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.getValue(String.class);
                    if (name != null && !name.isEmpty()) {
                        tvWelcome.setText("Hello " + name);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadSellerProducts() {
        mDatabase.child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    try {
                        Product product = data.getValue(Product.class);
                        if (product != null) {
                            // Store the Firebase key so we can look it up later
                            product.setProductKey(data.getKey());
                            productList.add(product);
                        }
                    } catch (Exception e) {
                        // Skip products that fail to parse
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
