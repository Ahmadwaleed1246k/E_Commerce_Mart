# E-Mart: Premium E-Commerce & Marketplace Application

E-Mart (also referred to as **FastMart** in internal configurations) is a feature-rich, high-performance Android application designed to bridge the gap between buyers and sellers. Built using native Java (Android SDK) and powered by Firebase, the application offers real-time synchronization, offline caching, and a modern user interface supporting dynamic theme-switching.

---

## 🚀 Key Features

### 🛒 For Buyers
*   **Dynamic Home Page:** Real-time fetching of "Deals of the Day" (featuring 5 curated top deals) and "Recommended Products" directly from Firebase.
*   **Personalized Welcome:** Smart welcome banner that retrieves the user's name dynamically from Firebase Realtime Database on login and caches it locally via `SharedPreferences` for fast loading.
*   **Search Engine:** A robust, real-time product search feature complete with persistent search history caching.
*   **Favorites & Cart:** Local SQLite persistence for managing shopping carts (with quantity control) and user wishlists.
*   **Product Detail View:** Beautiful product details page with image zooming, complete specs, and a checkout/purchase simulation dialog.
*   **Real-time Chat:** An integrated buyer-seller chat system with automatic scroll-to-newest and message persistence.

### 🏢 For Sellers
*   **Product Inventory Management:** An interface to add new products, specify prices, categorization, description, and image assets.
*   **Smart Google Drive Image Link Converter:** Allows sellers to paste a standard Google Drive sharing link, which the app automatically converts to a direct image download URL for immediate rendering.
*   **Automated Currency Formatting:** Dynamic price sanitization and automated prepending of currency symbols (`$`).
*   **Seller Dashboard:** Dedicated navigation drawer and interface designed specifically for sellers to track their inventory.

### 🌓 Theme & Personalization
*   **System-Wide Light/Dark Themes:** Built-in support for theme toggle buttons that update application styling instantly and persist user preferences across restarts.

---

## 🛠 Tech Stack

*   **Development Platform:** Android Studio (Gradle-based build system)
*   **Programming Language:** Java (Android SDK API 24+)
*   **Database (Cloud):** Firebase Realtime Database (Real-time product catalog & chat)
*   **Authentication:** Firebase Auth (Email/Password log in & sign up flows)
*   **Local Caching & Persistence:** SQLite (SQLiteOpenHelper for cart and wishlist items) & SharedPreferences (theme configuration & user credentials caching)
*   **Image Loading & Caching:** Glide library (with customized URL sanitization helpers)

---

## 📁 Codebase Architecture & Key Files

