package com.kii.beehive.portal.store.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum LocationType {

	building("^(\\d{2})"),floor("^(\\d{2}\\d{2})"),partition("^(\\d{4}\\w)"),area("^(\\d{4}\\w-[A-Z][\\d]{2})$");


	private Pattern pattern;


	LocationType(String regexp){
		pattern=Pattern.compile(regexp);
	}


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




	public static List<String> getLevelInList(String location){

		List<String> list=new ArrayList<>();

		LocationType type=getTypeByLocation(location);
		int level=type.ordinal();

		for(int i=0;i<=level;i++){
			list.add(LocationType.values()[i].getLevelSeq(location));

		}

		return list;
	}


	public String getLevelSeq(String location){

		Matcher matcher=this.pattern.matcher(location);

		if(matcher.find()) {
			return matcher.group(1);
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
