package com.kii.beehive.portal.store.entity.trigger;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonUnwrapped;


public class TriggerTarget {

	private List<String> thingList=new ArrayList<>();

	private List<String> tagList=new ArrayList<>();

	private boolean isAnd=true;

	private TargetAction command;

	public List<String> getThingList() {
		return thingList;
	}

	public void setThingList(List<String> thingList) {
		this.thingList = thingList;
	}

	public List<String> getTagList() {
		return tagList;
	}

	public void setTagList(List<String> tagList) {
		this.tagList = tagList;
	}

	public boolean isAnd() {
		return isAnd;
	}

	public void setAnd(boolean and) {
		isAnd = and;
	}

	@JsonUnwrapped
	public TargetAction getCommand() {
		return command;
	}

	public void setCommand(TargetAction command) {
		this.command = command;
	}
}
