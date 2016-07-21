package com.kii.beehive.portal.web.entity;

public class SearchRestBean {

	private String vendorThingID;
	private String[] vendorThingIDs;
	private Long startDate;
	private Long endDate;
	private String intervalField;
	private int unit;
	private String operatorField;
	private String[] fields;
	private int size;
	private int from;

	public String getVendorThingID() {
		return vendorThingID;
	}

	public void setVendorThingID(String vendorThingID) {
		this.vendorThingID = vendorThingID;
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

	public String[] getFields() {
		return fields;
	}

	public void setFields(String[] fields) {
		this.fields = fields;
	}

	public String getOperatorField() {
		return operatorField;
	}

	public void setOperatorField(String operatorField) {
		this.operatorField = operatorField;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	public String[] getVendorThingIDs() {
		return vendorThingIDs;
	}

	public void setVendorThingIDs(String[] vendorThingIDs) {
		this.vendorThingIDs = vendorThingIDs;
	}
}
