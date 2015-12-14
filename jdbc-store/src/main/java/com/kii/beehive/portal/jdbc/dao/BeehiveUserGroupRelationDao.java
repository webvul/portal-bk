package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.BeehiveUserGroupRelation;

@Repository
public class BeehiveUserGroupRelationDao extends BaseDao<BeehiveUserGroupRelation> {

	private Logger log= LoggerFactory.getLogger(BeehiveUserGroupRelationDao.class);
	
	public static final String TABLE_NAME = "rel_user_group";
	public static final String KEY = "id";
	
	public void delete( Long userGroupID, Long userID){
		String sql = "DELETE FROM " + this.getTableName() + " WHERE ";
		
		StringBuilder where = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		if(userGroupID != null){
			where.append(BeehiveUserGroupRelation.USER_GROUP_ID + " = ? ");
			params.add(userGroupID);
		}
		
		if(userID != null){
			if(where.length() > 0){
				where.append(" AND ");
			}
			where.append(BeehiveUserGroupRelation.USER_ID+" = ? ");
			params.add(userID);
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
	public List<BeehiveUserGroupRelation> mapToList(List<Map<String, Object>> rows) {
		List<BeehiveUserGroupRelation> list = new ArrayList<BeehiveUserGroupRelation>();
		for (Map<String, Object> row : rows) {
			BeehiveUserGroupRelation relation = new BeehiveUserGroupRelation();
			relation.setId((long)row.get(BeehiveUserGroupRelation.ID));
			relation.setUserGroupID((long)row.get(BeehiveUserGroupRelation.USER_GROUP_ID));
			relation.setUserID((long)row.get(BeehiveUserGroupRelation.USER_ID));
			list.add(relation);
		}
		return list;
	}
	
	@Override
	public long update(BeehiveUserGroupRelation entity) {
		// TODO Auto-generated method stub
		return 0;
	}
}
