package com.kii.beehive.portal.store.entity;

public class AuthInfoEntry {
	
	private String userID;

	private String token;
	
	private Long teamID;

	public AuthInfoEntry(String userID,Long teamID, String token){

		this.userID = userID;
		this.teamID = teamID;
		this.token = token;
	}

	/**
	 * get the header(function indicator, such as "/user", "/thing", etc) from url <br/>
	 * @param url
	 * @return
     */
	private String getHeader(String url) {
		int solIdx1=url.indexOf("/");
		int solIdx2=url.indexOf("/",solIdx1+1);
		solIdx2 = (solIdx2 == -1)? url.length() : solIdx2;

		return url.substring(solIdx1,solIdx2).trim();
	}


	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getTeamID() {
		return teamID;
	}

	public void setTeamID(Long teamID) {
		this.teamID = teamID;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AuthInfoEntry [userID=");
		builder.append(userID);
		builder.append(", token=");
		builder.append(token);
		builder.append(", teamID=");
		builder.append(teamID);
		builder.append("]");
		return builder.toString();
	}
	
	
}
