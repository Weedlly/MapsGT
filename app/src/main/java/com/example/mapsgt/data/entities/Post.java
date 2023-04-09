package com.example.mapsgt.data.entities;

import java.util.Date;

public class Post {
    private int id;
    private String userCreatorId;
    private int locationId;
    private String caption;
    private String image;
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
