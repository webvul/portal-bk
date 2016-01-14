package com.kii.beehive.portal.store.entity.trigger;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class SummaryTriggerRuntimeState extends TriggerRuntimeState {


	private Map<String,SummaryStateEntry> map=new HashMap<>();


	@JsonAnyGetter
	public Map<String, SummaryStateEntry> getListeners() {
		return map;
	}

	@JsonAnySetter
	public void setListener(String key,SummaryStateEntry entry){
		map.put(key,entry);
	}

	public void setListeners(Map<String, SummaryStateEntry> map) {
		this.map = map;
	}

	@JsonIgnore
	public void addListener(SummaryStateEntry  ids,String name){
		map.put(name,ids);
	}


}
