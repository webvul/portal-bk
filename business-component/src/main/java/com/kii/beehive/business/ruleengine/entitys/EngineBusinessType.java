package com.kii.beehive.business.ruleengine.entitys;

public enum EngineBusinessType {
	
	Business,Context;
	
	
	public String getFullID(String id, String name) {
		StringBuilder sb = new StringBuilder(this.name());
		
		sb.append("-").append(name);
		
		sb.append(".").append(id);
		
		return sb.toString();
	}
	
	
}
