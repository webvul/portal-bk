package com.kii.extension.ruleengine.sdk.exception;

import org.apache.http.HttpResponse;

/**
 * for http status code 403 from Kii Cloud
 *
 * Sample from Kii Cloud:
 *  "errorCode" : "WRONG_TOKEN",
 *  "message" : "The provided token is not valid",
 *  "appID" : "da0b6a25",
 *  "accessToken" : "W2EeRv_WVcO4xFsD326QwZpVqrBYed2FBEuwW8rOqOI"
 */
public class ForbiddenException extends KiiCloudException {

    private String appID;

    private String accessToken;

    public ForbiddenException() {
        super();
    }

    public ForbiddenException(HttpResponse response) {
        super(response);
    }

    @Override
    public int getStatusCode() {
        return 403;
    }

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}