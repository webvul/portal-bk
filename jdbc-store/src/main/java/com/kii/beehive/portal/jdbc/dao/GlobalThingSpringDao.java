package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.ThingUserRelation;

@Repository
public class GlobalThingSpringDao extends SpringBaseDao<GlobalThingInfo> {

	public static final String TABLE_NAME = "global_thing";
	public static final String KEY = GlobalThingInfo.ID_GLOBAL_THING;
	private static final String SQL_FIND_THINGIDS_BY_CREATOR = "SELECT " + GlobalThingInfo.ID_GLOBAL_THING + " FROM " +
			TABLE_NAME + " WHERE " + GlobalThingInfo.CREATE_BY + " = :creator ";

	@Override
	protected String getTableName() {
		return GlobalThingSpringDao.TABLE_NAME;
	}

	@Override
	protected String getKey() {
		return KEY;
	}

	public Optional<List<GlobalThingInfo>> getThingsByVendorIDArray(Collection<String> vendorIDs) {
		if (null == vendorIDs || vendorIDs.isEmpty()) {
			return Optional.ofNullable(null);
		}

		String sql = "SELECT g.* "
				+ "FROM global_thing g "
				+ " WHERE g.vendor_thing_id in (:ids) ";

		Map<String, Object> params = new HashMap<>();
		params.put("ids", vendorIDs);

		return Optional.ofNullable(queryByNamedParam(sql, params));

	}

	public Set<GlobalThingInfo> queryThingByUnionTags(List<String> tagCollect) {

		String sql = "SELECT g.* "
				+ "FROM global_thing g "
				+ "INNER JOIN rel_thing_tag r ON g.id_global_thing=r.thing_id "
				+ "INNER JOIN tag_index t ON t.tag_id=r.tag_id "
				+ " WHERE t.full_tag_name in (:names) ";
		sql = super.addDelSignPrefix(sql);
		return new HashSet<>(queryByNamedParam(sql, Collections.singletonMap("names", tagCollect)));

	}

	public Set<GlobalThingInfo> queryThingByIntersectionTags(List<String> tagCollect) {

		String sql = "select th.* from global_thing th where th.id_global_thing in  " +
				"  (SELECT r.thing_id from  rel_thing_tag r  "
				+ "INNER JOIN tag_index t ON t.tag_id=r.tag_id "
				+ " WHERE t.full_tag_name in (:names) group by r.thing_id having  count(r.tag_id) = :count )";
		Map<String, Object> params = new HashMap<>();
		params.put("names", tagCollect);
		params.put("count", tagCollect.size());

		sql = super.addDelSignPrefix(sql);

		return new HashSet<>(namedJdbcTemplate.query(sql, params, getRowMapper()));

	}

	public Set<GlobalThingInfo> queryThingByUnionTags(List<String> tagCollect, String type) {

		String sql = "SELECT g.* "
				+ "FROM  global_thing  g "
				+ "INNER JOIN rel_thing_tag r ON g.id_global_thing=r.thing_id "
				+ "INNER JOIN tag_index t ON t.tag_id=r.tag_id "
				+ " WHERE t.full_tag_name in (:names) and g.thing_type = :type ";

		Map<String, Object> params = new HashMap<>();
		params.put("names", tagCollect);
		params.put("type", type);

		sql = super.addDelSignPrefix(sql);


		return new HashSet<>(namedJdbcTemplate.query(sql, params, getRowMapper()));

	}

	public Set<GlobalThingInfo> queryThingByIntersectionTags(List<String> tagCollect, String type) {

		String sql = "select th.* from global_thing th where th.id_global_thing in  " +
				"  (SELECT  r.thing_id from rel_thing_tag r  "
				+ "INNER JOIN tag_index t ON t.tag_id=r.tag_id "
				+ " WHERE t.full_tag_name in (:names) group by r.thing_id having  count(r.tag_id) = :count )" +
				"  and  th.thing_type = :type ";
		Map<String, Object> params = new HashMap<>();

		params.put("names", tagCollect);
		params.put("count", tagCollect.size());
		params.put("type", type);

		sql = super.addDelSignPrefix(sql);


		return new HashSet<>(namedJdbcTemplate.query(sql, params, getRowMapper()));

	}

