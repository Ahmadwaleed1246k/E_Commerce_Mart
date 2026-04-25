package com.example.e_commerce_mart_asssign3;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment {

    private RecyclerView rvDeals;
    private RecyclerView rvRecommended;
    private TextView tvWelcome;
    private List<Product> dealsList;
    private List<Product> recommendedList;
    private DealsAdapter dealsAdapter;
    private RecommendedAdapter recommendedAdapter;
    private SharedPreferences sharedPreferences;

    private static final String PREF_FAVORITES = "user.favorites";
    private static final String PREF_USER_NAME = "user_name";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("FastMartPrefs", 0);

        rvDeals = view.findViewById(R.id.rv_deals);
        rvRecommended = view.findViewById(R.id.rv_recommended);
        tvWelcome = view.findViewById(R.id.tv_welcome);

        String userName = sharedPreferences.getString(PREF_USER_NAME, "User");

        if (userName != null && !userName.isEmpty()) {
            String capitalizedName = userName.substring(0, 1).toUpperCase() + userName.substring(1).toLowerCase();
            tvWelcome.setText("Hello, " + capitalizedName + "!");
        } else {
            tvWelcome.setText("Hello, User!");
        }

        loadProducts();

        LinearLayoutManager horizontalManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvDeals.setLayoutManager(horizontalManager);
        dealsAdapter = new DealsAdapter(dealsList, product -> toggleFavorite(product));
        rvDeals.setAdapter(dealsAdapter);

        rvRecommended.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recommendedAdapter = new RecommendedAdapter(recommendedList, product -> toggleFavorite(product));
        rvRecommended.setAdapter(recommendedAdapter);

        return view;
    }

    private void loadProducts() {
        dealsList = ProductData.getDealsOfTheDay();
        recommendedList = ProductData.getRecommendedProducts();

        Set<String> favoriteIds = sharedPreferences.getStringSet(PREF_FAVORITES, new HashSet<>());

        for (Product product : dealsList) {
            product.setFavorite(favoriteIds.contains(String.valueOf(product.getId())));
        }
        for (Product product : recommendedList) {
            product.setFavorite(favoriteIds.contains(String.valueOf(product.getId())));
        }

        // Fetch from Firebase
        com.google.firebase.database.FirebaseDatabase.getInstance().getReference("products")
                .addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull com.google.firebase.database.DataSnapshot snapshot) {
                        for (com.google.firebase.database.DataSnapshot data : snapshot.getChildren()) {
                            Product product = data.getValue(Product.class);
                            if (product != null) {
                                // Store the Firebase key
                                product.setProductKey(data.getKey());
                                
                                // Avoid duplicates if already in static list
                                boolean exists = false;
                                for (Product p : recommendedList) {
                                    if (p.getId() == product.getId()) {
                                        exists = true;
                                        break;
                                    }
                                }
                                if (!exists) {
                                    product.setFavorite(favoriteIds.contains(String.valueOf(product.getId())));
                                    recommendedList.add(product);
                                }
                            }
                        }
                        if (recommendedAdapter != null) {
                            recommendedAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull com.google.firebase.database.DatabaseError error) {}
                });
    }

    private void toggleFavorite(Product product) {
        Set<String> favoriteIds = new HashSet<>(sharedPreferences.getStringSet(PREF_FAVORITES, new HashSet<>()));
        String productId = String.valueOf(product.getId());

        if (product.isFavorite()) {
            favoriteIds.remove(productId);
            product.setFavorite(false);
            Toast.makeText(getContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
        } else {
            favoriteIds.add(productId);
            product.setFavorite(true);
            Toast.makeText(getContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(PREF_FAVORITES, favoriteIds);
        editor.apply();

        dealsAdapter.notifyDataSetChanged();
        recommendedAdapter.notifyDataSetChanged();
    }
}
