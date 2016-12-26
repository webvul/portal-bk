package com.kii.extension.ruleengine.store.trigger;

import org.apache.commons.lang3.StringUtils;

public enum BusinessObjType {
	User, TriggerGroup,Thing,Business,Global;
	
	public String getFullID(String id,String name){
		StringBuilder sb=new StringBuilder(this.name());
		
		if(StringUtils.isNotBlank(name)){
			sb.append("-").append(name);
		}
		
		sb.append(".").append(id);
		
		return sb.toString();
	}

}
