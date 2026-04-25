package com.example.e_commerce_mart_asssign3;

public class Product {
    private int id;
    private String name;
    private String price;
    private String originalPrice;
    private String description;
    private String model;
    private String type;
    private int imageResId;
    private boolean isFavorite;
    private String sellerId;

    public Product() {
        // Required for Firebase
    }

    public Product(int id, String name, String price, String originalPrice,
                   String description, String model, int imageResId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.originalPrice = originalPrice;
        this.description = description;
        this.model = model;
        this.imageResId = imageResId;
        this.isFavorite = false;
    }

    // Extended constructor for Seller added products
    public Product(int id, String name, String price, String description, String type, String sellerId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.type = type;
        this.sellerId = sellerId;
        this.imageResId = R.drawable.camera; // Default image for added products
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    
    public String getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(String originalPrice) { this.originalPrice = originalPrice; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    
    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }
}
