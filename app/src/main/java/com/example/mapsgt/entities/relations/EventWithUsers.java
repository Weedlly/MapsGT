package com.example.mapsgt.entities.relations;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.mapsgt.entities.Event;
import com.example.mapsgt.entities.User;

import java.util.List;

public class EventWithUsers {
    @Embedded
    public Event event;
    @Relation(
            parentColumn = "event_id",
            entityColumn = "user_id",
            associateBy = @Junction(UserEvent.class)
    )
    public List<User> userList;
}
