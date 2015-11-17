package com.kii.beehive.portal.store.entity;

import java.util.HashSet;
import java.util.Set;

import com.kii.extension.sdk.entity.KiiEntity;


public class TagIndex extends KiiEntity {

	private String tagType;
	
	private String displayName;
	
	private String description;

	private Set<String> globalThings=new HashSet<>();

	private Set<String> kiiAppIDs=new HashSet<>();

	@Override
	public String getId() {
		return tagType +"-" + displayName;
	}

	public Set<String> getGlobalThings() {
		return globalThings;
	}

	public void setGlobalThings(Set<String> globalThings) {
		this.globalThings = globalThings;
	}

	public Set<String> getKiiAppIDs() {
		return kiiAppIDs;
	}

	public void setKiiAppIDs(Set<String> kiiAppIDs) {
		this.kiiAppIDs = kiiAppIDs;
	}

	public String getTagType() {
		return tagType;
	}

	public void setTagType(String tagType) {
		this.tagType = tagType;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
