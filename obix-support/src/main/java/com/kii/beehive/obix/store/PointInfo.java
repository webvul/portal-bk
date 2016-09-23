package com.kii.beehive.obix.store;

public class PointInfo {

	private String fieldName;

	private Object value;

	private String location;

	private ObixPointDetail  schema;


	public PointInfo(){

	}

	public PointInfo(ObixPointDetail  detail,String name,Object val){

		fieldName=name;
		value=val;

		this.schema=detail;
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

	public ObixPointDetail getSchema() {
		return schema;
	}

	public void setSchema(ObixPointDetail schema) {
		this.schema = schema;
	}
}
