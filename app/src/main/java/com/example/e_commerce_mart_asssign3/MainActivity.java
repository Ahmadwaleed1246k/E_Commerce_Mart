package com.example.e_commerce_mart_asssign3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private SharedPreferences sharedPreferences;

    private DrawerLayout drawerLayout;
    private FrameLayout fragmentContainer;
    private MaterialCardView customBottomNav;
    private NavigationView navViewSeller;
    
    // Custom Nav Buttons
    private LinearLayout btnHome, btnSearch, btnFav, btnOrders, btnCart, btnProfile;
    private ImageView ivHome, ivSearch, ivFav, ivOrders, ivCart, ivProfile;
    private TextView tvHome, tvSearch, tvFav, tvOrders, tvCart, tvProfile;

    // Theme buttons
    private TextView btnThemeLight, btnThemeDark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences("FastMartPrefs", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("is_dark_mode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            navigateToLogin();
            return;
        }

        initViews();
        checkAccountTypeAndLoadUI();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        fragmentContainer = findViewById(R.id.fragment_container);
        customBottomNav = findViewById(R.id.custom_bottom_nav);
        navViewSeller = findViewById(R.id.nav_view_seller);
        
        btnThemeLight = findViewById(R.id.btn_theme_light);
        btnThemeDark = findViewById(R.id.btn_theme_dark);

        // Initialize Custom Nav Components
        btnHome = findViewById(R.id.btn_nav_home);
        btnSearch = findViewById(R.id.btn_nav_search);
        btnFav = findViewById(R.id.btn_nav_favourites);
        btnOrders = findViewById(R.id.btn_nav_orders);
        btnCart = findViewById(R.id.btn_nav_cart);
        btnProfile = findViewById(R.id.btn_nav_profile);

        ivHome = findViewById(R.id.iv_nav_home);
        ivSearch = findViewById(R.id.iv_nav_search);
        ivFav = findViewById(R.id.iv_nav_favourites);
        ivOrders = findViewById(R.id.iv_nav_orders);
        ivCart = findViewById(R.id.iv_nav_cart);
        ivProfile = findViewById(R.id.iv_nav_profile);

        tvHome = findViewById(R.id.tv_nav_home);
        tvSearch = findViewById(R.id.tv_nav_search);
        tvFav = findViewById(R.id.tv_nav_favourites);
        tvOrders = findViewById(R.id.tv_nav_orders);
        tvCart = findViewById(R.id.tv_nav_cart);
        tvProfile = findViewById(R.id.tv_nav_profile);

        setupCustomNav();
        setupThemeSwitching();

        // Seller Drawer Nav
        navViewSeller.setNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_seller_home) {
                selectedFragment = new SellerHomeFragment();
            } else if (itemId == R.id.nav_seller_history) {
                selectedFragment = new OrderHistoryFragment();
            } else if (itemId == R.id.nav_seller_account) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void setupCustomNav() {
        btnHome.setOnClickListener(v -> selectNav(1));
        btnSearch.setOnClickListener(v -> selectNav(2));
        btnFav.setOnClickListener(v -> selectNav(3));
        btnOrders.setOnClickListener(v -> selectNav(4));
        btnCart.setOnClickListener(v -> selectNav(5));
        btnProfile.setOnClickListener(v -> selectNav(6));
    }

    private void selectNav(int index) {
        // Reset all colors
        int inactiveColor = getResources().getColor(R.color.text_sub);
        int activeColor = getResources().getColor(R.color.accent_color);

        ivHome.setColorFilter(inactiveColor); tvHome.setTextColor(inactiveColor);
        ivSearch.setColorFilter(inactiveColor); tvSearch.setTextColor(inactiveColor);
        ivFav.setColorFilter(inactiveColor); tvFav.setTextColor(inactiveColor);
        ivOrders.setColorFilter(inactiveColor); tvOrders.setTextColor(inactiveColor);
        ivCart.setColorFilter(inactiveColor); tvCart.setTextColor(inactiveColor);
        ivProfile.setColorFilter(inactiveColor); tvProfile.setTextColor(inactiveColor);

        Fragment selectedFragment = null;
        switch (index) {
            case 1:
                ivHome.setColorFilter(activeColor); tvHome.setTextColor(activeColor);
                selectedFragment = new HomeFragment();
                break;
            case 2:
                ivSearch.setColorFilter(activeColor); tvSearch.setTextColor(activeColor);
                selectedFragment = new SearchFragment();
                break;
            case 3:
                ivFav.setColorFilter(activeColor); tvFav.setTextColor(activeColor);
                selectedFragment = new FavouritesFragment();
                break;
            case 4:
                ivOrders.setColorFilter(activeColor); tvOrders.setTextColor(activeColor);
                selectedFragment = new OrderHistoryFragment();
                break;
            case 5:
                ivCart.setColorFilter(activeColor); tvCart.setTextColor(activeColor);
                selectedFragment = new CartFragment();
                break;
            case 6:
                ivProfile.setColorFilter(activeColor); tvProfile.setTextColor(activeColor);
                selectedFragment = new ProfileFragment();
                break;
        }

        if (selectedFragment != null) {
            loadFragment(selectedFragment);
        }
    }

    private void setupThemeSwitching() {
        boolean isDarkMode = sharedPreferences.getBoolean("is_dark_mode", false);
        updateThemeUI(isDarkMode);

        btnThemeLight.setOnClickListener(v -> {
            if (sharedPreferences.getBoolean("is_dark_mode", false)) toggleTheme(false);
        });

        btnThemeDark.setOnClickListener(v -> {
            if (!sharedPreferences.getBoolean("is_dark_mode", false)) toggleTheme(true);
        });
    }

    private void toggleTheme(boolean dark) {
        sharedPreferences.edit().putBoolean("is_dark_mode", dark).apply();
        AppCompatDelegate.setDefaultNightMode(dark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        recreate();
    }

    private void updateThemeUI(boolean dark) {
        if (dark) {
            btnThemeDark.setBackgroundResource(R.drawable.tab_selected_bg);
            btnThemeDark.setTextColor(getResources().getColor(R.color.black));
            btnThemeLight.setBackground(null);
            btnThemeLight.setTextColor(getResources().getColor(R.color.text_sub));
        } else {
            btnThemeLight.setBackgroundResource(R.drawable.tab_selected_bg);
            btnThemeLight.setTextColor(getResources().getColor(R.color.black));
            btnThemeDark.setBackground(null);
            btnThemeDark.setTextColor(getResources().getColor(R.color.text_sub));
        }
    }

    private void checkAccountTypeAndLoadUI() {
        mDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String accountType = snapshot.child("accountType").getValue(String.class);
                    if ("buyer".equalsIgnoreCase(accountType)) {
                        showBuyerView();
                    } else {
                        showSellerView(snapshot.child("name").getValue(String.class), snapshot.child("email").getValue(String.class));
                    }
                } else {
                    showBuyerView();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showBuyerView() {
        customBottomNav.setVisibility(View.VISIBLE);
        navViewSeller.setVisibility(View.GONE);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        selectNav(1);
    }

    private void showSellerView(String name, String email) {
        customBottomNav.setVisibility(View.GONE);
        navViewSeller.setVisibility(View.VISIBLE);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        View headerView = navViewSeller.getHeaderView(0);
        ((TextView)headerView.findViewById(R.id.tv_drawer_name)).setText(name);
        ((TextView)headerView.findViewById(R.id.tv_drawer_email)).setText(email);
        loadFragment(new SellerHomeFragment());
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}