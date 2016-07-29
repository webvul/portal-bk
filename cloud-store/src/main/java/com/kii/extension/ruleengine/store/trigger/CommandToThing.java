package com.kii.extension.ruleengine.store.trigger;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.extension.sdk.entity.thingif.ThingCommand;

public class CommandToThing implements   ExecuteTarget {

	private TagSelector selector;

	private List<String> vendorThingIdList=new ArrayList<>(); // for local rule

	private ThingCommand command;

	private String delay;

	private boolean  check;

	public boolean isDoubleCheck(){
		return check;
	}

	public void setDoubleCheck(boolean sign){
		this.check=sign;
	}

	public List<String> getVendorThingIdList() {
		return vendorThingIdList;
	}

	public void setVendorThingIdList(List<String> vendorThingIdList) {
		this.vendorThingIdList = vendorThingIdList;
	}

	@Override
	public String getDelay() {
		return delay;
	}

	@Override
	public void setDelay(String delay) {
		this.delay=delay;
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
	public String getType() {
		return "ThingCommand";
	}
}
