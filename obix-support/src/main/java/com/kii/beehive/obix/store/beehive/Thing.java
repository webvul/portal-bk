package com.kii.beehive.obix.store.beehive;

import com.kii.extension.sdk.entity.thingif.ThingStatus;

public class Thing {

	private String thingID;

	private String schema;

	private String location;

	private ThingStatus  status;

	public String getThingID() {
		return thingID;
	}

	public void setThingID(String thingID) {
		this.thingID = thingID;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public ThingStatus getStatus() {
		return status;
	}

	public void setStatus(ThingStatus status) {
		this.status = status;
	}
}
