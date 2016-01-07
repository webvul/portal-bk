package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.UserGroup;

@Repository
public class UserGroupDao extends BaseDao<UserGroup> {

	//private Logger log= LoggerFactory.getLogger(UserGroupDao.class);
	
	public static final String TABLE_NAME = "permission";
	public static final String KEY = "permission_id";
	
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
			entity.setId((int)row.get(UserGroup.USER_GROUP_ID));
			entity.setName((String)row.get(UserGroup.NAME));
			entity.setDescription((String)row.get(UserGroup.DESCRIPTION));
			mapToListForDBEntity(entity, row);
			list.add(entity);
		}
		return list;
	}
}
