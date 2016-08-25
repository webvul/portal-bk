package com.kii.beehive.portal.plugin.searchguard.data;

import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;

/**
 * Created by hdchen on 8/25/16.
 */
public class AuthInfo {
	private BeehiveJdbcUser user;

	private String accessToken;

	private Long teamID;

	private String teamName;

	public BeehiveJdbcUser getUser() {
		return user;
	}

	public void setUser(BeehiveJdbcUser user) {
		this.user = user;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
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
