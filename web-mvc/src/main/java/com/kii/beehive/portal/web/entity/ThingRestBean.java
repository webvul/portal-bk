package com.kii.beehive.portal.web.entity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.web.exception.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;

public class ThingRestBean extends GlobalThingInfo {

	private final static Pattern validVendorThingIDPattern = Pattern.compile("([0-9a-zA-Z]|-)+");

	private Logger log = LoggerFactory.getLogger(ThingRestBean.class);

	private ObjectMapper mapper = new ObjectMapper();

	private String location;

	private String gatewayVendorThingID;

	private Set<String> tagNames = new HashSet<>();

	@JsonIgnore
	@Override
	public Long getId() {
		return super.getId();
	}

	@JsonSetter("globalThingID")
	public void setGlobalThingID(Long globalThingID) {
		this.setId(globalThingID);
	}

	@JsonGetter("globalThingID")
	public Long getGlobalThingID() {
		Long id = (this.getId() == 0) ? null : this.getId();
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
			if (custom != null) {
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
			if (status != null) {
				return mapper.readValue(status, Map.class);
			}
		} catch (Exception e) {
			log.error("Excetpion in getStatusJson(), status = " + this.getStatus(), e);
		}
		return null;
	}

	public String getGatewayVendorThingID() {
		return gatewayVendorThingID;
	}

	public void setGatewayVendorThingID(String gatewayVendorThingID) {
		this.gatewayVendorThingID = gatewayVendorThingID;
	}

	public void setInputTags(Set<String> tags) {
		this.tagNames = tags;
	}


	public void verifyInput() {

		this.verifyVendorThingID();

		if (Strings.isBlank(this.getKiiAppID())) {

			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,"field","kiiAppID");
		}
	}

	private void verifyVendorThingID() {

		String vendorThingID = this.getVendorThingID();

		if (Strings.isBlank(vendorThingID)) {

			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,"field","vendorThingID");
		}

		Matcher matcher = validVendorThingIDPattern.matcher(vendorThingID);

		if (!matcher.matches()) {
			throw new PortalException(ErrorCode.INVALID_INPUT,"field","vendorThingID","data",vendorThingID);
		}

	}

}
