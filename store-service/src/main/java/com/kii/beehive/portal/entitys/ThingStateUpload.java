package com.kii.beehive.portal.entitys;

import java.util.Date;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

public class ThingStateUpload {

	private Long globalThingID;
	private String appID;
	private String thingID;;
	private ThingStatus state;

	//TODO:fill this field in kiicloud's thing status change trigger
	private Date timestamp=new Date();

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public ThingStatus getState() {
		return state;
	}

	public void setState(ThingStatus state) {
		this.state = state;
	}

	public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}

	public String getThingID() {
		return thingID;
	}

	public void setThingID(String thingID) {
		this.thingID = thingID;
	}

	public Long getGlobalThingID() {
		return globalThingID;
	}

	public void setGlobalThingID(Long globalThingID) {
		this.globalThingID = globalThingID;
	}
}
