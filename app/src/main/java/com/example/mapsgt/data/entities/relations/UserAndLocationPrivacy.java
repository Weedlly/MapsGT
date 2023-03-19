package com.example.mapsgt.data.entities.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.mapsgt.data.entities.LocationPrivacy;
import com.example.mapsgt.data.entities.User;

public class UserAndLocationPrivacy {
    @Embedded
    public User user;
    @Relation(
            parentColumn = "user_id",
            entityColumn = "user_id"
    )
    public LocationPrivacy locationPrivacy;
}
