package com.example.mapsgt.data.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.example.mapsgt.enumeration.UserEventStatusEnum;

import java.util.Date;

@Entity(tableName = "user_event",
        primaryKeys = {"user_id", "event_id"},
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "user_id"),
                @ForeignKey(entity = Event.class,
                        parentColumns = "event_id",
                        childColumns = "event_id")},
        indices = {@Index(value = "event_id")}
)
public class UserEvent {
    @ColumnInfo(name = "user_id")
    @NonNull
    public String userId;
    @ColumnInfo(name = "event_id")
    public int eventId;
    public UserEventStatusEnum status;
    @ColumnInfo(name = "date_created")
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
