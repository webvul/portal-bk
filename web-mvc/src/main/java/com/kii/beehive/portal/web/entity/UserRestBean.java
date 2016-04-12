package com.kii.beehive.portal.web.entity;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.exception.PortalException;

public class UserRestBean {


	private BeehiveUser  beehiveUser;

	private String teamName;

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}



	@JsonUnwrapped
	public BeehiveUser getBeehiveUser() {
		return beehiveUser;
	}

	public void setBeehiveUser(BeehiveUser user) {
		this.beehiveUser = user.cloneView();
	}

	@JsonIgnore
	public void verifyInput(){
		if(StringUtils.isEmpty(beehiveUser.getUserName())&&StringUtils.isEmpty(beehiveUser.getMail())&&StringUtils.isEmpty(beehiveUser.getPhone())){
			throw new PortalException("RequiredFieldsMissing","username cannot been null", HttpStatus.BAD_REQUEST);
		}

	}
}
