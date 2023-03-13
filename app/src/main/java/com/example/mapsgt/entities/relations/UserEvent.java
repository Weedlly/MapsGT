package com.example.mapsgt.entities.relations;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.example.mapsgt.enumeration.UserEventStatus;

import java.util.Date;

@Entity(tableName = "user_event",primaryKeys = {"user_id", "event_id"})
public class UserEvent {
    @ColumnInfo(name = "user_id")
    public int userId;
    @ColumnInfo(name = "event_id")
    public int eventId;
    public UserEventStatus status;
    @ColumnInfo(name = "date_created")
    public Date dateCreated;
}
