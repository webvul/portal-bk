package com.kii.beehive.portal.web.entity;


import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.beehive.portal.store.entity.CustomProperty;
import com.kii.beehive.portal.store.entity.PortalSyncUser;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;

public class SyncUserRestBean extends PortalSyncUser {

	private String teamName;
	
	public SyncUserRestBean(){

	}

	public SyncUserRestBean(PortalSyncUser user){
		BeanUtils.copyProperties(user, this, "customFields", "customField");

		this.setCustomFields(user.getCustomFields());
	}


	public Map<String,Object> getCustom() {

		return super.getCustomFields().getOriginFields();
	}


	@JsonIgnore
	public CustomProperty getCustomFields() {

		return super.getCustomFields();
	}

	public void setCustomFields(CustomProperty properties) {

		super.setCustomFields(properties);
	}

	@JsonAnySetter
	public void setCustomField(String key,Object value){
		if(key.startsWith("custom.")) {
			key=key.substring(7);
			super.setCustomField(key, value);
		}
		if(key.equals("custom")){
			CustomProperty prop=new CustomProperty((Map)value);
			super.setCustomFields(prop);
		}
	}

	@JsonIgnore
	public PortalSyncUser getBeehiveUser(){

		PortalSyncUser user=new PortalSyncUser();

		BeanUtils.copyProperties(this, user, "customFields", "customField");

		user.setCustomFields(this.getCustomFields());

		return user;
	}

	@JsonIgnore
	public void verifyInput(){
		PortalException excep= new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,  HttpStatus.BAD_REQUEST);

		if(StringUtils.isBlank(this.getUserName())){
				excep.addParam("field","userName");
				throw excep;
		}

		if(StringUtils.isBlank(this.getAliUserID())){
			excep.addParam("field","aliUserID");
			throw excep;
		}

		if(StringUtils.isBlank(this.getRole())){
			excep.addParam("field","role");
			throw excep;
		}

	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

}
