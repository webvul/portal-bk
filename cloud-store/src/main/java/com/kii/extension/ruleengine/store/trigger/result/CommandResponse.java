package com.kii.extension.ruleengine.store.trigger.result;

import com.kii.extension.sdk.entity.thingif.ThingCommand;

public class CommandResponse extends  TriggerResult {



	private String result;

	private ThingCommand command;

	private String fullKiiThingID;
	
	private String appID;

	private long thingID;

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

	public String getFullKiiThingID() {
		return fullKiiThingID;
	}

	public void setFullKiiThingID(String thingID) {
		this.fullKiiThingID = thingID;

	}

	public long getThingID() {
		return thingID;
	}

	public void setThingID(long thingID) {
		this.thingID = thingID;
	}

	public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}
}
