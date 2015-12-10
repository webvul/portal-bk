package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.TagThingRelation;

@Repository
public class TagThingRelationDao extends BaseDao<TagThingRelation> {

	private Logger log= LoggerFactory.getLogger(TagThingRelationDao.class);
	
	public static final String TABLE_NAME = "rel_thing_tag";
	public static final String KEY = "id";
	

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
	

}
