package com.example.mapsgt.network.model.location;

import com.google.gson.annotations.SerializedName;

public class TollCollection{

	@SerializedName("type")
	private String type;

	@SerializedName("name")
	private String name;

	public String getType(){
		return type;
	}

	public String getName(){
		return name;
	}
}