package com.example.mapsgt.data.entities.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.mapsgt.data.entities.Event;
import com.example.mapsgt.data.entities.User;

import java.util.List;

public class UserWithEventCreators {
    @Embedded
    public User user;
    @Relation(
            parentColumn = "user_id",
            entityColumn = "user_creator_id"
    )
    public List<Event> eventList;
}
