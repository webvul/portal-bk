package com.kii.beehive.obix.store;

import com.kii.beehive.industrytemplate.PointDetail;

public enum PointDataType {

	Int,Float,Enum,Boolean,String,Datetime;
	
	public static PointDataType getInstance(PointDetail detail) {
		
		switch (detail.getType()) {
			case NUMBER:
				return detail.getPrecise() == 0 ? Int : Float;
			case BOOLEAN:
				return Boolean;
			case STRING:
				return String;
			default:
				return Int;
		}
	}
}
