package com.kii.beehive.portal.web.entity;

import java.util.Arrays;

public class ThingInput {

	private String thingID;

	private String vendorThingID;

	private String[] tags;

	private String locationID;


	public String getThingID() {
		return thingID;
	}

	public void setThingID(String thingID) {
		this.thingID = thingID;
	}

	public String getVendorThingID() {
		return vendorThingID;
	}

	public void setVendorThingID(String vendorThingID) {
		this.vendorThingID = vendorThingID;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public String getLocationID() {
		return locationID;
	}

	public void setLocationID(String locationID) {
		this.locationID = locationID;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ThingInput [thingID=");
		builder.append(thingID);
		builder.append(", vendorThingID=");
		builder.append(vendorThingID);
		builder.append(", tags=");
		builder.append(Arrays.toString(tags));
		builder.append(", locationID=");
		builder.append(locationID);
		builder.append("]");
		return builder.toString();
	}
}
