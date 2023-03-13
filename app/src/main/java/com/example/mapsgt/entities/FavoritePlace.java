package com.example.mapsgt.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "favorite_place")
public class FavoritePlace {
    @PrimaryKey(autoGenerate = true)
    private int favoritePlaceId;
    @ColumnInfo(name = "user_id")
    private int userId;
    @ColumnInfo(name = "place_name")
    private String placeName;
    private String description;
    @ColumnInfo(name = "location_id")
    private int locationId;
    @ColumnInfo(name = "date_added")
    private Date dateAdded;

    public FavoritePlace(int favoritePlaceId, int userId, String placeName, String description, int locationId, Date dateAdded) {
        this.favoritePlaceId = favoritePlaceId;
        this.userId = userId;
        this.placeName = placeName;
        this.description = description;
        this.locationId = locationId;
        this.dateAdded = dateAdded;
    }

    public int getFavoritePlaceId() {
        return favoritePlaceId;
    }

    public void setFavoritePlaceId(int favoritePlaceId) {
        this.favoritePlaceId = favoritePlaceId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
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

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }
}
