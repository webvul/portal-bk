package com.kii.extension.ruleengine.store.trigger;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TagSelector {

	private List<Long> thingList=new ArrayList<>();
	
	private List<String> userList=new ArrayList<>();
	
	private List<String> triggerList=new ArrayList<>();
	
	private List<String> businessIDList=new ArrayList<>();
	
	private String businessType= BusinessDataObject.BusinessObjType.Thing.name();
	
	public List<String> getUserList() {
		return userList;
	}
	
	public void setUserList(List<String> userList) {
		this.userList = userList;
		businessType= BusinessDataObject.BusinessObjType.User.name();
	}
	
	public List<String> getTriggerList() {
		return triggerList;
	}
	
	public void setTriggerList(List<String> triggerList) {
		this.triggerList = triggerList;
		businessType= BusinessDataObject.BusinessObjType.Trigger.name();
		
	}
	
	public List<String> getBusinessIDList() {
		return businessIDList;
	}
	
	public void setBusinessIDList(List<String> businessIDList) {
		this.businessIDList = businessIDList;
	}
	
	public String getBusinessType() {
		return businessType;
	}
	
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	
	public void setTagList(List<String> tagList) {
		this.tagList = tagList;
	}
	
	private List<String> tagList=new ArrayList<>();

	private boolean isAndExpress=false;

	private String type;


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getTagList() {
		return tagList;
	}

	public void setTagCollect(List<String> tagCollect) {
		this.tagList = tagCollect;
	}

	public boolean isAndExpress() {
		return isAndExpress;
	}

	public void setAndExpress(boolean andExpress) {
		isAndExpress = andExpress;
	}


	public List<Long> getThingList() {
		return thingList;
	}

	public void setThingList(List<Long> thingList) {
		this.thingList = thingList;
	}

	@JsonIgnore
	public void addTag(String tagName) {
		this.tagList.add(tagName);
	}
}