	public GlobalThingInfo getThingByFullKiiThingID(String kiiAppID, String kiiThingID) {

		String sql = "SELECT g.* "
				+ "FROM global_thing g "
				+ " WHERE g.full_kii_thing_id  = ? ";

		String fullKiiThingID = ThingIDTools.joinFullKiiThingID(kiiAppID, kiiThingID);

		sql = super.addDelSignPrefix(sql);


		List<GlobalThingInfo> list = jdbcTemplate.query(sql, new Object[]{fullKiiThingID}, getRowMapper());

		if (list.size() == 0) {
			return null;
		} else {
			return list.get(0);
		}

	}

	public void updateState(String state, String fullKiiThingID) {

		super.doUpdate("update global_thing set status = ? where full_kii_thing_id = ? ", state, fullKiiThingID);

	}

	public GlobalThingInfo getThingByVendorThingID(String vendorThingID) {
		List<GlobalThingInfo> list = super.findBySingleField(GlobalThingInfo.VANDOR_THING_ID, vendorThingID);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public void updateKiiThingID(String vendorID, String fullKiiThingID) {
		super.doUpdate("update global_thing set full_kii_thing_id = ? where vendor_thing_id = ? ", fullKiiThingID, vendorID);
	}

	public List<String> findAllThingTypes() {
		String sql = "SELECT DISTINCT th." + GlobalThingInfo.THING_TYPE + " FROM " + this.getTableName() + " th ";

		if (AuthInfoStore.getTeamID() != null) {
			sql += " INNER JOIN rel_team_thing r ON th.id_global_thing=r.thing_id WHERE th.is_deleted =false and  r.team_id = " + AuthInfoStore.getTeamID();
		}

		List<String> rows = jdbcTemplate.queryForList(sql, null, String.class);

		return rows;
	}

	public List<Map<String, Object>> findAllThingTypesWithThingCount() {
		String sql = "SELECT th." + GlobalThingInfo.THING_TYPE + " as type, COUNT(1) as count FROM " + this.getTableName() + " th ";

		if (AuthInfoStore.getTeamID() != null) {
			sql += " INNER JOIN rel_team_thing r ON th.id_global_thing=r.thing_id WHERE th.is_deleted=false and r.team_id = " + AuthInfoStore.getTeamID();
		}

		sql += " GROUP BY " + GlobalThingInfo.THING_TYPE;


		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[0]);

		return rows;
	}

	public List<Map<String, Object>> findThingTypeBytagIDs(String tagIDs) {
		StringBuilder sql = new StringBuilder("SELECT DISTINCT " + GlobalThingInfo.THING_TYPE + " as type FROM " + this.getTableName() + " g ");
		StringBuilder where = new StringBuilder(" WHERE t.tag_id in (?) and g.is_deleted = false");
		List<Object> params = new ArrayList<Object>();
		params.add(tagIDs);
		if (AuthInfoStore.getTeamID() != null) {
			sql.append(" INNER JOIN rel_team_thing rt ON g.id_global_thing=rt.thing_id ");
			where.append(" AND rt.team_id = ? ");
			params.add(AuthInfoStore.getTeamID());
		}
		sql.append(" INNER JOIN rel_thing_tag r ON g.id_global_thing=r.thing_id ");
		sql.append(" INNER JOIN tag_index t ON t.tag_id=r.tag_id ");
		sql.append(where);


		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql.toString(), params.toArray(new Object[params.size()]));

