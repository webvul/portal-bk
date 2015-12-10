package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
			tagIndex.setCreateBy((String)row.get(TagIndex.CREATE_BY));
			tagIndex.setCreateDate((Date)row.get(TagIndex.CREATE_DATE));
			tagIndex.setModifyBy((String)row.get(TagIndex.MODIFY_BY));
			tagIndex.setModifyDate((Date)row.get(TagIndex.CREATE_DATE));
			list.add(tagIndex);
		}
		return list;
	}
	

}
