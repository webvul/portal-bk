package com.kii.beehive.portal.web.entity;


import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.store.entity.CustomProperty;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.help.PortalException;

public class UserRestBean  extends BeehiveUser {

	public UserRestBean(){

	}

	public UserRestBean(BeehiveUser user){
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
	public BeehiveUser getBeehiveUser(){

		BeehiveUser user=new BeehiveUser();

		BeanUtils.copyProperties(this, user, "customFields", "customField");

		user.setCustomFields(this.getCustomFields());

		return user;
	}

	@JsonIgnore
	public void verifyInput(){
		if(StringUtils.isEmpty(this.getUserName())){
			throw new PortalException("RequiredFieldsMissing","username cannot been null", HttpStatus.BAD_REQUEST);
		}

		if(StringUtils.isEmpty(this.getAliUserID())){
			throw new PortalException("RequiredFieldsMissing","userID cannot been null", HttpStatus.BAD_REQUEST);
		}

		if(StringUtils.isEmpty(this.getRole())){
			throw new PortalException("RequiredFieldsMissing","role cannot been null", HttpStatus.BAD_REQUEST);
		}

	}

}
