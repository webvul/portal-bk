package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.GroupPermissionRelation;

@Repository
public class GroupPermissionRelationDao extends BaseDao<GroupPermissionRelation> {

	//private Logger log= LoggerFactory.getLogger(GroupPermissionRelationDao.class);
	
	public static final String TABLE_NAME = "rel_group_permission";
	public static final String KEY = "id";
	
	public void delete(Long permissionID, Long userGroupID){
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
	public List<GroupPermissionRelation> mapToList(List<Map<String, Object>> rows) {
		List<GroupPermissionRelation> list = new ArrayList<GroupPermissionRelation>();
		for (Map<String, Object> row : rows) {
			GroupPermissionRelation groupPermissionRelation = new GroupPermissionRelation();
			groupPermissionRelation.setId((int)row.get(GroupPermissionRelation.ID));
			groupPermissionRelation.setPermissionID((int)row.get(GroupPermissionRelation.PERMISSION_ID));
			groupPermissionRelation.setUserGroupID((int)row.get(GroupPermissionRelation.USER_GROUP_ID));
			list.add(groupPermissionRelation);
		}
		return list;
	}
	
	@Override
	public long update(GroupPermissionRelation entity) {
		
		String[] columns = new String[]{
				GroupPermissionRelation.ID,
				GroupPermissionRelation.PERMISSION_ID,
				GroupPermissionRelation.USER_GROUP_ID
		};

        return super.update(entity, columns);

	}
	
	public GroupPermissionRelation findByPermissionIDAndUserGroupID(Long permissionID, Long userGroupID) {  
		String sql = "SELECT * FROM " + this.getTableName() + " WHERE "+ GroupPermissionRelation.PERMISSION_ID +"=? AND "+ GroupPermissionRelation.USER_GROUP_ID + "=?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, permissionID,userGroupID);
        List<GroupPermissionRelation> list = mapToList(rows);
        if(list.size() > 0){
        	return list.get(0);
        }
        return null;
    }
}
