package com.example.mapsgt.data.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.example.mapsgt.enumeration.FriendshipStatusEnum;

import java.util.Date;

@Entity(tableName = "friendship",
        primaryKeys = {"user_id", "friend_id"},
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "user_id"),
                @ForeignKey(entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "friend_id"),
                @ForeignKey(entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "action_user_id")},
        indices = {@Index(value = {"friend_id", "action_user_id"})}
)
public class Friendship {
    @ColumnInfo(name = "user_id")
    @NonNull
    private String userId;
    @ColumnInfo(name = "friend_id")
    @NonNull
    private String friendId;
    private FriendshipStatusEnum status;
    @ColumnInfo(name = "action_user_id")
    private String actionUserId;
    @ColumnInfo(name = "date_created")
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
