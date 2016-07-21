package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.TeamTagRelation;

@Repository
public class TeamTagRelationDao extends SpringSimpleBaseDao<TeamTagRelation> {

	private Logger log= LoggerFactory.getLogger(TeamTagRelationDao.class);
	
	public static final String TABLE_NAME = "rel_team_tag";
	public static final String KEY = "id";
	
	public void delete(Long teamID, Long tagID){
		if(teamID != null || tagID != null){
			String sql = "DELETE FROM " + this.getTableName() + " WHERE ";
			
			StringBuilder where = new StringBuilder();
			List<Object> params = new ArrayList<Object>();
			if(teamID != null){
				where.append(TeamTagRelation.TEAM_ID + " = ? "); 
				params.add(teamID);
			}
			
			if(tagID != null){
				if(where.length() > 0){
					where.append(" AND ");
				}
				where.append(TeamTagRelation.TAG_ID+" = ? ");
				params.add(tagID);
			}
			Object[] paramArr = new Object[params.size()];
			paramArr = params.toArray(paramArr);
			
	        jdbcTemplate.update(sql+where.toString(),paramArr);
		}else{
			log.warn("teamID and tagID are null");
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
	
	public TeamTagRelation findByTeamIDAndTagID(Long teamID, Long tagID) {
		if(teamID!=null && tagID!=null){
			String sql = "SELECT * FROM " + this.getTableName() + " WHERE "+ TeamTagRelation.TEAM_ID +"=? AND "+ TeamTagRelation.TAG_ID + "=?";
			List<TeamTagRelation> list= jdbcTemplate.query(sql,new Object[]{teamID,tagID},getRowMapper());
	        if(list.size() > 0){
	        	return list.get(0);
	        }
		}
        return null;
    }
}
