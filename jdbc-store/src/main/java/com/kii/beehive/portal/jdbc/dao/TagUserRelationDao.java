package com.kii.beehive.portal.jdbc.dao;

import com.kii.beehive.portal.jdbc.entity.TagUserRelation;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Created by hdchen on 3/18/16.
 */
@Repository
public class TagUserRelationDao extends SpringBaseDao<TagUserRelation> {
	final public static String TABLE_NAME = "rel_tag_user";
	final public static String KEY = "id";

	final private static String SQL_FIND_TAGIDS = "SELECT " + TagUserRelation.TAG_ID + " FROM " + TABLE_NAME + " WHERE " +
			"" + TagUserRelation.USER_ID + " = ?";

	final private static String SQL_FIND_ACCESSIBLE_TAGIDS = "SELECT " + TagUserRelation.TAG_ID + " FROM " +
			TABLE_NAME + " WHERE " + TagUserRelation.USER_ID + " = :userId AND " + TagUserRelation.TAG_ID + " IN " +
			"(:list)";


	final private static String SQL_FIND_USERIDS = "SELECT " + TagUserRelation.USER_ID + " FROM " + TABLE_NAME + " WHERE " +
			"" + TagUserRelation.TAG_ID + " = ?";

	final private static String SQL_FIND_BY_TAGID_AND_USERID = "SELECT * FROM " + TABLE_NAME + " WHERE " +
			"" + TagUserRelation.TAG_ID + " = ? AND " + TagUserRelation.USER_ID + " = ?";

	final private static String SQL_DELETE_BY_TAGID = "DELETE t.* FROM " + TABLE_NAME + " t WHERE " + TagUserRelation
			.TAG_ID + " = ?";

	final private static String SQL_DELETE_BY_TAGID_AND_USERID = SQL_DELETE_BY_TAGID + " AND " + TagUserRelation
			.USER_ID + " = ?";

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected String getKey() {
		return KEY;
	}

	public Optional<List<Long>> findTagIds(String userId) {
		if (null == userId) {
			return Optional.ofNullable(null);
		}
		return Optional.ofNullable(jdbcTemplate.queryForList(SQL_FIND_TAGIDS, new Object[]{userId}, Long.class));
	}

	public List<String> findUserIds(Long tagId) {
		if (null == tagId) {
			return null;
		}
		return jdbcTemplate.queryForList(SQL_FIND_USERIDS, new Object[]{tagId}, String.class);
	}

	public List<TagUserRelation> findByTagId(Long tagId) {
		if (null == tagId) {
			return null;
		}
		return findBySingleField(TagUserRelation.TAG_ID, tagId);
	}

	public Optional<List<TagUserRelation>> findByUserId(String userId) {
		if (null == userId) {
			return Optional.ofNullable(null);
		}
		return Optional.ofNullable(findBySingleField(TagUserRelation.USER_ID, userId));
	}

	public TagUserRelation find(Long tagId, String userId) {
		if (null == tagId || null == userId) {
			return null;
		}
		List<TagUserRelation> list = jdbcTemplate.query(SQL_FIND_BY_TAGID_AND_USERID, new Object[]{tagId,
				userId}, getRowMapper());
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public void deleteByTagId(Long tagId) {
		if (null == tagId) {
			return;
		}
		jdbcTemplate.update(SQL_DELETE_BY_TAGID, tagId);
	}

	public void deleteByTagIdAndUserId(Long tagId, String userId) {
		if (null == tagId || null == userId) {
			return;
		}
		jdbcTemplate.update(SQL_DELETE_BY_TAGID_AND_USERID, tagId, userId);
	}

	public Optional<List<Long>> findAccessibleTagIds(String userId, Collection<Long> tagIds) {
		if (null == userId || null == tagIds || tagIds.isEmpty()) {
			return Optional.ofNullable(null);
		}
		Map<String, Object> params = new HashMap();
		params.put("userId", userId);
		params.put("list", tagIds);
		return Optional.ofNullable(namedJdbcTemplate.queryForList(SQL_FIND_ACCESSIBLE_TAGIDS, params, Long.class));
	}
}
