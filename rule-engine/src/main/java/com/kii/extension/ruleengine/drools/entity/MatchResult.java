package com.kii.extension.ruleengine.drools.entity;

public class MatchResult extends CommResult{


	public MatchResult(String triggerID){
		super.triggerID=triggerID;
	}

	public MatchResult(ResultParam param){

		this.triggerID=param.getTriggerID();
		this.delay=param.getDelay();
		this.params.putAll(param.getParams());
	}
}
