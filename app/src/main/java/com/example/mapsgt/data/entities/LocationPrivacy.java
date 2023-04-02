package com.example.mapsgt.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.mapsgt.enumeration.PrivacyLevelStatusEnum;

@Entity(tableName = "location_privacy",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "user_id")},
        indices = {@Index(value = "user_id")}
)
public class LocationPrivacy {
    @ColumnInfo(name = "location_privacy_id")
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "user_id")
    private String userId;
    @ColumnInfo(name = "privacy_level_status")
    private PrivacyLevelStatusEnum privacyLevelStatusEnum;

    public LocationPrivacy(String userId, PrivacyLevelStatusEnum privacyLevelStatusEnum) {
        this.userId = userId;
        this.privacyLevelStatusEnum = privacyLevelStatusEnum;
    }

    public PrivacyLevelStatusEnum getPrivacyLevelStatusEnum() {
        return privacyLevelStatusEnum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public PrivacyLevelStatusEnum getPrivacyLevelStatus() {
        return privacyLevelStatusEnum;
    }

    public void setPrivacyLevelStatus(PrivacyLevelStatusEnum privacyLevelStatusEnum) {
        this.privacyLevelStatusEnum = privacyLevelStatusEnum;
    }
}
