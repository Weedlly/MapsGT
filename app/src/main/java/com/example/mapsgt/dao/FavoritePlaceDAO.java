package com.example.mapsgt.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.mapsgt.entities.FavoritePlace;

@Dao
public interface FavoritePlaceDAO {
    @Insert
    void insertFavoritePlace(FavoritePlace favoritePlace);
}
