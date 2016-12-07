package com.kii.beehive.portal.store.entity;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AdditionFields {
	
	
	private Map<String,Object> additionFields=new HashMap<>();
	
	@JsonIgnore
	public void addString(int idx,String value){
		additionFields.put("str"+idx,value);
	}
	
	
	@JsonIgnore
	public void addInteger(int idx,Integer value){
		additionFields.put("int"+idx,value);
	}
	
	@JsonIgnore
	public String getStringByID(int idx){
		
		return (String) additionFields.get("str"+idx);
	}
	
	@JsonIgnore
	public Integer getIntegerByID(int idx){
		
		return (Integer) additionFields.get("int"+idx);
	}
	
	public Map<String, Object> getAdditionFields() {
		return additionFields;
	}
	
	public void setAdditionFields(Map<String, Object> additionFields) {
		this.additionFields = additionFields;
	}
}
