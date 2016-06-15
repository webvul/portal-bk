package com.kii.extension.ruleengine.drools.entity;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Objects;

public class TriggerResult {

	private final String triggerID;

	private String delay=null;

	private Map<String,String> params=new HashMap<>();

	public String getDelay() {
		return delay;
	}

	public void setDelay(String delay) {
		this.delay = delay;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public TriggerResult(MatchResult  result ){

		this.triggerID=result.getTriggerID();
		this.delay=result.getDelay();
		this.params.putAll(result.getParams());
	}

	public TriggerResult(String triggerID ){

		this.triggerID=triggerID;
	}

	public String getTriggerID() {
		return triggerID;
	}


	public MatchResult getMatchResult(){
		MatchResult result=new MatchResult(triggerID);

		result.setDelay(this.getDelay());
		result.setParams(this.getParams());

		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TriggerResult that = (TriggerResult) o;
		return Objects.equal(triggerID,that.triggerID) ;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(triggerID);
	}


	@Override
	public String toString() {
		return "TriggerResult{" +
				"triggerID='" + triggerID + '\'' +
				", delay=" + delay +
				", params=" + params +
				'}';
	}
}
