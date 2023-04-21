package com.example.mapsgt.data.entities;

import com.example.mapsgt.enumeration.UserStatusEnum;

import java.util.Date;

public class UserStatus {
    private int id;
    private String userId;
    private String statusContent;
    private UserStatusEnum statusType;
    private int pin;
    private Date timeout;
    private Date createdAt;

    public UserStatus(String userId, String statusContent, UserStatusEnum statusType, int pin, Date timeout, Date createdAt) {
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
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
