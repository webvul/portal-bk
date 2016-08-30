package com.kii.beehive.obix.store;

public class PointInfo {

	private String fieldName;

	private Object value;

	private String location;

	private String thingSchema;

	public String getThingSchema() {
		return thingSchema;
	}

	public void setThingSchema(String thingSchema) {
		this.thingSchema = thingSchema;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
