package com.example.mapsgt.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.mapsgt.data.entities.UserStatus;

@Dao
public interface UserStatusDAO {
    @Insert
    void insertUserStatus(UserStatus userStatus);
}
