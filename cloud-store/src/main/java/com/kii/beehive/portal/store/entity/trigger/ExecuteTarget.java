package com.kii.beehive.portal.store.entity.trigger;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.kii.beehive.portal.store.entity.trigger.task.CallBusinessFunction;
import com.kii.beehive.portal.store.entity.trigger.task.CallHttpApi;
import com.kii.beehive.portal.store.entity.trigger.task.CommandToThing;
import com.kii.beehive.portal.store.entity.trigger.task.CommandToThingInGW;
import com.kii.beehive.portal.store.entity.trigger.task.SettingTriggerGroupParameter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "type",
		defaultImpl=CommandToThing.class )
@JsonSubTypes({
		@JsonSubTypes.Type(value = CommandToThing.class,name="ThingCommand"),
		@JsonSubTypes.Type(value = CallHttpApi.class,name="HttpApiCall"),
		@JsonSubTypes.Type(value=CommandToThingInGW.class,name="ThingCommandInGW"),
		@JsonSubTypes.Type(value=SettingTriggerGroupParameter.class,name="SettingParameter"),
		@JsonSubTypes.Type(value=CallBusinessFunction.class,name="CallBusinessFunction")
})
public abstract class ExecuteTarget {

	
	public  enum TargetType{
		
		ThingCommand,HttpApiCall,ThingCommandInGW,SettingParameter,CallBusinessFunction;
		
	}

	public abstract TargetType  getType();


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
