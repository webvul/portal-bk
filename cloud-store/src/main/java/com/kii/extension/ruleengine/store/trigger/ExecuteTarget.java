package com.kii.extension.ruleengine.store.trigger;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.kii.extension.ruleengine.store.trigger.target.CallBusinessFunction;
import com.kii.extension.ruleengine.store.trigger.target.CallHttpApi;
import com.kii.extension.ruleengine.store.trigger.target.CommandToThing;
import com.kii.extension.ruleengine.store.trigger.target.CommandToThingInGW;
import com.kii.extension.ruleengine.store.trigger.target.SettingParameter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "type",
		defaultImpl=CommandToThing.class )
@JsonSubTypes({
		@JsonSubTypes.Type(value = CommandToThing.class,name="ThingCommand"),
		@JsonSubTypes.Type(value = CallHttpApi.class,name="HttpApiCall"),
		@JsonSubTypes.Type(value=CommandToThingInGW.class,name="ThingCommandInGW"),
		@JsonSubTypes.Type(value=SettingParameter.class,name="SettingParameter"),
		@JsonSubTypes.Type(value=CallBusinessFunction.class,name="CallBusinessFunction")
})
public abstract class ExecuteTarget {


	public abstract String  getType();


	private String delay;


	private boolean  check;

	public boolean isDoubleCheck(){
		return check;
	};

	public void setDoubleCheck(boolean sign){
		this.check=sign;
	};

	public String getDelay() {
		return delay;
	}

	public void setDelay(String delay) {
		this.delay=delay;
	}


}
