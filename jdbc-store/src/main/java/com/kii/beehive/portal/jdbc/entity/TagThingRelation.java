package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class TagThingRelation{

	private Long id;
	
	private Long tagID;

	private Long thingID;
	
	public final static String ID = "id";
	public final static String TAG_ID = "tag_id";
	public final static String THING_ID = "thing_id";
	
	public TagThingRelation() {}
	
	public TagThingRelation(Long tagID, Long thingID) {
		super();
		this.tagID = tagID;
		this.thingID = thingID;
	}

	@JdbcField(column=ID)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@JdbcField(column = TAG_ID)
	public Long getTagID() {
		return tagID;
	}

	public void setTagID(Long tagID) {
		this.tagID = tagID;
	}

	@JdbcField(column = THING_ID)
	public Long getThingID() {
		return thingID;
	}

	public void setThingID(Long thingID) {
		this.thingID = thingID;
	}
}
