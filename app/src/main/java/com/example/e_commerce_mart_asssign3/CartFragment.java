package com.example.e_commerce_mart_asssign3;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView rvCart;
    private TextView tvTotalPrice, tvShipping, tvGrandTotal;
    private Button btnCheckout;
    private CartAdapter adapter;
    private List<CartItem> cartItems;
    private DatabaseHelper dbHelper;

    private static final int SMS_PERMISSION_CODE = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        dbHelper = new DatabaseHelper(getContext());

        rvCart = view.findViewById(R.id.rv_cart);
        tvTotalPrice = view.findViewById(R.id.tv_total_price);
        tvShipping = view.findViewById(R.id.tv_shipping);
        tvGrandTotal = view.findViewById(R.id.tv_grand_total);
        btnCheckout = view.findViewById(R.id.btn_checkout);

        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));

        loadCartItems();

        btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(getContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
            } else {
                checkSmsPermissionAndSend();
            }
        });

        return view;
    }

    private void loadCartItems() {
        cartItems = dbHelper.getCartItems();
        adapter = new CartAdapter(getContext(), cartItems, this::updateTotals);
        rvCart.setAdapter(adapter);
        updateTotals();
    }

    private void updateTotals() {
        double subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getTotalPrice();
        }

        double shipping = subtotal > 0 ? 5.00 : 0;
        double total = subtotal + shipping;

        tvTotalPrice.setText(String.format("$%.2f", subtotal));
        tvShipping.setText(String.format("$%.2f", shipping));
        tvGrandTotal.setText(String.format("$%.2f", total));
    }

    private void checkSmsPermissionAndSend() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        } else {
            sendSms();
        }
    }

    private void sendSms() {
        try {
            String uid = FirebaseAuth.getInstance().getUid();
            if (uid == null) return;

            // Prepare Order Summary
            StringBuilder summary = new StringBuilder("FastMart Order Summary:\n");
            for (CartItem item : cartItems) {
                summary.append(item.getProduct().getName())
                        .append(" x")
                        .append(item.getQuantity())
                        .append(" ($")
                        .append(String.format("%.2f", item.getTotalPrice()))
                        .append(")\n");
            }
            summary.append("\nSubtotal: ").append(tvTotalPrice.getText().toString());
            summary.append("\nShipping: ").append(tvShipping.getText().toString());
            summary.append("\nTotal: ").append(tvGrandTotal.getText().toString());

            // Save to Firebase Order History
            String orderId = "ORD-" + System.currentTimeMillis();
            String date = DateFormat.getDateTimeInstance().format(new Date());
            double totalVal = Double.parseDouble(tvGrandTotal.getText().toString().replace("$", ""));
            
            Order order = new Order(orderId, uid, date, new ArrayList<>(cartItems), totalVal, "Processing");
            
            FirebaseDatabase.getInstance().getReference("orders")
                    .child(uid)
                    .child(orderId)
                    .setValue(order)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Only send SMS if Firebase save succeeds (as per requirement 7 logic)
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage("5551234567", null, summary.toString(), null, null);

                            Toast.makeText(getContext(), "Order placed and summary sent!", Toast.LENGTH_LONG).show();

                            // Clear SQLite Cart
                            dbHelper.clearCart();
                            cartItems.clear();
                            adapter.notifyDataSetChanged();
                            updateTotals();
                        }
                    });

        } catch (Exception e) {
            Toast.makeText(getContext(), "Checkout failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSms();
            } else {
                Toast.makeText(getContext(), "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        loadCartItems();
    }
}
