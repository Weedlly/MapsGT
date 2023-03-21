package com.example.mapsgt.network.model.location;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Maneuver{

	@SerializedName("instruction")
	private String instruction;

	@SerializedName("modifier")
	private String modifier;

	@SerializedName("bearing_after")
	private int bearingAfter;

	@SerializedName("bearing_before")
	private int bearingBefore;

	@SerializedName("location")
	private List<Object> location;

	@SerializedName("type")
	private String type;

	public String getInstruction(){
		return instruction;
	}

	public String getModifier(){
		return modifier;
	}

	public int getBearingAfter(){
		return bearingAfter;
	}

	public int getBearingBefore(){
		return bearingBefore;
	}

	public List<Object> getLocation(){
		return location;
	}

	public String getType(){
		return type;
	}
}