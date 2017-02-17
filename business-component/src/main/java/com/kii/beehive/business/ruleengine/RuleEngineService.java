package com.kii.beehive.business.ruleengine;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.kii.beehive.business.ruleengine.entitys.EngineTrigger;
import com.kii.extension.ruleengine.store.trigger.BusinessDataObject;

@Component
public class RuleEngineService {
	
	
	
	public String  addTrigger(EngineTrigger trigger){
		return null;
	}
	
	public void updateTrigger(EngineTrigger trigger){
		
	}
	
	public void removeTrigger(String triggerID){
		
	}
	
	public void enableTrigger(String triggerID){
		
	}
	
	public void disableTrigger(String triggerID){
		
	}

	public void addBusinessData(BusinessDataObject obj){
		
	}
	
	public void updateBusinessData(Set<BusinessDataObject> dataList) {
	}
}
