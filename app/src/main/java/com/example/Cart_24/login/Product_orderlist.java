package com.example.Cart_24.login;

public class Product_orderlist {
    public String product_id;
    public String product_name;
    public int price;
    public String image;
    public int quantity;

    public Product_orderlist(int product_id, String product_name, int price, String image, int quantity) {
        this.product_id = String.valueOf(product_id);
        this.product_name = product_name;
        this.price = price;
        this.image = image;
        this.quantity= quantity;

    }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
