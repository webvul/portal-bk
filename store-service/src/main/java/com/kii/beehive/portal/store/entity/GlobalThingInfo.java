package com.kii.beehive.portal.store.entity;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class GlobalThingInfo {

	@Id
	private String globalThingID;

	@Indexed
	private String vendorThingID;

	@Indexed
	private String[]  tags;

	private String appID;

	private String locationID;

	private String type;

	@LastModifiedDate
	private Date modifyDate;

	@CreatedDate
	private Date createDate;




	public String getGlobalThingID() {
		return globalThingID;
	}

	public void setGlobalThingID(String globalThingID) {
		this.globalThingID = globalThingID;
	}


	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}

	public String getLocationID() {
		return locationID;
	}

	public void setLocationID(String locationID) {
		this.locationID = locationID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVendorThingID() {
		return vendorThingID;
	}

	public void setVendorThingID(String vendorThingID) {
		this.vendorThingID = vendorThingID;
	}
}
