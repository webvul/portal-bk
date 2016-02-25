package com.kii.beehive.portal.jdbc.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.Team;
import com.kii.beehive.portal.jdbc.entity.TeamUserRelation;

@Repository
public class TeamUserRelationDao extends SpringBaseDao<TeamUserRelation> {

	private Logger log= LoggerFactory.getLogger(TeamUserRelationDao.class);
	
	public static final String TABLE_NAME = "rel_team_user";
	public static final String KEY = "id";
	
	public void delete(Long teamID, String userID){
		if(teamID != null || !Strings.isBlank(userID)){
			String sql = "DELETE FROM " + this.getTableName() + " WHERE ";
			
			StringBuilder where = new StringBuilder();
			List<Object> params = new ArrayList<Object>();
			if(teamID != null){
				where.append(TeamUserRelation.TEAM_ID + " = ? "); 
				params.add(teamID);
			}
			
			if(!Strings.isBlank(userID)){
				if(where.length() > 0){
					where.append(" AND ");
				}
				where.append(TeamUserRelation.USER_ID+" = ? ");
				params.add(userID);
			}
			Object[] paramArr = new Object[params.size()];
			paramArr = params.toArray(paramArr);
			
	        jdbcTemplate.update(sql+where.toString(),paramArr);
		}else{
			log.warn("teamID and userID are null");
		}
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}


	@Override
	public String getKey() {
		return KEY;
	}
	
	public Long countByTeamID(Long teamID){
		String sql = "SELECT count(1) FROM " + this.getTableName() + " WHERE "+ TeamUserRelation.TEAM_ID +"=?";  
		Long count =  jdbcTemplate.queryForObject(sql,new Object[]{teamID}, Long.class);
    	return count;
	}
	
	public TeamUserRelation findByTeamIDAndUserID(Long teamID, String userID) {
		if(teamID!=null && !Strings.isBlank(userID)){
			String sql = "SELECT * FROM " + this.getTableName() + " WHERE "+ TeamUserRelation.TEAM_ID +"=? AND "+ TeamUserRelation.USER_ID + "=?";
			List<TeamUserRelation> list= jdbcTemplate.query(sql,new Object[]{teamID,userID},getRowMapper());
	        if(list.size() > 0){
	        	return list.get(0);
	        }
		}
        return null;
    }
}
