package com.example.mapsgt.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "post")
public class Post {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "post_id")
    private int id;
    @ColumnInfo(name = "user_creator_id")
    private String userCreatorId;
    @ColumnInfo(name = "location_id")
    private int locationId;
    private String caption;
    private String image;
    @ColumnInfo(name = "created_at")
    private Date createdAt;

    public Post(String userCreatorId, int locationId, String caption, String image, Date createdAt) {
        this.userCreatorId = userCreatorId;
        this.locationId = locationId;
        this.caption = caption;
        this.image = image;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserCreatorId() {
        return userCreatorId;
    }

    public void setUserCreatorId(String userCreatorId) {
        this.userCreatorId = userCreatorId;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
