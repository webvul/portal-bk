package com.kii.extension.ruleengine.store.trigger.result;


import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.kii.extension.sdk.entity.KiiEntity;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = HttpCallResponse.class,name="httpResponse"),
		@JsonSubTypes.Type(value= CommandResponse.class,name="command")
})
public abstract class  TriggerResult extends KiiEntity {


	abstract  public String getType();


	private Date fireTime;


	private String triggerID;


	private String fireSource;

	private String relationThing;

	private Map<String,String> businessParam;


	private ExceptionInfo  exceptionInfo;

	public void  bindException(Throwable  throwable){
		exceptionInfo=new ExceptionInfo(throwable);
	}


	public ExceptionInfo getExceptionInfo() {
		return exceptionInfo;
	}

	public void setExceptionInfo(ExceptionInfo exceptionInfo) {
		this.exceptionInfo = exceptionInfo;
	}

	public Map<String, String> getBusinessParam() {
		return businessParam;
	}

	public void setBusinessParam(Map<String, String> businessParam) {
		this.businessParam = businessParam;
	}

	public String getFireSource() {
		return fireSource;
	}

	public void setFireSource(String fireSource) {
		this.fireSource = fireSource;
	}

	public String getRelationThing() {
		return relationThing;
	}

	public void setRelationThing(String relationThing) {
		this.relationThing = relationThing;
	}

	public String getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(String triggerID) {
		this.triggerID = triggerID;
	}


	public Date getFireTime() {
		return fireTime;
	}

	public void setFireTime(Date fireTime) {
		this.fireTime = fireTime;
	}
}
