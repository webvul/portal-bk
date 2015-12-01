package com.kii.beehive.portal.jdbc.entity;

public class TagIndex extends DBEntity {


	private String tagType;

	private String displayName;

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
}
