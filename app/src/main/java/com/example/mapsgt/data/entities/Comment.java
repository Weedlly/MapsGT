package com.example.mapsgt.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "comment",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "user_id"),
                @ForeignKey(entity = Post.class,
                        parentColumns = "post_id",
                        childColumns = "post_id")},
        indices = {@Index(value = {"user_id", "post_id"})}
)
public class Comment {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "comment_id")
    private int id;
    @ColumnInfo(name = "user_id")
    private String userId;
    @ColumnInfo(name = "post_id")
    private int postId;
    @ColumnInfo(name = "created_at")
    private Date createdAt;

    public Comment(String userId, int postId, Date createdAt) {
        this.userId = userId;
        this.postId = postId;
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
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
