package com.kii.beehive.portal.store.entity.trigger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TriggerSource {

	private List<String> thingList=new ArrayList<>();

	private List<String> tagCollect=new ArrayList<>();

	private boolean isAndExpress;

	private Map<String,Boolean> relationTag=new HashMap<>();

	private TriggerGroupPolicy groupPolicy=TriggerGroupPolicy.All;

	private int criticalNumber=0;

	public List<String> getTagCollect() {
		return tagCollect;
	}

	public void setTagCollect(List<String> tagCollect) {
		this.tagCollect = tagCollect;
	}

	public boolean isAndExpress() {
		return isAndExpress;
	}

	public void setAndExpress(boolean andExpress) {
		isAndExpress = andExpress;
	}

	public int getCriticalNumber() {
		return criticalNumber;
	}

	public void setCriticalNumber(int criticalNumber) {
		this.criticalNumber = criticalNumber;
	}

	public TriggerGroupPolicy getGroupPolicy() {
		return groupPolicy;
	}

	public void setGroupPolicy(TriggerGroupPolicy groupPolicy) {
		this.groupPolicy = groupPolicy;
	}

	public List<String> getThingList() {
		return thingList;
	}

	public void setThingList(List<String> thingList) {
		this.thingList = thingList;
	}

	public Map<String, Boolean> getRelationTag() {
		return relationTag;
	}

	public void setRelationTag(Map<String, Boolean> relationTag) {
		this.relationTag = relationTag;
	}
}
