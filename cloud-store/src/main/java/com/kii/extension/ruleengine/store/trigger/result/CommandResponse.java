package com.kii.extension.ruleengine.store.trigger.result;

import com.kii.extension.sdk.entity.KiiEntity;

public class CommandResponse extends KiiEntity implements TriggerResult {


	private String triggerID;

	private String result;

	public CommandResponse(){

	}

	public CommandResponse(String cmdResult) {
		super();
		result=cmdResult;
	}


	@Override
	public String getType() {
		return "command";
	}


	public String getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(String triggerID) {
		this.triggerID = triggerID;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
