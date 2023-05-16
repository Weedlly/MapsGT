package com.example.mapsgt.data.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class HistoryPlace implements Parcelable {
    private String id;
    private String userId;
    private double latitude;
    private double longitude;
    private String name;
    private String address;

    public HistoryPlace() {
        // Default constructor required for calls to DataSnapshot.getValue(FavoritePlace.class)
    }

    protected HistoryPlace(Parcel in) {
        userId = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        name = in.readString();
        address = in.readString();
    }

    public static final Creator<HistoryPlace> CREATOR = new Creator<HistoryPlace>() {
        @Override
        public HistoryPlace createFromParcel(Parcel in) {
            return new HistoryPlace(in);
        }

        @Override
        public HistoryPlace[] newArray(int size) {
            return new HistoryPlace[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(name);
        dest.writeString(address);
    }

    public HistoryPlace(String userId, double latitude, double longitude, String name, String address) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

