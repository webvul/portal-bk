package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagThingRelation;
import com.kii.beehive.portal.jdbc.entity.TagType;

@Repository
public class TagIndexDao extends SpringBaseDao<TagIndex> {

	//private Logger log= LoggerFactory.getLogger(TagIndexDao.class);
	
	public static final String TABLE_NAME = "tag_index";
	public static final String KEY = "tag_id";
	

	/**
	 * find tag list by tagType and displayName
	 *
	 * @param tagType
	 * @param displayName if it's null or empty, will query all displayName under the tagType
     * @return
     */
	public List<TagIndex> findTagByTagTypeAndName(String tagType,String displayName) {
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.*, COUNT(r.thing_id) count, GROUP_CONCAT(r.thing_id) things "
					+ "FROM " + this.getTableName() +" t "
					+ "LEFT JOIN rel_thing_tag r ON r.tag_id = t.tag_id ");
		
		StringBuilder where = new StringBuilder();
		if(AuthInfoStore.getTeamID() != null){
			sql.append(" INNER JOIN rel_team_tag rt ON t.tag_id=rt.tag_id ");
			where.append(" rt.team_id = ? ");
			params.add(AuthInfoStore.getTeamID());
		}
		
		if(!Strings.isBlank(tagType)){
			if(where.length() > 0){
				where.append(" AND ");
			}
			where.append("t.").append(TagIndex.TAG_TYPE).append(" = ? ");
			params.add(tagType);
		}
		
		if(!Strings.isBlank(displayName)){
			if(where.length() > 0){
				where.append(" AND ");
			}
			where.append("t.").append(TagIndex.DISPLAY_NAME).append(" = ? ");
			params.add(displayName);
		}
		if(where.length() > 0){
			where.insert(0, " WHERE ");
		}
		
		where.append("GROUP BY t.").append(TagIndex.TAG_ID);
		sql.append(where);

		List<TagIndex> rows = jdbcTemplate.query(sql.toString(), params.toArray(new Object[params.size()]), getRowMapper());
	    return rows;
	}

	public List<String> findLocations(String parentLocation) {

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT t.").append(TagIndex.DISPLAY_NAME).append(" FROM ").append(this.getTableName()+" t ");
		
		StringBuffer where = new StringBuffer();
		where.append(" WHERE t.").append(TagIndex.TAG_TYPE).append(" = ? ")
		.append(" AND t.").append(TagIndex.DISPLAY_NAME).append(" like ? ");
		
		
		if(AuthInfoStore.getTeamID() != null){
			sql.append(" INNER JOIN rel_team_tag rt ON t.tag_id=rt.tag_id ");
			where.append("AND rt.team_id = ").append(AuthInfoStore.getTeamID());
		}
		
		where.append(" ORDER BY t.").append(TagIndex.DISPLAY_NAME);
		sql.append(where);
		
		Object[] params = new Object[] {TagType.Location.toString(), parentLocation + "%"};
		List<String> rows = jdbcTemplate.queryForList(sql.toString(), params, String.class);

		return rows;
	}


	/**
	 * get the list of tags related to the thing
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

		List<TagIndex> rows = jdbcTemplate.query(sql.toString(), new Object[] {globalThingID}, getRowMapper());
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
}
