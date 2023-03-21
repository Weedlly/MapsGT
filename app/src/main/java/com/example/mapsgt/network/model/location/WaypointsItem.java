package com.example.mapsgt.network.model.location;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class WaypointsItem{

	@SerializedName("distance")
	private Object distance;

	@SerializedName("name")
	private String name;

	@SerializedName("location")
	private List<Object> location;

	public Object getDistance(){
		return distance;
	}

	public String getName(){
		return name;
	}

	public List<Object> getLocation(){
		return location;
	}
}