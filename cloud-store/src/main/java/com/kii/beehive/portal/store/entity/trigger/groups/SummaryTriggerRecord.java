package com.kii.beehive.portal.store.entity.trigger.groups;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.beehive.portal.store.entity.trigger.BeehiveTriggerType;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;

public class SummaryTriggerRecord extends TriggerRecord {

	private Map<String,SummarySource> summarySource=new HashMap<>();

	public Map<String, SummarySource> getSummarySource() {
		return summarySource;
	}

	public void setSummarySource(Map<String, SummarySource> summarySource) {
		this.summarySource = summarySource;
	}

	@JsonIgnore
	public void addSummarySource(String name,SummarySource source){
		summarySource.put(name,source);
	}

	@Override
	public BeehiveTriggerType getType() {
		return BeehiveTriggerType.Summary;
	}
}
