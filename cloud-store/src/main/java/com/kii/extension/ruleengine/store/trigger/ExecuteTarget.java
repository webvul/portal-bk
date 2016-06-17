package com.kii.extension.ruleengine.store.trigger;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonUnwrapped;


public class ExecuteTarget {


	private TagSelector selector;

	private TargetAction command;


	@JsonUnwrapped
	public TagSelector getSelector() {
		return selector;
	}

	public void setSelector(TagSelector selector) {
		this.selector = selector;
	}


	@JsonUnwrapped
	public TargetAction getCommand() {
		return command;
	}

	public void setCommand(TargetAction command) {
		this.command = command;
	}
}
