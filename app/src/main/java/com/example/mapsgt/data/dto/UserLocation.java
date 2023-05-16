package com.example.mapsgt.data.dto;

import com.example.mapsgt.data.entities.User;
import com.google.android.gms.maps.model.BitmapDescriptor;

public class UserLocation {
    private String id;
    private BitmapDescriptor profileImg;
    private String displayName;
    private boolean isSharing;
    private double latitude;
    private double longitude;

    public UserLocation(User user) {
        this.id = user.getId();
        this.displayName = user.getFirstName() + " " + user.getLastName();
        this.isSharing = user.getIsSharing();
        this.latitude = user.getLatitude();
        this.longitude = user.getLongitude();
    }

    public UserLocation(String id, String displayName, boolean isSharing, double latitude, double longitude) {
        this.id = id;
        this.displayName = displayName;
        this.isSharing = isSharing;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BitmapDescriptor getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(BitmapDescriptor profileImg) {
        this.profileImg = profileImg;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isSharing() {
        return isSharing;
    }

    public void setIsSharing(boolean isSharing) {
        this.isSharing = isSharing;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
