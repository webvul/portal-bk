package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;
import com.kii.beehive.portal.jdbc.annotation.JdbcFieldType;

public class TagThingRelation {

	private int tagID;

	private int thingID;

	@JdbcField(column = "tag_id")
	public int getTagID() {
		return tagID;
	}

	public void setTagID(int tagID) {
		this.tagID = tagID;
	}

	@JdbcField(column = "thing_id")
	public int getThingID() {
		return thingID;
	}

	public void setThingID(int thingID) {
		this.thingID = thingID;
	}
}
