package com.kii.extension.ruleengine.drools.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MatchResult {

	private final String triggerID;

	private String delay=null;

	private Map<String,String> params=new HashMap<>();

	public MatchResult(String triggerID){
		this.triggerID=triggerID;
	}

	public MatchResult(ResultParam param){

		this.triggerID=param.getTriggerID();
		this.delay=param.getDelay();
		this.params.putAll(param.getParams());
	}


	public String getTriggerID() {
		return triggerID;
	}



	@Override
	public String toString() {
		return "MatchResult{" +
				"triggerID='" + triggerID + '\'' +
				", delay=" + delay +
				", params=" + params +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MatchResult that = (MatchResult) o;
		return Objects.equals(triggerID, that.triggerID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(triggerID);
	}

	public String getDelay() {
		return delay;
	}

	public void setDelay(String delay) {
		this.delay = delay;
	}

	public void setParam(String key,Object value){
		params.put(key,String.valueOf(value));
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}
}
