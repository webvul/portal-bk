package com.kii.extension.ruleengine.drools.entity;

public class TriggerResult extends  CommResult{


	public TriggerResult(MatchResult  result ){

		result.fill(this);
	}

	public TriggerResult(String triggerID ){

		this.triggerID=triggerID;
	}


	public MatchResult getMatchResult(){
		MatchResult result=new MatchResult(triggerID);

		this.fill(result);

		return result;
	}

}
