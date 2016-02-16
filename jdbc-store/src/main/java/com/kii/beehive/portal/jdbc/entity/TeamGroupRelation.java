package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class TeamGroupRelation extends DBEntity{

	private Long id;
	
	private Long userGroupID;

	private Long teamID;
	
	public final static String ID = "id";
	public final static String USER_GROUP_ID = "user_group_id";
	public final static String TEAM_ID = "team_id";
	
	public TeamGroupRelation() {}
	

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

	@JdbcField(column=USER_GROUP_ID)
	public Long getUserGroupID() {
		return userGroupID;
	}


	public void setUserGroupID(Long userGroupID) {
		this.userGroupID = userGroupID;
	}


	
	
}
