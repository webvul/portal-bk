package com.kii.beehive.portal.web.entity;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.help.PortalException;

public class ThingInput extends GlobalThingInfo {

	private Set<String> tagNames=new HashSet<>();

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
	}

}
