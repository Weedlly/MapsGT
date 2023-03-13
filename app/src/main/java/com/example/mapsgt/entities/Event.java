package com.example.mapsgt.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "event")
public class Event {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "event_id")
    private int eventId;
    @ColumnInfo(name = "user_creator_id")
    private int userCreatorId;
    @ColumnInfo(name = "event_name")
    private String eventName;
    @ColumnInfo(name = "event_description")
    private String eventDescription;
    @ColumnInfo(name = "event_date")
    private Date eventDate;
    @ColumnInfo(name = "event_location_id")
    private int eventLocationId;

    public Event(int eventId, int userCreatorId, String eventName, String eventDescription, Date eventDate, int eventLocationId) {
        this.eventId = eventId;
        this.userCreatorId = userCreatorId;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.eventLocationId = eventLocationId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getUserCreatorId() {
        return userCreatorId;
    }

    public void setUserCreatorId(int userCreatorId) {
        this.userCreatorId = userCreatorId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public int getEventLocationId() {
        return eventLocationId;
    }

    public void setEventLocationId(int eventLocationId) {
        this.eventLocationId = eventLocationId;
    }
}