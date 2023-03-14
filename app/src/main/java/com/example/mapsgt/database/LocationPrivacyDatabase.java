package com.example.mapsgt.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mapsgt.dao.LocationPrivacyDAO;
import com.example.mapsgt.entities.LocationPrivacy;

@Database(entities = {LocationPrivacy.class}, version = 1)
public abstract class LocationPrivacyDatabase extends RoomDatabase {
    private static String DATABASE_NAME = "location_privacy";
    private static LocationPrivacyDatabase instance;

    public static synchronized LocationPrivacyDatabase getInstance(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), LocationPrivacyDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract LocationPrivacyDAO locationPrivacyDAO();
}
