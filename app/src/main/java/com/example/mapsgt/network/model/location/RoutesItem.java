package com.example.mapsgt.network.model.location;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class RoutesItem{

	@SerializedName("duration")
	private Object duration;

	@SerializedName("distance")
	private Object distance;

	@SerializedName("legs")
	private List<LegsItem> legs;

	@SerializedName("weight_name")
	private String weightName;

	@SerializedName("weight")
	private Object weight;

	@SerializedName("geometry")
	private Geometry geometry;

	public Object getDuration(){
		return duration;
	}

	public Object getDistance(){
		return distance;
	}

	public List<LegsItem> getLegs(){
		return legs;
	}

	public String getWeightName(){
		return weightName;
	}

	public Object getWeight(){
		return weight;
	}

	public Geometry getGeometry(){
		return geometry;
	}
}