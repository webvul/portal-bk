package com.kii.beehive.portal.jdbc.dao;

import com.kii.beehive.portal.jdbc.entity.ThingUserRelation;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hdchen on 3/18/16.
 */
@Repository
public class ThingUserRelationDao extends SpringBaseDao<ThingUserRelation> {
    final public static String TABLE_NAME = "rel_thing_user";
    final public static String KEY = "id";

    final private static String SQL_FIND_THINGIDS = "SELECT " + ThingUserRelation.THING_ID + " FROM " +
            "" + TABLE_NAME + " WHERE " + ThingUserRelation.USER_ID + " = ?";

    final private static String SQL_FIND_USERIDS = "SELECT " + ThingUserRelation.USER_ID + " FROM " +
            TABLE_NAME
            + " WHERE " + ThingUserRelation.THING_ID + " = ?";

    final private static String SQL_FIND_BY_THINGID_AND_USERID = "SELECT * FROM " + TABLE_NAME + " WHERE " +
            ThingUserRelation
                    .THING_ID + " = ? AND " + ThingUserRelation.USER_ID + " = ?";

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String getKey() {
        return KEY;
    }

    public List<Long> findThingIds(String userId) {
        if (null == userId) {
            return null;
        }
        return jdbcTemplate.queryForList(SQL_FIND_THINGIDS, new Object[]{userId}, Long.class);
    }

    public List<String> findUserIds(Long thingId) {
        if (null == thingId) {
            return null;
        }
        return jdbcTemplate.queryForList(SQL_FIND_USERIDS, new Object[]{thingId}, String.class);
    }

    public List<ThingUserRelation> findByThingId(Long thingId) {
        if (null == thingId) {
            return null;
        }
        return findBySingleField(ThingUserRelation.THING_ID, thingId);
    }

    public List<ThingUserRelation> findByUserId(String userId) {
        if (null == userId) {
            return null;
        }
        return findBySingleField(ThingUserRelation.USER_ID, userId);
    }

    public ThingUserRelation find(Long thingId, String userId) {
        if (null == userId || null == thingId) {
            return null;
        }
        List<ThingUserRelation> list = jdbcTemplate.query(SQL_FIND_BY_THINGID_AND_USERID, new Object[]{thingId,
                userId}, getRowMapper());
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    public void deleteByThingIdAndUserId(Long thingId, String userId) {
        if (null == thingId || null == userId) {
            return;
        }
        jdbcTemplate.update("DELETE t.* FROM " + TABLE_NAME + " t WHERE " + ThingUserRelation
                .THING_ID + " = ? AND " + ThingUserRelation
                .USER_ID + " = ?", thingId, userId);
    }
}
