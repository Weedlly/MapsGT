package com.example.mapsgt.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.mapsgt.entities.Location;

@Dao
public interface LocationDAO {
    @Insert
    void insertLocation(Location location);
}
