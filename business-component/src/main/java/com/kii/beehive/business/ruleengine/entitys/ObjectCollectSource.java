package com.kii.beehive.business.ruleengine.entitys;

import java.util.ArrayList;
import java.util.List;

public class ObjectCollectSource {
	
	
	private List<String> objIDList = new ArrayList<>();
	
	private String groupName;
	
	private EngineBusinessType objDataType=EngineBusinessType.Business;
	
	
	public EngineBusinessType getObjDataType() {
		return objDataType;
	}
	
	public void setObjDataType(EngineBusinessType objDataType) {
		this.objDataType = objDataType;
	}
	
	public String getGroupName() {
		return groupName;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	
	public List<String> getobjIDList() {
		return objIDList;
	}
	
	public void setobjIDList(List<String> businessIDList) {
		
		
		this.objIDList = businessIDList;
		
	}


	

}
