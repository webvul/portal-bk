package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.GroupPermissionRelation;

@Repository
public class GroupPermissionRelationDao extends SpringBaseRelDao<GroupPermissionRelation> {

	private Logger log= LoggerFactory.getLogger(GroupPermissionRelationDao.class);
	
	public static final String TABLE_NAME = "rel_group_permission";
	public static final String KEY = "id";
	
	public void delete(Long permissionID, Long userGroupID){
		if(permissionID != null || userGroupID != null){
			String sql = "DELETE FROM " + this.getTableName() + " WHERE ";
			
			StringBuilder where = new StringBuilder();
			List<Object> params = new ArrayList<Object>();
			if(permissionID != null){
				where.append(GroupPermissionRelation.PERMISSION_ID + " = ? "); 
				params.add(permissionID);
			}
			
			if(userGroupID != null){
				if(where.length() > 0){
					where.append(" AND ");
				}
				where.append(GroupPermissionRelation.USER_GROUP_ID+" = ? ");
				params.add(userGroupID);
			}
			Object[] paramArr = new Object[params.size()];
			paramArr = params.toArray(paramArr);
			
	        jdbcTemplate.update(sql+where.toString(),paramArr);
		}else{
			log.warn("permissionID and userGroupID are null");
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
	
	@SuppressWarnings("unchecked")
	public GroupPermissionRelation findByPermissionIDAndUserGroupID(Long permissionID, Long userGroupID) {
		if(permissionID!=null && userGroupID!=null){
			String sql = "SELECT * FROM " + this.getTableName() + " WHERE "+ GroupPermissionRelation.PERMISSION_ID +"=? AND "+ GroupPermissionRelation.USER_GROUP_ID + "=?";
			List<GroupPermissionRelation> list= jdbcTemplate.query(sql,new Object[]{permissionID,userGroupID},getRowMapper());
	        if(list.size() > 0){
	        	return list.get(0);
	        }
		}
        return null;
    }
}
