package com.example.mapsgt.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.mapsgt.entities.Like;

@Dao
public interface LikeDAO {
    @Insert
    void insertLike(Like like);
}
