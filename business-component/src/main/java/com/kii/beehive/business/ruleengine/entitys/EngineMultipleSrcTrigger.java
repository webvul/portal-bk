package com.kii.beehive.business.ruleengine.entitys;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;

import com.kii.extension.ruleengine.store.trigger.BeehiveTriggerType;
import com.kii.extension.ruleengine.store.trigger.MultipleSrcTriggerRecord;

public class EngineMultipleSrcTrigger extends EngineTrigger {


	private Map<String,EngineSourceElement> summarySource=new HashMap<>();

	public Map<String, EngineSourceElement> getSummarySource() {
		return summarySource;
	}

	public void setSummarySource(Map<String, EngineSourceElement> summarySource){
		this.summarySource=summarySource;
	}
	
	@Override
	public BeehiveTriggerType getType() {
		return BeehiveTriggerType.Multiple;
	}
	
	public void addSource(String name,EngineSourceElement elem) {
		summarySource.put(name,elem);
	}
	
	public EngineMultipleSrcTrigger(){
		
	}
	
	public EngineMultipleSrcTrigger(MultipleSrcTriggerRecord record){
		
		
		BeanUtils.copyProperties(record,this,"summarySource");
		
		
		
	}
}
