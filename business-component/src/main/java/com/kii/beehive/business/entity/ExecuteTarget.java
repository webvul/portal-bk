package com.kii.beehive.business.entity;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * Created by USER on 6/28/16.
 */
public class ExecuteTarget {


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
