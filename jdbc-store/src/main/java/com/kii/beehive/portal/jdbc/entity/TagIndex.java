package com.kii.beehive.portal.jdbc.entity;

import java.util.List;

import org.springframework.util.StringUtils;

import com.kii.beehive.portal.jdbc.annotation.DisplayField;
import com.kii.beehive.portal.jdbc.annotation.JdbcField;
import com.kii.beehive.portal.jdbc.annotation.JdbcFieldType;

public class TagIndex extends BusinessEntity {


	private TagType tagType;
	private String displayName;
	private String description;
	private String fullTagName;

	private List<Long> things;

	public final static String TAG_ID = "tag_id";
	public final static String TAG_TYPE = "tag_type";
	public final static String FULL_TAG_NAME="full_tag_name";
	public final static String DISPLAY_NAME = "display_name";
	public final static String DESCRIPTION = "description";
	public final static String THINGS = "things";


	public TagIndex(){

	}
	
	public TagIndex(TagType tagType, String displayName, String description) {
		super();
		this.tagType = tagType;
		this.displayName = displayName;
		this.description = description;
		this.setFullTagName(tagType, displayName);
	}

	private void setFullTagName(TagType tagType, String displayName) {
		if(tagType == null || displayName == null) {
			this.fullTagName = null;
		} else {
			this.fullTagName = tagType.getTagName(displayName);
		}
	}

	public TagIndex(String fullTagName){
		this.setFullTagName(fullTagName);
	}
	@Override
	@JdbcField(column=TAG_ID)
	public Long getId(){
		return super.getId();
	}


	@JdbcField(column=FULL_TAG_NAME)
	public String getFullTagName(){
		return fullTagName;
	}

	public void setFullTagName(String fullTagName){
		String[] arrays= StringUtils.split(fullTagName,"-");
		this.tagType=TagType.valueOf(arrays[0]);
		this.displayName=arrays[1];
		this.fullTagName=fullTagName;
	}
	
	@JdbcField(column = TAG_TYPE,type= JdbcFieldType.EnumStr)
	public TagType getTagType() {
		return tagType;
	}

	public void setTagType(TagType tagType) {
		this.tagType = tagType;

		// sync fullTagName
		this.setFullTagName(this.tagType, this.displayName);
	}

	@JdbcField(column=DISPLAY_NAME)
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;

		// sync fullTagName
		this.setFullTagName(this.tagType, this.displayName);
	}
	
	@JdbcField(column=DESCRIPTION)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@DisplayField(column=THINGS)
	public List<Long> getThings() {
		return things;
	}

	public void setThings(List<Long> things) {
		this.things = things;
	}

	public static TagIndex generCustomTagIndex(String name){
		TagIndex tag=new TagIndex();
		tag.tagType=TagType.Custom;
		tag.displayName=name;
		return tag;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TagIndex [tagType=");
		builder.append(tagType);
		builder.append(", displayName=");
		builder.append(displayName);
		builder.append(", description=");
		builder.append(description);
		builder.append(", fullTagName=");
		builder.append(fullTagName);
		builder.append(", things=");
		builder.append(things);
		builder.append("]");
		return builder.toString();
	}
	
}
