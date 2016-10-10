package com.kii.beehive.portal.entitys;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;

public class ThingID {

	private Long thingID;

	private String vendorThingID;

	public ThingID(GlobalThingInfo th){

		this.thingID=th.getId();
		this.vendorThingID=th.getVendorThingID();
	}

	public Long getThingID() {
		return thingID;
	}

	public void setThingID(Long thingID) {
		this.thingID = thingID;
	}

	public String getVendorThingID() {
		return vendorThingID;
	}

	public void setVendorThingID(String vendorThingID) {
		this.vendorThingID = vendorThingID;
	}
}
