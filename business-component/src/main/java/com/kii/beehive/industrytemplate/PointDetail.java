package com.kii.beehive.industrytemplate;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PointDetail {

	/*
	        "displayNameCN": "设置温度",
        "enum": null,
        "maximum": 30,
        "minimum": 10,
        "type": "float",
        "unit": "˚C",
        "enumType": null
	 */

	private String displayNameCN;

	private Map<String,Object> enumMap=new HashMap<>();

	private Map<Object,String> valueMap=new HashMap<>();

	private Number  maximum;

	private Number minimum;
	
	private FieldType type;

	private String unit;

	private EnumType enumType;
	
	private int precise;
	
	public int getPrecise() {
		return precise;
	}
	
	public void setPrecise(int precise) {
		this.precise = precise;
	}
	
	public String getDisplayNameCN() {
		return displayNameCN;
	}
	
	public void setDisplayNameCN(String displayNameCN) {
		this.displayNameCN = displayNameCN;
	}

	@JsonProperty("enum")
	public Map<String, Object> getEnumMap() {
		return enumMap;
	}

	public void setEnumMap(Map<String, Object> enumMap) {

		this.enumMap = enumMap;

		if(enumMap!=null){
			enumMap.forEach((k,v)->{
				valueMap.put(v,k);
			});
		}
	}

	@JsonIgnore
	public Map<Object,String> getValueMap(){

		return valueMap;
	}

	public Number getMaximum() {
		return maximum;
	}

	public void setMaximum(Number maximum) {
		this.maximum = maximum;
	}

	public Number getMinimum() {
		return minimum;
	}

	public void setMinimum(Number minimum) {
		this.minimum = minimum;
	}
	
	public FieldType getType() {
		return type;
	}
	
	public void setType(FieldType type) {
		this.type = type;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public EnumType getEnumType() {
		return enumType;
	}

	public void setEnumType(EnumType enumType) {
		this.enumType = enumType;
	}
	
	public enum EnumType {
		BOOLEAN, NUMBER;
	}
	
	public enum FieldType {
		
		NUMBER, BOOLEAN, STRING;
	}
}
