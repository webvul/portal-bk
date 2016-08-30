package com.kii.beehive.obix.store;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LocationInfo  {

	private String location;

	private String parentLoc;

	private int  level;

	private String displayName;

	private Map<String,LocationInfo> subLocations=new HashMap<>();


	private Set<ThingInfo>  thingCollect=new HashSet<>();

	public Set<ThingInfo> getThingCollect() {
		return thingCollect;
	}

	public void setThingCollect(Set<ThingInfo> thingCollect) {
		this.thingCollect = thingCollect;
	}




	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getParent() {
		return parentLoc;
	}

	public void setParent(String parentLoc) {
		this.parentLoc = parentLoc;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Map<String, LocationInfo> getSubLocations() {
		return subLocations;
	}

	public void setSubLocations(Map<String, LocationInfo> subLocations) {
		this.subLocations = subLocations;
	}


	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void fillSubLocations(Set<LocationInfo> subLocs) {

		subLocs.forEach(l->subLocations.put(l.getLocation(),l));
	}
}
