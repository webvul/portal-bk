package com.kii.beehive.portal.store.entity.trigger;

import com.fasterxml.jackson.annotation.JsonUnwrapped;


public class TriggerTarget {


	private TagSelector selector;

	@JsonUnwrapped
	public TagSelector getSelector() {
		return selector;
	}

	public void setSelector(TagSelector selector) {
		this.selector = selector;
	}
	private TargetAction command;


	@JsonUnwrapped
	public TargetAction getCommand() {
		return command;
	}

	public void setCommand(TargetAction command) {
		this.command = command;
	}
}
