package com.example.mapsgt.network.model.location;

import com.google.gson.annotations.SerializedName;

public class MapboxStreetsV8 {

    @SerializedName("class")
    private String jsonMemberClass;

    public String getJsonMemberClass() {
        return jsonMemberClass;
    }
}