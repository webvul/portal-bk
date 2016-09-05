package com.kii.beehive.portal.jdbc.dao;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.ThingLocationRelation;

public class ThingLocQuery {

	private String type;

	private String location;

	private boolean includeSub;


	public String fillSubQuery(Map<String,Object> paramList){

		String sqlTmp="";
		if(StringUtils.isNoneBlank(getType())){
			sqlTmp+=" and th."+ GlobalThingInfo.THING_TYPE+" =  :type ";
			paramList.put("type",getType());
		}


		if(includeLocation()){
			sqlTmp+="and loc."+ ThingLocationRelation.LOCATION+getLocationQuery();
			paramList.put("loc",getLocationParam());
		}

		return sqlTmp;
	}


	public String fillLocQuery(Map<String,Object> paramList){

		String sqlTmp="";

		if(includeLocation()){
			sqlTmp+="and loc."+ ThingLocationRelation.LOCATION+getLocationQuery();
			paramList.put("loc",getLocationParam());
		}

		return sqlTmp;
	}

	public String fillTypeQuery(Map<String,Object> paramList){

		String sqlTmp="";

		if(StringUtils.isNoneBlank(getType())){
			sqlTmp+=" and th."+ GlobalThingInfo.THING_TYPE+" =  :type ";
			paramList.put("type",getType());
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
			return " like :loc ";
		}else{
			return " = :loc ";
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
