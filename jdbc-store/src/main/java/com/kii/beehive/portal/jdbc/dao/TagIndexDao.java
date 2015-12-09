package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagType;

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
		for (Map row : rows) {
			TagIndex tagIndex = new TagIndex();
			tagIndex.setId((int)row.get("id_global_thing"));
			tagIndex.setDisplayName((String)row.get("display_name"));
			tagIndex.setTagType((TagType)row.get("tag_type"));
			tagIndex.setCreateBy((String)row.get("createBy"));
			tagIndex.setCreateDate((Date)row.get("createDate"));
			tagIndex.setModifyBy((String)row.get("modifyBy"));
			tagIndex.setModifyDate((Date)row.get("createDate"));
			list.add(tagIndex);
		}
		return list;
	}
	

}
