package com.example.mapsgt.network.model.location;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Geometry {

    @SerializedName("coordinates")
    private List<List<Double>> coordinates;

    @SerializedName("type")
    private String type;

    public List<List<Double>> getCoordinates() {
        return coordinates;
    }

    public String getType() {
        return type;
    }
}