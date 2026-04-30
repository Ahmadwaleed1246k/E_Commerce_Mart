package com.example.e_commerce_mart_asssign3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvDeals;
    private RecyclerView rvRecommended;
    private TextView tvWelcome;
    private FloatingActionButton fabChat;
    private List<Product> dealsList;
    private List<Product> recommendedList;
    private DealsAdapter dealsAdapter;
    private RecommendedAdapter recommendedAdapter;
    private SharedPreferences sharedPreferences;
    private DatabaseHelper dbHelper;

    private static final String PREF_USER_NAME = "user_name";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("FastMartPrefs", 0);
        dbHelper = new DatabaseHelper(getContext());

        rvDeals = view.findViewById(R.id.rv_deals);
        rvRecommended = view.findViewById(R.id.rv_recommended);
        tvWelcome = view.findViewById(R.id.tv_welcome);
        fabChat = view.findViewById(R.id.fab_chat);

        String userName = sharedPreferences.getString(PREF_USER_NAME, "User");

        if (userName != null && !userName.isEmpty()) {
            String capitalizedName = userName.substring(0, 1).toUpperCase() + userName.substring(1).toLowerCase();
            tvWelcome.setText("Hello, " + capitalizedName + "!");
        } else {
            tvWelcome.setText("Hello, User!");
        }

        loadProducts();

        fabChat.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadProducts() {
        dealsList = new java.util.ArrayList<>();
        recommendedList = new java.util.ArrayList<>();

        // Initialize adapters
        setupAdapters();

        // Real-time fetch from Firebase
        com.google.firebase.database.FirebaseDatabase.getInstance().getReference("products")
                .addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull com.google.firebase.database.DataSnapshot snapshot) {
                        dealsList.clear();
                        recommendedList.clear();
                        for (com.google.firebase.database.DataSnapshot data : snapshot.getChildren()) {
                            Product product = data.getValue(Product.class);
                            if (product != null) {
                                product.setProductKey(data.getKey());
                                product.setFavorite(dbHelper.isFavourite(product.getId()));
                                
                                recommendedList.add(product);
                                dealsList.add(product);
                            }
                        }
                        if (dealsAdapter != null) dealsAdapter.notifyDataSetChanged();
                        if (recommendedAdapter != null) recommendedAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull com.google.firebase.database.DatabaseError error) {}
                });
    }

    private void setupAdapters() {
        LinearLayoutManager horizontalManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvDeals.setLayoutManager(horizontalManager);
        dealsAdapter = new DealsAdapter(dealsList, product -> toggleFavorite(product));
        rvDeals.setAdapter(dealsAdapter);

        rvRecommended.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recommendedAdapter = new RecommendedAdapter(recommendedList, product -> toggleFavorite(product));
        rvRecommended.setAdapter(recommendedAdapter);
    }

    private void toggleFavorite(Product product) {
        if (product.isFavorite()) {
            dbHelper.removeFavourite(product.getId());
            product.setFavorite(false);
            Toast.makeText(getContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.addFavourite(product);
            product.setFavorite(true);
            Toast.makeText(getContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
        }

        dealsAdapter.notifyDataSetChanged();
        recommendedAdapter.notifyDataSetChanged();
    }
}
