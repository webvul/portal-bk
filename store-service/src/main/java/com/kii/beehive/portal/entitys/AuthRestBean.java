package com.kii.beehive.portal.entitys;


import java.util.Set;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.beehive.portal.store.entity.BeehiveUser;

public class AuthRestBean  {

    public AuthRestBean() {
        super();
    }

	private BeehiveUser user;

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
	public BeehiveUser getUser() {
		return user;
	}

	public void setUser(BeehiveUser user) {
		this.user = user;
	}
}