package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.TagGroupRelation;

@Repository
public class TagGroupRelationDao extends SpringBaseDao<TagGroupRelation> {

	private Logger log= LoggerFactory.getLogger(TagGroupRelationDao.class);
	
	public static final String TABLE_NAME = "rel_tag_group";
	public static final String KEY = "id";
	
	public void delete(Long tagID, Long userGroupID){
		if(tagID != null || userGroupID != null){
			String sql = "DELETE FROM " + this.getTableName() + " WHERE ";
			
			StringBuilder where = new StringBuilder();
			List<Object> params = new ArrayList<Object>();
			if(tagID != null){
				where.append(TagGroupRelation.TAG_ID + " = ? "); 
				params.add(tagID);
			}
			
			if(userGroupID != null){
				if(where.length() > 0){
					where.append(" AND ");
				}
				where.append(TagGroupRelation.USER_GROUP_ID+" = ? ");
				params.add(userGroupID);
			}
			Object[] paramArr = new Object[params.size()];
			paramArr = params.toArray(paramArr);
			
	        jdbcTemplate.update(sql+where.toString(),paramArr);
		}else{
			log.warn("tagID and userGroupID are null");
		}
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}


	@Override
	public String getKey() {
		return KEY;
	}
	
	public TagGroupRelation findByTagIDAndUserGroupID(Long tagID, Long userGroupID) {
		if(tagID!=null && userGroupID!=null){
			String sql = "SELECT * FROM " + this.getTableName() + " WHERE "+ TagGroupRelation.TAG_ID +"=? AND "+ TagGroupRelation.USER_GROUP_ID + "=?";
			List<TagGroupRelation> list= jdbcTemplate.query(sql,new Object[]{tagID,userGroupID},getRowMapper());
	        if(list.size() > 0){
	        	return list.get(0);
	        }
		}
        return null;
    }
}
