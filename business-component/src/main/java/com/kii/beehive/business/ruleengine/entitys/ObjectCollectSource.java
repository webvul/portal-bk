package com.kii.beehive.business.ruleengine.entitys;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.extension.ruleengine.store.trigger.BusinessDataObject;
import com.kii.extension.ruleengine.store.trigger.BusinessObjType;

public class ObjectCollectSource {
	
	
	private List<String> businessIDList=new ArrayList<>();
	
	private String businessName;
	
	private BusinessObjType type=BusinessObjType.Business;
	
	public BusinessObjType getBusinessType() {
		return type;
	}
	
	public void setBusinessType(BusinessObjType type) {
		this.type = type;
	}
	
	public String getBusinessName() {
		return businessName;
	}
	
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	
	public List<String> getBusinessIDList() {
		return businessIDList;
	}
	
	public void setBusinessIDList(List<String> businessIDList) {
		
		
		this.businessIDList = businessIDList;
		
	}

	
	@JsonIgnore
	public Set<BusinessDataObject> getFullBusinessObjs() {
		
		
		List<String>  idList=new ArrayList<>();
		
		return idList.stream().map((k) -> {
				
				BusinessDataObject obj = new BusinessDataObject(k, businessName, type);
				
				return obj;
			}).collect(Collectors.toSet());
		
	}
	
	@JsonIgnore
	public Set<String> getFullBusinessObjIDs() {
		
		return getFullBusinessObjs().stream().map(BusinessDataObject::toString).collect(Collectors.toSet());
		
	}

}
