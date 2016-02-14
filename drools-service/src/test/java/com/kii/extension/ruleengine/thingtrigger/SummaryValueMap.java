package com.kii.extension.ruleengine.thingtrigger;

import java.util.HashMap;
import java.util.Map;

public class SummaryValueMap {

	private int triggerID;

	private Map<String,Number> numberMap=new HashMap<>();

	public Map<String, Number> getNumberMap() {
		return numberMap;
	}

	public Number getNumber(String field){
		return numberMap.get(field);
	}

	public void setNumber(String field,Number value){
		numberMap.put(field,value);
	}

	public void setNumberMap(Map<String, Number> numberMap) {
		this.numberMap = numberMap;
	}

	public int getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(int triggerID) {
		this.triggerID = triggerID;
	}
}
