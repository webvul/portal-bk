package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;
import com.kii.beehive.portal.jdbc.annotation.JdbcFieldType;

public class TagIndex extends DBEntity {


	private TagType tagType;
	private String displayName;
	private String description;
	private Long count;
	
	public final static String TAG_ID = "tag_id";
	public final static String TAG_TYPE = "tag_type";
	public final static String DISPLAY_NAME = "display_name";
	public final static String DESCRIPTION = "description";
	public final static String THING_COUNT = "count";
	
	@Override
	@JdbcField(column="tag_id")
	public long getId(){
		return super.getId();
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
	
	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public static TagIndex generCustomTagIndex(String name){
		TagIndex tag=new TagIndex();
		tag.tagType=TagType.Custom;
		tag.displayName=name;
		return tag;
	}

	public static TagIndex generTagIndex(TagType tagType, String displayName, String description) {
		TagIndex tag=new TagIndex();
		tag.tagType=tagType;
		tag.displayName=displayName;
		tag.description = description;
		return tag;
	}
	
}
