package com.example.mapsgt.data.entities;

import com.example.mapsgt.enumeration.PrivacyLevelStatusEnum;


public class LocationPrivacy {
    private int id;
    private String userId;
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
