package com.kii.beehive.portal.store.entity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class LocationTree {


	private String location;

	private LocationType  locationLevel;

	private Map<String,LocationTree> subLocations=new HashMap<>();


	public LocationTree(){

		location=".";

	}

	public LocationTree(LocationInfo info){
		this.location=info.getLocation();
		this.locationLevel=info.getLevel();

	}


	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Map<String,LocationTree>  getSubLocations() {
		return subLocations;
	}

	public void setSubLocations(Map<String,LocationTree>  subLocations) {
		this.subLocations = subLocations;
	}

	public LocationType getLocationLevel() {
		return locationLevel;
	}

	public void setLocationLevel(LocationType locationLevel) {
		this.locationLevel = locationLevel;
	}


	private void addLocation(LocationTree entry, LinkedList<String> upperList){

		if(upperList.isEmpty()){
			this.subLocations.put(entry.getLocation(),entry);
			return;
		}

		String top=upperList.removeFirst();

		LocationTree subs=subLocations.get(top);
		if(subs == null){
			return;
//			throw new IllegalArgumentException("top:"+top+" loc:"+entry.getLocation());

		}
		subs.addLocation(entry,upperList);

	}

	public void addSubLocation(LocationInfo loc){

		LinkedList<String> upperList=new LinkedList<>(LocationType.getLevelInList(loc.getLocation()));

		addLocation(new LocationTree(loc),upperList);

	}
}
