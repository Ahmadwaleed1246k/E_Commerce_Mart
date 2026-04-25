package com.example.e_commerce_mart_asssign3;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddProductActivity extends AppCompatActivity {

    private EditText etName, etType, etPrice, etDescription;
    private MaterialButton btnAddProduct;
    private ProgressBar progressBar;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            currentUserId = user.getUid();
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();

        initViews();
    }

    private void initViews() {
        etName = findViewById(R.id.et_product_name);
        etType = findViewById(R.id.et_product_type);
        etPrice = findViewById(R.id.et_product_price);
        etDescription = findViewById(R.id.et_product_description);
        btnAddProduct = findViewById(R.id.btn_add_product);
        progressBar = findViewById(R.id.add_product_progress);

        btnAddProduct.setOnClickListener(v -> addProductToFirebase());
    }

    private void addProductToFirebase() {
        String name = etName.getText().toString().trim();
        String type = etType.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(type) || TextUtils.isEmpty(price) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress(true);

        // Generate unique ID
        String productIdStr = mDatabase.child("products").push().getKey();
        int productId = productIdStr != null ? productIdStr.hashCode() : (int) System.currentTimeMillis();

        Product newProduct = new Product(productId, name, price, description, type, currentUserId);

        // Save to products and seller_products
        mDatabase.child("products").child(String.valueOf(productId)).setValue(newProduct)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mDatabase.child("seller_products").child(currentUserId).child(String.valueOf(productId)).setValue(newProduct)
                                .addOnCompleteListener(innerTask -> {
                                    showProgress(false);
                                    if (innerTask.isSuccessful()) {
                                        Toast.makeText(AddProductActivity.this, "Product added successfully!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(AddProductActivity.this, "Failed to link product to profile", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        showProgress(false);
                        Toast.makeText(AddProductActivity.this, "Failed to add product: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnAddProduct.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
