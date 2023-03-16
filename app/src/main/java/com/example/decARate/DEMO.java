package com.example.decARate;

public class DEMO {

    private final String quantity;

    private final String price;
    private final int productID;

    private final String pName;
    private final String imgURL;
    private final String email;


    public DEMO(int productID, String pName, String price, String quantity, String imgURL, String email) {
        this.email = email;
        this.productID = productID;
        this.price = price;
        this.pName = pName;
        this.quantity = quantity;
        this.imgURL = imgURL;
    }

    public String getpName() {
        return pName;
    }

    public String getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getImgURL() {
        return imgURL;
    }

    public int getProductID() {
        return productID;
    }

    public String getEmail() {
        return email;
    }


}
