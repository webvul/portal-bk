package com.kii.beehive.portal.entitys;

import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;

public class MLTriggerCombine {
	
	
	private Condition  mlCondition;
	
	private String mlTaskID;
	
	private TriggerRecord businessTrigger;
	
	private boolean joinWithAND=true;
	
	public Condition getMlCondition() {
		return mlCondition;
	}
	
	public void setMlCondition(Condition mlCondition) {
		this.mlCondition = mlCondition;
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
	
	public boolean isJoinWithAND() {
		return joinWithAND;
	}
	
	public void setJoinWithAND(boolean joinWithAND) {
		this.joinWithAND = joinWithAND;
	}
}
