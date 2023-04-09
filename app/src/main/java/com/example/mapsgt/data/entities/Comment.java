package com.example.mapsgt.data.entities;

import java.util.Date;

public class Comment {
    private int id;
    private String userId;
    private int postId;
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
