package com.kii.beehive.portal.web.entity;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.beehive.portal.jdbc.dao.ThingLocationDao;

public class ThingIDsForReportWithGroup {

	private String groupName;

	private ThingLocationDao.ThingIDs  thingIDArray;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@JsonUnwrapped
	public ThingLocationDao.ThingIDs getThingIDArray() {
		return thingIDArray;
	}

	public void setThingIDArray(ThingLocationDao.ThingIDs thingIDArray) {
		this.thingIDArray = thingIDArray;
	}
}
