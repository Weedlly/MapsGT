package com.example.mapsgt.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "event",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "user_creator_id")},
        indices = {@Index(value = "user_creator_id")}
)
public class Event {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "event_id")
    private int id;
    @ColumnInfo(name = "user_creator_id")
    private String userCreatorId;
    @ColumnInfo(name = "event_name")
    private String eventName;
    @ColumnInfo(name = "event_description")
    private String eventDescription;
    @ColumnInfo(name = "event_date")
    private Date eventDate;
    @ColumnInfo(name = "event_location_id")
    private int eventLocationId;

    public Event(String userCreatorId, String eventName, String eventDescription, Date eventDate, int eventLocationId) {
        this.userCreatorId = userCreatorId;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.eventLocationId = eventLocationId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserCreatorId() {
        return userCreatorId;
    }

    public void setUserCreatorId(String userCreatorId) {
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
