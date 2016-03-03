package com.kii.extension.sdk.entity.thingif;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class OnBoardingParam {



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

	@JsonIgnore
	public void setUserID(String userID) {
		this.owner = "USER:"+userID;
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

	public void addThingProperty(String key, Object val) {
		this.thingProperties.put(key,val);
	}


}
