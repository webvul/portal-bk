package com.kii.beehive.portal.jdbc.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.beehive.portal.jdbc.entity.ThingLocationRelation;

@Repository
public class ThingLocationRelDao extends SpringSimpleBaseDao<ThingLocationRelation> {


	public static final String TABLE_NAME="rel_thing_location";

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected String getKey() {
		return ThingLocationRelation.ID;
	}

	public void addRelation(Long thingID,List<String> locations){

		locations.forEach(loc->{
			ThingLocationRelation rel=new ThingLocationRelation();
			rel.setLocation(loc);
			rel.setThingId(thingID);
			super.insert(rel);
		});


	}


	private String SqlTmp="delete from ${0}  where ${1} = :th  and ${2} in (:loc)  ";
	public void removeRelation(Long thingID,List<String> locations){

		String fullSql= StrTemplate.gener(SqlTmp,TABLE_NAME,ThingLocationRelation.THING_ID,ThingLocationRelation.LOCATION);

		Map<String,Object> params=new HashMap<>();
		params.put("th",thingID);
		params.put("loc",locations);

		super.namedJdbcTemplate.update(fullSql,params);
	}


	private String SqlQueryTmp="select th.${1} from ${0} th where th.${2} = ? ";
	public List<String> getRelation(Long thingID){

		String fullSql=StrTemplate.gener(SqlQueryTmp,TABLE_NAME,ThingLocationRelation.THING_ID,ThingLocationRelation.LOCATION);


		return super.jdbcTemplate.queryForList(fullSql,new Object[]{thingID},String.class);


	}

	private String SqlClearTmp="delete from  ${0}  where ${1} = :th  ";

	public void clearAllRelation(Long thingID){

		String fullSql= StrTemplate.gener(SqlClearTmp,TABLE_NAME,ThingLocationRelation.THING_ID,ThingLocationRelation.LOCATION);

		Map<String,Object> params=new HashMap<>();
		params.put("th",thingID);

		super.namedJdbcTemplate.update(fullSql,params);

	}

}
