package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class TeamUserRelation extends DBEntity{

	private Long id;
	
	private String userID;

	private Long teamID;
	
	private int vaild;
	
	public final static String ID = "id";
	public final static String USER_ID = "user_id";
	public final static String TEAM_ID = "team_id";
	public final static String VAILD = "vaild";
	
	public TeamUserRelation() {}
	

	@JdbcField(column=ID)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JdbcField(column=TEAM_ID)
	public Long getTeamID() {
		return teamID;
	}


	public void setTeamID(Long teamID) {
		this.teamID = teamID;
	}


	@JdbcField(column=USER_ID)
	public String getUserID() {
		return userID;
	}


	public void setUserID(String userID) {
		this.userID = userID;
	}

	@JdbcField(column=VAILD)
	public int getVaild() {
		return vaild;
	}


	public void setVaild(int vaild) {
		this.vaild = vaild;
	}
	
	
}
