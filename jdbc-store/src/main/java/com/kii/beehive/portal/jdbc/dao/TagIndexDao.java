package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagType;

@Repository
public class TagIndexDao extends BaseDao<TagIndex> {

	private Logger log= LoggerFactory.getLogger(TagIndexDao.class);
	
	public static final String TABLE_NAME = "tag_index";
	public static final String KEY = "tag_id";
	
	/*public List<TagIndex> findTagIndexByTagNameArray(String[] tagNameArray){
		List<TagIndex> tagIndexList = super.findByIDs(tagNameArray);
		return tagIndexList;
	}*/
	
	public List<TagIndex> findTagByTagTypeAndName(String tagType,String displayName) {
		String sql = "SELECT * "
					+ "FROM " + this.getTableName();
		
		StringBuilder where = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		if(!Strings.isBlank(tagType)){
			where.append(TagIndex.TAG_TYPE).append(" = ? "); 
			params.add(tagType);
		}
		
		if(!Strings.isBlank(displayName)){
			if(where.length() > 0){
				where.append(" AND ");
			}
			where.append(TagIndex.DISPLAY_NAME).append(" = ? ");
			params.add(displayName);
		}
		
		if(where.length() > 0){
			where.insert(0, " WHERE ");
		}
		
		Object[] paramArr = new String[params.size()];
		paramArr = params.toArray(paramArr);
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql+where.toString(), paramArr);
	    return mapToList(rows);
	}

	public List<String> findLocations(String parentLocation) {

		String sql = "SELECT display_name FROM " + this.getTableName()
				+ " WHERE tag_type='" + TagType.Location + "' AND display_name like ? " +
				"ORDER BY display_name";

		Object[] params = new String[] {parentLocation + "%"};
		List<String> rows = jdbcTemplate.queryForList(sql, params, String.class);

		return rows;
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
	protected Class<TagIndex> getEntityCls() {
		return TagIndex.class;
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
			mapToListForDBEntity(tagIndex, row);
			list.add(tagIndex);
		}
		return list;
	}
}
