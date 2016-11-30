package com.kii.extension.ruleengine.store.trigger.target;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.TagSelector;
import com.kii.extension.sdk.entity.thingif.ThingCommand;

public class CommandToThing extends ExecuteTarget {

	private TagSelector selector;


	private ThingCommand command;


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
	public String getType() {
		return "ThingCommand";
	}
}
