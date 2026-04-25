package com.example.e_commerce_mart_asssign3;

public class CartItem {
    private Product product;
    private int quantity;

    public CartItem() {
        // Required for Firebase
    }

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPriceValue() {
        if (product == null || product.getPrice() == null) return 0.0;
        String priceStr = product.getPrice().replace("$", "");
        try {
            return Double.parseDouble(priceStr);
        } catch (Exception e) {
            return 0.0;
        }
    }

    public double getTotalPrice() {
        return getPriceValue() * quantity;
    }
}
