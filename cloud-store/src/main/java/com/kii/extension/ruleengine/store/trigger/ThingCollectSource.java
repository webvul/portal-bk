package com.kii.extension.ruleengine.store.trigger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class ThingCollectSource {
	
	private List<Long> thingList=new ArrayList<>();
	
	private List<String> userList=new ArrayList<>();
	
	private List<String> triggerList=new ArrayList<>();
	
	private List<String> businessIDList=new ArrayList<>();
	
	
	private TagSelector selector=new TagSelector();
	
	private BusinessObjType businessType;
	
	private String businessName;
	
	
	public String getBusinessName() {
		return businessName;
	}
	
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	
	public List<Long> getThingList(){
		return thingList;
	}
	
	public void setUserList(List<String> userList) {
		if(!userList.isEmpty()) {
			this.userList = userList;
			this.businessType = BusinessObjType.User;
		}
	}
	
	public void setTriggerList(List<String> triggerList) {
		if(!triggerList.isEmpty()) {
			this.triggerList = triggerList;
			this.businessType = BusinessObjType.TriggerGroup;
		}
	}
	
	public List<String> getUserList() {
		return userList;
	}
	
	public List<String> getTriggerList() {
		return triggerList;
	}
	
	
	public List<String> getBusinessIDList() {
		return businessIDList;
	}
	
	
	public void setBusinessIDList(List<String> businessIDList) {
		
		if(!businessIDList.isEmpty()) {
			this.businessIDList = businessIDList;
			this.businessType = BusinessObjType.Business;
		}
	}
	
	public void setThingList(List<Long> thingList) {
		if(!thingList.isEmpty()) {
			this.thingList = thingList;
			this.businessType = BusinessObjType.Thing;
		}
//		this.thingList  = thingList.stream().map(String::valueOf).collect(Collectors.toList());
	}
	
	@JsonIgnore
	public Set<BusinessDataObject> getFullBusinessObjs() {
		
		
		List<String>  idList=new ArrayList<>();
		
		switch (businessType){
			case Thing:idList=thingList.stream().map(String::valueOf).collect(Collectors.toList());
				break;
			case User:idList=userList;break;
			case TriggerGroup:idList=triggerList;break;
			case Business:idList=businessIDList;break;
			
		}
		
		return idList.stream().map((k) -> {
			
			BusinessDataObject obj = new BusinessDataObject(k, businessName, businessType);
			
			return obj;
		}).collect(Collectors.toSet());
		
	}
	
	@JsonUnwrapped
	public TagSelector getSelector() {
		return selector;
	}
	
	public void setSelector(TagSelector selector) {
		this.selector = selector;
		this.businessType=BusinessObjType.Thing;
	}
}
