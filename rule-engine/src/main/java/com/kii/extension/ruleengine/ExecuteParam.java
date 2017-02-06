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

	private Map<String,Object> businessParams=new HashMap<>();


	public ExecuteParam(){

		fireSource="Schedule";
		relationThing="NONE";
		fireTime=new Date();

	}

	public ExecuteParam(CommResult params){


		String relThings=null;
		String source=null;
		if(params.getFireSource()!=null){
			relThings=String.valueOf(params.getFireSource().getCurrThings());
			source=params.getFireSource().getStatus().name();
		}else{
			relThings="Schedule";
			source="NONE";
		}

		fireSource=relThings;
		relationThing=source;

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


	public Map<String, Object> getBusinessParams() {
		return businessParams;
	}
	
	public Map<String,String> getBusinessParamsInStr(){
		Map<String,String> paramMap=new HashMap<>();
		
		businessParams.forEach((k,v)->{
			paramMap.put(k,String.valueOf(v));
		});
		
		return paramMap;
	}


	public String getDelayParam(int idx) {

		return String.valueOf(businessParams.get("delay_"+idx));

	}
}
