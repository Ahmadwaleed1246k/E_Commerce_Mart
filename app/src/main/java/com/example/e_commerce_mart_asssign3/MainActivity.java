package com.example.e_commerce_mart_asssign3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    private BottomNavigationView bottomNavigationView;
    private NavigationView navViewSeller;
    
    // Theme buttons
    private TextView btnThemeLight, btnThemeDark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before super.onCreate
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
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        navViewSeller = findViewById(R.id.nav_view_seller);
        
        btnThemeLight = findViewById(R.id.btn_theme_light);
        btnThemeDark = findViewById(R.id.btn_theme_dark);

        setupThemeSwitching();

        // Buyer Bottom Nav
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_search) {
                selectedFragment = new SearchFragment();
            } else if (itemId == R.id.nav_orders) {
                selectedFragment = new OrderHistoryFragment();
            } else if (itemId == R.id.nav_cart) {
                selectedFragment = new CartFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });

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
            // Add other cases as implemented

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void setupThemeSwitching() {
        boolean isDarkMode = sharedPreferences.getBoolean("is_dark_mode", false);
        updateThemeUI(isDarkMode);

        btnThemeLight.setOnClickListener(v -> {
            if (sharedPreferences.getBoolean("is_dark_mode", false)) {
                toggleTheme(false);
            }
        });

        btnThemeDark.setOnClickListener(v -> {
            if (!sharedPreferences.getBoolean("is_dark_mode", false)) {
                toggleTheme(true);
            }
        });
    }

    private void toggleTheme(boolean dark) {
        sharedPreferences.edit().putBoolean("is_dark_mode", dark).apply();
        if (dark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
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
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);

                    if ("buyer".equalsIgnoreCase(accountType)) {
                        showBuyerView();
                    } else {
                        showSellerView(name, email);
                    }
                } else {
                    // If no profile, we might be in the middle of signup, or it's a legacy user
                    showBuyerView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error checking account type", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showBuyerView() {
        bottomNavigationView.setVisibility(View.VISIBLE);
        navViewSeller.setVisibility(View.GONE);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        loadFragment(new HomeFragment());
    }

    private void showSellerView(String name, String email) {
        bottomNavigationView.setVisibility(View.GONE);
        navViewSeller.setVisibility(View.VISIBLE);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        
        // Update Drawer Header
        View headerView = navViewSeller.getHeaderView(0);
        TextView tvName = headerView.findViewById(R.id.tv_drawer_name);
        TextView tvEmail = headerView.findViewById(R.id.tv_drawer_email);
        tvName.setText(name);
        tvEmail.setText(email);

        loadFragment(new SellerHomeFragment());
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void logout() {
        mAuth.signOut();
        navigateToLogin();
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