package com.example.mapsgt.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.mapsgt.data.dao.UserDAO;
import com.example.mapsgt.data.entities.Location;
import com.example.mapsgt.data.entities.LocationPrivacy;
import com.example.mapsgt.data.entities.Message;
import com.example.mapsgt.data.entities.UserEvent;
import com.example.mapsgt.data.entities.UserStatus;
import com.example.mapsgt.database.converters.FriendshipStatusConverter;
import com.example.mapsgt.database.converters.PrivacyLevelStatusConverter;
import com.example.mapsgt.database.converters.TimestampConverter;
import com.example.mapsgt.data.entities.Comment;
import com.example.mapsgt.data.entities.Event;
import com.example.mapsgt.data.entities.FavoritePlace;
import com.example.mapsgt.data.entities.Friendship;
import com.example.mapsgt.data.entities.Like;
import com.example.mapsgt.data.entities.Post;
import com.example.mapsgt.data.entities.User;
import com.example.mapsgt.database.converters.UserGenderConverter;
import com.example.mapsgt.database.converters.UserStatusConverter;

@Database(entities = {User.class, Friendship.class, Post.class, Comment.class, Like.class, Event.class, FavoritePlace.class, Location.class, LocationPrivacy.class, Message.class, UserEvent.class, UserStatus.class}, version = 1)
@TypeConverters({TimestampConverter.class, FriendshipStatusConverter.class, PrivacyLevelStatusConverter.class, UserGenderConverter.class, UserStatusConverter.class})
public abstract class MyDatabase extends RoomDatabase {
    private static String DATABASE_NAME = "map";
    private static MyDatabase instance;

    public abstract UserDAO userDAO();

    public static synchronized MyDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), MyDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}

