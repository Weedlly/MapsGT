package com.example.mapsgt.entities.relations;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.example.mapsgt.enumeration.FriendshipStatus;

import java.util.Date;

@Entity(tableName = "friendship", primaryKeys = {"user_id", "friend_id"})
public class Friendship {
    @ColumnInfo(name = "user_id")
    private int userId1;
    @ColumnInfo(name = "friend_id")
    private int friendId;
    private FriendshipStatus status;
    @ColumnInfo(name = "action_user_id")
    private int actionUserId;
    @ColumnInfo(name = "date_created")
    private Date dateCreated;
}
