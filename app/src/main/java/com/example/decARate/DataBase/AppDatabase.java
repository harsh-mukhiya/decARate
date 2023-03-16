package com.example.decARate.DataBase;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
        version = 1,
        entities = {Product.class},
        autoMigrations = { // not in used as the project is rebuild again
//                @AutoMigration(
//                        from = 3,
//                        to = 4
//                )
        }
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProductDao productDao();
}
