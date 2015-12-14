package com.kii.beehive.portal.store.entity;

public enum TagType {
	Location,System,Custom;


	public String getTagName(String sub){
		return this.name()+"-"+sub;
	}
}
