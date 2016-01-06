package com.kii.beehive.portal.store.entity.trigger;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.extension.sdk.entity.KiiEntity;
import com.kii.extension.sdk.entity.thingif.TriggerWhen;

public class GroupTriggerRuntimeState extends TriggerRuntimeState{



	private MemberState  memberState=new MemberState();

	private TriggerGroupPolicy.TriggerGroupPolicyType policy;

	private int criticalNumber;

//	private String relationTriggerID;

	private boolean currentStatus;

	private TriggerWhen whenType=TriggerWhen.CONDITION_TRUE;


	private Map<String,KiiTriggerCol> currThingTriggerMap=new HashMap<>();

	public Map<String, KiiTriggerCol> getCurrThingTriggerMap() {
		return currThingTriggerMap;
	}

	public void setCurrThingTriggerMap(Map<String,KiiTriggerCol> currThingTriggerMap) {
		this.currThingTriggerMap = currThingTriggerMap;
	}

	public void addThingTriggerInfo(String thingID,KiiTriggerCol triggerCol){
		this.currThingTriggerMap.put(thingID,triggerCol);
	}

//	public String getRelationTriggerID() {
//		return relationTriggerID;
//	}
//
//	public void setRelationTriggerID(String relationTriggerID) {
//		this.relationTriggerID = relationTriggerID;
//	}

	public TriggerGroupPolicy.TriggerGroupPolicyType getPolicy() {
		return policy;
	}

	public void setPolicy(TriggerGroupPolicy.TriggerGroupPolicyType policy) {
		this.policy = policy;
	}

	public int getCriticalNumber() {
		return criticalNumber;
	}

	public void setCriticalNumber(int criticalNumber) {
		this.criticalNumber = criticalNumber;
	}

	public TriggerWhen getWhenType() {
		return whenType;
	}

	public void setWhenType(TriggerWhen whenType) {
		this.whenType = whenType;
	}

	public boolean isCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(boolean currentStatus) {
		this.currentStatus = currentStatus;
	}

	@JsonUnwrapped(prefix="member-")
	public MemberState getMemberState() {
		return memberState;
	}

	public void setMemberState(MemberState memberState) {
		this.memberState = memberState;
	}

	@JsonIgnore
	public boolean checkPolicy(){

		return memberState.checkPolicy(policy,criticalNumber);

	}

	private String listenerID;
	
	public void setListenerID(String listenerID) {
		this.listenerID = listenerID;
	}

	public String getListenerID() {
		return listenerID;
	}
}
