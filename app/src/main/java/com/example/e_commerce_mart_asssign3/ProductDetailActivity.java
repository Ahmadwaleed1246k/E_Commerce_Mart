package com.example.e_commerce_mart_asssign3;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView ivProductImage, ivBack;
    private TextView tvProductName, tvProductPrice, tvProductModel, tvProductDescription;
    private Button btnBuyNow;
    private int productId;
    private String productKey;
    private Product product;
    private List<Product> allProducts;
    private DatabaseReference mDatabase;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        dbHelper = new DatabaseHelper(this);
        mDatabase = FirebaseDatabase.getInstance().getReference("products");
        allProducts = ProductData.getAllProducts();

        productId = getIntent().getIntExtra("product_id", -1);
        productKey = getIntent().getStringExtra("product_key");

        initViews();
        checkAccountType();
        loadProductData();
    }

    private void checkAccountType() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            FirebaseDatabase.getInstance().getReference("users").child(uid).child("accountType")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String type = snapshot.getValue(String.class);
                                if ("seller".equalsIgnoreCase(type)) {
                                    btnBuyNow.setVisibility(View.GONE);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
        }
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
        if (productKey != null && !productKey.isEmpty()) {
            fetchFromFirebase(productKey);
            return;
        }

        for (Product p : allProducts) {
            if (p.getId() == productId) {
                product = p;
                displayProduct();
                return;
            }
        }

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    try {
                        Product p = data.getValue(Product.class);
                        if (p != null && p.getId() == productId) {
                            product = p;
                            product.setProductKey(data.getKey());
                            displayProduct();
                            return;
                        }
                    } catch (Exception e) {}
                }
                Toast.makeText(ProductDetailActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductDetailActivity.this, "Error loading product", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void fetchFromFirebase(String key) {
        mDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        product = snapshot.getValue(Product.class);
                        if (product != null) {
                            product.setProductKey(key);
                            displayProduct();
                            return;
                        }
                    } catch (Exception e) {}
                }
                Toast.makeText(ProductDetailActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
                finish();
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

        if (product.hasImageUrl()) {
            Glide.with(this).load(product.getImageUrl()).placeholder(R.drawable.camera).centerCrop().into(ivProductImage);
        } else if (product.getImageResId() != 0) {
            ivProductImage.setImageResource(product.getImageResId());
        } else {
            ivProductImage.setImageResource(R.drawable.camera);
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
        if (product != null) {
            dbHelper.addToCart(product, 1);
            Toast.makeText(this, product.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
