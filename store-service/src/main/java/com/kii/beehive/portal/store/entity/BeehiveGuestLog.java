package com.kii.beehive.portal.store.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.extension.sdk.entity.KiiEntity;

public class BeehiveGuestLog extends PortalEntity{

	private Date createTime;

	private String party3thName;


	private Map<String,Object> customFields=new HashMap<>();

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getParty3thName() {
		return party3thName;
	}

	public void setParty3thName(String party3thName) {
		this.party3thName = party3thName;
	}

	@JsonUnwrapped
	public Map<String, Object> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(Map<String, Object> customFields) {
		this.customFields = customFields;
	}

	@JsonAnySetter
	public void setCustomField(String key,Object value){
		customFields.put(key,value);

	}
}
