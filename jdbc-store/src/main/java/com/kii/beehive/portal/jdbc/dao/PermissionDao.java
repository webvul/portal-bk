package com.kii.beehive.portal.jdbc.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.Permission;

@Repository
public class PermissionDao extends SpringBaseDao<Permission> {

	
	public static final String TABLE_NAME = "permission";
	public static final String KEY = "permission_id";
	
	@Override
	public String getTableName() {
		return TABLE_NAME;
	}


	@Override
	public String getKey() {
		return KEY;
	}
	
	public List<Permission> findByUserGroupID(Long userGroupID) {
		String sql = "SELECT p.permission_id, p.source_id,p.name,p.action,p.description,p.create_by,p.create_date,p.modify_by,p.modify_date "
					+ "FROM " + this.getTableName() +" p "
					+ "INNER JOIN rel_group_permission r ON r.permission_id = p.permission_id "
					+ "WHERE r.user_group_id = ?";
		
		List<Permission> list = jdbcTemplate.query(sql,new Object[]{userGroupID},getRowMapper());
	    return list;
	}
	
	public List<Permission> findAll() {
		String sql = "SELECT p.permission_id, p.source_id,s.name sourceName,p.name,p.action,p.description,p.create_by,p.create_date,p.modify_by,p.modify_date "
					+ "FROM " + this.getTableName() +" p "
					+ "INNER JOIN source s ON s.source_id = p.source_id ";
		
		List<Permission> rows = jdbcTemplate.query(sql,getRowMapper());
	    return rows;
	}
}
