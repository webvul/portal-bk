package com.kii.beehive.portal.web.entity;

public class SearchRestBean {

	private String venderThingID;
	private Long startDate;
	private Long endDate;
	private String intervalField;
	private String[] avgFields;

	public String getVenderThingID() {
		return venderThingID;
	}

	public void setVenderThingID(String venderThingID) {
		this.venderThingID = venderThingID;
	}

	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}

	public String getIntervalField() {
		return intervalField;
	}

	public void setIntervalField(String intervalField) {
		this.intervalField = intervalField;
	}

	public String[] getAvgFields() {
		return avgFields;
	}

	public void setAvgFields(String[] avgFields) {
		this.avgFields = avgFields;
	}
}
