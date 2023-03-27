package com.example.mapsgt.network.model.location;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StepsItem {

    @SerializedName("duration")
    private Object duration;

    @SerializedName("mode")
    private String mode;

    @SerializedName("distance")
    private Object distance;

    @SerializedName("name")
    private String name;

    @SerializedName("weight")
    private Object weight;

    @SerializedName("geometry")
    private Geometry geometry;

    @SerializedName("driving_side")
    private String drivingSide;

    @SerializedName("intersections")
    private List<IntersectionsItem> intersections;

    @SerializedName("maneuver")
    private Maneuver maneuver;

    @SerializedName("exits")
    private String exits;

    @SerializedName("destinations")
    private String destinations;

    @SerializedName("ref")
    private String ref;

    public Object getDuration() {
        return duration;
    }

    public String getMode() {
        return mode;
    }

    public Object getDistance() {
        return distance;
    }

    public String getName() {
        return name;
    }

    public Object getWeight() {
        return weight;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public String getDrivingSide() {
        return drivingSide;
    }

    public List<IntersectionsItem> getIntersections() {
        return intersections;
    }

    public Maneuver getManeuver() {
        return maneuver;
    }

    public String getExits() {
        return exits;
    }

    public String getDestinations() {
        return destinations;
    }

    public String getRef() {
        return ref;
    }
}