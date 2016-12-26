package com.kii.extension.ruleengine.store.trigger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class ThingCollectSource {
	
	private List<Long> thingList=new ArrayList<>();
	
	private TagSelector selector=new TagSelector();
	
	private List<String> userList=new ArrayList<>();
	
	private List<String> triggerGroupList=new ArrayList<>();
	
	private List<String> businessIDList=new ArrayList<>();
	
	private BusinessObjType businessType= BusinessObjType.Thing;
	
	private String businessName;
	
	
	
	public String getBusinessName() {
		return businessName;
	}
	
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	
	public List<String> getUserList() {
		return userList;
	}
	
	public void setUserList(List<String> userList) {
		this.userList = userList;
		businessType= BusinessObjType.User;
	}
	
	public List<String> getTriggerGroupList() {
		return triggerGroupList;
	}
	
	public void setTriggerGroupList(List<String> triggerList) {
		this.triggerGroupList = triggerList;
		businessType= BusinessObjType.TriggerGroup;
		
	}
	
	public List<String> getBusinessIDList() {
		return businessIDList;
	}
	
	public void setBusinessIDList(List<String> businessIDList) {
		this.businessIDList = businessIDList;
	}
	
	public BusinessObjType getBusinessType() {
		return businessType;
	}
	
	public void setBusinessType(BusinessObjType businessType) {
		this.businessType = businessType;
	}
	
	public List<Long> getThingList() {
		return thingList;
	}
	
	public void setThingList(List<Long> thingList) {
		businessType= BusinessObjType.Thing;
		this.thingList = thingList;
	}
	
	@JsonIgnore
	public Set<String> getFullBusinessObjIDs() {
		
		return businessIDList.stream().map((k) -> {
				
				BusinessDataObject obj=new BusinessDataObject(k,businessName,businessType);
				
				return obj.getFullObjID();
		}).collect(Collectors.toSet());
	}
	
	@JsonUnwrapped
	public TagSelector getSelector() {
		return selector;
	}
	
	public void setSelector(TagSelector selector) {
		this.selector = selector;
		businessType= BusinessObjType.Thing;
		
	}
}
