package com.kii.beehive.portal.entitys;

import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;
import com.kii.beehive.portal.jdbc.entity.Team;

public class AuthUser {


	private BeehiveJdbcUser user;

	private String token;

	private Team team;


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
