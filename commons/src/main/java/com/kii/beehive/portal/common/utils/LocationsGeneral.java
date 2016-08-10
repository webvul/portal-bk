package com.kii.beehive.portal.common.utils;

import org.springframework.util.StringUtils;

public  class LocationsGeneral {

	private LocationsGeneral(){

	}

	public static final  String general(String blockNo) {
		return general(blockNo,null,null,null);
	}

	public static final String general(String blockNo,String floorNo) {
		return general(blockNo,floorNo,null,null);
	}



	public static final String general(String blockNo,String floorNo,String type) {
		return general(blockNo,floorNo,type,null);
	}

	public static final String general(String blockNo,String floorNo,String type,String number){


		StringBuilder sb=new StringBuilder();

		if(StringUtils.isEmpty(blockNo)){
			return "*";
		}

		sb.append(blockNo).append("-");

		if(StringUtils.isEmpty(floorNo)){
			sb.append("*");
			return sb.toString();
		}

		sb.append(floorNo).append("-");

		if(StringUtils.isEmpty(type)){
			sb.append("*");
			return sb.toString();
		}

		sb.append(type).append("-");

		if(StringUtils.isEmpty(number)){
			sb.append("*");
			return sb.toString();
		}

		sb.append(number);
		return sb.toString();

	}
}
