package com.kii.beehive.portal.store.entity;

import com.kii.extension.ruleengine.store.trigger.TriggerRecord;

public class MLTriggerCombine extends PortalEntity {
	
	private String mlTaskID;
	
	private TriggerRecord businessTrigger;
	
	private String relationTriggerID;
	
	public String getRelationTriggerID() {
		return relationTriggerID;
	}
	
	public void setRelationTriggerID(String relationTriggerID) {
		this.relationTriggerID = relationTriggerID;
	}
	
	public String getCombineTriggerID(){
		return super.getId();
	}
	
	
	public String getMlTaskID() {
		return mlTaskID;
	}
	
	public void setMlTaskID(String mlTaskID) {
		this.mlTaskID = mlTaskID;
	}
	
	public TriggerRecord getBusinessTrigger() {
		return businessTrigger;
	}
	
	public void setBusinessTrigger(TriggerRecord businessTrigger) {
		this.businessTrigger = businessTrigger;
	}
	
}
