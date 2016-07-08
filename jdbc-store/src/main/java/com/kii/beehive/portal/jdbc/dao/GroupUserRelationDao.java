package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.GroupUserRelation;

@Repository
public class GroupUserRelationDao extends SpringSimpleBaseDao<GroupUserRelation> {


	public static final String TABLE_NAME = "rel_group_user";
	public static final String KEY = "id";


	public void delete(Long userID, Long userGroupID) {
		if (userID!=null || userGroupID != null) {
			String sql = "DELETE FROM " + this.getTableName() + " WHERE ";

			StringBuilder where = new StringBuilder();
			List<Object> params = new ArrayList<Object>();
			if (userID!=null) {
				where.append(GroupUserRelation.USER_ID + " = ? ");
				params.add(userID);
			}

			if (userGroupID != null) {
				if (where.length() > 0) {
					where.append(" AND ");
				}
				where.append(GroupUserRelation.USER_GROUP_ID + " = ? ");
				params.add(userGroupID);
			}
			Object[] paramArr = new Object[params.size()];
			paramArr = params.toArray(paramArr);

			jdbcTemplate.update(sql + where.toString(), paramArr);
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




	public List<GroupUserRelation> findByUserID(Long userID) {
		return super.findBySingleField(GroupUserRelation.USER_ID, userID);
	}

	public GroupUserRelation findByUserIDAndUserGroupID(Long userID, Long userGroupID) {
		if (userID!=null && userGroupID != null) {
			String sql = "SELECT * FROM " + this.getTableName() + " WHERE " + GroupUserRelation.USER_ID + "=? AND " + GroupUserRelation.USER_GROUP_ID + "=?";
			List<GroupUserRelation> list = jdbcTemplate.query(sql, new Object[]{userID, userGroupID}, getRowMapper());
			if (list.size() > 0) {
				return list.get(0);
			}
		}
		return null;
	}

//	public List<String> findUserIDByUserGroupID(Long userGroupID) {
//
//		String sql = "SELECT " + GroupUserRelation.USER_ID + " FROM " + this.getTableName() + " WHERE " + GroupUserRelation.USER_GROUP_ID + "=?";
//		List<String> rows = jdbcTemplate.queryForList(sql, String.class, userGroupID);
//		return rows;
//	}
//
//	public Optional<List<Long>> findUserIds(Collection<Long> userGroupIds) {
//		if (null == userGroupIds || userGroupIds.isEmpty()) {
//			return Optional.ofNullable(null);
//		}
//		Map<String, Object> params = new HashMap();
//		params.put("groupIds", userGroupIds);
//		return Optional.ofNullable(namedJdbcTemplate.queryForList(SQL_FIND_USERIDS_BY_GROUPIDS, params, String.class));
//	}

	/**
	 * delete users from user group
	 *
	 * @param userIDList
	 * @param userGroupID mandatory field
	 * @return
	 */
	public int deleteUsers(List<Long> userIDList, Long userGroupID) {

		int size = userIDList.size();
		if (size > 0) {
			Object[] params = new Object[size + 1];
			params[0] = userGroupID;

			StringBuilder sb = new StringBuilder(size * 2 - 1);
			for (int i = 0; i < size; i++) {
				if (sb.length() > 0)
					sb.append(",");
				sb.append("?");
				params[i + 1] = userIDList.get(i);
			}

			String sql = "DELETE FROM  " + this.getTableName()
					+ " WHERE " + GroupUserRelation.USER_GROUP_ID + "=? AND " + GroupUserRelation.USER_ID + " IN (" + sb.toString() + ")";
			return jdbcTemplate.update(sql, params);
		}

		return 0;
	}

	public List<Long> findUserGroupIds(Long userId) {

		return findSingleFieldBySingleField(GroupUserRelation.USER_GROUP_ID,
				GroupUserRelation.USER_ID, userId, Long.class);
	}
}
