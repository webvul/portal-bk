package com.kii.beehive.obix.store;

import org.apache.commons.lang3.StringUtils;

public enum PointDataType {

	Int,Float,Enum,Boolean,String,Datetime;
	
	public static PointDataType getInstance(String type) {

		return  PointDataType.valueOf(StringUtils.capitalize(type));
	}
}
