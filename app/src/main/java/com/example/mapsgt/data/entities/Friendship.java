package com.example.mapsgt.data.entities;

import com.example.mapsgt.enumeration.FriendshipStatusEnum;

import java.util.Date;

public class Friendship {
    private String userId;
    private String friendId;
    private FriendshipStatusEnum status;
    private String actionUserId;
    private Date createdAt;

    public Friendship(String userId, String friendId, FriendshipStatusEnum status, String actionUserId, Date createdAt) {
        this.userId = userId;
        this.friendId = friendId;
        this.status = status;
        this.actionUserId = actionUserId;
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public FriendshipStatusEnum getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatusEnum status) {
        this.status = status;
    }

    public String getActionUserId() {
        return actionUserId;
    }

    public void setActionUserId(String actionUserId) {
        this.actionUserId = actionUserId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "userId=" + userId +
                ", friendId=" + friendId +
                ", status=" + status +
                ", actionUserId=" + actionUserId +
                ", createdAt=" + createdAt +
                '}';
    }
}
