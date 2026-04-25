package com.example.e_commerce_mart_asssign3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class FavouritesFragment extends Fragment {

    private RecyclerView rvFavourites;
    private FavouriteAdapter adapter;
    private List<Product> favList;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        dbHelper = new DatabaseHelper(getContext());
        rvFavourites = view.findViewById(R.id.rv_favourites);
        rvFavourites.setLayoutManager(new LinearLayoutManager(getContext()));

        loadFavourites();

        return view;
    }

    private void loadFavourites() {
        favList = dbHelper.getAllFavourites();
        adapter = new FavouriteAdapter(getContext(), favList, () -> {
            // Callback when item is removed
            if (favList.isEmpty()) {
                // Show empty state if needed
            }
        });
        rvFavourites.setAdapter(adapter);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Reload data in case it was modified in another fragment (e.g. Home)
        loadFavourites();
    }
}
