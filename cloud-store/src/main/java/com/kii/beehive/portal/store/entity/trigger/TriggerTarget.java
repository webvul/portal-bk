package com.kii.beehive.portal.store.entity.trigger;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;


public class TriggerTarget {

	private List<Long> thingList=new ArrayList<>();

	private List<String> tagList=new ArrayList<>();

	private boolean isAndExpress=false;

	private TargetAction command;

	public List<Long> getThingList() {
		return thingList;
	}

	public void setThingList(List<Long> thingList) {
		this.thingList = thingList;
	}

	@JsonIgnore
	public void addThingList(long thingID){
		this.thingList.add(thingID);
	}

	public List<String> getTagList() {
		return tagList;
	}

	public void setTagList(List<String> tagList) {
		this.tagList = tagList;
	}

	@JsonIgnore
	public void addTag(String tagName){
		this.tagList.add(tagName);
	}

	public boolean isAndExpress() {
		return isAndExpress;
	}

	public void setAndExpress(boolean isAndExpress) {
		isAndExpress = isAndExpress;
	}

	@JsonUnwrapped
	public TargetAction getCommand() {
		return command;
	}

	public void setCommand(TargetAction command) {
		this.command = command;
	}
}
