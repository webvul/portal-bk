package com.kii.beehive.portal.web.entity;

public class CreatedThing {
	/*

	    "vendorThingID": "vendorThingDemo1452225320889",
    "uri": "kiicloud://things/th.aba700e36100-4558-5e11-bb5b-082ac14a",
    "thingID": "th.aba700e36100-4558-5e11-bb5b-082ac14a"

	 */

	private String vendorThingID;

	private String uri;

	private String thingID;

	public String getVendorThingID() {
		return vendorThingID;
	}

	public void setVendorThingID(String vendorThingID) {
		this.vendorThingID = vendorThingID;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getThingID() {
		return thingID;
	}

	public void setThingID(String thingID) {
		this.thingID = thingID;
	}
}
