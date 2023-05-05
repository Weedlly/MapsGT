package com.example.mapsgt.ui.map;

import com.google.android.gms.maps.model.LatLng;

public class FavouritePlace {
    private String userId;
    private double latitude;
    private double longitude;
    private String name;

    public FavouritePlace() {
        // Default constructor required for calls to DataSnapshot.getValue(FavoritePlace.class)
    }

    public FavouritePlace(String userId, double latitude, double longitude, String name) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getName() {
        return name;
    }

}

