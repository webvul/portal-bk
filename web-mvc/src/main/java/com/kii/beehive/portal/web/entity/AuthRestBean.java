package com.kii.beehive.portal.web.entity;


import com.kii.beehive.portal.store.entity.BeehiveUser;

public class AuthRestBean extends UserRestBean {

    public AuthRestBean() {
        super();
    }

    public AuthRestBean(BeehiveUser user) {
        super(user);
    }

    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
