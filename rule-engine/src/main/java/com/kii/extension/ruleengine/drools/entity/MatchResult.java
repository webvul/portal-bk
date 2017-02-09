package com.kii.extension.ruleengine.drools.entity;

import java.util.Objects;

public class MatchResult extends CommResult{


	public MatchResult(String triggerID){
		super.triggerID=triggerID;
	}

	public MatchResult(ResultParam param){

		param.fill(this);
	}
	
	
	public void setResult(boolean result) {
		super.getParams().put("result",result);
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CommResult that = (CommResult) o;
		return Objects.equals(triggerID, that.triggerID);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(triggerID);
	}
}
