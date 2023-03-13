package com.example.mapsgt.entities.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.mapsgt.entities.FavoritePlace;
import com.example.mapsgt.entities.User;

import java.util.List;

public class UserWithFavoritePlaces {
    @Embedded
    public User user;
    @Relation(
            parentColumn = "user_id",
            entityColumn = "user_id"
    )
    public List<FavoritePlace> favoritePlaceList;
}
