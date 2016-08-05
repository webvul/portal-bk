package com.kii.beehive.portal.web.entity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;

public class ThingDetail {

	private GlobalThingInfo  thing;

	private List<String> locations;

	private String locs;

	public ThingDetail(){

	}

	public ThingDetail(Map<String,Object> param){

		this.thing= (GlobalThingInfo) param.get("thing");

		this.locs= (String) param.get("loc");

		this.locations= Arrays.asList(StringUtils.split(locs,","));
	}


	@JsonUnwrapped
	public GlobalThingInfo getThing() {
		return thing;
	}

	public void setThing(GlobalThingInfo thing) {
		this.thing = thing;
	}

	public List<String> getLocations() {
		return locations;
	}

	public void setLocations(List<String> locations) {
		this.locations = locations;
	}

	public String getLocs() {
		return locs;
	}

	public void setLocs(String locs) {
		this.locs = locs;
	}
}
