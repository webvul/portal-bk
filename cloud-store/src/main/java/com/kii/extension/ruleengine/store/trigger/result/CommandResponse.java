package com.kii.extension.ruleengine.store.trigger.result;

import com.kii.extension.sdk.entity.thingif.ThingCommand;

public class CommandResponse extends  TriggerResult {



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



	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
