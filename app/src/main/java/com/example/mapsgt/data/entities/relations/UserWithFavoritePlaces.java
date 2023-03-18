package com.example.mapsgt.data.entities.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.mapsgt.data.entities.FavoritePlace;
import com.example.mapsgt.data.entities.User;

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
