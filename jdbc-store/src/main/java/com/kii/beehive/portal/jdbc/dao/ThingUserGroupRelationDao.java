package com.kii.beehive.portal.jdbc.dao;

import com.kii.beehive.portal.jdbc.entity.GroupUserRelation;
import com.kii.beehive.portal.jdbc.entity.ThingUserGroupRelation;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by hdchen on 3/18/16.
 */
@Repository
public class ThingUserGroupRelationDao extends SpringBaseDao<ThingUserGroupRelation> {
	final public static String TABLE_NAME = "rel_thing_group";
	final public static String KEY = "id";

	final private static String SQL_FIND_THINGIDS = "SELECT " + ThingUserGroupRelation.THING_ID + " FROM " + TABLE_NAME + " " +
			"WHERE " + ThingUserGroupRelation.USER_GROUP_ID + " = ?";

	final private static String SQL_FIND_USERGROUPIDS = "SELECT " + ThingUserGroupRelation.USER_GROUP_ID + " FROM " + TABLE_NAME + " " +
			"WHERE " + ThingUserGroupRelation.THING_ID + " = ?";

	final private static String SQL_FIND_BY_THINGID_AND_USERGROUPID = "SELECT * FROM " + TABLE_NAME + " " +
			"WHERE " + ThingUserGroupRelation.THING_ID + " = ? AND " + ThingUserGroupRelation.USER_GROUP_ID + " = ?";

	final private static String SQL_FIND_BY_THINGID_AND_USERID = "SELECT * FROM " + TABLE_NAME + " WHERE " +
			ThingUserGroupRelation.THING_ID + " = ? AND " + ThingUserGroupRelation.USER_GROUP_ID + " IN (SELECT " +
			GroupUserRelation.USER_GROUP_ID + " FROM " + GroupUserRelationDao.TABLE_NAME + " WHERE " +
			GroupUserRelation.USER_ID + " = ?)";

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected String getKey() {
		return KEY;
	}

	public Optional<List<Long>> findThingIds(Long userGroupId) {
		if (null == userGroupId) {
			return Optional.ofNullable(null);
		}
		return Optional.ofNullable(jdbcTemplate.queryForList(SQL_FIND_THINGIDS, new Object[]{userGroupId}, Long.class));
	}

	public Optional<List<Long>> findUserGroupIds(Long thingId) {
		if (null == thingId) {
			return Optional.ofNullable(null);
		}
		return Optional.ofNullable(jdbcTemplate.queryForList(SQL_FIND_USERGROUPIDS, new Object[]{thingId}, Long.class));
	}

	public List<ThingUserGroupRelation> findByThingId(Long thingId) {
		if (null == thingId) {
			return null;
		}
		return findBySingleField(ThingUserGroupRelation.THING_ID, thingId);
	}

	public List<ThingUserGroupRelation> findByUserGroupId(Long userGroupId) {
		if (null == userGroupId) {
			return null;
		}
		return findBySingleField(ThingUserGroupRelation.USER_GROUP_ID, userGroupId);
	}

	public ThingUserGroupRelation find(Long thingId, Long userGroupId) {
		if (null == thingId || null == userGroupId) {
			return null;
		}
		List<ThingUserGroupRelation> list = jdbcTemplate.query(SQL_FIND_BY_THINGID_AND_USERGROUPID, new Object[]{thingId,
				userGroupId}, getRowMapper());
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public void deleteByThingIdAndUserGroupId(Long thingId, Long userGroupId) {
		if (null == thingId || null == userGroupId) {
			return;
		}
		jdbcTemplate.update("DELETE t.* FROM " + TABLE_NAME + " t WHERE " + ThingUserGroupRelation
				.THING_ID + " = ? AND " + ThingUserGroupRelation
				.USER_GROUP_ID + " = ?", thingId, userGroupId);
	}

	public List<ThingUserGroupRelation> findByThingIdAndUserId(Long thingId, String userId) {
		if (null == thingId || null == userId) {
			return Collections.emptyList();
		}
		return Optional.ofNullable(jdbcTemplate.query(SQL_FIND_BY_THINGID_AND_USERID, new Object[]{thingId, userId},
				getRowMapper())).orElse(Collections.emptyList());
	}
}
