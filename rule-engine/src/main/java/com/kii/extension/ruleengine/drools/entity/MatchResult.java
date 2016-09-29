package com.kii.extension.ruleengine.drools.entity;

public class MatchResult extends CommResult{


	public MatchResult(String triggerID){
		super.triggerID=triggerID;
	}

	public MatchResult(ResultParam param){

		param.fill(this);
	}


}
