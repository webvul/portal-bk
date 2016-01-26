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
    
    
}
