package com.kii.extension.ruleengine.store.trigger.target;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.VendorThingList;
import com.kii.extension.sdk.entity.thingif.ThingCommand;

public class CommandToThingInGW extends ExecuteTarget {


	private VendorThingList venderThingList=new VendorThingList();


	private ThingCommand command;

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
