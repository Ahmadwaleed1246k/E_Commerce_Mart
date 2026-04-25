package com.example.e_commerce_mart_asssign3;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchFragment extends Fragment {

    private EditText etSearch;
    private Button btnSearch, btnClearAll;
    private LinearLayout llRecentSearches;
    private ImageView ivBack;
    private SharedPreferences sharedPreferences;
    private List<Product> allProducts;

    private static final String PREF_SEARCH_HISTORY = "search.history";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("FastMartPrefs", 0);
        allProducts = ProductData.getAllProducts();

        etSearch = view.findViewById(R.id.et_search);
        btnSearch = view.findViewById(R.id.btn_search);
        btnClearAll = view.findViewById(R.id.btn_clear_all);
        llRecentSearches = view.findViewById(R.id.ll_recent_searches);
        ivBack = view.findViewById(R.id.iv_back);

        ivBack.setOnClickListener(v -> {
            hideKeyboard();
            etSearch.clearFocus();
        });

        btnSearch.setOnClickListener(v -> performSearch());
        btnClearAll.setOnClickListener(v -> clearSearchHistory());

        etSearch.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                hideKeyboard();
                return true;
            }
            return false;
        });

        etSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard();
            }
        });

        loadRecentSearches();

        return view;
    }

    private void performSearch() {
        String query = etSearch.getText().toString().trim().toLowerCase();

        if (query.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a search term", Toast.LENGTH_SHORT).show();
            return;
        }

        saveSearchQuery(query);

        boolean found = false;
        for (Product product : allProducts) {
            if (product.getName().toLowerCase().contains(query) ||
                    product.getDescription().toLowerCase().contains(query) ||
                    product.getModel().toLowerCase().contains(query)) {
                found = true;
                break;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Search Result");
        if (found) {
            builder.setMessage("Product Found!");
        } else {
            builder.setMessage("Product Not Found!");
        }
        builder.setPositiveButton("OK", null);
        builder.show();

        etSearch.setText("");
        hideKeyboard();
        loadRecentSearches();
    }

    private void saveSearchQuery(String query) {
        Set<String> searchHistory = new HashSet<>(
                sharedPreferences.getStringSet(PREF_SEARCH_HISTORY, new HashSet<>())
        );
        searchHistory.add(query);
        sharedPreferences.edit().putStringSet(PREF_SEARCH_HISTORY, searchHistory).apply();
    }

    private void clearSearchHistory() {
        sharedPreferences.edit().remove(PREF_SEARCH_HISTORY).apply();
        loadRecentSearches();
        Toast.makeText(getContext(), "Search history cleared", Toast.LENGTH_SHORT).show();
    }

    private void loadRecentSearches() {
        llRecentSearches.removeAllViews();

        Set<String> searchHistory = sharedPreferences.getStringSet(PREF_SEARCH_HISTORY, new HashSet<>());

        if (searchHistory.isEmpty()) {
            TextView tvEmpty = new TextView(getContext());
            tvEmpty.setText("No recent searches");
            tvEmpty.setPadding(16, 32, 16, 32);
            tvEmpty.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvEmpty.setTextColor(getResources().getColor(android.R.color.darker_gray));
            llRecentSearches.addView(tvEmpty);
            return;
        }

        for (String query : searchHistory) {
            TextView tvQuery = new TextView(getContext());
            tvQuery.setText(query);
            tvQuery.setPadding(16, 12, 16, 12);
            tvQuery.setTextSize(14);
            tvQuery.setBackgroundResource(android.R.drawable.list_selector_background);
            tvQuery.setOnClickListener(v -> {
                etSearch.setText(query);
                performSearch();
            });
            llRecentSearches.addView(tvQuery);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireActivity()
                .getSystemService(requireContext().INPUT_METHOD_SERVICE);
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