### 🧩 Activities & Fragments
*   [SplashActivity.java](file:///c:/Users/dell/Desktop/E_Commerce_Mart_Asssign3/app/src/main/java/com/example/e_commerce_mart_asssign3/SplashActivity.java): Handles animated entry and checks user status (Firebase User Session) to redirect to the home screen or login page.
*   [LoginActivity.java](file:///c:/Users/dell/Desktop/E_Commerce_Mart_Asssign3/app/src/main/java/com/example/e_commerce_mart_asssign3/LoginActivity.java): Manages tabbed registration and login screens, handling validation and progress feedback.
*   [ProfileActivity.java](file:///c:/Users/dell/Desktop/E_Commerce_Mart_Asssign3/app/src/main/java/com/example/e_commerce_mart_asssign3/ProfileActivity.java): Guided onboarding for new users to fill out details, select gender, date of birth, and specify whether they are a **Buyer** or a **Seller**.
*   [MainActivity.java](file:///c:/Users/dell/Desktop/E_Commerce_Mart_Asssign3/app/src/main/java/com/example/e_commerce_mart_asssign3/MainActivity.java): Main entry point containing bottom navigation for buyers, drawer layout navigation for sellers, and theme controllers.
*   [HomeFragment.java](file:///c:/Users/dell/Desktop/E_Commerce_Mart_Asssign3/app/src/main/java/com/example/e_commerce_mart_asssign3/HomeFragment.java): The central feed for buyers, displaying personalized greetings, Deals of the Day (limited to 5 items), and recommended products.
*   [ProductDetailActivity.java](file:///c:/Users/dell/Desktop/E_Commerce_Mart_Asssign3/app/src/main/java/com/example/e_commerce_mart_asssign3/ProductDetailActivity.java): Renders detailed product information and holds "Buy Now" workflows.
*   [ChatActivity.java](file:///c:/Users/dell/Desktop/E_Commerce_Mart_Asssign3/app/src/main/java/com/example/e_commerce_mart_asssign3/ChatActivity.java): Interactive chat screen facilitating messaging between buyers and the seller admin using dynamic Firebase rooms.
*   [SearchFragment.java](file:///c:/Users/dell/Desktop/E_Commerce_Mart_Asssign3/app/src/main/java/com/example/e_commerce_mart_asssign3/SearchFragment.java): Search interface with search filtering and history list.
*   [SellerHomeFragment.java](file:///c:/Users/dell/Desktop/E_Commerce_Mart_Asssign3/app/src/main/java/com/example/e_commerce_mart_asssign3/SellerHomeFragment.java): Dedicated seller home page displaying the items added by that specific seller.
*   [AddProductActivity.java](file:///c:/Users/dell/Desktop/E_Commerce_Mart_Asssign3/app/src/main/java/com/example/e_commerce_mart_asssign3/AddProductActivity.java): Seller interface to add a new product.

### 💾 Local Databases & Models
*   [DatabaseHelper.java](file:///c:/Users/dell/Desktop/E_Commerce_Mart_Asssign3/app/src/main/java/com/example/e_commerce_mart_asssign3/DatabaseHelper.java): Manages local SQLite tables for Favorites and Cart with complete CRUD operations.
*   [Product.java](file:///c:/Users/dell/Desktop/E_Commerce_Mart_Asssign3/app/src/main/java/com/example/e_commerce_mart_asssign3/Product.java): Product model containing logic for sanitizing image urls (converting Google Drive links) and type checks.
*   [Message.java](file:///c:/Users/dell/Desktop/E_Commerce_Mart_Asssign3/app/src/main/java/com/example/e_commerce_mart_asssign3/Message.java): Chat message model holding message body, timestamp, sender, and recipient details.
*   [CartItem.java](file:///c:/Users/dell/Desktop/E_Commerce_Mart_Asssign3/app/src/main/java/com/example/e_commerce_mart_asssign3/CartItem.java) & [Order.java](file:///c:/Users/dell/Desktop/E_Commerce_Mart_Asssign3/app/src/main/java/com/example/e_commerce_mart_asssign3/Order.java): Struct models for shopping cart items and historical purchases.

---

## 🗄 Database Schemas

### SQLite Local Storage (`FastMart.db`)

#### 🌟 `favourites` Table
*   `id` (INTEGER, Primary Key) - Product identifier
*   `name` (TEXT) - Name of product
*   `price` (TEXT) - Product price
*   `image_url` (TEXT) - Cloud image URL (if any)
*   `res_id` (INTEGER) - Local fallback drawable resource ID
*   `product_key` (TEXT) - Unique Firebase database reference key

#### 🛒 `cart` Table
*   `id` (INTEGER, Primary Key) - Product identifier
*   `name` (TEXT) - Name of product
*   `price` (TEXT) - Product price
*   `quantity` (INTEGER) - Quantity added to cart
*   `image_url` (TEXT) - Cloud image URL (if any)
*   `res_id` (INTEGER) - Local fallback drawable resource ID
*   `product_key` (TEXT) - Unique Firebase database reference key

---

## 🛠 Setup & Installation

1.  **Clone the Repository:**
    ```bash
    git clone <repository-url>
    ```
2.  **Open in Android Studio:**
    - Choose *File > Open* and select the directory containing this project.
3.  **Configure Firebase:**
    - Create a project on the [Firebase Console](https://console.firebase.google.com/).
    - Enable **Email/Password Authentication** in Firebase Auth.
    - Setup **Firebase Realtime Database** with basic read/write rules.
    - Download the generated `google-services.json` file and place it in the `app/` directory of the project.
4.  **Sync Gradle & Run:**
    - Click **Sync Project with Gradle Files** in Android Studio.
    - Run the application on an Android Virtual Device (AVD) or physical testing device.

---
*Built as a high-performance, real-time marketplace solution.*
