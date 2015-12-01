package com.kii.beehive.portal.store.entity;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.util.Strings;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.kii.beehive.portal.exception.TagFormatInvalidException;
import com.kii.extension.sdk.entity.KiiEntity;


public class TagIndex extends PortalEntity {

	private TagType tagType;
	
	private String displayName;
	
	private String description;

	private Set<String> globalThings=new HashSet<>();

	private Set<String> kiiAppIDs=new HashSet<>();

	public static TagIndex generCustomTagIndex(String name){


		TagIndex tag=new TagIndex();

		tag.tagType=TagType.Custom;
		tag.displayName=name;

		tag.fillID();


		return tag;
	}

	public void fillID(){
		setId(tagType.getTagName(displayName));
	}

	public void verify(){


		if(StringUtils.isEmpty(getDisplayName())){
			throw new TagFormatInvalidException("DisplayName");
		}
	}


	public String getTagName(){
		return getId();
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

	public TagType getTagType() {
		return tagType;
	}

	public void setTagType(TagType tagType) {
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
