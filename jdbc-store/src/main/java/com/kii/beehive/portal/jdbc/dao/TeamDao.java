package com.kii.beehive.portal.jdbc.dao;

import java.util.List;

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
	
	public List<Team> findTeamByTeamName(String teamName) {
		return this.findBySingleField(Team.NAME, teamName);
	}
	
	public List<Team> findTeamByUserID(Long userID) {
		String sql = "SELECT t.* from "+TABLE_NAME+" t  "
					+ "INNER JOIN rel_team_user r ON t.team_id=r.team_id "
					+ " WHERE r.beehive_user_id= ?";

		sql=super.addDelSignPrefix(sql);


		List<Team> rows = jdbcTemplate.query(sql, new Object[]{userID} ,getRowMapper() );
	    return rows;
	}

	public Team getTeamByUserID(Long userID){
		List<Team> teamList = findTeamByUserID(userID);
		if (teamList != null && teamList.size() > 0) {
			return teamList.get(0);
		} else {
			return null;
		}
	}

}
