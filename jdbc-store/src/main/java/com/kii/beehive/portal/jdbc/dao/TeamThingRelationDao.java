package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.TeamThingRelation;

@Repository
public class TeamThingRelationDao extends SpringBaseDao<TeamThingRelation> {

	private Logger log= LoggerFactory.getLogger(TeamThingRelationDao.class);
	
	public static final String TABLE_NAME = "rel_team_thing";
	public static final String KEY = "id";
	
	public void delete(Long teamID, Long thingID){
		if(teamID != null || thingID != null){
			String sql = "DELETE FROM " + this.getTableName() + " WHERE ";
			
			StringBuilder where = new StringBuilder();
			List<Object> params = new ArrayList<Object>();
			if(teamID != null){
				where.append(TeamThingRelation.TEAM_ID + " = ? "); 
				params.add(teamID);
			}
			
			if(thingID != null){
				if(where.length() > 0){
					where.append(" AND ");
				}
				where.append(TeamThingRelation.THING_ID+" = ? ");
				params.add(thingID);
			}
			Object[] paramArr = new Object[params.size()];
			paramArr = params.toArray(paramArr);
			
	        jdbcTemplate.update(sql+where.toString(),paramArr);
		}else{
			log.warn("teamID and thingID are null");
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
	
	public TeamThingRelation findByTagIDAndUserGroupID(Long teamID, Long thingID) {
		if(teamID!=null && thingID!=null){
			String sql = "SELECT * FROM " + this.getTableName() + " WHERE "+ TeamThingRelation.TEAM_ID +"=? AND "+ TeamThingRelation.THING_ID + "=?";
			List<TeamThingRelation> list= jdbcTemplate.query(sql,new Object[]{teamID,thingID},getRowMapper());
	        if(list.size() > 0){
	        	return list.get(0);
	        }
		}
        return null;
    }
}
