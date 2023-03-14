package com.example.mapsgt.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.mapsgt.entities.LocationPrivacy;

@Dao
public interface LocationPrivacyDAO {
    @Insert
    void insertLocationPrivacy(LocationPrivacy locationPrivacy);
}
