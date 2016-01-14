package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.Permission;

@Repository
public class PermissionDao extends BaseDao<Permission> {

	//private Logger log= LoggerFactory.getLogger(PermissionDao.class);
	
	public static final String TABLE_NAME = "permission";
	public static final String KEY = "permission_id";
	
	@Override
	public long update(Permission tag) {
		String[] columns = new String[]{
				Permission.SOURCE_ID,
				Permission.NAME,
				Permission.ACTION,
				Permission.DESCRIPTION,
				Permission.CREATE_DATE,
				Permission.CREATE_BY,
				Permission.MODIFY_DATE,
				Permission.MODIFY_BY,
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
	public List<Permission> mapToList(List<Map<String, Object>> rows) {
		List<Permission> list = new ArrayList<Permission>();
		for (Map<String, Object> row : rows) {
			Permission entity = new Permission();
			entity.setId(Long.valueOf((Integer)row.get(Permission.PERMISSION_ID)));
			entity.setSourceID(Long.valueOf((Integer)row.get(Permission.SOURCE_ID)));
			entity.setName((String)row.get(Permission.NAME));
			entity.setAction((String)row.get(Permission.ACTION));
			entity.setDescription((String)row.get(Permission.DESCRIPTION));
			entity.setSourceName((String)row.get(Permission.SOURCE_NAME));
			mapToListForDBEntity(entity, row);
			list.add(entity);
		}
		return list;
	}
	
	public List<Permission> findByUserGroupID(Long userGroupID) {
		String sql = "SELECT p.permission_id, p.source_id,p.name,p.action,p.description,p.create_by,p.create_date,p.modify_by,p.modify_date "
					+ "FROM " + this.getTableName() +" p "
					+ "INNER JOIN rel_group_permission r ON r.permission_id = p.permission_id "
					+ "WHERE r.user_group_id = ?";
		
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql,userGroupID);
	    return mapToList(rows);
	}
	
	public List<Permission> findAll() {
		String sql = "SELECT p.permission_id, p.source_id,s.name sourceName,p.name,p.action,p.description,p.create_by,p.create_date,p.modify_by,p.modify_date "
					+ "FROM " + this.getTableName() +" p "
					+ "INNER JOIN source s ON s.source_id = p.source_id ";
		
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
	    return mapToList(rows);
	}
}
