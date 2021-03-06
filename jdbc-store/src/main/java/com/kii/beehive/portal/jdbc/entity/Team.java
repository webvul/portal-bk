package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class Team extends BusinessEntity {


	private String name;
	
	public final static String TEAM_ID = "team_id";
	public final static String NAME = "name";
	
	@Override
	@JdbcField(column=TEAM_ID)
	public Long getId(){
		return super.getId();
	}
	
	@JdbcField(column=NAME)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
