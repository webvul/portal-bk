package com.kii.beehive.portal.web.entity;

import java.util.Map;

import org.apache.logging.log4j.util.Strings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.beehive.portal.jdbc.entity.IndustryTemplate;
import com.kii.beehive.portal.web.exception.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;

public class IndustryTemplateRestBean {

	private IndustryTemplate industryTemplate;

	private Map<String, Object> content;

	@JsonUnwrapped
	public IndustryTemplate getIndustryTemplate() {
		return industryTemplate;
	}

	public void setIndustryTemplate(IndustryTemplate industryTemplate) {
		this.industryTemplate = industryTemplate;
	}

	public Map<String, Object> getContent() {
		return content;
	}

	public void setContent(Map<String, Object> content) {
		this.content = content;
	}

	@JsonIgnore
	public void verifyInput(){
		if(Strings.isBlank(industryTemplate.getName())) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,"field","name");
		}
		if(Strings.isBlank(industryTemplate.getThingType())) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,"field","thingType");
		}
		if(Strings.isBlank(industryTemplate.getVersion())) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,"field","version");
		}
		if(Strings.isBlank(industryTemplate.getContent())) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,"field","content");
		}

	}
}
