package com.kii.beehive.portal.jdbc.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.ThingLocationRelation;

public class ThingLocQuery {

	private String type;

	private String location;

	private boolean includeSub;


	public String fillSubQuery(List<Object> paramList){

		String sqlTmp="";
		if(StringUtils.isNoneBlank(getType())){
			sqlTmp+=" and th."+ GlobalThingInfo.THING_TYPE+" =  ? ";
			paramList.add(getType());
		}


		if(includeLocation()){
			sqlTmp+="and loc."+ ThingLocationRelation.LOCATION+getLocationQuery();
			paramList.add(getLocationParam());
		}

		return sqlTmp;
	}

	private boolean includeLocation(){
		return StringUtils.isNoneBlank(location);
	}


	private String getLocationParam() {

		if(includeSub) {
			return location+"%";
		}else{
			return location;
		}
	}

	private String getLocationQuery(){
		if(includeSub) {
			return " like ? ";
		}else{
			return " = ? ";
		}
	}

	@JsonProperty("locationPrefix")
	public String getLocation() {
		return location;
	}

	@JsonProperty("includeSubLevel")
	public boolean isIncludeSub() {
		return includeSub;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setIncludeSub(boolean includeSub) {
		this.includeSub = includeSub;
	}


	@JsonProperty("type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
