package com.example.mapsgt.network.model.location;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LegsItem {

    @SerializedName("duration")
    private Object duration;

    @SerializedName("summary")
    private String summary;

    @SerializedName("distance")
    private Object distance;

    @SerializedName("weight")
    private Object weight;

    @SerializedName("via_waypoints")
    private List<Object> viaWaypoints;

    @SerializedName("steps")
    private List<StepsItem> steps;

    @SerializedName("admins")
    private List<AdminsItem> admins;

    public Object getDuration() {
        return duration;
    }

    public String getSummary() {
        return summary;
    }

    public Object getDistance() {
        return distance;
    }

    public Object getWeight() {
        return weight;
    }

    public List<Object> getViaWaypoints() {
        return viaWaypoints;
    }

    public List<StepsItem> getSteps() {
        return steps;
    }

    public List<AdminsItem> getAdmins() {
        return admins;
    }
}