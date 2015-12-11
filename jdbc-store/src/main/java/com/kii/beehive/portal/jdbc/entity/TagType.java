package com.kii.beehive.portal.jdbc.entity;

public enum TagType {

	System,Location,Custom;

	public String getTagName(String displayName){
		return this.name()+"-"+displayName;
	}

}
