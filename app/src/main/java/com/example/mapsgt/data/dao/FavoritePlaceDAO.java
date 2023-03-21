package com.example.mapsgt.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.mapsgt.data.entities.FavoritePlace;

@Dao
public interface FavoritePlaceDAO {
    @Insert
    void insertFavoritePlace(FavoritePlace favoritePlace);
}
