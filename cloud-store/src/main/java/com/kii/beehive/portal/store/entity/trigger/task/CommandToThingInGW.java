package com.kii.beehive.portal.store.entity.trigger.task;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.beehive.portal.store.entity.trigger.ExecuteTarget;
import com.kii.beehive.portal.store.entity.trigger.VendorThingList;
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
	public TargetType getType() {
		return TargetType.ThingCommandInGW;
	}
}
