package com.example.mapsgt.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.mapsgt.entities.Event;

@Dao
public interface CommentDAO {
    @Insert
    void insertEvent(Event event);
}
