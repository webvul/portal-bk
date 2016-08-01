package com.kii.beehive.portal.entitys;

import java.util.Set;

import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;
import com.kii.beehive.portal.jdbc.entity.Team;

public class AuthUser {

//	private AuthInfo authInfo;

	private BeehiveJdbcUser user;

	private String token;

	private Team team;

	private Set<String>  permissionSet;

	public Set<String> getPermissionSet() {
		return permissionSet;
	}

	public void setPermissionSet(Set<String> permissionSet) {
		this.permissionSet = permissionSet;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}



	public BeehiveJdbcUser getUser() {
		return user;
	}

	public void setUser(BeehiveJdbcUser user) {
		this.user = user;
	}
}
