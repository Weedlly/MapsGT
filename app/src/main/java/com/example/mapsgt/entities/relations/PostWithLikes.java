package com.example.mapsgt.entities.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.mapsgt.entities.Like;
import com.example.mapsgt.entities.Post;

import java.util.List;

public class PostWithLikes {
    @Embedded
    public Post post;
    @Relation(
            parentColumn = "post_id",
            entityColumn = "post_id"
    )
    public List<Like> likeList;
}
