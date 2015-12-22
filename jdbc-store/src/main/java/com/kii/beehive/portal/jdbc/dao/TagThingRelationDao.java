package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagThingRelation;
import com.kii.beehive.portal.jdbc.entity.TagType;

@Repository
public class TagThingRelationDao extends BaseDao<TagThingRelation> {

	private Logger log= LoggerFactory.getLogger(TagThingRelationDao.class);
	
	public static final String TABLE_NAME = "rel_thing_tag";
	public static final String KEY = "id";
	
	public void delete( Long tagID, Long thingID){
		String sql = "DELETE FROM " + this.getTableName() + " WHERE ";
		
		StringBuilder where = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		if(thingID != null){
			where.append(TagThingRelation.THING_ID + " = ? "); 
			params.add(thingID);
		}
		
		if(tagID != null){
			if(where.length() > 0){
				where.append(" AND ");
			}
			where.append(TagThingRelation.TAG_ID+" = ? ");
			params.add(tagID);
		}
		Object[] paramArr = new Object[params.size()];
		paramArr = params.toArray(paramArr);
		
        jdbcTemplate.update(sql+where.toString(),paramArr);
	}

	@Override
	protected Class<TagThingRelation> getEntityCls() {
		return TagThingRelation.class;
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
	public List<TagThingRelation> mapToList(List<Map<String, Object>> rows) {
		List<TagThingRelation> list = new ArrayList<TagThingRelation>();
		for (Map<String, Object> row : rows) {
			TagThingRelation tagThingRelation = new TagThingRelation();
			tagThingRelation.setId((int)row.get(TagThingRelation.ID));
			tagThingRelation.setTagID((int)row.get(TagThingRelation.TAG_ID));
			tagThingRelation.setThingID((int)row.get(TagThingRelation.THING_ID));
			list.add(tagThingRelation);
		}
		return list;
	}
	
	@Override
	public long update(TagThingRelation entity) {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ").append(this.getTableName()).append(" SET ");
		sql.append(TagThingRelation.TAG_ID).append("=?, ");
		sql.append(TagThingRelation.THING_ID).append("=?, ");
		sql.append("WHERE ").append(TagThingRelation.ID).append("=? ");

		return jdbcTemplate.update(sql.toString(),
				entity.getTagID(),
				entity.getThingID(),
				entity.getId());

	}

	public List<TagThingRelation> find(Long globalThingID, TagType tagType, String tagDisplayName) {

		List<Object> params = new ArrayList<>();

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT r_").append(TagThingRelation.ID).append(" AS ").append(TagThingRelation.ID)
				.append(", t_").append(TagIndex.TAG_ID).append(" AS ").append(TagThingRelation.TAG_ID)
				.append(", g_").append(GlobalThingInfo.ID_GLOBAL_THING).append(" AS ").append(TagThingRelation.THING_ID)
				.append(" FROM v_").append(getTableName())
				.append(" WHERE g_").append(GlobalThingInfo.ID_GLOBAL_THING).append("=?");
		params.add(globalThingID);

		if(tagType != null) {
			sql.append(" AND t_").append(TagIndex.TAG_TYPE).append("=?");
			params.add(tagType);
		}
		if(!Strings.isBlank(tagDisplayName)){
			sql.append(" AND t_").append(TagIndex.DISPLAY_NAME).append("=?");
			params.add(tagDisplayName);
		}

		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString(), params.toArray(new Object[params.size()]));

		return mapToList(result);
	}
}
