package com.example.mapsgt.network.model.location;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class IntersectionsItem{

	@SerializedName("entry")
	private List<Boolean> entry;

	@SerializedName("bearings")
	private List<Integer> bearings;

	@SerializedName("is_urban")
	private boolean isUrban;

	@SerializedName("admin_index")
	private int adminIndex;

	@SerializedName("geometry_index")
	private int geometryIndex;

	@SerializedName("location")
	private List<Object> location;

	@SerializedName("mapbox_streets_v8")
	private MapboxStreetsV8 mapboxStreetsV8;

	@SerializedName("out")
	private int out;

	@SerializedName("in")
	private int in;

	@SerializedName("turn_weight")
	private int turnWeight;

	@SerializedName("turn_duration")
	private Object turnDuration;

	@SerializedName("weight")
	private Object weight;

	@SerializedName("duration")
	private Object duration;

	@SerializedName("lanes")
	private List<LanesItem> lanes;

	@SerializedName("classes")
	private List<String> classes;

	@SerializedName("toll_collection")
	private TollCollection tollCollection;

	@SerializedName("traffic_signal")
	private boolean trafficSignal;

	@SerializedName("yield_sign")
	private boolean yieldSign;

	public List<Boolean> getEntry(){
		return entry;
	}

	public List<Integer> getBearings(){
		return bearings;
	}

	public boolean isIsUrban(){
		return isUrban;
	}

	public int getAdminIndex(){
		return adminIndex;
	}

	public int getGeometryIndex(){
		return geometryIndex;
	}

	public List<Object> getLocation(){
		return location;
	}

	public MapboxStreetsV8 getMapboxStreetsV8(){
		return mapboxStreetsV8;
	}

	public int getOut(){
		return out;
	}

	public int getIn(){
		return in;
	}

	public int getTurnWeight(){
		return turnWeight;
	}

	public Object getTurnDuration(){
		return turnDuration;
	}

	public Object getWeight(){
		return weight;
	}

	public Object getDuration(){
		return duration;
	}

	public List<LanesItem> getLanes(){
		return lanes;
	}

	public List<String> getClasses(){
		return classes;
	}

	public TollCollection getTollCollection(){
		return tollCollection;
	}

	public boolean isTrafficSignal(){
		return trafficSignal;
	}

	public boolean isYieldSign(){
		return yieldSign;
	}
}