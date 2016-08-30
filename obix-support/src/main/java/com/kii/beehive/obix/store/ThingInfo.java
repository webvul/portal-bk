package com.kii.beehive.obix.store;

import java.util.HashSet;
import java.util.Set;

public class ThingInfo {

	private Set<PointInfo> pointCollect=new HashSet<>();

	private String name;

	private String schema;

	private String location;

	private Set<String> locationCollect=new HashSet<>();

	private Set<String> customTags=new HashSet<>();



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Set<String> getLocationCollect() {
		return locationCollect;
	}

	public void setLocationCollect(Set<String> locationCollect) {
		this.locationCollect = locationCollect;
	}

	public Set<String> getCustomTags() {
		return customTags;
	}

	public void setCustomTags(Set<String> customTags) {
		this.customTags = customTags;
	}

	public Set<PointInfo> getPointCollect() {
		return pointCollect;
	}

	public void setPointCollect(Set<PointInfo> pointCollect) {
		this.pointCollect = pointCollect;
	}
}
