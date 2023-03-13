package com.example.mapsgt.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.util.Date;

@Entity(tableName = "like")
public class Like {
    @ColumnInfo(name = "like_id")
    private int id;
    @ColumnInfo(name = "user_id")
    private int userId;
    @ColumnInfo(name = "post_id")
    private int postId;
    @ColumnInfo(name = "created_at")
    private Date createdAt;

    public Like(int userId, int postId, Date createdAt) {
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
}
