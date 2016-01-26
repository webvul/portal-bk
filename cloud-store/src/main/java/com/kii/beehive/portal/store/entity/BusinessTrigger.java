package com.kii.beehive.portal.store.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.extension.sdk.entity.KiiEntity;
import com.kii.extension.sdk.entity.thingif.TriggerWhen;
import com.kii.extension.sdk.query.Condition;

public class BusinessTrigger extends KiiEntity{

	private boolean enable;

//	private Set<String> thingIDList=new HashSet<>();
	private String targetID;

	private Condition condition;

	private String listenerID;

	private TriggerMemberState states=new TriggerMemberState();

	private Map<String,Object> additionParam=new HashMap<>();

	private TriggerWhen  when;

	public TriggerWhen getWhen() {
		return when;
	}

	public void setWhen(TriggerWhen when) {
		this.when = when;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	@JsonUnwrapped
	public TriggerMemberState getMemberStates() {
		return states;
	}

	public void setMemberStates(TriggerMemberState states) {
		this.states = states;
	}

	public Map<String, Object> getAdditionParam() {
		return additionParam;
	}

	public void setAdditionParam(Map<String, Object> additionParam) {
		this.additionParam = additionParam;
	}

	@JsonIgnore
	public void addAdditionParam(String key,Object value){

		this.additionParam.put(key,value);
	}

	public String getTargetID() {
		return targetID;
	}

	public void setTargetID(String targetID) {
		this.targetID = targetID;
	}

	@JsonIgnore
	public Set<String> getThingIDSet() {
		return states.getMemberStatusMap().keySet();
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public String getListenerID() {
		return listenerID;
	}

	public void setListenerID(String listenerID) {
		this.listenerID = listenerID;
	}
}
