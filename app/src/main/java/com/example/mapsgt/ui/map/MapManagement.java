package com.example.mapsgt.ui.map;

import com.google.android.gms.maps.model.LatLng;

public class MapManagement {
    private MapMode mapMode;
    private LatLng destination;
    private String goingFriendId;
    private boolean isGoingToFriend;

    public MapManagement() {
        mapMode = MapMode.SHOW;
    }

    public LatLng getDestination() {
        return destination;
    }

    public void setDestination(LatLng destination) {
        this.destination = destination;
    }

    public String getGoingFriendId() {
        return goingFriendId;
    }

    public void setGoingFriendId(String goingFriendId) {
        this.goingFriendId = goingFriendId;
    }

    public boolean isGoingToFriend() {
        return isGoingToFriend;
    }

    public void setGoingToFriend(boolean goingToFriend) {
        isGoingToFriend = goingToFriend;
    }

    public MapMode getMapMode() {
        return mapMode;
    }

    public void setMapMode(MapMode mapMode) {
        this.mapMode = mapMode;
    }
}
