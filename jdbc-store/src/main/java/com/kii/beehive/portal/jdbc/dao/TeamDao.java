package com.kii.beehive.portal.jdbc.dao;

import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.Team;

@Repository
public class TeamDao extends SpringBaseDao<Team> {

	
	public static final String TABLE_NAME = "team";
	public static final String KEY = "team_id";
	
	@Override
	public String getTableName() {
		return TABLE_NAME;
	}


	@Override
	public String getKey() {
		return KEY;
	}

}
