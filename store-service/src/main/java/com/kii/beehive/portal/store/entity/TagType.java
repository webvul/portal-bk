package com.kii.beehive.portal.store.entity;

/**
 * @deprecated replaced with jdbc TagType
 */
public enum TagType {
	Location,System,Custom;


	public String getTagName(String sub){
		return this.name()+"-"+sub;
	}
}
