package com.kii.extension.ruleengine.store.trigger;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MultipleSrcTriggerRecord extends TriggerRecord {


	private Map<String,ThingSource> summarySource=new HashMap<>();

	public Map<String, ThingSource> getSummarySource() {
		return summarySource;
	}

	public void setSummarySource(Map<String, ThingSource> summarySource) {
		this.summarySource = summarySource;
	}

	@JsonIgnore
	public void addSummarySource(String name,ThingSource source){
		summarySource.put(name,source);
	}


	@Override
	public BeehiveTriggerType getType() {
		return BeehiveTriggerType.Multiple;
	}
}
