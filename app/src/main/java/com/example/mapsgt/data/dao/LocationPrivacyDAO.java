package com.example.mapsgt.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.mapsgt.data.entities.LocationPrivacy;

@Dao
public interface LocationPrivacyDAO {
    @Insert
    void insertLocationPrivacy(LocationPrivacy locationPrivacy);
}
