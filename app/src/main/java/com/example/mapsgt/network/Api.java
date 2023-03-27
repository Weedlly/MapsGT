package com.example.mapsgt.network;

import com.example.mapsgt.network.model.location.LocationResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface Api {
    String BASE_URL = "https://api.mapbox.com";

    @GET("/directions/v5/mapbox/" +
            "{style}/" +
            "{lonOrigin}%2C" +
            "{latOrigin}%3B" +
            "{lonDes}%2C" +
            "{latDes}?alternatives=false&geometries=geojson&overview=simplified&steps=false&" +
            "access_token=" +
            "pk.eyJ1Ijoid2VlZGx5IiwiYSI6ImNsN2VpNGhzdzAwa3kzcG1rejUyMmx5bWgifQ.yWa9VGg1_-dNypDTcohLCg")
    Call<LocationResponse> getMyDirection(
            @Path("style") String style,
            @Path("lonOrigin") Double lonOrigin,
            @Path("latOrigin") Double latOrigin,
            @Path("lonDes") Double lonDes,
            @Path("latDes") Double latDes
    );
}
