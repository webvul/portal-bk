package com.kii.beehive.portal.store.entity.trigger;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.extension.sdk.entity.thingif.TriggerWhen;

public class GroupTriggerRuntimeState extends TriggerRuntimeState{



//	private PortalMemberState portalMemberState =new PortalMemberState();

	private TriggerGroupPolicy.TriggerGroupPolicyType policy;

	private int criticalNumber;

	private boolean currentStatus;

	private TriggerWhen whenType=TriggerWhen.CONDITION_TRUE;


	private String businessTriggerID;

	public String getBusinessTriggerID() {
		return businessTriggerID;
	}

	public void setBusinessTriggerID(String businessTriggerID) {
		this.businessTriggerID = businessTriggerID;
	}


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

	private String listenerID;
	
	public void setListenerID(String listenerID) {
		this.listenerID = listenerID;
	}

	public String getListenerID() {
		return listenerID;
	}

	@JsonIgnore
	public void checkPolicy(Map<String,Boolean> memberMap){


		long accessNum=memberMap.values().stream().filter(v->v).count();

		long sumNum=memberMap.values().stream().filter(v->v!=null).count();

		boolean sign=false;
		switch(policy){
			case All:
				sign= sumNum==accessNum;
				break;
			case Any:
				sign= accessNum>0;
				break;
			case Some:
				sign= accessNum>=criticalNumber;
				break;
			case Percent:
				sign= 100*accessNum>=(criticalNumber*sumNum);
				break;
			default:
				sign=false;
		}

		this.currentStatus=getWhenType().checkStatus(currentStatus,sign);
	}

	public BeehiveTriggerType getType() {
		return BeehiveTriggerType.Group;
	}
}
