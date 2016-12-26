package com.kii.extension.ruleengine.store.trigger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class ThingCollectSource {
	
	private List<String> idList=new ArrayList<>();
	
	private TagSelector selector=new TagSelector();
	
	private BusinessObjType businessType;
	
//	private String businessName;
	
	private List<String> businessIDList=new ArrayList<>();
	
	
	public List<Long> getThingList(){
		return thingList;
	}
	
	public void setUserList(List<String> userList) {
		this.idList = userList;
		businessType= BusinessObjType.User;
	}
	
	public void setTriggerGroupList(List<String> triggerList) {
		this.idList = triggerList;
		businessType= BusinessObjType.TriggerGroup;
		
	}
	
	public void setBusinessIDList(List<String> businessIDList) {
		this.businessIDList = businessIDList;
	}
	
	private List<Long> thingList;
	
	public void setThingList(List<Long> thingList) {
		businessType= BusinessObjType.Thing;
		this.idList = thingList.stream().map(String::valueOf).collect(Collectors.toList());
		this.thingList=thingList;
	}
	
	@JsonIgnore
	public Set<BusinessDataObject> getFullBusinessObjs() {
		
		if(!businessIDList.isEmpty()){
			
			return businessIDList.stream().map(BusinessDataObject::getInstance).collect(Collectors.toSet());
		}else {
			
			return idList.stream().map((k) -> {
				
				BusinessDataObject obj = new BusinessDataObject(k, null, businessType);
				
				return obj;
			}).collect(Collectors.toSet());
		}
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
