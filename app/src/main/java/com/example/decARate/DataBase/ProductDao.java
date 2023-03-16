package com.example.decARate.DataBase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ProductDao {
    @Query("SELECT * FROM Product WHERE Email LIKE :email ")
    List<Product> getAll(String email);

    @Query("SELECT * FROM Product WHERE Email LIKE :email AND uid LIKE :id LIMIT 1")
    Product findById(int id, String email);

    @Insert
    void insert(Product product);

    @Delete
    void delete(Product product);

    @Query("UPDATE Product SET quantity = :newQuantity WHERE Email = :email AND uid = :id")
    void update(String email, int id, int newQuantity);

    @Query("SELECT quantity FROM Product WHERE Email LIKE :email AND uid LIKE :id LIMIT 1")
    int getQuantity(int id, String email);
}