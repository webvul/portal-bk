package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagThingRelation;
import com.kii.beehive.portal.jdbc.entity.TagType;

@Repository
public class TagThingRelationDao extends BaseDao<TagThingRelation> {

	//private Logger log= LoggerFactory.getLogger(TagThingRelationDao.class);
	
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
		
		String[] columns = new String[]{
				TagThingRelation.ID,
				TagThingRelation.TAG_ID,
				TagThingRelation.THING_ID
		};

        return super.update(entity, columns);

	}
	
	public TagThingRelation findByThingIDAndTagID(Long thingID, Long tagID) {  
		String sql = "SELECT * FROM " + this.getTableName() + " WHERE "+ TagThingRelation.THING_ID +"=? AND "+ TagThingRelation.TAG_ID + "=?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, thingID,tagID);
        List<TagThingRelation> list = mapToList(rows);
        if(list.size() > 0){
        	return list.get(0);
        }
        return null;
    }

	/**
	 * get the tag thing relation by global thing id, tag type and tag display name <br>
	 *     if tag display name is not specified, will get all the relations under the global thing id and tag type
	 * @param globalThingID
	 * @param tagType
	 * @param tagDisplayName
     * @return
     */
	public List<TagThingRelation> find(Long globalThingID, TagType tagType, String tagDisplayName) {

		List<Object> params = new ArrayList<>();

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT r.").append(TagThingRelation.ID)
				.append(", r.").append(TagThingRelation.TAG_ID)
				.append(", r.").append(TagThingRelation.THING_ID)
				.append(" FROM ")
				.append(TagIndexDao.TABLE_NAME).append(" t, ")
				.append(" ( SELECT ").append(TagThingRelation.ID)
				.append("        , ").append(TagThingRelation.TAG_ID)
				.append("        , ").append(TagThingRelation.THING_ID)
				.append("     FROM ").append(TagThingRelationDao.TABLE_NAME)
				.append("    WHERE ").append(TagThingRelation.THING_ID).append("=? ) r ")
				.append(" WHERE r.").append(TagThingRelation.TAG_ID).append("=t.").append(TagIndex.TAG_ID);
		params.add(globalThingID);

		if(tagType != null) {
			sql.append(" AND t.").append(TagIndex.TAG_TYPE).append("=?");
			params.add(tagType);
		}
		if(!Strings.isBlank(tagDisplayName)){
			sql.append(" AND t.").append(TagIndex.DISPLAY_NAME).append("=?");
			params.add(tagDisplayName);
		}

		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString(), params.toArray(new Object[params.size()]));

		return mapToList(result);
	}
}