		return rows;
	}

	/**
	 * find thing type by tags
	 *
	 * @param tagCollect list of full tag name
	 * @return list of thing type under the tags
	 */
	public List<String> findThingTypeByFullTagNames(List<String> tagCollect) {
		StringBuilder sql = new StringBuilder("SELECT DISTINCT " + GlobalThingInfo.THING_TYPE + " as type FROM " + this.getTableName() + " g ");
		StringBuilder where = new StringBuilder(" WHERE t.full_tag_name in (:names) and g.is_deleted = false ");
		Map<String, Object> params = new HashMap<>();
		params.put("names", tagCollect);
		if (AuthInfoStore.getTeamID() != null) {
			sql.append(" INNER JOIN rel_team_thing rt ON g.id_global_thing=rt.thing_id ");
			where.append(" AND rt.team_id = :teamid ");
			params.put("teamid", AuthInfoStore.getTeamID());
		}
		sql.append(" INNER JOIN rel_thing_tag r ON g.id_global_thing=r.thing_id ");
		sql.append(" INNER JOIN tag_index t ON t.tag_id=r.tag_id ");
		sql.append(where);


		return namedJdbcTemplate.queryForList(sql.toString(), params, String.class);

	}

	public List<GlobalThingInfo> getThingByType(String type) {
		StringBuilder sql = new StringBuilder("SELECT g.* from " + this.getTableName() + " g ");
		StringBuilder where = new StringBuilder(" WHERE g." + GlobalThingInfo.THING_TYPE + " = ? ");
		List<Object> params = new ArrayList<Object>();
		params.add(type);
		if (AuthInfoStore.getTeamID() != null) {
			sql.append(" INNER JOIN rel_team_thing r ON g.id_global_thing=r.thing_id WHERE r.team_id = " + AuthInfoStore.getTeamID());
			params.add(AuthInfoStore.getTeamID());
		}
		sql.append(where);

		sql = super.addDelSignPrefix(sql);


		List<GlobalThingInfo> rows = jdbcTemplate.query(sql.toString(), params.toArray(new Object[params.size()]), getRowMapper());
		return rows;
	}

	public List<GlobalThingInfo> findThingByTag(String tagName) {
		StringBuilder sql = new StringBuilder("SELECT g.* from " + this.getTableName() + " g ");
		StringBuilder where = new StringBuilder(" WHERE t.full_tag_name = ? ");
		List<Object> params = new ArrayList<Object>();
		params.add(tagName);
		if (AuthInfoStore.getTeamID() != null) {
			sql.append(" INNER JOIN rel_team_thing rt ON g.id_global_thing=rt.thing_id ");
			where.append(" AND rt.team_id = ? ");
			params.add(AuthInfoStore.getTeamID());
		}
		sql.append(" INNER JOIN rel_thing_tag r ON g.id_global_thing=r.thing_id ");
		sql.append(" INNER JOIN tag_index t ON t.tag_id=r.tag_id ");
		sql.append(where);

		sql = super.addDelSignPrefix(sql);

		List<GlobalThingInfo> rows = jdbcTemplate.query(sql.toString(), params.toArray(new Object[params.size()]), getRowMapper());
		return rows;
	}

	public List<GlobalThingInfo> getAllThing(PagerTag pager) {

		String sql = "SELECT g.* "
				+ "FROM global_thing g ";


		List<GlobalThingInfo> list = super.queryWithPage(sql, new Object[]{}, pager);

		return list;

	}
//
//	public Optional<List<GlobalThingInfo>> findByIDsAndType(Set<Long> thingIds, String thingType) {
//		if (null == thingIds || thingIds.isEmpty()) {
//			return Optional.ofNullable(null);
//		}
//
//		Map<String, Object> params = new HashMap();
//		params.put("list", thingIds);
//		StringBuilder sb = new StringBuilder(SQL_FIND_BY_IDS);
//		if (null != thingType && !thingType.isEmpty()) {
//			sb.append(" AND t.").append(GlobalThingInfo.THING_TYPE).append(" = :type");
//			params.put("type", thingType);
//		}
//		return Optional.ofNullable(namedJdbcTemplate.query(sb.toString(), params, getRowMapper()));
//	}

