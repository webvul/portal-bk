package com.kii.beehive.business.ruleengine.entitys;

import java.util.List;

import com.kii.extension.ruleengine.store.trigger.TagSelector;
import com.kii.extension.sdk.entity.thingif.ThingCommand;

public class ThingCommandExecuteParam {
	
	private long userID;
	
	
	private TagSelector selector;
	
	private List<Long> thingList;
	
	private ThingCommand command;
	
	public TagSelector getSelector() {
		return selector;
	}
	
	public void setSelector(TagSelector selector) {
		this.selector = selector;
	}
	
	public List<Long> getThingList() {
		return thingList;
	}
	
	public void setThingList(List<Long> thingList) {
		this.thingList = thingList;
	}
	
	public long getUserID() {
		return userID;
	}
	
	public void setUserID(long userID) {
		this.userID = userID;
	}
	
	
	public ThingCommand getCommand() {
		return command;
	}
	
	public void setCommand(ThingCommand command) {
		this.command = command;
	}
}
