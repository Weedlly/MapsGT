package com.example.mapsgt.data.dto;

public class UserLocationDto {
    private Double latitude;
    private Double longitude;
    private Boolean isSharing;

    public UserLocationDto() {
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getIsSharing() {
        return isSharing;
    }

    public void setIsSharing(Boolean isSharing) {
        this.isSharing = isSharing;
    }
}
