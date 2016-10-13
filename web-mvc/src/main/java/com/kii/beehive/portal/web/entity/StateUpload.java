package com.kii.beehive.portal.web.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.extension.sdk.entity.thingif.ThingStatus;

public class StateUpload {

	private ThingStatus state;

	private String target;

	private Map<String,Object> expandFields=new HashMap<>();

	//TODO:fill this field in kiicloud's thing status change trigger
	private Date timestamp=new Date();

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public ThingStatus getState() {
		return state;
	}

	public void setState(ThingStatus state) {
		this.state = state;
	}

	public Map<String, Object> getExpandFields() {
		return expandFields;
	}

	public void setExpandFields(Map<String, Object> expandFields) {
		this.expandFields = expandFields;
	}

	@JsonAnySetter
	public void addExpandField(String name,Object val){
		expandFields.put(name,val);
	}

	@JsonIgnore
	public String getThingID(){

		int idx=target.indexOf(":");

		return target.substring(idx+1);

	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
}
