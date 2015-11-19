package com.kii.beehive.portal.web.entity;


import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.store.entity.CustomProperty;

public class OutputUser  extends BeehiveUser{

	public OutputUser(){

	}

	public OutputUser(BeehiveUser user){
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
	}

	@JsonIgnore
	public BeehiveUser getBeehiveUser(){

		BeehiveUser user=new BeehiveUser();

		BeanUtils.copyProperties(this, user, "customFields", "customField");

		user.setCustomFields(this.getCustomFields());

		return user;
	}

}
