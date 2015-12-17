package com.kii.beehive.portal.jdbc.entity;

import org.springframework.util.StringUtils;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;
import com.kii.beehive.portal.jdbc.annotation.JdbcFieldType;

public class TagIndex extends DBEntity {


	private TagType tagType;
	private String displayName;
	private String description;
	private String fullTagName;

	
	public final static String TAG_ID = "tag_id";
	public final static String TAG_TYPE = "tag_type";
	public final static String FULL_TAG_NAME="full_tag_name";
	public final static String DISPLAY_NAME = "display_name";
	public final static String DESCRIPTION = "description";


	public TagIndex(){

	}

	public TagIndex(String fullTagName){
		String[] arrays= StringUtils.split(fullTagName,"-");
		tagType=TagType.valueOf(arrays[0]);
		displayName=arrays[1];
	}
	
	@Override
	@JdbcField(column="tag_id")
	public long getId(){
		return super.getId();
	}


	@JdbcField(column=FULL_TAG_NAME)
	public String getFullTagName(){
		return fullTagName;
	}

	public void setFullTagName(String name){
		this.fullTagName=name;
	}
	
	@JdbcField(column = TAG_TYPE,type= JdbcFieldType.EnumStr)
	public TagType getTagType() {
		return tagType;
	}

	public void setTagType(TagType tagType) {
		this.tagType = tagType;
	}

	@JdbcField(column=DISPLAY_NAME)
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	@JdbcField(column=DESCRIPTION)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public static TagIndex generCustomTagIndex(String name){
		TagIndex tag=new TagIndex();
		tag.tagType=TagType.Custom;
		tag.displayName=name;
		return tag;
	}
	
}
