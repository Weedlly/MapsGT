package com.example.mapsgt.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "favorite_place", foreignKeys = {
        @ForeignKey(entity = User.class,
                parentColumns = "user_id",
                childColumns = "user_id"),
        @ForeignKey(entity = Location.class,
                parentColumns = "location_id",
                childColumns = "location_id")
})
public class FavoritePlace {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "favorite_place_id")
    private int id;
    @ColumnInfo(name = "user_id")
    private int userId;
    @ColumnInfo(name = "place_name")
    private String placeName;
    private String description;
    @ColumnInfo(name = "location_id")
    private int locationId;
    @ColumnInfo(name = "created_at")
    private Date createdAt;

    public FavoritePlace(int userId, String placeName, String description, int locationId, Date createdAt) {
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
