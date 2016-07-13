package com.kii.beehive.portal.store.entity;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserCustomData extends PortalEntity {


	private String userID;

	private Map<String,CustomData>  dataMap=new HashMap<>();


	@Override
	public String getId(){
		return userID;
	}

	@JsonAnyGetter
	public Map<String, CustomData> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, CustomData> dataMap) {
		this.dataMap = dataMap;
	}

	@JsonIgnore
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	@JsonAnySetter
	public void addData(String type, CustomData data) {
		dataMap.put(type,data);
	}
}
