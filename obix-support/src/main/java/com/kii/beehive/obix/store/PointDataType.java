package com.kii.beehive.obix.store;

public enum PointDataType {

	Int,Float,Enum,Boolean,String,Datetime;
	
	public static PointDataType getInstance(String type) {

		return  PointDataType.valueOf(type.toLowerCase());
	}
}
