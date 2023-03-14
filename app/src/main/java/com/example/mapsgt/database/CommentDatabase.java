package com.example.mapsgt.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mapsgt.dao.CommentDAO;
import com.example.mapsgt.entities.Comment;

@Database(entities = {Comment.class}, version = 1)
public abstract class CommentDatabase extends RoomDatabase {
    private static String DATABASE_NAME = "comment";
    private static CommentDatabase instance;

    public static synchronized CommentDatabase getInstance(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), CommentDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract CommentDAO commentDAO();
}
