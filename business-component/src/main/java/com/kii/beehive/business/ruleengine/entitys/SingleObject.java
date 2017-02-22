package com.kii.beehive.business.ruleengine.entitys;

public class SingleObject {

	
	private String groupName;
	
	private String businessID;
	
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
	
	public String getBusinessID() {
		return businessID;
	}
	
	public void setBusinessID(String businessID) {
		this.businessID = businessID;
	}
	
}
