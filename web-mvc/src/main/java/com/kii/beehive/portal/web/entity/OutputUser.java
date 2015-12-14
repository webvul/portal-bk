package com.kii.beehive.portal.web.entity;


import java.util.Map;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kii.beehive.portal.jdbc.entity.BeehiveUser;
import com.kii.beehive.portal.store.entity.CustomProperty;

public class OutputUser  extends BeehiveUser{

	public OutputUser(){

	}

	public OutputUser(BeehiveUser user){
		BeanUtils.copyProperties(user, this, "customFields", "customField");

		// TODO
//		this.setCustomFields(user.getCustomFields());
	}


	public Map<String,Object> getCustom() {
		// TODO
		return null;
//		return super.getCustomFields().getOriginFields();
	}


	@JsonIgnore
	public CustomProperty getCustomFields() {
		// TODO
		return null;
//		return super.getCustomFields();
	}

	public void setCustomFields(CustomProperty properties) {
		// TODO
//		super.setCustomFields(properties);
	}

	@JsonAnySetter
	public void setCustomField(String key,Object value){
		// TODO
//		if(key.startsWith("custom.")) {
//			key=key.substring(7);
//			super.setCustomField(key, value);
//		}
//		if(key.equals("custom")){
//			CustomProperty prop=new CustomProperty((Map)value);
//			super.setCustomFields(prop);
//		}
	}

	@JsonIgnore
	public BeehiveUser getBeehiveUser(){

		BeehiveUser user=new BeehiveUser();

		BeanUtils.copyProperties(this, user, "customFields", "customField");

		// TODO
//		user.setCustomFields(this.getCustomFields());

		return user;
	}

}
