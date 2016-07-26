package com.kii.beehive.portal.jdbc.dao;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.beehive.portal.jdbc.entity.*;
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

	private static final String SQL_FIND_TAGIDS_BY_CREATOR = "SELECT " + TagIndex.TAG_ID + " FROM " +
			TABLE_NAME + " WHERE " + TagIndex.CREATE_BY + " = :creator ";

	private static final String SQL_FIND_TAGIDS_BY_CREATOR_AND_FULLNAMES = SQL_FIND_TAGIDS_BY_CREATOR +
			" AND " + TagIndex.FULL_TAG_NAME + " IN (:names)";

	private static final String SQL_FIND_TAGIDS_BY_CREATOR_AND_TYPE = SQL_FIND_TAGIDS_BY_CREATOR +
			" AND " + TagIndex.TAG_TYPE + " = :type";

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

		sql = super.addDelSignPrefix(sql);

		List<TagIndex> rows = jdbcTemplate.query(sql.toString(), params.toArray(new Object[params.size()]), getRowMapper());
		return rows;
	}


	public List<TagIndex> getTagListByGroupID(Long userId) {


		String sqlTmp = "select t.* from ${0} t inner join  ${1} rel on rel.${3} = t.${4} where  rel.${2}  = ? ";
		String sql = StrTemplate.gener(sqlTmp, TABLE_NAME, TagGroupRelationDao.TABLE_NAME, TagGroupRelation.USER_GROUP_ID, TagIndex.TAG_ID, TagGroupRelation.TAG_ID);
		sql = super.addDelSignPrefix(sql);

		return jdbcTemplate.query(sql, new Object[]{userId}, getRowMapper());
	}

	public List<TagIndex> findUserTagByUserID(Long userId) {


		String sqlTmp = "select t.* from ${0} t inner join  ${1} rel on rel.tag_id = t.tag_id where  rel.beehive_user_id = ? ";
		String sql = StrTemplate.gener(sqlTmp, TABLE_NAME, TagUserRelationDao.TABLE_NAME);
		sql = super.addDelSignPrefix(sql);

		return jdbcTemplate.query(sql, new Object[]{userId}, getRowMapper());
	}


	public List<TagIndex> findUserTagByTypeAndName(Long userId, String tagType, String displayName) {
		List<Object> params = new ArrayList<>();
		params.add(String.valueOf(userId));
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
		sb = super.addDelSignPrefix(sb);

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

		sql = super.addDelSignPrefix(sql);

		List<TagIndex> rows = jdbcTemplate.query(sql.toString(), params.toArray(new Object[params.size()]), getRowMapper());
		return rows;
	}

	public List<String> findUserLocations(Long userId, String parentLocation) {
		List<Object> params = new ArrayList<>();
		params.add(userId);
		StringBuilder sb = new StringBuilder(SQL_FIND_USER_LOCATION);
		if (!Strings.isBlank(parentLocation)) {
			sb.append(" AND t.").append(TagIndex.DISPLAY_NAME).append(" LIKE ?");
			params.add(parentLocation + '%');
		}
		sb.append(" ORDER BY t.").append(TagIndex.DISPLAY_NAME);
		sb = super.addDelSignPrefix(sb);

		return jdbcTemplate.queryForList(sb.toString(), params.toArray(new Object[0]), String.class);
	}

	public List<String> findLocations(String parentLocation) {

		StringBuilder sql = new StringBuilder();
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

		sql = super.addDelSignPrefix(sql);

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

		sql = super.addDelSignPrefix(sql);
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
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.*")
				.append(" FROM ")
				.append(TagIndexDao.TABLE_NAME).append(" t, ")
				.append(" ( SELECT ").append(TagThingRelation.TAG_ID)
				.append("     FROM ").append(TagThingRelationDao.TABLE_NAME)
				.append("    WHERE ").append(TagThingRelation.THING_ID).append("=? ) r ")
				.append(" WHERE is_deleted = false and r.").append(TagThingRelation.TAG_ID).append("=t.").append(TagIndex.TAG_ID);


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

	public Optional<List<Long>> findTagIdsByCreatorAndFullTagNames(Long userId, List<String> fullTagNameList) {
		if (null == userId) {
			return Optional.ofNullable(null);
		}

		if (null == fullTagNameList || fullTagNameList.isEmpty()) {
			return Optional.ofNullable(findSingleFieldBySingleField(TagIndex.TAG_ID, TagIndex.CREATE_BY, userId,
					Long.class));
		}
		Map<String, Object> params = new HashMap();
		params.put("creator", String.valueOf(userId));
		params.put("names", fullTagNameList);
		return Optional.ofNullable(namedJdbcTemplate.queryForList(SQL_FIND_TAGIDS_BY_CREATOR_AND_FULLNAMES, params,
				Long.class));
	}

	public Optional<List<Long>> getCreatedTagIdsByTypeAndDisplayNames(Long userId, TagType type,
																	  List<String> displayNames) {
		if (null == userId) {
			return Optional.ofNullable(null);
		}
		if (null == type) {
			return Optional.ofNullable(findSingleFieldBySingleField(TagIndex.TAG_ID, TagIndex.CREATE_BY, userId,
					Long.class));
		}
		Map<String, Object> params = new HashMap();
		params.put("creator", String.valueOf(userId));
		params.put("type", type.name());

		if (null == displayNames || displayNames.isEmpty()) {
			return Optional.ofNullable(namedJdbcTemplate.queryForList(SQL_FIND_TAGIDS_BY_CREATOR_AND_TYPE, params,
					Long.class));
		}

		StringBuilder sb = new StringBuilder(SQL_FIND_TAGIDS_BY_CREATOR_AND_TYPE);
		sb.append(" AND ").append(TagIndex.DISPLAY_NAME).append(" IN (:names)");
		params.put("names", displayNames);

		return Optional.ofNullable(namedJdbcTemplate.queryForList(sb.toString(), params, Long.class));
	}

	public Optional<List<TagIndex>> findTagsByTagIdsAndLocations(Collection<Long> tagIds, String parentLocation) {
		if ((null == tagIds || tagIds.isEmpty()) && Strings.isBlank(parentLocation)) {
			return Optional.ofNullable(null);
		}

		Map<String, Object> params = new HashMap();
		params.put("type", TagType.Location.name());
		StringBuilder sb = new StringBuilder("SELECT * FROM ").append(TABLE_NAME).append(" WHERE ").
				append(TagIndex.TAG_TYPE).append(" = ").append(":type");

		if (null != tagIds && !tagIds.isEmpty()) {
			sb.append(" AND ").append(TagIndex.TAG_ID).append(" IN (:ids) ");
			params.put("ids", tagIds);
		}

		if (!Strings.isBlank(parentLocation)) {
			sb.append(" AND ").append(TagIndex.DISPLAY_NAME).append(" LIKE :name");
			params.put("name", parentLocation + "%");
		}

		sb = super.addDelSignPrefix(sb);

		return Optional.ofNullable(namedJdbcTemplate.query(sb.toString(), params, getRowMapper()));
	}

	public Optional<List<Long>> findTagIdsByTeamAndTagTypeAndName(Long teamId, TagType type, String displayName) {
		StringBuilder sb = new StringBuilder("SELECT t1.").append(TagIndex.TAG_ID).append(" FROM ").
				append(TABLE_NAME).append(" t1");

		List<Object> params = new ArrayList();
		if (null != teamId) {
			sb.append(" INNER JOIN ").append(TeamUserRelationDao.TABLE_NAME).append(" t2 ON t2.").
					append(TeamUserRelation.USER_ID).append(" = t1.").append(TagIndex.CREATE_BY).append(" AND t2.").
					append(TeamUserRelation.TEAM_ID).append(" = ?");
			params.add(teamId);
		}

		StringBuilder sbWhere = new StringBuilder();

		if (null != type) {
			sbWhere.append(" WHERE t1.").append(TagIndex.TAG_TYPE).append(" = ?");
			params.add(type.name());
		}

		if (!Strings.isBlank(displayName)) {
			sbWhere.append(0 == sbWhere.length() ? " WHERE t1." : " AND t1.").append(TagIndex.DISPLAY_NAME).
					append(" = ?");
			params.add(displayName);
		}

		sb = super.addDelSignPrefix(sb);

		return Optional.ofNullable(jdbcTemplate.queryForList(sb.append(sbWhere).toString(),
				params.toArray(new Object[]{}), Long.class));
	}
}
