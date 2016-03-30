package com.kii.beehive.portal.jdbc.dao;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagThingRelation;
import com.kii.beehive.portal.jdbc.entity.TagType;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TagIndexDao extends SpringBaseDao<TagIndex> {

	//private Logger log= LoggerFactory.getLogger(TagIndexDao.class);

	public static final String TABLE_NAME = "tag_index";
	public static final String KEY = "tag_id";

	private static final String SQL_FIND_USER_TAG = "SELECT * FROM " + TABLE_NAME + " WHERE " + TagIndex.CREATE_BY +
			" = ?";

	private static final String SQL_FIND_USER_LOCATION = "SELECT t." + TagIndex.DISPLAY_NAME + " FROM " + TABLE_NAME + "" +
			" t WHERE " + TagIndex.CREATE_BY + " = ?";

	private static final String SQL_FIND_TAGIDS_BY_TAGIDS_AND_FULLNAMES = "SELECT " + TagIndex.TAG_ID + " FROM " +
			TABLE_NAME + " WHERE " + TagIndex.TAG_ID + " IN (:tagIds) AND " + TagIndex.FULL_TAG_NAME +
			" IN (:fullNames)";

	/**
	 * find tag list by tagType and displayName
	 *
	 * @param tagType
	 * @param displayName if it's null or empty, will query all displayName under the tagType
	 * @return
	 */
	public List<TagIndex> findTag(Long tagID, String tagType, String displayName) {
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.* FROM " + this.getTableName() + " t "
				+ " LEFT JOIN rel_tag_group rt ON t.tag_id = rt.tag_id "
				+ " LEFT JOIN user_group u ON u.user_group_id = rt.user_group_id "
				+ " LEFT JOIN rel_group_user rg ON u.user_group_id = rg.user_group_id  ");

		StringBuilder where = new StringBuilder();
		where.append(" WHERE (rg.user_id = ? OR t.create_by=?)");
		params.add(AuthInfoStore.getUserID());
		params.add(AuthInfoStore.getUserID());

		if (tagID != null) {
			where.append(" AND t.").append(TagIndex.TAG_ID).append(" = ? ");
			params.add(tagID);
		}

		if (!Strings.isBlank(tagType)) {
			where.append(" AND t.").append(TagIndex.TAG_TYPE).append(" = ? ");
			params.add(tagType);
		}

		if (!Strings.isBlank(displayName)) {
			where.append(" AND t.").append(TagIndex.DISPLAY_NAME).append(" = ? ");
			params.add(displayName);
		}

		sql.append(where);
		List<TagIndex> rows = jdbcTemplate.query(sql.toString(), params.toArray(new Object[params.size()]), getRowMapper());
		return rows;
	}

	public List<TagIndex> findUserTagByTypeAndName(String userId, String tagType, String displayName) {
		List<Object> params = new ArrayList<>();
		params.add(userId);
		StringBuilder sb = new StringBuilder(SQL_FIND_USER_TAG);
		if (!Strings.isBlank(tagType)) {
			sb.append(" AND ");
			sb.append(TagIndex.TAG_TYPE);
			sb.append(" = ?");
			params.add(tagType);
		}
		if (!Strings.isBlank(displayName)) {
			sb.append(" AND ");
			sb.append(TagIndex.DISPLAY_NAME);
			sb.append(" = ?");
			params.add(displayName);
		}
		return jdbcTemplate.query(sb.toString(), params.toArray(new Object[]{}), getRowMapper());
	}

	/**
	 * find tag list by tagType and displayName
	 *
	 * @param tagType
	 * @param displayName if it's null or empty, will query all displayName under the tagType
	 * @return
	 */
	public List<TagIndex> findTagByTagTypeAndName(String tagType, String displayName) {
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.*, COUNT(r.thing_id) count, GROUP_CONCAT(r.thing_id) things "
				+ "FROM " + this.getTableName() + " t "
				+ "LEFT JOIN rel_thing_tag r ON r.tag_id = t.tag_id ");

		StringBuilder where = new StringBuilder();
		if (AuthInfoStore.getTeamID() != null) {
			sql.append(" INNER JOIN rel_team_tag rt ON t.tag_id=rt.tag_id ");
			where.append(" rt.team_id = ? ");
			params.add(AuthInfoStore.getTeamID());
		}

		if (!Strings.isBlank(tagType)) {
			if (where.length() > 0) {
				where.append(" AND ");
			}
			where.append("t.").append(TagIndex.TAG_TYPE).append(" = ? ");
			params.add(tagType);
		}

		if (!Strings.isBlank(displayName)) {
			if (where.length() > 0) {
				where.append(" AND ");
			}
			where.append("t.").append(TagIndex.DISPLAY_NAME).append(" = ? ");
			params.add(displayName);
		}
		if (where.length() > 0) {
			where.insert(0, " WHERE ");
		}

		where.append("GROUP BY t.").append(TagIndex.TAG_ID);
		sql.append(where);

		List<TagIndex> rows = jdbcTemplate.query(sql.toString(), params.toArray(new Object[params.size()]), getRowMapper());
		return rows;
	}

	public List<String> findUserLocations(String userId, String parentLocation) {
		List<Object> params = new ArrayList<>();
		params.add(userId);
		StringBuilder sb = new StringBuilder(SQL_FIND_USER_LOCATION);
		if (!Strings.isBlank(parentLocation)) {
			sb.append(" AND t.").append(TagIndex.DISPLAY_NAME).append(" LIKE ?");
			params.add(parentLocation + '%');
		}
		sb.append(" ORDER BY t.").append(TagIndex.DISPLAY_NAME);
		return jdbcTemplate.queryForList(sb.toString(), params.toArray(new Object[]{}), String.class);
	}

	public List<String> findLocations(String parentLocation) {

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT t.").append(TagIndex.DISPLAY_NAME).append(" FROM ").append(this.getTableName() + " t ");

		StringBuffer where = new StringBuffer();
		where.append(" WHERE t.").append(TagIndex.TAG_TYPE).append(" = ? ")
				.append(" AND t.").append(TagIndex.DISPLAY_NAME).append(" like ? ");


		if (AuthInfoStore.getTeamID() != null) {
			sql.append(" INNER JOIN rel_team_tag rt ON t.tag_id=rt.tag_id ");
			where.append("AND rt.team_id = ").append(AuthInfoStore.getTeamID());
		}

		where.append(" ORDER BY t.").append(TagIndex.DISPLAY_NAME);
		sql.append(where);

		Object[] params = new Object[]{TagType.Location.toString(), parentLocation + "%"};
		List<String> rows = jdbcTemplate.queryForList(sql.toString(), params, String.class);

		return rows;
	}

	public List<TagIndex> findTagByFullTagName(String fullTagName) {
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.* "
				+ "FROM " + this.getTableName() + " t ");

		StringBuilder where = new StringBuilder();
		if (AuthInfoStore.getTeamID() != null) {
			sql.append(" INNER JOIN rel_team_tag rt ON t.tag_id=rt.tag_id ");
			where.append(" rt.team_id = ? ");
			params.add(AuthInfoStore.getTeamID());
		}

		if (!Strings.isBlank(fullTagName)) {
			if (where.length() > 0) {
				where.append(" AND ");
			}
			where.append("t.").append(TagIndex.FULL_TAG_NAME).append(" = ? ");
			params.add(fullTagName);
		}

		if (where.length() > 0) {
			where.insert(0, " WHERE ");
		}

		sql.append(where);

		List<TagIndex> rows = jdbcTemplate.query(sql.toString(), params.toArray(new Object[params.size()]), getRowMapper());
		return rows;
	}


	/**
	 * get the list of tags related to the thing
	 *
	 * @param globalThingID
	 * @return
	 */
	public List<TagIndex> findTagByGlobalThingID(Long globalThingID) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT t.*")
				.append(" FROM ")
				.append(TagIndexDao.TABLE_NAME).append(" t, ")
				.append(" ( SELECT ").append(TagThingRelation.TAG_ID)
				.append("     FROM ").append(TagThingRelationDao.TABLE_NAME)
				.append("    WHERE ").append(TagThingRelation.THING_ID).append("=? ) r ")
				.append(" WHERE r.").append(TagThingRelation.TAG_ID).append("=t.").append(TagIndex.TAG_ID);

		List<TagIndex> rows = jdbcTemplate.query(sql.toString(), new Object[]{globalThingID}, getRowMapper());
		return rows;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}


	@Override
	public String getKey() {
		return KEY;
	}

	public Optional<List<Long>> findTagIdsByIDsAndFullname(List<Long> tagIds, Collection<String> fullTagNames) {
		if (null == tagIds || tagIds.isEmpty() || null == fullTagNames || fullTagNames.isEmpty()) {
			return Optional.ofNullable(null);
		}
		Map<String, Object> params = new HashMap();
		params.put("tagIds", tagIds);
		params.put("fullNames", fullTagNames);
		return Optional.ofNullable(namedJdbcTemplate.queryForList(SQL_FIND_TAGIDS_BY_TAGIDS_AND_FULLNAMES, params,
				Long.class));
	}
}
