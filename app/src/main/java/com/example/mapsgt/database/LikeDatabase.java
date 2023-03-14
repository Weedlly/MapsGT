package com.example.mapsgt.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mapsgt.dao.LikeDAO;
import com.example.mapsgt.entities.Like;

@Database(entities = {Like.class}, version = 1)
public abstract class LikeDatabase extends RoomDatabase {
    private static String DATABASE_NAME = "like";
    private static LikeDatabase instance;

    public static synchronized LikeDatabase getInstance(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), LikeDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract LikeDAO likeDAO();
}
