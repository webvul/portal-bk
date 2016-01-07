package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.UserGroup;

@Repository
public class UserGroupDao extends BaseDao<UserGroup> {

	private Logger log= LoggerFactory.getLogger(UserGroupDao.class);
	
	public static final String TABLE_NAME = "user_group";
	public static final String KEY = "user_group_id";
	
	public List<UserGroup> findUserGroup(String userID, Long userGroupID, String name) {
		String sql = "SELECT u.user_group_id,u.name,u.description,u.create_by,u.create_date,u.modify_by,u.modify_date "
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
		
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, paramArr);
	    return mapToList(rows);
	}
	
	
	@Override
	public long update(UserGroup tag) {
		String[] columns = new String[]{
				UserGroup.NAME,
				UserGroup.DESCRIPTION,
				UserGroup.CREATE_DATE,
				UserGroup.CREATE_BY,
				UserGroup.MODIFY_DATE,
				UserGroup.MODIFY_BY,
		};

        return super.update(tag, columns);
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
	public List<UserGroup> mapToList(List<Map<String, Object>> rows) {
		List<UserGroup> list = new ArrayList<UserGroup>();
		for (Map<String, Object> row : rows) {
			UserGroup entity = new UserGroup();
			entity.setId(Long.valueOf((Integer)row.get(UserGroup.USER_GROUP_ID)));
			entity.setName((String)row.get(UserGroup.NAME));
			entity.setDescription((String)row.get(UserGroup.DESCRIPTION));
			mapToListForDBEntity(entity, row);
			list.add(entity);
		}
		return list;
	}
}
