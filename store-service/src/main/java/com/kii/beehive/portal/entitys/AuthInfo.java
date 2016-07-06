package com.kii.beehive.portal.entitys;

import java.util.Date;

public class AuthInfo  {

    private String userID;
	private Long  userIDInLong;
    private Long teamID;
    private Date expireTime;
	private boolean is3Party;

	public Long getUserIDInLong() {
		return userIDInLong;
	}

	public void setUserIDInLong(Long userIDInLong) {
		this.userIDInLong = userIDInLong;
	}

	public boolean is3Party() {
		return is3Party;
	}

	public void setIs3Party(boolean is3Party) {
		this.is3Party = is3Party;
	}

	public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
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




}
