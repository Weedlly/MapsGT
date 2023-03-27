package com.example.mapsgt.network.model.location;

import com.google.gson.annotations.SerializedName;

public class AdminsItem {

    @SerializedName("iso_3166_1")
    private String iso31661;

    @SerializedName("iso_3166_1_alpha3")
    private String iso31661Alpha3;

    public String getIso31661() {
        return iso31661;
    }

    public String getIso31661Alpha3() {
        return iso31661Alpha3;
    }
}