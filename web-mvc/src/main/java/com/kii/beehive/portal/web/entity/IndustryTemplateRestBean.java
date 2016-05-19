package com.kii.beehive.portal.web.entity;

import java.util.Map;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.kii.beehive.portal.jdbc.entity.IndustryTemplate;
import com.kii.beehive.portal.web.constant.ErrorCode;
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
		if(Strings.isBlank(industryTemplate.getThingType()) || Strings.isBlank(industryTemplate.getName()) || Strings.isBlank
				(industryTemplate.getVersion()) || getContent() == null) {
			throw new PortalException(ErrorCode.INVALID_INPUT, HttpStatus.BAD_REQUEST);
		}

	}
}
