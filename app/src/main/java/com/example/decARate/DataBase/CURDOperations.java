package com.example.decARate.DataBase;


import android.content.Context;


import androidx.room.Room;

import java.util.List;

public class CURDOperations {
    public static AppDatabase db;

    public CURDOperations(Context context) {
        db = Room.databaseBuilder(context,
                AppDatabase.class, "myDatabase").build();
    }

    public void addToCart(Product product) {
        db.productDao().insert(product);
    }

    public void update(String email, int id, int newQuantity){
        db.productDao().update(email,id,newQuantity);
    }

    public int getQuantity(int id, String email){
        return  db.productDao().getQuantity(id,email);
    }


    public boolean getProductById(int id, String email) {
        Product product = db.productDao().findById(id, email);
        if (product != null) {
            return true;
        } else {
            return false;
        }
    }



    public List<Product> findAll(String email) {

        return db.productDao().getAll(email);
    }





//    public boolean getProductById(int id){
//        Product product= db.productDao().findById(id);
//        if(product!=null){
//            return true;
//        }
//        else {
//            return false;
//        }
//    }
//
//    public List<Product> findAll(){
//
//        return db.productDao().getAll();
//    }

    public void removeFromCart(Product product) {

        db.productDao().delete(product);
    }

}
