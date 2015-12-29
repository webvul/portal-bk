package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.common.utils.CollectUtils;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagType;

@Repository
public class TagIndexDao extends BaseDao<TagIndex> {

	//private Logger log= LoggerFactory.getLogger(TagIndexDao.class);
	
	public static final String TABLE_NAME = "tag_index";
	public static final String KEY = "tag_id";
	
	/*public List<TagIndex> findTagIndexByTagNameArray(String[] tagNameArray){
		List<TagIndex> tagIndexList = super.findByIDs(tagNameArray);
		return tagIndexList;
	}*/

	/**
	 * find tag list by tagType and displayName
	 *
	 * @param tagType
	 * @param displayName if it's null or empty, will query all displayName under the tagType
     * @return
     */
	public List<TagIndex> findTagByTagTypeAndName(String tagType,String displayName) {
		String sql = "SELECT t.*, r.count "
					+ "FROM " + this.getTableName() +" t, "
					+ "(SELECT tag_id, count(thing_id) as count FROM rel_thing_tag group by tag_id) r ";
		
		StringBuilder where = new StringBuilder();
		where.append(" WHERE r.tag_id = t.tag_id ");
		
		List<Object> params = new ArrayList<Object>();
		if(!Strings.isBlank(tagType)){
			where.append(" AND ").append("t.").append(TagIndex.TAG_TYPE).append(" = ? ");
			params.add(tagType);
		}
		
		if(!Strings.isBlank(displayName)){
			where.append(" AND ").append("t.").append(TagIndex.DISPLAY_NAME).append(" = ? ");
			params.add(displayName);
		}

		Object[] paramArr = new String[params.size()];
		paramArr = params.toArray(paramArr);
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql+where.toString(), paramArr);
	    return mapToList(rows);
	}

	public TagIndex findOneTagByTagTypeAndName(TagType tagType,String displayName) {
		String type = (tagType == null)? null : tagType.toString();
		List<TagIndex> tagIndexList = findTagByTagTypeAndName(type, displayName);

		return CollectUtils.getFirst(tagIndexList);
	}

	public List<String> findLocations(String parentLocation) {

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ").append(TagIndex.DISPLAY_NAME)
				.append(" FROM ").append(this.getTableName())
				.append(" WHERE ").append(TagIndex.TAG_TYPE).append("='").append(TagType.Location)
				.append("' AND ").append(TagIndex.DISPLAY_NAME).append(" like ? ")
				.append(" ORDER BY ").append(TagIndex.DISPLAY_NAME);

		Object[] params = new String[] {parentLocation + "%"};
		List<String> rows = jdbcTemplate.queryForList(sql.toString(), params, String.class);

		return rows;
	}


	public List<TagIndex> findTagByGlobalThingID(String globalThingID) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT t_").append(TagIndex.TAG_ID).append(" AS ").append(TagIndex.TAG_ID)
				.append(", t_").append(TagIndex.TAG_TYPE).append(" AS ").append(TagIndex.TAG_TYPE)
				.append(", t_").append(TagIndex.DISPLAY_NAME).append(" AS ").append(TagIndex.DISPLAY_NAME)
				.append(", t_").append(TagIndex.DESCRIPTION).append(" AS ").append(TagIndex.DESCRIPTION)
				.append(", t_").append(TagIndex.CREATE_DATE).append(" AS ").append(TagIndex.CREATE_DATE)
				.append(", t_").append(TagIndex.CREATE_BY).append(" AS ").append(TagIndex.CREATE_BY)
				.append(", t_").append(TagIndex.MODIFY_DATE).append(" AS ").append(TagIndex.MODIFY_DATE)
				.append(", t_").append(TagIndex.MODIFY_BY).append(" AS ").append(TagIndex.MODIFY_BY)
				.append(" FROM v_").append(TagThingRelationDao.TABLE_NAME)
				.append(" WHERE g_").append(GlobalThingInfo.ID_GLOBAL_THING).append("=?")
				.append(" ORDER BY ").append(TagIndex.TAG_TYPE).append(",").append(TagIndex.DISPLAY_NAME);

		Object[] paramArr = new Object[] {globalThingID};
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql.toString(), paramArr);
		return mapToList(rows);
	}
	
	public long update(TagIndex tag) {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ").append(this.getTableName()).append(" SET ");
		sql.append(TagIndex.TAG_TYPE).append("=?, ");
		sql.append(TagIndex.DISPLAY_NAME).append("=?, ");
		sql.append(TagIndex.DESCRIPTION).append("=?, ");
		sql.append(TagIndex.CREATE_DATE).append("=?, ");
		sql.append(TagIndex.CREATE_BY).append("=?, ");
		sql.append(TagIndex.MODIFY_DATE).append("=?, ");
		sql.append(TagIndex.MODIFY_BY).append("=? ");
		sql.append("WHERE ").append(TagIndex.TAG_ID).append("=? ");
		
        return jdbcTemplate.update(sql.toString(), tag.getTagType().toString(),
        								tag.getDisplayName(),
        								tag.getDescription(),
        								tag.getCreateDate(),
        								tag.getCreateBy(),
        								tag.getModifyDate(),
        								tag.getModifyBy(),
        								tag.getId());
    }

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}


	@Override
	public String getKey() {
		return KEY;
	}
	
	@Override
	public List<TagIndex> mapToList(List<Map<String, Object>> rows) {
		List<TagIndex> list = new ArrayList<TagIndex>();
		for (Map<String, Object> row : rows) {
			TagIndex tagIndex = new TagIndex();
			tagIndex.setId((int)row.get(TagIndex.TAG_ID));
			tagIndex.setDisplayName((String)row.get(TagIndex.DISPLAY_NAME));
			tagIndex.setTagType(TagType.valueOf((String) row.get(TagIndex.TAG_TYPE)));
			tagIndex.setDescription((String)row.get(TagIndex.DESCRIPTION));
			tagIndex.setCount((Long)row.get(TagIndex.THING_COUNT));
			mapToListForDBEntity(tagIndex, row);
			list.add(tagIndex);
		}
		return list;
	}
}
