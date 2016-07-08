package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.beehive.portal.jdbc.entity.UserGroup;

@Repository
public class UserGroupDao extends SpringBaseDao<UserGroup> {

	public static final String TABLE_NAME = "user_group";
	public static final String KEY = "user_group_id";
	private Logger log = LoggerFactory.getLogger(UserGroupDao.class);

	public List<UserGroup> findUserGroup(Long userID, Long userGroupID, String name) {

		List<Object> params = new ArrayList<Object>();

		StringBuilder sql = new StringBuilder("SELECT u.* FROM " + this.getTableName() + " u ");
		StringBuilder where = new StringBuilder();

		if (AuthInfoStore.getTeamID() != null) {
			sql.append(" INNER JOIN rel_team_group rt ON u.user_group_id = rt.user_group_id ");
			if (where.length() > 0) where.append(" AND ");
			where.append(" rt.team_id = ? ");
			params.add(AuthInfoStore.getTeamID());
		}

		sql.append(" INNER JOIN rel_group_user r ON u.user_group_id = r.user_group_id ");

		if (userID!=null) {
			if (where.length() > 0) where.append(" AND ");
			where.append(" r.user_id = ? ");
			params.add(userID);
		}

		if (userGroupID != null) {
			if (where.length() > 0) where.append(" AND ");
			where.append(" u.user_group_id = ? ");
			params.add(userGroupID);
		}

		if (!Strings.isBlank(name)) {
			if (where.length() > 0) where.append(" AND ");
			where.append(" u.name = ? ");
			params.add(name);
		}

		if (where.length() == 0) {
			return null;
		}

		sql.append(" WHERE ").append(where);

		sql=super.addDelSignPrefix(sql);

		List<UserGroup> rows = jdbcTemplate.query(sql.toString(), params.toArray(new Object[params.size()]), getRowMapper());
		return rows;
	}

	public List<UserGroup> findUserGroupByName(String userGroupName) {

		List<Object> params = new ArrayList<Object>();

		StringBuilder sql = new StringBuilder("SELECT u.* FROM " + this.getTableName() + " u ");
		StringBuilder where = new StringBuilder();

		if (AuthInfoStore.getTeamID() != null) {
			sql.append(" INNER JOIN rel_team_group rt ON u.user_group_id = rt.user_group_id ");
			if (where.length() > 0) where.append(" AND ");
			where.append(" rt.team_id = ? ");
			params.add(AuthInfoStore.getTeamID());
		}


		if (!Strings.isBlank(userGroupName)) {
			if (where.length() > 0) where.append(" AND ");
			where.append(" u.name = ? ");
			params.add(userGroupName);
		}

		if (where.length() == 0) {
			return null;
		}

		sql.append(" WHERE ").append(where);
		sql=super.addDelSignPrefix(sql);

		List<UserGroup> rows = jdbcTemplate.query(sql.toString(), params.toArray(new Object[params.size()]), getRowMapper());
		return rows;
	}


	public  List<UserGroup> getAllGroupByRelTagRelThing(Long thingID){

		String sql="select g.* from ${0} g " +
				" inner join ${1} rel_tag on rel_tag.user_group_id = g.user_group_id  "+
				" inner join ${3} rel_tag_th on rel_tag_th.thing_id = rel_tag.tag_id "+
				" where rel_tag_th.thing_id = ?";

		String fullSql= StrTemplate.gener(sql,TABLE_NAME, TagGroupRelationDao.TABLE_NAME,TagIndexDao.TABLE_NAME, TagThingRelationDao.TABLE_NAME);

		fullSql=super.addDelSignPrefix(fullSql);

		return super.jdbcTemplate.query(fullSql,new Object[]{thingID},getRowMapper());

	}


	public  List<UserGroup> getAllGroupByRelThing(Long thingID){

		String sql="select g.* from ${0} g " +
				" inner join ${1} rel_th on rel_th.user_group_id = g.user_group_id  "+
				" where rel_th.thing_id = ?";

		String fullSql= StrTemplate.gener(sql,TABLE_NAME, ThingUserGroupRelationDao.TABLE_NAME);
		fullSql=super.addDelSignPrefix(fullSql);

		return super.jdbcTemplate.query(fullSql,new Object[]{thingID},getRowMapper());

	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}


	@Override
	public String getKey() {
		return KEY;
	}

//	public Optional<List<Long>> findUserGroupIds(Set<Long> groupIdSet) {
//		if (null == groupIdSet || groupIdSet.isEmpty()) {
//			return Optional.ofNullable(null);
//		}
//		return Optional.ofNullable(findSingleFieldBySingleField(UserGroup.USER_GROUP_ID, UserGroup.USER_GROUP_ID,
//				groupIdSet, Long.class));
//	}
}
