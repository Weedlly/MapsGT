package com.example.mapsgt.network.model.location;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RoutesItem {

    @SerializedName("duration")
    private Double duration;

    @SerializedName("distance")
    private Double distance;

    @SerializedName("legs")
    private List<LegsItem> legs;

    @SerializedName("weight_name")
    private String weightName;

    @SerializedName("weight")
    private Object weight;

    @SerializedName("geometry")
    private Geometry geometry;

    public Double getDuration() {
        return duration;
    }

    public Double getDistance() {
        return distance;
    }

    public List<LegsItem> getLegs() {
        return legs;
    }

    public String getWeightName() {
        return weightName;
    }

    public Object getWeight() {
        return weight;
    }

    public Geometry getGeometry() {
        return geometry;
    }
}