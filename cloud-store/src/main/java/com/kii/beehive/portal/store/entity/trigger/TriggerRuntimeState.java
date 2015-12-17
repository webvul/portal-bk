package com.kii.beehive.portal.store.entity.trigger;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.extension.sdk.entity.KiiEntity;
import com.kii.extension.sdk.entity.thingif.TriggerWhen;

public class TriggerRuntimeState extends KiiEntity{


	private Map<String,Boolean> memberStatusMap=new HashMap<>();

	private TriggerGroupPolicy policy;

	private int criticalNumber;

	private String relationTriggerID;

	private boolean currentStatus;

	private TriggerWhen whenType=TriggerWhen.CONDITION_TRUE;

	public String getRelationTriggerID() {
		return relationTriggerID;
	}

	public void setRelationTriggerID(String relationTriggerID) {
		this.relationTriggerID = relationTriggerID;
	}

	public TriggerGroupPolicy getPolicy() {
		return policy;
	}

	public void setPolicy(TriggerGroupPolicy policy) {
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

	@JsonAnySetter
	public void setMemberStatus(String thingID,boolean sign){

		memberStatusMap.put(thingID,sign);
	}

	@JsonAnyGetter
	public Map<String,Boolean> getMemberStatusMap(){
		return memberStatusMap;
	}


	@JsonIgnore
	public boolean checkPolicy(){


		long accessNum=memberStatusMap.values().stream().filter(v->v).count();

		long sumNum=memberStatusMap.size();

		switch(policy){
			case All:
				return sumNum==accessNum;
			case Any:
				return accessNum>0;
			case Some:
				return accessNum>criticalNumber;
			default:
				return false;
		}

	}

}
