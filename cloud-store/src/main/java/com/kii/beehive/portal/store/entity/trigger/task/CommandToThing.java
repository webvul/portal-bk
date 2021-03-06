package com.kii.beehive.portal.store.entity.trigger.task;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.beehive.portal.store.entity.trigger.ExecuteTarget;
import com.kii.beehive.portal.store.entity.trigger.TagSelector;
import com.kii.extension.sdk.entity.thingif.ThingCommand;

public class CommandToThing extends ExecuteTarget {

	private TagSelector selector;


	private List<String> thingList;
	
	
	private ThingCommand command;
	
	
	public List<String> getThingList() {
		return thingList;
	}
	
	public void setThingList(List<String> thingList) {
		this.thingList = thingList;
	}
	
	public ThingCommand getCommand() {
		return command;
	}

	public void setCommand(ThingCommand command) {
		this.command = command;
	}

	@JsonUnwrapped
	public TagSelector getSelector() {
		return selector;
	}

	public void setSelector(TagSelector selector) {
		this.selector = selector;
	}

	@Override
	public TargetType getType() {
		return TargetType.ThingCommand;
	}
}
