package com.example.mapsgt.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.mapsgt.data.entities.Message;

@Dao
public interface MessageDAO {
    @Insert
    void insertMessage(Message message);
}
