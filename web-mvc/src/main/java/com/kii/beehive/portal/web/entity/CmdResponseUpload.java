package com.kii.beehive.portal.web.entity;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.extension.sdk.entity.thingif.ThingCommand;

public class CmdResponseUpload {


	private ThingCommand command;

	private Map<String,Object> expandFields=new HashMap<>();

	@JsonUnwrapped
	public ThingCommand getCommand() {
		return command;
	}

	public void setCommand(ThingCommand command) {
		this.command = command;
	}

	public Map<String, Object> getExpandFields() {
		return expandFields;
	}

	public void setExpandFields(Map<String, Object> expandFields) {
		this.expandFields = expandFields;
	}

	@JsonAnySetter
	public void addExpandField(String name,Object val){
		expandFields.put(name,val);
	}

}
