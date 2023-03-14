package com.example.mapsgt.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.mapsgt.entities.Message;

@Dao
public interface MessageDAO {
    @Insert
    void insertMessage(Message message);
}
