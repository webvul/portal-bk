package com.kii.extension.sdk.entity.thingif;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
