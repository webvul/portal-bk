package com.kii.extension.ruleengine.sdk.entity;

import java.util.Date;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginInfo {

	private String userID;

	private String  token;

	private Date expainAt;
	
	private Set<String> permissionSet;


	@JsonProperty("id")
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	@JsonProperty("access_token")
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@JsonIgnore
	public Date getExpainAt() {
		return expainAt;
	}

	@JsonProperty("expires_in")
	public void setExpainIn(long expainIn) {


		this.expainAt = new Date(System.currentTimeMillis()+expainIn);
	}

	@JsonIgnore
	public Set<String> getPermissionSet() {
		return permissionSet;
	}

	public void setPermissionSet(Set<String> permissionSet) {
		this.permissionSet = permissionSet;
	}
	
	
}
