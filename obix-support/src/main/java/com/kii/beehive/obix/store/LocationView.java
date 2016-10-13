package com.kii.beehive.obix.store;

import java.util.ArrayList;
import java.util.List;

import com.kii.beehive.portal.store.entity.LocationInfo;

public class LocationView {


	private LocationInfo location;

	private List<EntityInfo>  entityCollect=new ArrayList<>();

	public LocationView(){

	}

	public LocationView(LocationInfo info){
		this.location=info;
	}

	public List<EntityInfo> getEntityCollect() {
		return entityCollect;
	}

	public void setEntityCollect(List<EntityInfo> entityCollect) {
		this.entityCollect = entityCollect;
	}

	public  void addEntity(EntityInfo entity){
		this.entityCollect.add(entity);
	}


	public LocationInfo getLocation() {
		return location;
	}

	public void setLocation(LocationInfo location) {
		this.location = location;
	}
}
