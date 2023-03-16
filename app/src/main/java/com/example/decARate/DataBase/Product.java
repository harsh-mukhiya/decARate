package com.example.decARate.DataBase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "Product", primaryKeys = {"uid", "Email"})
public class Product {
    @NonNull
    @ColumnInfo(name = "uid")
    public int uid;

    @NonNull
    @ColumnInfo(name = "Email", defaultValue = "cart24@gmail.com")
    public String email;

    @ColumnInfo(name = "Product_name")
    public String product_name;

    @ColumnInfo(name = "price")
    public String price;

    @ColumnInfo(name = "image")
    public String imageUrl;

    @ColumnInfo(name = "quantity",defaultValue = "1")
    public int quantity;

    @ColumnInfo(name = "maxQuantity",defaultValue = "1")
    public int maxQuantity;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getmaxQuantity() {
        return maxQuantity;
    }

    public void setmaxQuantity(int maxquantity) {
        this.maxQuantity = maxquantity;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }
}