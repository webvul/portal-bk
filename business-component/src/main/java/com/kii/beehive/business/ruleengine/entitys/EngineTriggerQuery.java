package com.kii.beehive.business.ruleengine.entitys;

import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;

public class EngineTriggerQuery {
	
	private String fullObjID;
	
	private TriggerRecord.StatusType status;
	
	private String creator;
	
	private String description;
	
	private String name;
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getFullObjID() {
		return fullObjID;
	}
	
	public void setFullObjID(String objID) {
		this.fullObjID = objID;
	}
	
	
	public TriggerRecord.StatusType getStatus() {
		return status;
	}
	
	public void setStatus(TriggerRecord.StatusType status) {
		this.status = status;
	}
	
	public String getCreator() {
		return creator;
	}
	
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
}
