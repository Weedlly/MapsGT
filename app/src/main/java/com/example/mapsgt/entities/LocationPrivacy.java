package com.example.mapsgt.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.mapsgt.enumeration.PrivacyLevelStatus;

@Entity(tableName = "location_privacy")
public class LocationPrivacy {
    @ColumnInfo(name = "location_privacy_id")
    @PrimaryKey(autoGenerate = true)
    private int locationPrivacyId;
    @ColumnInfo(name = "user_id")
    private int userId;
    @ColumnInfo(name = "privacy_level_status")
    private PrivacyLevelStatus privacyLevelStatus;

    public LocationPrivacy(int locationPrivacyId, int userId, PrivacyLevelStatus privacyLevelStatus) {
        this.locationPrivacyId = locationPrivacyId;
        this.userId = userId;
        this.privacyLevelStatus = privacyLevelStatus;
    }

    public int getLocationPrivacyId() {
        return locationPrivacyId;
    }

    public void setLocationPrivacyId(int locationPrivacyId) {
        this.locationPrivacyId = locationPrivacyId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public PrivacyLevelStatus getPrivacyLevelStatus() {
        return privacyLevelStatus;
    }

    public void setPrivacyLevelStatus(PrivacyLevelStatus privacyLevelStatus) {
        this.privacyLevelStatus = privacyLevelStatus;
    }
}
