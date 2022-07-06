package com.example.Cart_24.DataBase;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
        version = 4,
        entities = {Product.class},
        autoMigrations = {
                @AutoMigration(
                        from = 3,
                        to = 4
                )
        }
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProductDao productDao();
}
