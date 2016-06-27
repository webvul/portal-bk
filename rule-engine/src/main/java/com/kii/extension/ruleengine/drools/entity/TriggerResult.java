package com.kii.extension.ruleengine.drools.entity;

import java.util.HashMap;
import java.util.Map;

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
	public String toString() {
		return "TriggerResult{" +
				"triggerID='" + triggerID + '\'' +
				", delay=" + delay +
				", params=" + params +
				'}';
	}
}
