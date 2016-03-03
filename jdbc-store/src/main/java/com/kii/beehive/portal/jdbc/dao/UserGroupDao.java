package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.jdbc.entity.UserGroup;

@Repository
public class UserGroupDao extends SpringBaseDao<UserGroup> {

	private Logger log= LoggerFactory.getLogger(UserGroupDao.class);
	
	public static final String TABLE_NAME = "user_group";
	public static final String KEY = "user_group_id";
	
	public List<UserGroup> findUserGroup(String userID, Long userGroupID, String name) {
		
		List<Object> params = new ArrayList<Object>();
		
		StringBuilder sql = new StringBuilder("SELECT u.* FROM " + this.getTableName() +" u ");
		StringBuilder where = new StringBuilder();
		
		if(AuthInfoStore.getTeamID() != null){
			sql.append(" INNER JOIN rel_team_group rt ON u.user_group_id = rt.user_group_id ");
			if(where.length() > 0) where.append(" AND ");
			where.append(" rt.team_id = ? ");
			params.add(AuthInfoStore.getTeamID());
		}
		
		sql.append(" INNER JOIN rel_group_user r ON u.user_group_id = r.user_group_id ");
		
		if(!Strings.isBlank(userID)){
			if(where.length() > 0) where.append(" AND ");
			where.append(" r.user_id = ? ");
			params.add(userID);
		}
		
		if(userGroupID != null){
			if(where.length() > 0) where.append(" AND ");
			where.append(" u.user_group_id = ? ");
			params.add(userGroupID);
		}
		
		if(!Strings.isBlank(name)){
			if(where.length() > 0) where.append(" AND ");
			where.append(" u.name = ? ");
			params.add(name);
		}
		
		if(where.length() == 0){
			return null;
		}
		
		sql.append(" WHERE ").append(where);
		List<UserGroup> rows = jdbcTemplate.query(sql.toString(), params.toArray(new Object[params.size()]) ,getRowMapper());
	    return rows;
	}
	
	public List<UserGroup> findUserGroup(Long permissionID, Long userGroupID) {
		if(permissionID == null){
			return null;
		}
		
		String sql = "SELECT u.* "
					+ "FROM " + this.getTableName() +" u "
					+ "INNER JOIN rel_group_permission r ON u.user_group_id = r.user_group_id " 
					+ "WHERE r.permission_id = ? ";
		
		List<Object> params = new ArrayList<Object>();
		params.add(permissionID);
		
		if(userGroupID != null){
			sql += " AND u.user_group_id = ? "; 
			params.add(userGroupID);
		}
		
		Object[] paramArr = new Object[params.size()];
		paramArr = params.toArray(paramArr);
		
		List<UserGroup> rows = jdbcTemplate.query(sql, paramArr,getRowMapper());
	    return rows;
	}
	

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}


	@Override
	public String getKey() {
		return KEY;
	}
}
