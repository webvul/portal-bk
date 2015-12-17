package com.kii.beehive.portal.jdbc.dao;

import java.util.*;

import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.exception.StoreException;
import com.kii.beehive.portal.jdbc.entity.BeehiveUserGroup;

@Repository
public class BeehiveUserGroupDao extends BaseDao<BeehiveUserGroup>{


	public static final String TABLE_NAME = "beehive_user_group";

	@Override
	protected Class<BeehiveUserGroup> getEntityCls() {
		return BeehiveUserGroup.class;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}


	@Override
	public String getKey() {
		return BeehiveUserGroup.USER_GROUP_ID;
	}

	@Override
	public List<BeehiveUserGroup> mapToList(List<Map<String, Object>> rows) {
		List<BeehiveUserGroup> list = new ArrayList<BeehiveUserGroup>();
		for (Map<String, Object> row : rows) {

			BeehiveUserGroup beehiveUserGroup = new BeehiveUserGroup();
			beehiveUserGroup.setId((Integer)row.get(BeehiveUserGroup.USER_GROUP_ID));
			beehiveUserGroup.setUserGroupName((String)row.get(BeehiveUserGroup.USER_GROUP_NAME));
			beehiveUserGroup.setDescription((String)row.get(BeehiveUserGroup.DESCRIPTION));

			mapToListForDBEntity(beehiveUserGroup, row);

			list.add(beehiveUserGroup);
		}
		return list;
	}

	public long createUserGroup(BeehiveUserGroup userGroup) {

		long userGroupID = super.saveOrUpdate(userGroup);
		userGroup.setId(userGroupID);

		return userGroupID;
	}

	public void updateUserGroup(long userGroupID, BeehiveUserGroup userGroup) {


		boolean isExist = super.IsIdExist(userGroupID);
		if (isExist) {
			super.saveOrUpdate(userGroup);
		} else {
			throw new StoreException(userGroupID + " not found");
		}

	}

	public void deleteUserGroup(String userGroupID) {
		super.deleteByID(userGroupID);
	}

	public BeehiveUserGroup getUserGroupByID(String userGroupID) {

		return super.findByID(userGroupID);
	}

	public List<BeehiveUserGroup> getUserGroupByIDs(List<String> userGroupIDs) {

		return super.findByIDs(userGroupIDs.toArray(new String[0]));
	}

	public List<BeehiveUserGroup> getAll() {
		return super.findAll();
	}

	public List<BeehiveUserGroup> findUserGroupByUserGroupName(String userGroupName) {

		String sql = "SELECT * FROM " + this.getTableName() + " WHERE user_group_name = ?";

		Object[] params = new String[] {userGroupName};
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, params);
		return mapToList(rows);

	}

	public List<BeehiveUserGroup> findUserGroupByUserGroupNameLike(String userGroupName) {

		String sql = "SELECT * FROM " + this.getTableName() + " WHERE user_group_name like ?";

		Object[] params = new String[] {"%" + userGroupName + "%"};
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, params);
		return mapToList(rows);

	}
	


	@Override
	public long update(BeehiveUserGroup entity) {
		// TODO Auto-generated method stub
		return 0;
	}
}
