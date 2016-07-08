package com.kii.beehive.portal.jdbc.dao;

import com.kii.beehive.portal.jdbc.entity.TagThingRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public class TagThingRelationDao extends SpringSimpleBaseDao<TagThingRelation> {

	public static final String TABLE_NAME = "rel_thing_tag";
	public static final String KEY = "id";
	private static final String SQL_FIND_THINGIDS = "SELECT " + TagThingRelation.THING_ID + " FROM " + TABLE_NAME + "" +
			" WHERE " + TagThingRelation.TAG_ID + " = ?";
	private static final String SQL_FIND_THINGIDS_BY_TAGIDS = "SELECT " + TagThingRelation.THING_ID + " FROM " +
			TABLE_NAME + "" +
			" WHERE " + TagThingRelation.TAG_ID + " IN (:tagIds)";
	private Logger log = LoggerFactory.getLogger(TagThingRelationDao.class);

	public void delete(Long tagID, Long thingID) {
		if (tagID != null || thingID != null) {
			String sql = "DELETE FROM " + this.getTableName() + " WHERE ";

			StringBuilder where = new StringBuilder();
			List<Object> params = new ArrayList<Object>();
			if (thingID != null) {
				where.append(TagThingRelation.THING_ID + " = ? ");
				params.add(thingID);
			}

			if (tagID != null) {
				if (where.length() > 0) {
					where.append(" AND ");
				}
				where.append(TagThingRelation.TAG_ID + " = ? ");
				params.add(tagID);
			}
			Object[] paramArr = new Object[params.size()];
			paramArr = params.toArray(paramArr);

			jdbcTemplate.update(sql + where.toString(), paramArr);
		} else {
			log.warn("tagID and thingID are null");
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


	public TagThingRelation findByThingIDAndTagID(Long thingID, Long tagID) {
		if (tagID != null && thingID != null) {
			String sql = "SELECT * FROM " + this.getTableName() + " WHERE " + TagThingRelation.THING_ID + "=? AND " + TagThingRelation.TAG_ID + "=?";
			List<TagThingRelation> list = jdbcTemplate.query(sql, new Object[]{thingID, tagID}, getRowMapper());
			if (list.size() > 0) {
				return list.get(0);
			}
		}
		return null;
	}

	public Optional<List<Long>> findThingIds(Long tagId) {
		if (null == tagId) {
			return Optional.ofNullable(null);
		}
		return Optional.ofNullable(jdbcTemplate.queryForList(SQL_FIND_THINGIDS, new Object[]{tagId}, Long.class));
	}

	public Optional<List<Long>> findThingIds(Collection<Long> tagIds) {
		if (null == tagIds || tagIds.isEmpty()) {
			return Optional.ofNullable(null);
		}
		Map<String, Object> param = new HashMap();
		param.put("tagIds", tagIds);
		return Optional.ofNullable(namedJdbcTemplate.queryForList(SQL_FIND_THINGIDS_BY_TAGIDS, param, Long.class));
	}

	public Optional<List<Long>> findTagIds(Long thingId) {
		if (null == thingId) {
			return Optional.ofNullable(null);
		}
		return Optional.ofNullable(findSingleFieldBySingleField(TagThingRelation.TAG_ID,
				TagThingRelation.THING_ID, thingId, Long.class));
	}
}
