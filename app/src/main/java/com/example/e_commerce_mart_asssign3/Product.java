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
    private String imageUrl;
    private String productKey; // Firebase key like "prod_001"
    private String desc; // Alternative field name used in some Firebase entries

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
    public void setId(Object id) {
        if (id instanceof Number) {
            this.id = ((Number) id).intValue();
        } else if (id instanceof String) {
            try { this.id = Integer.parseInt((String) id); } catch (Exception e) { this.id = 0; }
        }
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPrice() { return price; }
    public void setPrice(Object price) {
        if (price instanceof String) {
            this.price = (String) price;
        } else if (price != null) {
            this.price = "$" + String.valueOf(price);
        }
    }
    
    public String getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(Object originalPrice) {
        if (originalPrice instanceof String) {
            this.originalPrice = (String) originalPrice;
        } else if (originalPrice != null) {
            this.originalPrice = "$" + String.valueOf(originalPrice);
        }
    }
    
    public String getDescription() {
        // Return desc if description is null (Firebase uses "desc" field)
        if (description != null) return description;
        return desc;
    }
    public void setDescription(String description) { this.description = description; }
    
    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    // Support "category" field from Firebase
    public void setCategory(String category) { this.type = category; }
    
    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    
    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) {
        if (imageUrl != null && imageUrl.contains("drive.google.com") && imageUrl.contains("/view")) {
            // Automatically convert Google Drive view links to direct image links
            try {
                String fileId = "";
                if (imageUrl.contains("/d/")) {
                    int start = imageUrl.indexOf("/d/") + 3;
                    int end = imageUrl.indexOf("/", start);
                    if (end == -1) end = imageUrl.indexOf("?", start);
                    fileId = (end == -1) ? imageUrl.substring(start) : imageUrl.substring(start, end);
                }
                if (!fileId.isEmpty()) {
                    this.imageUrl = "https://drive.google.com/uc?id=" + fileId + "&export=download";
                    return;
                }
            } catch (Exception e) {
                // Fallback to original
            }
        }
        this.imageUrl = imageUrl;
    }
    
    public String getProductKey() { return productKey; }
    public void setProductKey(String productKey) { this.productKey = productKey; }

    // Helper: does this product have a URL-based image?
    public boolean hasImageUrl() {
        return imageUrl != null && !imageUrl.isEmpty();
    }
}
