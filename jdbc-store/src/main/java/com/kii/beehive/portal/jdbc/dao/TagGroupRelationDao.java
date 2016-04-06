package com.kii.beehive.portal.jdbc.dao;

import com.kii.beehive.portal.jdbc.entity.GroupUserRelation;
import com.kii.beehive.portal.jdbc.entity.TagGroupRelation;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TagGroupRelationDao extends SpringBaseDao<TagGroupRelation> {

	public static final String TABLE_NAME = "rel_tag_group";
	public static final String KEY = "id";
	private static final String SQL_FIND_TAGIDS_BY_USER_AND_FILTER_BY = "SELECT DISTINCT t." + TagGroupRelation.TAG_ID +
			" FROM (SELECT DISTINCT " + TagGroupRelation.TAG_ID + " FROM " + TABLE_NAME + " t1 INNER JOIN (SELECT " +
			GroupUserRelation.USER_GROUP_ID + " FROM " + GroupUserRelationDao.TABLE_NAME + " WHERE " +
			GroupUserRelation.USER_ID + " = ?) t2 ON t1." + TagGroupRelation.USER_GROUP_ID + " = t2." +
			GroupUserRelation.USER_GROUP_ID + ") t ";

	private static final String SQL_FIND_TAGIDS_BY_USER_AND_FILTER_BY_FULLNAME = "SELECT DISTINCT t." +
			TagGroupRelation.TAG_ID +
			" FROM (SELECT DISTINCT " + TagGroupRelation.TAG_ID + " FROM " + TABLE_NAME + " t1 INNER JOIN (SELECT " +
			GroupUserRelation.USER_GROUP_ID + " FROM " + GroupUserRelationDao.TABLE_NAME + " WHERE " +
			GroupUserRelation.USER_ID + " = :userID) t2 ON t1." + TagGroupRelation.USER_GROUP_ID + " = t2." +
			GroupUserRelation.USER_GROUP_ID + ") t ";


	private Logger log = LoggerFactory.getLogger(TagGroupRelationDao.class);

	public void delete(Long tagID, Long userGroupID) {
		if (tagID != null || userGroupID != null) {
			String sql = "DELETE FROM " + this.getTableName() + " WHERE ";

			StringBuilder where = new StringBuilder();
			List<Object> params = new ArrayList<Object>();
			if (tagID != null) {
				where.append(TagGroupRelation.TAG_ID + " = ? ");
				params.add(tagID);
			}

			if (userGroupID != null) {
				if (where.length() > 0) {
					where.append(" AND ");
				}
				where.append(TagGroupRelation.USER_GROUP_ID + " = ? ");
				params.add(userGroupID);
			}
			Object[] paramArr = new Object[params.size()];
			paramArr = params.toArray(paramArr);

			jdbcTemplate.update(sql + where.toString(), paramArr);
		} else {
			log.warn("tagID and userGroupID are null");
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

	public TagGroupRelation findByTagIDAndUserGroupID(Long tagID, Long userGroupID) {
		if (tagID != null && userGroupID != null) {
			String sql = "SELECT * FROM " + this.getTableName() + " WHERE " + TagGroupRelation.TAG_ID + "=? AND " + TagGroupRelation.USER_GROUP_ID + "=?";
			List<TagGroupRelation> list = jdbcTemplate.query(sql, new Object[]{tagID, userGroupID}, getRowMapper());
			if (list.size() > 0) {
				return list.get(0);
			}
		}
		return null;
	}

	public List<TagGroupRelation> findByUserGroupID(Long userGroupID) {
		return super.findBySingleField(TagGroupRelation.USER_GROUP_ID, userGroupID);
	}

	public Optional<List<Long>> findTagIdsByUserGroupId(Long userGroupId) {
		if (null == userGroupId) {
			return Optional.ofNullable(null);
		}
		return Optional.ofNullable(findSingleFieldBySingleField(TagGroupRelation.TAG_ID,
				TagGroupRelation.USER_GROUP_ID, userGroupId, Long.class));
	}

	public Optional<List<Long>> findTagIdsByUserId(String userId, String tagType, String displayName) {
		if (Strings.isBlank(userId)) {
			return Optional.ofNullable(null);
		}
		List<Object> params = new ArrayList();
		params.add(userId);
		if (Strings.isBlank(tagType) && Strings.isBlank(displayName)) {
			return Optional.ofNullable(jdbcTemplate.queryForList(SQL_FIND_TAGIDS_BY_USER_AND_FILTER_BY,
					params.toArray(new Object[]{}), Long.class));
		}

		StringBuilder sb = new StringBuilder();
		if (!Strings.isBlank(tagType)) {
			sb.append("t3.").append(TagIndex.TAG_TYPE).append(" = ?");
			params.add(tagType);
		}
		if (!Strings.isBlank(displayName)) {
			if (0 != sb.length()) {
				sb.append(" AND ");
			}
			sb.append("t3.").append(TagIndex.DISPLAY_NAME).append(" = ?");
			params.add(displayName);
		}
		sb = new StringBuilder(SQL_FIND_TAGIDS_BY_USER_AND_FILTER_BY).append(" INNER JOIN ").
				append(TagIndexDao.TABLE_NAME).append(" t3 ON t.").append(TagGroupRelation.TAG_ID)
				.append(" = t3.").append(TagIndex.TAG_ID).append(" AND ").append(sb);
		return Optional.ofNullable(jdbcTemplate.queryForList(sb.toString(), params.toArray(new Object[]{}),
				Long.class));
	}

	public Optional<List<Long>> findUserGroupIdsByTagIds(List<Long> tagIds) {
		if (null == tagIds || tagIds.isEmpty()) {
			return Optional.ofNullable(null);
		}
		return Optional.ofNullable(findSingleFieldBySingleField(TagGroupRelation.USER_GROUP_ID, TagGroupRelation.TAG_ID,
				tagIds, Long.class));
	}

	public Optional<List<Long>> findTagIdsByUserIdAndFullTagName(String userId, List<String> fullTagName) {
		if (Strings.isBlank(userId)) {
			return Optional.ofNullable(null);
		}

		Map<String, Object> params = new HashMap();
		params.put("userID", userId);

		StringBuilder sb = new StringBuilder(SQL_FIND_TAGIDS_BY_USER_AND_FILTER_BY_FULLNAME).append(" INNER JOIN ").
				append(TagIndexDao.TABLE_NAME).append(" t3 ON t.").append(TagGroupRelation.TAG_ID)
				.append(" = t3.").append(TagIndex.TAG_ID);

		if (null != fullTagName && !fullTagName.isEmpty()) {
			sb.append(" AND ").append("t3.").append(TagIndex.FULL_TAG_NAME).append(" IN (:fullNames)");
			params.put("fullNames", fullTagName);
		}

		return Optional.ofNullable(namedJdbcTemplate.queryForList(sb.toString(), params,
				Long.class));
	}

	/**
	 * @param userId
	 * @return a list of tag ids which user can access through the user groups
	 */
	public Optional<List<Long>> findTagIdsByUserId(String userId) {
		return findTagIdsByUserIdAndFullTagName(userId, null);
	}
}
