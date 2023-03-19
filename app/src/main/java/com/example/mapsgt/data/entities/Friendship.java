package com.example.mapsgt.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.example.mapsgt.enumeration.FriendshipStatusEnum;

import java.util.Date;

@Entity(tableName = "friendship", primaryKeys = {"user_id", "friend_id"}, foreignKeys = {
        @ForeignKey(entity = User.class,
                parentColumns = "user_id",
                childColumns = "user_id"),
        @ForeignKey(entity = User.class,
                parentColumns = "user_id",
                childColumns = "friend_id"),
        @ForeignKey(entity = User.class,
                parentColumns = "user_id",
                childColumns = "action_user_id"),
})
public class Friendship {
    @ColumnInfo(name = "user_id")
    private int userId;
    @ColumnInfo(name = "friend_id")
    private int friendId;
    private FriendshipStatusEnum status;
    @ColumnInfo(name = "action_user_id")
    private int actionUserId;
    @ColumnInfo(name = "date_created")
    private Date createdAt;

    public Friendship(int userId, int friendId, FriendshipStatusEnum status, int actionUserId, Date createdAt) {
        this.userId = userId;
        this.friendId = friendId;
        this.status = status;
        this.actionUserId = actionUserId;
        this.createdAt = createdAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public FriendshipStatusEnum getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatusEnum status) {
        this.status = status;
    }

    public int getActionUserId() {
        return actionUserId;
    }

    public void setActionUserId(int actionUserId) {
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
