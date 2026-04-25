package com.example.e_commerce_mart_asssign3;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavouritesFragment extends Fragment {

    private RecyclerView rvFavourites;
    private FavouritesAdapter adapter;
    private List<Product> favouriteProducts;
    private SharedPreferences sharedPreferences;
    private List<Product> allProducts;

    private static final String PREF_FAVORITES = "user.favorites";
    private static final String PREF_CART = "user.cart";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("FastMartPrefs", 0);
        allProducts = ProductData.getAllProducts();

        rvFavourites = view.findViewById(R.id.rv_favourites);
        rvFavourites.setLayoutManager(new LinearLayoutManager(getContext()));

        loadFavourites();

        adapter = new FavouritesAdapter(favouriteProducts,
                new FavouritesAdapter.OnItemClickListener() {
                    @Override
                    public void onCartClick(Product product) {
                        addToCart(product);
                    }

                    @Override
                    public void onDeleteClick(Product product, int position) {
                        showDeleteDialog(product, position);
                    }
                });

        rvFavourites.setAdapter(adapter);

        return view;
    }

    private void loadFavourites() {
        favouriteProducts = new ArrayList<>();
        Set<String> favoriteIds = sharedPreferences.getStringSet(PREF_FAVORITES, new HashSet<>());

        for (Product product : allProducts) {
            if (favoriteIds.contains(String.valueOf(product.getId()))) {
                product.setFavorite(true);
                favouriteProducts.add(product);
            }
        }
    }

    private void addToCart(Product product) {
        Set<String> cartItems = new HashSet<>(
                sharedPreferences.getStringSet(PREF_CART, new HashSet<>())
        );
        cartItems.add(String.valueOf(product.getId()));
        sharedPreferences.edit().putStringSet(PREF_CART, cartItems).apply();

        Toast.makeText(getContext(), product.getName() + " added to cart", Toast.LENGTH_SHORT).show();
    }

    private void showDeleteDialog(Product product, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete from Favourites");
        builder.setMessage("Do you want to delete " + product.getName() + " from favourites?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            removeFromFavourites(product, position);
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void removeFromFavourites(Product product, int position) {
        Set<String> favoriteIds = new HashSet<>(
                sharedPreferences.getStringSet(PREF_FAVORITES, new HashSet<>())
        );
        favoriteIds.remove(String.valueOf(product.getId()));
        sharedPreferences.edit().putStringSet(PREF_FAVORITES, favoriteIds).apply();

        favouriteProducts.remove(position);
        adapter.notifyItemRemoved(position);

        Toast.makeText(getContext(), "Removed from favourites", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavourites();
        if (adapter != null) {
            adapter.updateList(favouriteProducts);
        }
    }
}
