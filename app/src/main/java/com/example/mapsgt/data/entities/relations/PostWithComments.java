package com.example.mapsgt.data.entities.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.mapsgt.data.entities.Comment;
import com.example.mapsgt.data.entities.Post;

import java.util.List;

public class PostWithComments {
    @Embedded
    public Post post;
    @Relation(
            parentColumn = "post_id",
            entityColumn = "post_id"
    )
    public List<Comment> commentList;
}
