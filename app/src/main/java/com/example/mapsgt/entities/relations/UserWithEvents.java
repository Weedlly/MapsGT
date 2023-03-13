package com.example.mapsgt.entities.relations;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.mapsgt.entities.Event;
import com.example.mapsgt.entities.User;

import java.util.List;

public class UserWithEvents {
    @Embedded
    public User user;
    @Relation(
            parentColumn = "user_id",
            entityColumn = "event_id",
            associateBy = @Junction(UserEvent.class)
    )
    public List<Event> eventList;
}
