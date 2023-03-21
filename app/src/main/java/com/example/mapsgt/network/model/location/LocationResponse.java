package com.example.mapsgt.network.model.location;

import java.util.List;
import com.google.gson.annotations.SerializedName;

	public class LocationResponse {

	@SerializedName("routes")
	private List<RoutesItem> routes;

	@SerializedName("code")
	private String code;

	@SerializedName("waypoints")
	private List<WaypointsItem> waypoints;

	@SerializedName("uuid")
	private String uuid;

	public List<RoutesItem> getRoutes(){
		return routes;
	}

	public String getCode(){
		return code;
	}

	public List<WaypointsItem> getWaypoints(){
		return waypoints;
	}

	public String getUuid(){
		return uuid;
	}
}