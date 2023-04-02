package com.example.mapsgt.network.model.location;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LanesItem {

    @SerializedName("valid")
    private boolean valid;

    @SerializedName("indications")
    private List<String> indications;

    @SerializedName("valid_indication")
    private String validIndication;

    @SerializedName("active")
    private boolean active;

    public boolean isValid() {
        return valid;
    }

    public List<String> getIndications() {
        return indications;
    }

    public String getValidIndication() {
        return validIndication;
    }

    public boolean isActive() {
        return active;
    }
}