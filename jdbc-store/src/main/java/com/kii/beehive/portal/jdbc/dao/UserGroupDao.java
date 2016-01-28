package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.UserGroup;

@Repository
public class UserGroupDao extends SpringBaseDao<UserGroup> {

	private Logger log= LoggerFactory.getLogger(UserGroupDao.class);
	
	public static final String TABLE_NAME = "user_group";
	public static final String KEY = "user_group_id";
	
	public List<UserGroup> findUserGroup(String userID, Long userGroupID, String name) {
		if(Strings.isBlank(userID)){
			return null;
		}
		
		String sql = "SELECT u.* "
					+ "FROM " + this.getTableName() +" u "
					+ "INNER JOIN rel_group_user r ON u.user_group_id = r.user_group_id " 
					+ "WHERE r.user_id = ? ";
		
		List<Object> params = new ArrayList<Object>();
		params.add(userID);
		
		if(userGroupID != null){
			sql += " AND u.user_group_id = ? "; 
			params.add(userGroupID);
		}
		
		if(!Strings.isBlank(name)){
			sql += " AND u.name = ? "; 
			params.add(name);
		}
		
		Object[] paramArr = new Object[params.size()];
		paramArr = params.toArray(paramArr);
		
		List<UserGroup> rows = jdbcTemplate.query(sql, paramArr,getRowMapper());
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

	@Override
	protected Class getEntityCls() {
		return UserGroup.class;
	}
	
}
