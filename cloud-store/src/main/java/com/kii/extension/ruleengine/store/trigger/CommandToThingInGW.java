package com.kii.extension.ruleengine.store.trigger;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.extension.sdk.entity.thingif.ThingCommand;

public class CommandToThingInGW implements   ExecuteTarget {


	private VendorThingList venderThingList=new VendorThingList();


	private ThingCommand command;

	private String delay;

	private boolean check;

	public boolean isDoubleCheck() {
		return check;
	}

	public void setDoubleCheck(boolean sign) {
		this.check = sign;
	}


	@Override
	public String getDelay() {
		return delay;
	}

	@Override
	public void setDelay(String delay) {
		this.delay = delay;
	}

	public ThingCommand getCommand() {
		return command;
	}

	public void setCommand(ThingCommand command) {
		this.command = command;
	}

	@JsonUnwrapped
	public VendorThingList getSelector() {
		return venderThingList;
	}

	public void setSelector(VendorThingList selector) {
		this.venderThingList = selector;
	}

	@Override
	public String getType() {
		return "ThingCommandInGW";
	}
}
