package com.kii.extension.ruleengine.store.trigger;

import org.apache.commons.lang3.StringUtils;

public enum BusinessObjType {
	User, TriggerGroup,Thing,Business,Global,Context;
	
	public String getFullID(String id,String name){
		StringBuilder sb=new StringBuilder(this.name());
		
		if(StringUtils.isNotBlank(name)){
			sb.append("-").append(name);
		}
		
		sb.append(".").append(id);
		
		return sb.toString();
	}
	
	public static  BusinessObjType getType(String fullID){
		int idx1=fullID.indexOf(".");
		int idx2=fullID.indexOf("-");
		int idx=idx1;
		if(idx2!=-1){
			idx=idx2;
		}
		return BusinessObjType.valueOf(StringUtils.substring(fullID,0,idx));
	}

}
