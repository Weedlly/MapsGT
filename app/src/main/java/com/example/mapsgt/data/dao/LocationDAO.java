package com.example.mapsgt.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.mapsgt.data.entities.Location;

import java.util.List;

@Dao
public interface LocationDAO {
    @Insert
    void insertLocation(Location location);

    @Query("SELECT * FROM location WHERE location_name LIKE '%' || :name || '%'")
    List<Location> searchLocationByName(String name);
}
