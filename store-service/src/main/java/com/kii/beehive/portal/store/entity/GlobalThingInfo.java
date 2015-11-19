package com.kii.beehive.portal.store.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import com.kii.extension.sdk.entity.KiiEntity;

public class GlobalThingInfo extends KiiEntity {

	private String globalThingID;

	private String vendorThingID;

	private String type;
	
	private String password;

	private String status;
	
	private Set<String> tags=new HashSet<>();

	private String kiiAppID;

	private Date statusUpdatetime;

	private Map<String,Object> custom=new HashMap<>();

	@Override
	public String getId() {
		return globalThingID;
	}

	@Override
	public void setId(String globalThingID) {
		this.globalThingID = globalThingID;
	}


	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	public String getKiiAppID() {
		return kiiAppID;
	}

	public void setKiiAppID(String kiiAppID) {
		this.kiiAppID = kiiAppID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVendorThingID() {
		return vendorThingID;
	}

	public void setVendorThingID(String vendorThingID) {
		this.vendorThingID = vendorThingID;
	}

	public String getGlobalThingID() {
		return globalThingID;
	}

	public void setGlobalThingID(String globalThingID) {
		this.globalThingID = globalThingID;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getStatusUpdatetime() {
		return statusUpdatetime;
	}

	public void setStatusUpdatetime(Date statusUpdatetime) {
		this.statusUpdatetime = statusUpdatetime;
	}

	public Map<String, Object> getCustom() {
		return custom;
	}

	public void setCustom(Map<String, Object> custom) {
		this.custom = custom;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@JsonAnySetter
	public void setCustom(String key,Object val){
		this.custom.put(key,val);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GlobalThingInfo [globalThingID=");
		builder.append(globalThingID);
		builder.append(", vendorThingID=");
		builder.append(vendorThingID);
		builder.append(", tags=");
		builder.append(tags);
		builder.append(", kiiAppID=");
		builder.append(kiiAppID);
		builder.append(", type=");
		builder.append(type);
		builder.append(", status=");
		builder.append(status);
		builder.append(", statusUpdatetime=");
		builder.append(statusUpdatetime);
		builder.append("]");
		return builder.toString();
	}
	
	
}
