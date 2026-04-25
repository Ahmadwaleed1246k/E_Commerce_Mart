package com.example.e_commerce_mart_asssign3;

import android.Manifest;
import android.content.SharedPreferences;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CartFragment extends Fragment {

    private RecyclerView rvCart;
    private TextView tvTotalPrice, tvShipping, tvGrandTotal;
    private Button btnCheckout;
    private CartAdapter adapter;
    private List<CartItem> cartItems;
    private SharedPreferences sharedPreferences;
    private List<Product> allProducts;

    private static final String PREF_CART = "user.cart";
    private static final int SMS_PERMISSION_CODE = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("FastMartPrefs", 0);
        allProducts = ProductData.getAllProducts();

        rvCart = view.findViewById(R.id.rv_cart);
        tvTotalPrice = view.findViewById(R.id.tv_total_price);
        tvShipping = view.findViewById(R.id.tv_shipping);
        tvGrandTotal = view.findViewById(R.id.tv_grand_total);
        btnCheckout = view.findViewById(R.id.btn_checkout);

        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));

        loadCartItems();

        adapter = new CartAdapter(cartItems, new CartAdapter.OnCartItemListener() {
            @Override
            public void onQuantityChanged() {
                updateTotals();
                saveCartToPreferences();
            }

            @Override
            public void onRemoveClick(int position) {
                cartItems.remove(position);
                updateTotals();
                saveCartToPreferences();
                adapter.updateList(cartItems);
            }
        });

        rvCart.setAdapter(adapter);

        btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(getContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
            } else {
                checkSmsPermissionAndSend();
            }
        });

        updateTotals();

        return view;
    }

    private void loadCartItems() {
        cartItems = new ArrayList<>();
        Set<String> cartIds = sharedPreferences.getStringSet(PREF_CART, new HashSet<>());

        for (String idStr : cartIds) {
            try {
                int id = Integer.parseInt(idStr);
                for (Product product : allProducts) {
                    if (product.getId() == id) {
                        cartItems.add(new CartItem(product, 1));
                        break;
                    }
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    private void saveCartToPreferences() {
        Set<String> cartIds = new HashSet<>();
        for (CartItem item : cartItems) {
            cartIds.add(String.valueOf(item.getProduct().getId()));
        }
        sharedPreferences.edit().putStringSet(PREF_CART, cartIds).apply();
    }

    private void updateTotals() {
        double subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getTotalPrice();
        }

        double shipping = subtotal > 0 ? 5.00 : 0; // Adjusted shipping to $5
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
            StringBuilder orderDetails = new StringBuilder("Order Details:\n");
            for (CartItem item : cartItems) {
                orderDetails.append(item.getProduct().getName())
                        .append(" x")
                        .append(item.getQuantity())
                        .append(" = $")
                        .append(String.format("%.2f", item.getTotalPrice()))
                        .append("\n");
            }
            orderDetails.append("\nShipping: $").append(tvShipping.getText().toString().replace("$", ""));
            orderDetails.append("\nTotal: $").append(tvGrandTotal.getText().toString().replace("$", ""));

            SmsManager smsManager = SmsManager.getDefault();
            // Using a dummy number for now
            smsManager.sendTextMessage("5551234567", null, orderDetails.toString(), null, null);

            Toast.makeText(getContext(), "Order sent via SMS", Toast.LENGTH_LONG).show();

            cartItems.clear();
            saveCartToPreferences();
            adapter.updateList(cartItems);
            updateTotals();

        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
}
