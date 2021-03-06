package com.kii.beehive.portal.entitys;

import java.util.Date;

public class AuthInfo {

	private Long userID;
	private String userIDStr;
	private Long teamID;
	private Date expireTime;
	private boolean is3Party;


	public boolean is3Party() {
		return is3Party;
	}

	public void setIs3Party(boolean is3Party) {
		this.is3Party = is3Party;
	}

	public Long getUserID() {
		return userID;
	}

	public void setUserID(Long userID) {
		this.userID = userID;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public Long getTeamID() {
		return teamID;
	}

	public void setTeamID(Long teamID) {
		this.teamID = teamID;
	}

	public String getUserIDStr() {
		return userIDStr;
	}

	public void setUserIDStr(String userIDStr) {
		this.userIDStr = userIDStr;
	}
}
