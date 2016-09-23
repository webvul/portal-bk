package com.kii.extension.ruleengine.drools.entity;

public class TriggerResult extends  CommResult{


	public TriggerResult(MatchResult  result ){

		this.triggerID=result.getTriggerID();
		this.delay=result.getDelay();
		this.params.putAll(result.getParams());
	}

	public TriggerResult(String triggerID ){

		this.triggerID=triggerID;
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
