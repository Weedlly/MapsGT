package com.example.mapsgt.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "comment")
public class Comment {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "comment_id")
    private int id;
    @ColumnInfo(name = "user_id")
    private int userId;
    @ColumnInfo(name = "post_id")
    private int postId;
    @ColumnInfo(name = "created_at")
    private Date createdAt;

    public Comment(int id, int userId, int postId, Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.createdAt = createdAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
