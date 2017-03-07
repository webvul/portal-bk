package com.kii.beehive.business.ruleengine.entitys;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class EngineBusinessObj {
	
	private static final Pattern pattern = Pattern.compile("(([^\\.\\-]+)(\\-([^.]+))?)\\.([\\S]+)");
	private String objID;
	private Map<String,Object> state;
	private EngineBusinessType type = EngineBusinessType.Business;
	private String groupName;
	
	public static EngineBusinessObj getInstance(String fullBuinessID) {
		
		Matcher match = pattern.matcher(fullBuinessID);
		
		if (match.find()) {
			EngineBusinessObj obj = new EngineBusinessObj();
			obj.type = EngineBusinessType.valueOf(match.group(2));
			obj.groupName = match.group(4);
			obj.objID = match.group(5);
			
			return obj;
			
		} else {
			return null;
		}
	}
	
	@JsonIgnore
	public String generFullID() {
		return type.getFullID(objID, groupName);
	}
	
	@JsonIgnore
	public String getGroupName() {
		return groupName;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	@JsonIgnore
	public EngineBusinessType getType() {
		return type;
	}
	
	public void setType(EngineBusinessType type) {
		this.type = type;
	}
	
	public String getObjID() {
		return objID;
	}
	
	public void setObjID(String businessID) {
		this.objID = businessID;
	}
	
	public Map<String, Object> getState() {
		return state;
	}
	
	public void setState(Map<String, Object> state) {
		this.state = state;
	}
}
