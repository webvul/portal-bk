package com.kii.beehive.industrytemplate;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

public class Thing {

	private String thingID;

	private String schema;

	private String location;

	private ThingStatus  status;

	private long dbID;

	public Thing(){

	}

	public Thing(GlobalThingInfo thing){

		this.thingID=thing.getVendorThingID();
		this.schema=thing.getSchemaName();

		this.status=new ThingStatus();

		this.status.setFields(thing.getStatus());

		this.dbID=thing.getId();

	}

	public Long getDBId(){
		return dbID;
	}

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
