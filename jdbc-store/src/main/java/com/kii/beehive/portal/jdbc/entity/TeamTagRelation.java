package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class TeamTagRelation extends DBEntity{

	private Long id;
	
	private Long tagID;

	private Long teamID;
	
	public final static String ID = "id";
	public final static String TAG_ID = "tag_id";
	public final static String TEAM_ID = "team_id";
	
	public TeamTagRelation() {}
	

	public TeamTagRelation(Long teamID, Long tagID) {
		super();
		this.tagID = tagID;
		this.teamID = teamID;
	}


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

	@JdbcField(column=TAG_ID)
	public Long getTagID() {
		return tagID;
	}

	public void setTagID(Long tagID) {
		this.tagID = tagID;
	}
	
}
