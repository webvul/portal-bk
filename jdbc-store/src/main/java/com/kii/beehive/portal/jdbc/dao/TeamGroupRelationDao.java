package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.TeamGroupRelation;

@Repository
public class TeamGroupRelationDao extends SpringSimpleBaseDao<TeamGroupRelation> {

	private Logger log= LoggerFactory.getLogger(TeamGroupRelationDao.class);
	
	public static final String TABLE_NAME = "rel_team_group";
	public static final String KEY = "id";
	
	public void delete(Long teamID, Long userGroupID){
		if(teamID != null || userGroupID != null){
			String sql = "DELETE FROM " + this.getTableName() + " WHERE ";
			
			StringBuilder where = new StringBuilder();
			List<Object> params = new ArrayList<Object>();
			if(teamID != null){
				where.append(TeamGroupRelation.TEAM_ID + " = ? "); 
				params.add(teamID);
			}
			
			if(userGroupID != null){
				if(where.length() > 0){
					where.append(" AND ");
				}
				where.append(TeamGroupRelation.USER_GROUP_ID+" = ? ");
				params.add(userGroupID);
			}
			Object[] paramArr = new Object[params.size()];
			paramArr = params.toArray(paramArr);
			
	        jdbcTemplate.update(sql+where.toString(),paramArr);
		}else{
			log.warn("teamID and userGroupID are null");
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
	
	public TeamGroupRelation findByTeamIDAndUserGroupID(Long teamID, Long userGroupID) {
		if(teamID!=null && userGroupID!=null){
			String sql = "SELECT * FROM " + this.getTableName() + " WHERE "+ TeamGroupRelation.TEAM_ID +"=? AND "+ TeamGroupRelation.USER_GROUP_ID + "=?";
			List<TeamGroupRelation> list= jdbcTemplate.query(sql,new Object[]{teamID,userGroupID},getRowMapper());
	        if(list.size() > 0){
	        	return list.get(0);
	        }
		}
        return null;
    }
}
