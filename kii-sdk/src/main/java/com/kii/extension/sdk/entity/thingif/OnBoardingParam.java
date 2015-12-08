package com.kii.extension.sdk.entity.thingif;

import java.util.HashMap;
import java.util.Map;

public class OnBoardingParam {

	/*
	  "thingPassword" : "xxxxx",
  "owner" : "user:1576cc4f512b-9e8b-5e11-b966-0e713954",
  "vendorThingID" : "dummy",
  "thingType" : "LIGHT",
  "thingProperties" : {
    "_vendor":"Phillips"
  }
	 */

	private String vendorThingID;

	private String thingType;

	private String owner;

	private String thingID;

	private String thingPassword;

	private Map<String,Object> thingProperties=new HashMap<>();

	private LayoutPosition layoutPosition;

	public LayoutPosition getLayoutPosition() {
		return layoutPosition;
	}

	public void setLayoutPosition(LayoutPosition layoutPosition) {
		this.layoutPosition = layoutPosition;
	}

	public String getVendorThingID() {
		return vendorThingID;
	}

	public void setVendorThingID(String vendorThingID) {
		this.vendorThingID = vendorThingID;
	}

	public String getThingType() {
		return thingType;
	}

	public void setThingType(String thingType) {
		this.thingType = thingType;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getThingID() {
		return thingID;
	}

	public void setThingID(String thingID) {
		this.thingID = thingID;
	}

	public String getThingPassword() {
		return thingPassword;
	}

	public void setThingPassword(String thingPassword) {
		this.thingPassword = thingPassword;
	}

	public Map<String, Object> getThingProperties() {
		return thingProperties;
	}

	public void setThingProperties(Map<String, Object> thingProperties) {
		this.thingProperties = thingProperties;
	}
}
