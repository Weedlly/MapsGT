package com.example.mapsgt.data.entities;

import com.example.mapsgt.enumeration.UserEventStatusEnum;

import java.util.Date;

public class UserEvent {
    public String userId;
    public int eventId;
    public UserEventStatusEnum status;
    public Date dateCreated;

    public UserEvent(String userId, int eventId, UserEventStatusEnum status, Date dateCreated) {
        this.userId = userId;
        this.eventId = eventId;
        this.status = status;
        this.dateCreated = dateCreated;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public UserEventStatusEnum getStatus() {
        return status;
    }

    public void setStatus(UserEventStatusEnum status) {
        this.status = status;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
