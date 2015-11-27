package com.kii.beehive.portal.web.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.TagIndex;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.help.PortalException;

public class ThingInput extends GlobalThingInfo {

//	private String globalThingID;
//
//	private String vendorThingID;
//
//	private String kiiAppID;
//
//	private String password;
//
	private Set<String> tagNames=new HashSet<>();
////
//	private String type;
//
//	private String status;
//
//	private Map<String,Object> custom=new HashMap<>();

//	@JsonAnySetter
//	public void setCustom(String key,Object val){
//		this.custom.put(key,val);
//	}
//
//	public String getGlobalThingID() {
//		return globalThingID;
//	}
//
//	public void setGlobalThingID(String globalThingID) {
//		this.globalThingID = globalThingID;
//	}

	@JsonProperty("tags")
	public Set<String> getInputTags() {
		return tagNames;
	}


	public void setInputTags(Set<String> tags) {
		this.tagNames = tags;
	}


	public void verifyInput(){

		if(StringUtils.isEmpty(this.getVendorThingID())){
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,"VendorThingID is empty", HttpStatus.BAD_REQUEST);
		}

		if(StringUtils.isEmpty(this.getKiiAppID())){
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,"KiiAppID is empty", HttpStatus.BAD_REQUEST);
		}

//		if(StringUtils.isEmpty(this.getPassword())){
//			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,"Password is empty", HttpStatus.BAD_REQUEST);
//		}
	}

//	public String getVendorThingID() {
//		return vendorThingID;
//	}
//
//	public void setVendorThingID(String vendorThingID) {
//		this.vendorThingID = vendorThingID;
//	}
//
//	public String getType() {
//		return type;
//	}
//
//	public void setType(String type) {
//		this.type = type;
//	}
//
//	public String getStatus() {
//		return status;
//	}
//
//	public void setStatus(String status) {
//		this.status = status;
//	}
//
//	public void setTags(List<TagIndex> tags) {
//		this.tags = tags;
//	}
//
//	public String getKiiAppID() {
//		return kiiAppID;
//	}
//
//	public void setKiiAppID(String kiiAppID) {
//		this.kiiAppID = kiiAppID;
//	}
//
//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}
//
//	public Map<String, Object> getCustom() {
//		return custom;
//	}
//
//	public void setCustom(Map<String, Object> custom) {
//		this.custom = custom;
//	}
//
//	@Override
//	public String toString() {
//		StringBuilder builder = new StringBuilder();
//		builder.append("ThingInput [globalThingID=");
//		builder.append(globalThingID);
//		builder.append(", vendorThingID=");
//		builder.append(vendorThingID);
//		builder.append(", kiiAppID=");
//		builder.append(kiiAppID);
//		builder.append(", tags=");
//		builder.append(tags);
//		builder.append(", type=");
//		builder.append(type);
//		builder.append(", status=");
//		builder.append(status);
//		builder.append("]");
//		return builder.toString();
//	}
}
