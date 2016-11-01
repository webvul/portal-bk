package com.kii.extension.sdk.entity.trigger;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.extension.sdk.entity.thingif.ThingCommand;

public class TargetCommand extends ThingCommand {

	private String target;

	@JsonIgnore
	public void setThingID(String thingID){
		target="THING:"+thingID;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
}
