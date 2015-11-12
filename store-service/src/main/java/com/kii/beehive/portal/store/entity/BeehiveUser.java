package com.kii.beehive.portal.store.entity;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.extension.sdk.entity.KiiEntity;

public class BeehiveUser extends KiiEntity {


	public static final String PREFIX = "custom-";

	private String beehiveUserID;

	private String kiiUserID;

	private String terminal;

	private String userName;

	private String role;

	private String company;

	private Map<String,Object> customFields=new HashMap<>();


	public String getBeehiveUserID() {
		return beehiveUserID;
	}

	public void setBeehiveUserID(String beehiveUserID) {
		this.beehiveUserID = beehiveUserID;
	}

	public String getKiiUserID() {
		return kiiUserID;
	}

	public void setKiiUserID(String kiiUserID) {
		this.kiiUserID = kiiUserID;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}


	@JsonUnwrapped(prefix = PREFIX)
	public Map<String, Object> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(Map<String, Object> customFields) {
		this.customFields = customFields;
	}

	@JsonAnySetter
	public void setCustomField(String key,Object value){
		this.customFields.put(key,value);
	}
}
