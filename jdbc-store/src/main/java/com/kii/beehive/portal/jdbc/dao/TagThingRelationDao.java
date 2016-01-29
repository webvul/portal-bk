package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagThingRelation;
import com.kii.beehive.portal.jdbc.entity.TagType;

@Repository
public class TagThingRelationDao extends SpringBaseRelDao<TagThingRelation> {

	private Logger log= LoggerFactory.getLogger(TagThingRelationDao.class);
	
	public static final String TABLE_NAME = "rel_thing_tag";
	public static final String KEY = "id";
	
	public void delete( Long tagID, Long thingID){
		if(tagID != null || thingID != null){
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
		}else{
			log.warn("tagID and thingID are null");
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
	
	
	public TagThingRelation findByThingIDAndTagID(Long thingID, Long tagID) {  
		if(tagID != null && thingID != null){
			String sql = "SELECT * FROM " + this.getTableName() + " WHERE "+ TagThingRelation.THING_ID +"=? AND "+ TagThingRelation.TAG_ID + "=?";
	        List<TagThingRelation> list = jdbcTemplate.query(sql, new Object[]{thingID,tagID},getRowMapper());
	        if(list.size() > 0){
	        	return list.get(0);
	        }
		}
        return null;
    }
}
