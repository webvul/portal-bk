package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class TeamThingRelation extends DBEntity{

	private Long id;
	
	private Long thingID;

	private Long teamID;
	
	public final static String ID = "id";
	public final static String THING_ID = "thing_id";
	public final static String TEAM_ID = "team_id";
	
	public TeamThingRelation() {}
	

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

	@JdbcField(column=THING_ID)
	public Long getThingID() {
		return thingID;
	}


	public void setThingID(Long thingID) {
		this.thingID = thingID;
	}
}
