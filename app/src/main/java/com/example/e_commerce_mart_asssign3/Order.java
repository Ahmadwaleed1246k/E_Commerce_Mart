package com.example.e_commerce_mart_asssign3;

import java.util.List;

public class Order {
    private String orderId;
    private String userId;
    private String date;
    private List<CartItem> items;
    private double totalAmount;
    private String status;

    public Order() {
        // Required for Firebase
    }

    public Order(String orderId, String userId, String date, List<CartItem> items, double totalAmount, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.date = date;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
