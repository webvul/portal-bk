package com.kii.beehive.obix.store;

public class PointInfo implements  EntityInfo {

	private String fieldName;

	private Object value;

	private String location;

	private ObixPointDetail  schema;

	private String thingID;


	public PointInfo(){

	}

	public PointInfo(ObixPointDetail  detail,String name,Object val,String thingID){

		fieldName=name;
		value=val;
		this.thingID=thingID;

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

	public String getThingID() {
		return thingID;
	}

	public void setThingID(String thingID) {
		this.thingID = thingID;
	}



}
