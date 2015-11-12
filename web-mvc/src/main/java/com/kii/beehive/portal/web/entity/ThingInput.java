package com.kii.beehive.portal.web.entity;

import java.util.List;

import com.kii.beehive.portal.store.entity.TagIndex;

public class ThingInput {

	private String globalThingID;

	private String vendorThingID;

	private List<TagIndex> tags;

	private String type;

	private String status;

	public String getGlobalThingID() {
		return globalThingID;
	}

	public void setGlobalThingID(String globalThingID) {
		this.globalThingID = globalThingID;
	}

	public List<TagIndex> getTags() {
		return tags;
	}

	public String getVendorThingID() {
		return vendorThingID;
	}

	public void setVendorThingID(String vendorThingID) {
		this.vendorThingID = vendorThingID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public void setTags(List<TagIndex> tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ThingInput [globalThingID=");
		builder.append(globalThingID);
		builder.append(", vendorThingID=");
		builder.append(vendorThingID);
		builder.append(", tags=");
		builder.append(tags);
		builder.append(", type=");
		builder.append(type);
		builder.append(", status=");
		builder.append(status);
		builder.append("]");
		return builder.toString();
	}
}
