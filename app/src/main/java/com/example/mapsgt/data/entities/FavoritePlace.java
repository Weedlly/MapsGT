package com.example.mapsgt.data.entities;

import java.util.Date;

public class FavoritePlace {
    private int id;
    private String userId;
    private String placeName;
    private String description;
    private int locationId;
    private Date createdAt;

    public FavoritePlace(String userId, String placeName, String description, int locationId, Date createdAt) {
        this.userId = userId;
        this.placeName = placeName;
        this.description = description;
        this.locationId = locationId;
        this.createdAt = createdAt;
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

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
