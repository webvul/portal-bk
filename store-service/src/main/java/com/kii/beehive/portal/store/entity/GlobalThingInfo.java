package com.kii.beehive.portal.store.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import com.kii.extension.sdk.entity.KiiEntity;

public class GlobalThingInfo extends KiiEntity {

	private String globalThingID;

	private String vendorThingID;

	private Set<String> tags=new HashSet<>();

	private String appID;

	private String locationID;

	private String type;

	private Date modifyDate;

	private Date createDate;



	@Override
	public String getId() {
		return globalThingID;
	}

	@Override
	public void setId(String globalThingID) {
		this.globalThingID = globalThingID;
	}


	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
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
