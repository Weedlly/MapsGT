package com.example.mapsgt.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mapsgt.dao.FavoritePlaceDAO;
import com.example.mapsgt.entities.FavoritePlace;

@Database(entities = {FavoritePlace.class}, version = 1)
public abstract class FavoritePlaceDatabase extends RoomDatabase {
    private static String DATABASE_NAME = "favorite_place";
    private static FavoritePlaceDatabase instance;

    public static synchronized FavoritePlaceDatabase getInstance(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), FavoritePlaceDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract FavoritePlaceDAO favoritePlaceDAO();
}
