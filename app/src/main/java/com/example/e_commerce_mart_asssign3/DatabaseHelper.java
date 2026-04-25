package com.example.e_commerce_mart_asssign3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FastMart.db";
    private static final int DATABASE_VERSION = 1;

    // Favourites Table
    private static final String TABLE_FAVOURITES = "favourites";
    private static final String COL_FAV_ID = "id";
    private static final String COL_FAV_NAME = "name";
    private static final String COL_FAV_PRICE = "price";
    private static final String COL_FAV_IMAGE_URL = "image_url";
    private static final String COL_FAV_RES_ID = "res_id";
    private static final String COL_FAV_PRODUCT_KEY = "product_key";

    // Cart Table
    private static final String TABLE_CART = "cart";
    private static final String COL_CART_ID = "id";
    private static final String COL_CART_NAME = "name";
    private static final String COL_CART_PRICE = "price";
    private static final String COL_CART_QUANTITY = "quantity";
    private static final String COL_CART_IMAGE_URL = "image_url";
    private static final String COL_CART_RES_ID = "res_id";
    private static final String COL_CART_PRODUCT_KEY = "product_key";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createFavTable = "CREATE TABLE " + TABLE_FAVOURITES + " (" +
                COL_FAV_ID + " INTEGER PRIMARY KEY, " +
                COL_FAV_NAME + " TEXT, " +
                COL_FAV_PRICE + " TEXT, " +
                COL_FAV_IMAGE_URL + " TEXT, " +
                COL_FAV_RES_ID + " INTEGER, " +
                COL_FAV_PRODUCT_KEY + " TEXT)";

        String createCartTable = "CREATE TABLE " + TABLE_CART + " (" +
                COL_CART_ID + " INTEGER PRIMARY KEY, " +
                COL_CART_NAME + " TEXT, " +
                COL_CART_PRICE + " TEXT, " +
                COL_CART_QUANTITY + " INTEGER, " +
                COL_CART_IMAGE_URL + " TEXT, " +
                COL_CART_RES_ID + " INTEGER, " +
                COL_CART_PRODUCT_KEY + " TEXT)";

        db.execSQL(createFavTable);
        db.execSQL(createCartTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }

    // --- Favourites Methods ---

    public boolean addFavourite(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FAV_ID, product.getId());
        values.put(COL_FAV_NAME, product.getName());
        values.put(COL_FAV_PRICE, product.getPrice());
        values.put(COL_FAV_IMAGE_URL, product.getImageUrl());
        values.put(COL_FAV_RES_ID, product.getImageResId());
        values.put(COL_FAV_PRODUCT_KEY, product.getProductKey());

        long result = db.insertWithOnConflict(TABLE_FAVOURITES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    public void removeFavourite(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVOURITES, COL_FAV_ID + " = ?", new String[]{String.valueOf(productId)});
    }

    public boolean isFavourite(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVOURITES, null, COL_FAV_ID + " = ?",
                new String[]{String.valueOf(productId)}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public List<Product> getAllFavourites() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FAVOURITES, null);

        if (cursor.moveToFirst()) {
            do {
                Product p = new Product();
                p.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_FAV_ID)));
                p.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_FAV_NAME)));
                p.setPrice(cursor.getString(cursor.getColumnIndexOrThrow(COL_FAV_PRICE)));
                p.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COL_FAV_IMAGE_URL)));
                p.setImageResId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_FAV_RES_ID)));
                p.setProductKey(cursor.getString(cursor.getColumnIndexOrThrow(COL_FAV_PRODUCT_KEY)));
                p.setFavorite(true);
                products.add(p);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return products;
    }

    // --- Cart Methods ---

    public void addToCart(Product product, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Check if already in cart
        Cursor cursor = db.query(TABLE_CART, null, COL_CART_ID + " = ?",
                new String[]{String.valueOf(product.getId())}, null, null, null);
        
        if (cursor.getCount() > 0) {
            // Update quantity
            cursor.moveToFirst();
            int currentQty = cursor.getInt(cursor.getColumnIndexOrThrow(COL_CART_QUANTITY));
            updateCartQuantity(product.getId(), currentQty + quantity);
        } else {
            // Insert new
            ContentValues values = new ContentValues();
            values.put(COL_CART_ID, product.getId());
            values.put(COL_CART_NAME, product.getName());
            values.put(COL_CART_PRICE, product.getPrice());
            values.put(COL_CART_QUANTITY, quantity);
            values.put(COL_CART_IMAGE_URL, product.getImageUrl());
            values.put(COL_CART_RES_ID, product.getImageResId());
            values.put(COL_CART_PRODUCT_KEY, product.getProductKey());
            db.insert(TABLE_CART, null, values);
        }
        cursor.close();
    }

    public void updateCartQuantity(int productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (quantity <= 0) {
            removeFromCart(productId);
            return;
        }
        ContentValues values = new ContentValues();
        values.put(COL_CART_QUANTITY, quantity);
        db.update(TABLE_CART, values, COL_CART_ID + " = ?", new String[]{String.valueOf(productId)});
    }

    public void removeFromCart(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, COL_CART_ID + " = ?", new String[]{String.valueOf(productId)});
    }

    public List<CartItem> getCartItems() {
        List<CartItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CART, null);

        if (cursor.moveToFirst()) {
            do {
                Product p = new Product();
                p.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_CART_ID)));
                p.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_CART_NAME)));
                p.setPrice(cursor.getString(cursor.getColumnIndexOrThrow(COL_CART_PRICE)));
                p.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COL_CART_IMAGE_URL)));
                p.setImageResId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_CART_RES_ID)));
                p.setProductKey(cursor.getString(cursor.getColumnIndexOrThrow(COL_CART_PRODUCT_KEY)));
                
                int qty = cursor.getInt(cursor.getColumnIndexOrThrow(COL_CART_QUANTITY));
                items.add(new CartItem(p, qty));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    public void clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CART);
    }
}
