package com.example.mapsgt.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.mapsgt.data.entities.UserEvent;

@Dao
public interface UserEventDAO {
    @Insert
    void insertUserEvent(UserEvent userEvent);
}
