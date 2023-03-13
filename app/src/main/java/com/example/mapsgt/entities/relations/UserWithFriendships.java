package com.example.mapsgt.entities.relations;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.mapsgt.entities.User;

import java.util.List;

public class UserWithFriendships {
    @Embedded
    private User user;
    @Relation(
            parentColumn = "user_id",
            entityColumn = "friend_id",
            associateBy = @Junction(Friendship.class)
    )
    private List<User> friendList;

}
