package com.kii.beehive.portal.web.entity;

public class SearchRestBean {

	private String vendorThingID;
	private String[] vendorThingIDs;
	private Long startDate;
	private Long endDate;
	private String intervalField;
	private int unit;
	private String operatorField;
	private String order;
	private String orderField;
	private String[] fields;
	private String indexType;
	private String dateField;
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

	public String getIndexType() {
		return indexType;
	}

	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}

	public String getDateField() {
		return dateField;
	}

	public void setDateField(String dateField) {
		this.dateField = dateField;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getOrderField() {
		return orderField;
	}

	public void setOrderField(String orderField) {
		this.orderField = orderField;
	}
}
