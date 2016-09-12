package com.kii.extension.ruleengine.store.trigger.result;

import com.kii.extension.sdk.entity.KiiEntity;
import com.kii.extension.sdk.entity.thingif.ThingCommand;

public class CommandResponse extends KiiEntity implements TriggerResult {


	private String triggerID;

	private String result;

	private ThingCommand command;




	public CommandResponse(){

	}


	public ThingCommand getCommand() {
		return command;
	}

	public void setCommand(ThingCommand command) {
		this.command = command;
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
