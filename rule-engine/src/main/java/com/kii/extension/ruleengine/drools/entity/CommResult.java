package com.kii.extension.ruleengine.drools.entity;

import java.util.HashMap;
import java.util.Map;

public abstract  class CommResult implements WithTrigger{


	protected    String triggerID;

	private String delay=null;

	private  Map<String,String> params=new HashMap<>();


	private CurrThing  currThing;

	private boolean enable=false;


	public void  fill(CommResult target){

		target.setEnable(enable);
		target.setFireSource(currThing);
		target.setDelay(delay);
		target.setParams(new HashMap<>(params));
		target.triggerID=this.triggerID;

	}

	public void setFireSource(CurrThing curr){

		this.currThing=curr;
	}

	public CurrThing getFireSource(){
		return currThing;
	}

	public String getTriggerID() {
		return triggerID;
	}


	@Override
	public String toString() {
		return super.getClass().getName()+":{" +
				"triggerID='" + triggerID + '\'' +
				", delay=" + delay +
				", params=" + params +
				'}';
	}


	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getDelay() {
		return delay;
	}

	public void setDelay(String delay) {
		this.delay = delay;
	}

	public void setParam(String key,Object value){
		params.put(key,String.valueOf(value));
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}
}
