package com.kii.beehive.portal.jdbc.helper;

public class SqlCondition {
	
	
	private String fieldName;
	
//	private int additionIdx;
	
	private Object value;
	
	private Number start;
	
	private Number end;
	
	private SqlExpress express;
	
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
	
	public Number getStart() {
		return start;
	}
	
	public void setStart(Number start) {
		this.start = start;
	}
	
	public Number getEnd() {
		return end;
	}
	
	public void setEnd(Number end) {
		this.end = end;
	}
	
	public SqlExpress getExpress() {
		return express;
	}
	
	public void setExpress(SqlExpress express) {
		this.express = express;
	}
	
	public enum SqlExpress {
		
		Eq,Like,Range;
		
	}
}
