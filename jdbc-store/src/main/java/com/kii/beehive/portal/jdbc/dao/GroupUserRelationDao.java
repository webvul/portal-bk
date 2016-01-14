package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.GroupUserRelation;

@Repository
public class GroupUserRelationDao extends BaseDao<GroupUserRelation> {

	//private Logger log= LoggerFactory.getLogger(GroupUserRelationDao.class);
	
	public static final String TABLE_NAME = "rel_group_user";
	public static final String KEY = "id";
	
	public void delete(String userID, Long userGroupID){
		String sql = "DELETE FROM " + this.getTableName() + " WHERE ";
		
		StringBuilder where = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		if(!Strings.isBlank(userID)){
			where.append(GroupUserRelation.USER_ID + " = ? "); 
			params.add(userID);
		}
		
		if(userGroupID != null){
			if(where.length() > 0){
				where.append(" AND ");
			}
			where.append(GroupUserRelation.USER_GROUP_ID+" = ? ");
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
	public List<GroupUserRelation> mapToList(List<Map<String, Object>> rows) {
		List<GroupUserRelation> list = new ArrayList<GroupUserRelation>();
		for (Map<String, Object> row : rows) {
			GroupUserRelation groupPermissionRelation = new GroupUserRelation();
			groupPermissionRelation.setId(Long.valueOf((Integer)row.get(GroupUserRelation.ID)));
			groupPermissionRelation.setUserID((String)row.get(GroupUserRelation.USER_ID));
			groupPermissionRelation.setUserGroupID(Long.valueOf((Integer)row.get(GroupUserRelation.USER_GROUP_ID)));
			list.add(groupPermissionRelation);
		}
		return list;
	}
	
	@Override
	public long update(GroupUserRelation entity) {
		
		String[] columns = new String[]{
				GroupUserRelation.USER_ID,
				GroupUserRelation.USER_GROUP_ID
		};

        return super.update(entity, columns);

	}
	
	public List<GroupUserRelation> findByUserGroupID(Long userGroupID) {
		return super.findBySingleField(GroupUserRelation.USER_GROUP_ID, userGroupID);
	}
	
	public List<GroupUserRelation> findByUserID(String userID) {
		return super.findBySingleField(GroupUserRelation.USER_ID, userID);
	}
	
	public GroupUserRelation findByUserIDAndUserGroupID(String userID, Long userGroupID) {  
		String sql = "SELECT * FROM " + this.getTableName() + " WHERE "+ GroupUserRelation.USER_ID +"=? AND "+ GroupUserRelation.USER_GROUP_ID + "=?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, userID,userGroupID);
        List<GroupUserRelation> list = mapToList(rows);
        if(list.size() > 0){
        	return list.get(0);
        }
        return null;
    }
}
