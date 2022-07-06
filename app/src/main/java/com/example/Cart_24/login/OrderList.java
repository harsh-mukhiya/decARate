package com.example.Cart_24.login;

import java.util.ArrayList;

public class OrderList {
    public String date;
    public String orderId;
    public String total;
    public String productIds;
    public ArrayList<Product_orderlist> products;
    public Boolean isActive = false;

    public OrderList(String date, String orderId, int total, String productIds, ArrayList<Product_orderlist> products) {
        this.date = date;
        this.orderId = orderId;
        this.total = String.valueOf(total);
        this.productIds = productIds;
        this.products = products;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getProductIds() {
        return productIds;
    }

    public void setProductIds(String productIds) {
        this.productIds = productIds;
    }

    public ArrayList<Product_orderlist> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product_orderlist> products) {
        this.products = products;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
