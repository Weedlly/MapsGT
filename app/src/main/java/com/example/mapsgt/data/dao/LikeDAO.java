package com.example.mapsgt.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.mapsgt.data.entities.Like;

@Dao
public interface LikeDAO {
    @Insert
    void insertLike(Like like);
}
