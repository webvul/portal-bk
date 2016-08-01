package com.kii.beehive.portal.web.entity;


import java.util.Set;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.beehive.portal.entitys.AuthUser;
import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;

public class AuthRestBean  {

    public AuthRestBean() {

    }


	public AuthRestBean(AuthUser user) {

		this.user=user.getUser().cloneView();

		this.accessToken=user.getToken();
		this.teamID=user.getTeam().getId();
		this.teamName=user.getTeam().getName();

	}

	private BeehiveJdbcUser user;

    private String accessToken;
    
    private Set<String> permissions;
    
    private Long teamID;
    
    private String teamName;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

	public Set<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<String> permissions) {
		this.permissions = permissions;
	}

	public Long getTeamID() {
		return teamID;
	}

	public void setTeamID(Long teamID) {
		this.teamID = teamID;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}



	@JsonUnwrapped
	public BeehiveJdbcUser getUser() {
		return user;
	}



}
