package com.kii.beehive.portal.jdbc.dao;

import java.util.*;

import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.exception.StoreException;
import com.kii.beehive.portal.jdbc.entity.BeehiveUserGroup;
import com.kii.beehive.portal.jdbc.entity.TagType;

@Repository
public class BeehiveUserGroupDao extends BaseDao<BeehiveUserGroup>{


	public static final String TABLE_NAME = "beehive_user_group";

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
			beehiveUserGroup.setUserGroupID((String)row.get(BeehiveUserGroup.USER_GROUP_ID));
			beehiveUserGroup.setUserGroupName((String)row.get(BeehiveUserGroup.USER_GROUP_NAME));
			beehiveUserGroup.setDescription((String)row.get(BeehiveUserGroup.DESCRIPTION));

			mapToListForDBEntity(beehiveUserGroup, row);

			list.add(beehiveUserGroup);
		}
		return list;
	}

	public String createUserGroup(BeehiveUserGroup userGroup) {


		String userGroupID = userGroup.getUserGroupID();
		boolean isExist = super.IsIdExist(userGroupID);
		if (isExist) {
			throw new StoreException(userGroupID);
		} else {
			super.saveOrUpdate(userGroup);
		}

		return userGroupID;
	}

	public void updateUserGroup(String userGroupID, BeehiveUserGroup userGroup) {


		boolean isExist = super.IsIdExist(userGroupID);
		if (isExist) {
			super.saveOrUpdate(userGroup);
		} else {
			throw new StoreException(userGroupID);
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

		String sql = "SELECT * FROM " + this.getTableName() + " WHERE user_group_name like '%?%'";

		Object[] params = new String[] {userGroupName};
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, params);
		return mapToList(rows);

	}
}
