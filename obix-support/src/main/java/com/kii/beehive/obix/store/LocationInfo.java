package com.kii.beehive.obix.store;

import java.util.ArrayList;
import java.util.List;

public class LocationInfo  {

	private String location;

	private String parentLoc;

	private int  level;

	private String displayName;

	private List<String> subLocations=new ArrayList<>();


	private List<EntityInfo>  entityCollect=new ArrayList<>();

	public List<EntityInfo> getEntityCollect() {
		return entityCollect;
	}

	public void setEntityCollect(List<EntityInfo> entityCollect) {
		this.entityCollect = entityCollect;
	}

	public  void addEntity(EntityInfo entity){
		this.entityCollect.add(entity);
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


	public String getParentLoc() {
		return parentLoc;
	}

	public void setParentLoc(String parentLoc) {
		this.parentLoc = parentLoc;
	}

	public List<String> getSubLocations() {
		return subLocations;
	}

	public void setSubLocations(List<String> subLocations) {
		this.subLocations = subLocations;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}


}
