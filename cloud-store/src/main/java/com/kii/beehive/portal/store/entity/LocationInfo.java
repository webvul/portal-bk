package com.kii.beehive.portal.store.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.kii.extension.sdk.entity.KiiEntity;

public class LocationInfo extends KiiEntity {

	private String location;

	private String parentLoc;

	private LocationType  locationLevel;

	private String displayName;

	private AreaType  areaType;

	private Map<String,String> subLocations=new HashMap<>();


	@Override
	public String getId(){
		return location;
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

	public LocationType getLevel() {
		return locationLevel;
	}

	public void setLevel(LocationType locationLevel) {
		this.locationLevel = locationLevel;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public AreaType getAreaType() {
		return areaType;
	}

	public void setAreaType(AreaType areaType) {
		this.areaType = areaType;
	}

	public Map<String, String> getSubLocations() {
		return subLocations;
	}

	public void setSubLocations(Map<String, String> subLocations) {
		this.subLocations = subLocations;
	}

	public static enum AreaType{

		W,M,F,C;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LocationInfo that = (LocationInfo) o;
		return Objects.equals(location, that.location);
	}

	@Override
	public int hashCode() {
		return Objects.hash(location);
	}
}