//	public Optional<List<Map<String, Object>>> findThingTypesWithThingCount(Set<Long> thingIds) {
//		if (null == thingIds || thingIds.isEmpty()) {
//			return Optional.ofNullable(null);
//		}
//		StringBuilder sb = new StringBuilder("SELECT ");
//		sb.append(GlobalThingInfo.THING_TYPE).append(" AS type, COUNT(*) AS count FROM ").append(this.getTableName()).
//				append(" WHERE ").append(this.getKey()).append(" IN (:list) ").append(" GROUP BY ").
//				append(GlobalThingInfo.THING_TYPE);
//		Map<String, Object> params = new HashMap();
//		params.put("list", thingIds);
//		return Optional.ofNullable(namedJdbcTemplate.queryForList(sb.toString(), params));
//	}

	public Optional<List<Long>> findThingIdsByCreator(Long userId, List<Long> thingIds) {
		if (null == userId) {
			return Optional.ofNullable(null);
		}
		if (null == thingIds || thingIds.isEmpty()) {
			return Optional.ofNullable(findSingleFieldBySingleField(GlobalThingInfo.ID_GLOBAL_THING,
					GlobalThingInfo.CREATE_BY, userId, Long.class));
		}
		StringBuilder sb = new StringBuilder(SQL_FIND_THINGIDS_BY_CREATOR);
		sb.append(" AND ").append(GlobalThingInfo.ID_GLOBAL_THING).append(" IN (:ids)");

		Map<String, Object> params = new HashMap();
		params.put("creator", String.valueOf(userId));
		params.put("ids", thingIds);

		sb = super.addDelSignPrefix(sb);


		return Optional.ofNullable(namedJdbcTemplate.queryForList(sb.toString(), params, Long.class));
	}


	public List<GlobalThingInfo> findThingByUserID(Long userId) {
		String sqlTmp = "select th.* from  ${0} th  inner join  ${1} rel   on th.${2} = rel.${3}  where rel.${4}  = ? ";
		String sql = StrTemplate.gener(sqlTmp, TABLE_NAME, ThingUserRelationDao.TABLE_NAME, GlobalThingInfo.ID_GLOBAL_THING, ThingUserRelation.THING_ID, ThingUserRelation.USER_ID);

		sql = super.addDelSignPrefix(sql);

		List<GlobalThingInfo> rows = jdbcTemplate.query(sql, new Object[]{userId}, getRowMapper());
		return rows;
	}


	public GlobalThingInfo findThingByUserIDThingID(Long userId, Long thingId) {
		String sqlTmp = "select th.* from  ${0} th  inner join  ${1} rel   on th.${2} = rel.${3}  where rel.${4}  = ? and rel.${5} = ? ";
		String sql = StrTemplate.gener(sqlTmp, TABLE_NAME, ThingUserRelationDao.TABLE_NAME, GlobalThingInfo.ID_GLOBAL_THING, ThingUserRelation.THING_ID, ThingUserRelation.USER_ID, ThingUserRelation.THING_ID);
		sql = super.addDelSignPrefix(sql);

		List<GlobalThingInfo> rows = jdbcTemplate.query(sql, new Object[]{userId, thingId}, getRowMapper());
		if (rows.isEmpty()) {
			return null;
		} else {
			return rows.get(0);
		}
	}

	public GlobalThingInfo findThingByGroupIDRelUserIDWithThingID(Long userId, Long thingID) {
		String sqlTmp = "select th.* from  ${0} th " +
				" inner join  ${1} rel   on th.id_global_thing = rel.thing_id" +
				" inner join  ${2} rel_user on rel_user.user_group_id = rel.user_group_id " +
				" where rel_user.beehive_user_id   = ? and th.id_global_thing = ?  ";
		String sql = StrTemplate.gener(sqlTmp, TABLE_NAME, ThingUserGroupRelationDao.TABLE_NAME, GroupUserRelationDao.TABLE_NAME);
		sql = super.addDelSignPrefix(sql);

		List<GlobalThingInfo> rows = jdbcTemplate.query(sql, new Object[]{userId, thingID}, getRowMapper());
		if (rows.isEmpty()) {
			return null;
		} else {
			return rows.get(0);
		}
	}

	public List<GlobalThingInfo> findThingByGroupIDRelUserID(Long userId) {
		String sqlTmp = "select th.* from  ${0} th " +
				" inner join  ${1} rel   on th.id_global_thing = rel.thing_id " +
				" inner join  ${2} rel_user on rel_user.user_group_id  = rel.user_group_id " +
				" where rel_user.beehive_user_id   = ? ";
		String sql = StrTemplate.gener(sqlTmp, TABLE_NAME, ThingUserGroupRelationDao.TABLE_NAME, GroupUserRelationDao.TABLE_NAME);
		sql = super.addDelSignPrefix(sql);

		return jdbcTemplate.query(sql, new Object[]{userId}, getRowMapper());

	}

	public List<GlobalThingInfo> findThingByTagRelUserID(Long userId) {
		String sqlTmp = "select th.* from  ${0} th " +
				" inner join  ${1} rel   on th.id_global_thing = rel.thing_id" +
				" inner join  ${2} rel_user on rel_user.tag_id = rel.tag_id " +
				" where rel_user.beehive_user_id   = ? ";
		String sql = StrTemplate.gener(sqlTmp, TABLE_NAME, TagThingRelationDao.TABLE_NAME, TagUserRelationDao.TABLE_NAME);
		sql = super.addDelSignPrefix(sql);

		return jdbcTemplate.query(sql, new Object[]{userId}, getRowMapper());

	}


	public List<GlobalThingInfo> findByKiiThingId(String kiiThingId) {
		return super.findBySingleField(GlobalThingInfo.FULL_KII_THING_ID, kiiThingId);
	}
}
