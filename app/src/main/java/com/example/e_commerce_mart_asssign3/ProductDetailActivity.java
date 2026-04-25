package com.example.e_commerce_mart_asssign3;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView ivProductImage, ivBack;
    private TextView tvProductName, tvProductPrice, tvProductModel, tvProductDescription;
    private Button btnBuyNow;
    private int productId;
    private Product product;
    private SharedPreferences sharedPreferences;
    private List<Product> allProducts;
    private DatabaseReference mDatabase;

    private static final String PREF_CART = "user.cart";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        sharedPreferences = getSharedPreferences("FastMartPrefs", MODE_PRIVATE);
        mDatabase = FirebaseDatabase.getInstance().getReference("all_products");
        allProducts = ProductData.getAllProducts();

        productId = getIntent().getIntExtra("product_id", -1);

        initViews();
        loadProductData();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        ivProductImage = findViewById(R.id.iv_product_image);
        tvProductName = findViewById(R.id.tv_product_name);
        tvProductPrice = findViewById(R.id.tv_product_price);
        tvProductModel = findViewById(R.id.tv_product_model);
        tvProductDescription = findViewById(R.id.tv_product_description);
        btnBuyNow = findViewById(R.id.btn_buy_now);

        ivBack.setOnClickListener(v -> finish());
        btnBuyNow.setOnClickListener(v -> showBuyNowDialog());
    }

    private void loadProductData() {
        // First check static data
        for (Product p : allProducts) {
            if (p.getId() == productId) {
                product = p;
                displayProduct();
                return;
            }
        }

        // If not in static data, fetch from Firebase
        mDatabase.child(String.valueOf(productId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    product = snapshot.getValue(Product.class);
                    if (product != null) {
                        displayProduct();
                    }
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductDetailActivity.this, "Error loading product", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayProduct() {
        tvProductName.setText(product.getName());
        tvProductPrice.setText(product.getPrice());
        tvProductModel.setText(product.getModel() != null ? product.getModel() : product.getType());
        tvProductDescription.setText(product.getDescription());
        
        if (product.getImageResId() != 0) {
            ivProductImage.setImageResource(product.getImageResId());
        } else {
            ivProductImage.setImageResource(R.drawable.camera); // Default
        }
    }

    private void showBuyNowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add to Cart");
        builder.setMessage("Do you want to add " + product.getName() + " to your cart?");
        builder.setPositiveButton("Yes", (dialog, which) -> addToCart());
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void addToCart() {
        Set<String> cartItems = new HashSet<>(
                sharedPreferences.getStringSet(PREF_CART, new HashSet<>())
        );

        cartItems.add(String.valueOf(product.getId()));
        sharedPreferences.edit().putStringSet(PREF_CART, cartItems).apply();

        Toast.makeText(this, product.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
