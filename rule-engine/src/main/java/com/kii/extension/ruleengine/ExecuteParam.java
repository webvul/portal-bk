package com.kii.extension.ruleengine;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.kii.extension.ruleengine.drools.entity.CommResult;

public class ExecuteParam {

	private String triggerID;

	private final Date fireTime;

	private final  String fireSource;

	private final  String relationThing;

	private Map<String,String> businessParams=new HashMap<>();


	public ExecuteParam(){

		fireSource="Schedule";
		relationThing="NONE";
		fireTime=new Date();

	}

	public ExecuteParam(CommResult params){
		if(params.getFireSource()!=null){
			relationThing=params.getFireSource().getCurrThing();
			fireSource=params.getFireSource().getStatus().name();
		}else{
			fireSource="Other";
			relationThing="NONE";
		}

		this.triggerID=params.getTriggerID();
		fireTime=new Date();
		this.businessParams=params.getParams();
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


	public String getFireSource() {
		return fireSource;
	}


	public String getRelationThing() {
		return relationThing;
	}


	public Map<String, String> getBusinessParams() {
		return businessParams;
	}


	public String getDelayParam(int idx) {

		return businessParams.get("delay_"+idx);

	}
}
