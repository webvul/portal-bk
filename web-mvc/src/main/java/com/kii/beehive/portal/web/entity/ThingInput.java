package com.kii.beehive.portal.web.entity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.help.PortalException;

public class ThingInput extends GlobalThingInfo {

	private Logger log= LoggerFactory.getLogger(ThingInput.class);

	private ObjectMapper mapper = new ObjectMapper();

	private String location;

	private Set<String> tagNames=new HashSet<>();

	@JsonIgnore
	@Override
	public long getId() {
		return super.getId();
	}

	@JsonSetter("globalThingID")
	public void setGlobalThingID(Long globalThingID) {
		this.setId(globalThingID);
	}

	@JsonGetter("globalThingID")
	public Long getGlobalThingID() {
		Long id = (this.getId() == 0)? null : this.getId();
		return id;
	}

	@JsonGetter("location")
	public String getLocation() {
		return location;
	}

	@JsonSetter("location")
	public void setLocation(String location) {
		this.location = location;
	}

	@JsonProperty("tags")
	public Set<String> getInputTags() {
		return tagNames;
	}

	@JsonSetter("custom")
	public void setCustomJson(Map<String, Object> custom) {
		try {
			this.setCustom(mapper.writeValueAsString(custom));
		} catch (Exception e) {
			log.error("Excetpion in setCustomJson()", e);
		}
	}

	@JsonGetter("custom")
	public Map<String, Object> getCustomJson() {
		try {
			String custom = this.getCustom();
			if(custom != null) {
				return mapper.readValue(custom, Map.class);
			}
		} catch (Exception e) {
			log.error("Excetpion in getCustomJson()", e);
		}
		return null;
	}

	@JsonSetter("status")
	public void setStatusJson(Object status) {
		try {
			this.setStatus(mapper.writeValueAsString(status));
		} catch (Exception e) {
			log.error("Excetpion in setStatusJson()", e);
		}
	}

	@JsonGetter("status")
	public Map<String, Object> getStatusJson() {
		try {
			String status = this.getStatus();
			if(status != null) {
				return mapper.readValue(status, Map.class);
			}
		} catch (Exception e) {
			log.error("Excetpion in getStatusJson()", e);
		}
		return null;
	}

	public void setInputTags(Set<String> tags) {
		this.tagNames = tags;
	}


	public void verifyInput(){

		if(Strings.isBlank(this.getVendorThingID())){
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,"VendorThingID is empty", HttpStatus.BAD_REQUEST);
		}

		if(Strings.isBlank(this.getKiiAppID())){
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,"KiiAppID is empty", HttpStatus.BAD_REQUEST);
		}
	}

}
