package com.example.mapsgt.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mapsgt.dao.MessageDAO;
import com.example.mapsgt.entities.Message;

@Database(entities = {Message.class}, version = 1)
public abstract class MessageDatabase extends RoomDatabase {
    private static String DATABASE_NAME = "message";
    private static MessageDatabase instance;

    public static synchronized MessageDatabase getInstance(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), MessageDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract MessageDAO messageDAO();
}
