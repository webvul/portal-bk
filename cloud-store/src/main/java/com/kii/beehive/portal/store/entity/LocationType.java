package com.kii.beehive.portal.store.entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum LocationType {




	building,floor,partition,area;

	private static Pattern pattern=Pattern.compile("^(\\d{2})(\\d{2})(\\w)-([A-Z][\\d]{2})$");


	public static LocationType getTypeByLocation(String location){


		int length=location.length();

		switch(length){
			case 2:return building;
			case 4:return floor;
			case 5:return partition;
			case 9:return area;
			default:
				throw new IllegalArgumentException("invalid location format");
		}

	}


	public String getLevelSeq(String location){

		Matcher matcher=pattern.matcher(location);

		if(matcher.find()) {
			return matcher.group(this.ordinal() + 1);
		}else{
			return null;
		}


	}

	public static LocationType getNextLevel(LocationType type){
		if(type == area){
			throw new IllegalArgumentException("this is least level");
		}
		return LocationType.values()[type.ordinal()+1];
	}
}
