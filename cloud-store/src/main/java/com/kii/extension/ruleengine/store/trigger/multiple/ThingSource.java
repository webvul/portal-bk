package com.kii.extension.ruleengine.store.trigger.multiple;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.extension.ruleengine.store.trigger.Express;

public class ThingSource implements SourceElement {

	private String thingID;

	@JsonUnwrapped
	private Express express=new Express();


	public Express getExpress() {
		return express;
	}

	public void setExpress(Express express) {
		this.express = express;
	}

	public String getThingID() {
		return thingID;
	}

	public void setThingID(String thingID) {
		this.thingID = thingID;
	}

	@Override
	public SourceElementType getType() {
		return SourceElementType.thing;
	}
}
