package com.kii.extension.ruleengine.sdk.query;

import javax.xml.bind.annotation.XmlElement;

public class LatlonPoint {
	
	private float lat;
	
	private float lon;
	
	private String type;
	
	
	@XmlElement(name="_type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public float getLat() {
		return lat;
	}

	public void setLat(float lat) {
		this.lat = lat;
	}

	public float getLon() {
		return lon;
	}

	public void setLon(float lon) {
		this.lon = lon;
	}
	
	

}
