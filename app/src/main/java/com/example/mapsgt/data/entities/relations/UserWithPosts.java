package com.example.mapsgt.data.entities.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.mapsgt.data.entities.Post;
import com.example.mapsgt.data.entities.User;

import java.util.List;

public class UserWithPosts {
    @Embedded
    public User user;
    @Relation(
            parentColumn = "user_id",
            entityColumn = "user_id"
    )
    public List<Post> postList;
}
