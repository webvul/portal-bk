package com.kii.beehive.portal.web.entity;

import java.util.List;

public class ThingIDsForReportWithDoubleGroup {


	private String groupName;

	private List<ThingIDsForReportWithGroup> subGroupArray;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<ThingIDsForReportWithGroup>  getSubGroupArray() {
		return subGroupArray;
	}

	public void setSubGroupArray(List<ThingIDsForReportWithGroup>  subGroupArray) {
		this.subGroupArray = subGroupArray;
	}
}
