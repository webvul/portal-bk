package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class ThingLocationRelation  extends DBEntity{

	public static final String ID="id";
	public static final String THING_ID="thing_id";
	public static final String LOCATION="location";

	private Long thingId;

	private String location;

	@JdbcField(column= LOCATION)
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}



	@Override
	@JdbcField(column = ID)
	public Long getId() {
		return super.getId();
	}

	@Override
	public void setId(Long id) {
		super.setId(id);
	}

	@JdbcField(column = THING_ID)
	public Long getThingId() {
		return thingId;
	}

	public void setThingId(Long thingId) {
		this.thingId = thingId;
	}

}
