package com.kii.beehive.portal.web.entity;

public class SearchRestBean {

	private String venderThingID;
	private Long startDate;
	private Long endDate;
	private String intervalField;
	private String operatorField;
	private String[] fields;
	private int size;
	private int from;

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
}
