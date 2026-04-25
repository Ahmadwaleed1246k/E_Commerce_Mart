package com.example.e_commerce_mart_asssign3;

import java.util.ArrayList;
import java.util.List;

public class ProductData {

    // Get all products (for search)
    public static List<Product> getAllProducts() {
        List<Product> all = new ArrayList<>();
        all.addAll(getDealsOfTheDay());
        all.addAll(getRecommendedProducts());
        return all;
    }

    public static List<Product> getDealsOfTheDay() {
        List<Product> deals = new ArrayList<>();
        deals.add(new Product(1, "RODE PodMic", "$108.20", "$199.99",
                "Dynamic microphone, Speaker microphone", "Professional Mic",
                R.drawable.mic));
        deals.add(new Product(2, "GOOGLE Speaker", "$70.99", "$89.99",
                "Google Assistant, IFTTT", "Smart Speaker",
                R.drawable.speakers));
        deals.add(new Product(3, "SONY Camera", "$349.99", "$499.99",
                "3D,LED lens and sensors", "WH-1000XM4",
                R.drawable.camera));
        return deals;
    }

    public static List<Product> getRecommendedProducts() {
        List<Product> products = new ArrayList<>();

        products.add(new Product(4, "SONY Premium Headphones", "$349.99", "$499.99",
                "Wireless Headphones", "WH-1000XM4, Black",R.drawable.headphones));
        products.add(new Product(5, "SONY PowerBank", "$349.99", "$499.99",
                "Wireless Headphones", "WH-1000XM4, Beige", R.drawable.powerbank));
        products.add(new Product(6, "SHURE SM7B Microphone", "$94.90", "$129.99",
                "Studio Microphone", "Professional Microphone", R.drawable.microphone));
        products.add(new Product(7, "XIAOMI Redmi Watch 3", "$94.90", "$119.99",
                "Smart Watch", "42.58 mm, Aluminium", R.drawable.watch));
        products.add(new Product(9, "Apple AirPods Pro", "$249.99", "$299.99",
                "Noise Cancelling", "2nd Generation", R.drawable.airpods));
        products.add(new Product(17, "DJI Mini 3 Pro", "$759.99", "$899.99",
                "Drone", "4K Video", R.drawable.drone));
        products.add(new Product(18, "Apple Laptop", "$139.99", "$169.99",
                "White", "MacBook Pro", R.drawable.laptop));
        products.add(new Product(26, "Jeans", "$79.49", "$99.99",
                "Casual Jeans", "Regular Fit", R.drawable.jeans));
        products.add(new Product(27, "Nike Shoes Black", "$379.49", "$499.99",
                "Running Shoes", "Size 42", R.drawable.shoes));
        products.add(new Product(28, "Hoodie", "$79.49", "$99.99",
                "Winter Hoodie", "Cotton", R.drawable.hoodie));
        products.add(new Product(29, "V-neck Tshirt", "$49.99", "$69.99",
                "Cotton Tshirt", "Black", R.drawable.shirt));
        products.add(new Product(30, "Winter Jacket", "$149.99", "$199.99",
                "Warm Jacket", "Waterproof", R.drawable.jacket));

        return products;
    }
}
