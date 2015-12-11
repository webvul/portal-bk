package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class TagThingRelation extends DBEntity {
	
	private long id;
	
	private long tagID;

	private long thingID;
	
	public final static String ID = "id";
	public final static String TAG_ID = "tag_id";
	public final static String THING_ID = "thing_id";
	
	public TagThingRelation() {}
	
	public TagThingRelation(long tagID, long thingID) {
		super();
		this.tagID = tagID;
		this.thingID = thingID;
	}

	@JdbcField(column="id")
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	@JdbcField(column = TAG_ID)
	public long getTagID() {
		return tagID;
	}

	public void setTagID(long tagID) {
		this.tagID = tagID;
	}

	@JdbcField(column = THING_ID)
	public long getThingID() {
		return thingID;
	}

	public void setThingID(long thingID) {
		this.thingID = thingID;
	}
}
