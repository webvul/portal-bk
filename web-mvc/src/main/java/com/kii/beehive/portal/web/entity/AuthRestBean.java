package com.kii.beehive.portal.web.entity;


import java.util.Set;

import com.kii.beehive.portal.store.entity.BeehiveUser;

public class AuthRestBean extends UserRestBean {

    public AuthRestBean() {
        super();
    }

    public AuthRestBean(BeehiveUser user) {
        super(user);
    }

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
    
    
}
