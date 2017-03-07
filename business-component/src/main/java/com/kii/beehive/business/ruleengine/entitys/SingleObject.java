package com.kii.beehive.business.ruleengine.entitys;

public class SingleObject {

	
	private String groupName;
	
	private String objID;
	
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
	
	public String getObjID() {
		return objID;
	}
	
	public void setObjID(String objID) {
		this.objID = objID;
	}
	
}
