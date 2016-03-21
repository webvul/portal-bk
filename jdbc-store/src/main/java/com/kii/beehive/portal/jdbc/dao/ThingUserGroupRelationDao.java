package com.kii.beehive.portal.jdbc.dao;

import com.kii.beehive.portal.jdbc.entity.ThingUserGroupRelation;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String getKey() {
        return KEY;
    }

    public List<Long> findThingIds(Long userGroupId) {
        if (null == userGroupId) {
            return null;
        }
        return jdbcTemplate.queryForList(SQL_FIND_THINGIDS, new Object[]{userGroupId}, Long.class);
    }

    public List<Long> findUserGroupIds(Long thingId) {
        if (null == thingId) {
            return null;
        }
        return jdbcTemplate.queryForList(SQL_FIND_USERGROUPIDS, new Object[]{thingId}, Long.class);
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
}
