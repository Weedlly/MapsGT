package com.example.mapsgt.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.mapsgt.enumeration.UserStatusEnum;

import java.util.Date;

@Entity(tableName = "user_status", foreignKeys = {
        @ForeignKey(entity = User.class,
                parentColumns = "user_id",
                childColumns = "user_id")
})
public class UserStatus {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_status_id")
    private int id;
    @ColumnInfo(name = "user_id")
    private int userId;
    @ColumnInfo(name = "status_content")
    private String statusContent;
    @ColumnInfo(name = "status_type")
    private UserStatusEnum statusType;
    private int pin;
    private Date timeout;
    @ColumnInfo(name = "created_at")
    private Date createdAt;

    public UserStatus(int userId, String statusContent, UserStatusEnum statusType, int pin, Date timeout, Date createdAt) {
        this.userId = userId;
        this.statusContent = statusContent;
        this.statusType = statusType;
        this.pin = pin;
        this.timeout = timeout;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStatusContent() {
        return statusContent;
    }

    public void setStatusContent(String statusContent) {
        this.statusContent = statusContent;
    }

    public UserStatusEnum getStatusType() {
        return statusType;
    }

    public void setStatusType(UserStatusEnum statusType) {
        this.statusType = statusType;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public Date getTimeout() {
        return timeout;
    }

    public void setTimeout(Date timeout) {
        this.timeout = timeout;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
