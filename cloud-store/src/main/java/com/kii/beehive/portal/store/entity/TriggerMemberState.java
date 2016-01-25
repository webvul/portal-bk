package com.kii.beehive.portal.store.entity;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;


public class TriggerMemberState {

	private Map<String,Boolean> memberStatusMap=new HashMap<>();

	@JsonAnyGetter
	public Map<String, Boolean> getMemberStatusMap() {
		return memberStatusMap;
	}

	@JsonAnySetter
	public void setMemberStatus(String key,Boolean value) {
		this.memberStatusMap.put(key,value);
	}

	@JsonIgnore
	public boolean getMemberStatus(String thingID) {
		return memberStatusMap.get(thingID);
	}


}
