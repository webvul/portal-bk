package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;

@Repository
public class GlobalThingSpringDao extends SpringBaseDao<GlobalThingInfo> {
	
	public static final String TABLE_NAME = "global_thing";
	public static final String KEY = GlobalThingInfo.ID_GLOBAL_THING;
	
	@Override
	protected String getTableName() {
		return GlobalThingSpringDao.TABLE_NAME;
	}

	@Override
	protected String getKey() {
		return KEY;
	}

	public List<GlobalThingInfo> getThingsByIDArray(List<Long> thingIDs){

		String sql = "SELECT g.* "
				+ "FROM global_thing g "
				+ " WHERE g.id_global_thing in (:ids) ";

		Map<String,Object> params=new HashMap<>();
		params.put("ids",thingIDs);

		return namedJdbcTemplate.query(sql,params,getRowMapper());

	}

	public List<GlobalThingInfo> getThingsByVendorIDArray(List<String> vendorIDs){

		String sql = "SELECT g.* "
				+ "FROM global_thing g "
				+ " WHERE g.vendor_thing_id in (:ids) ";

		Map<String,Object> params=new HashMap<>();
		params.put("ids",vendorIDs);

		return namedJdbcTemplate.query(sql,params,getRowMapper());

	}

	public Set<GlobalThingInfo> queryThingByUnionTags(List<String> tagCollect){

		String sql = "SELECT g.* "
				+ "FROM global_thing g "
				+ "INNER JOIN rel_thing_tag r ON g.id_global_thing=r.thing_id "
				+ "INNER JOIN tag_index t ON t.tag_id=r.tag_id "
				+ " WHERE t.full_tag_name in (:names) ";

		return new HashSet<>(namedJdbcTemplate.query(sql, Collections.singletonMap("names",tagCollect),getRowMapper()));

	}

	public Set<GlobalThingInfo>  queryThingByIntersectionTags(List<String> tagCollect){

		String sql = "select th.* from global_thing th where th.id_global_thing in  " +
				"  (SELECT g.id_global_thing "
				+ "FROM global_thing g "
				+ "INNER JOIN rel_thing_tag r ON g.id_global_thing=r.thing_id "
				+ "INNER JOIN tag_index t ON t.tag_id=r.tag_id "
				+ " WHERE t.full_tag_name in (:names) group by g.id_global_thing having  count(r.tag_id) = :count )";
		Map<String,Object> params=new HashMap<>();
		params.put("names",tagCollect);
		params.put("count",tagCollect.size());

		return new HashSet<>(namedJdbcTemplate.query(sql,params,getRowMapper()));

	}

	public GlobalThingInfo getThingByFullKiiThingID(String kiiAppID,String kiiThingID) {

		String sql = "SELECT g.* "
				+ "FROM global_thing g "
				+ " WHERE g.full_kii_thing_id  = ? ";

		String fullKiiThingID= ThingIDTools.joinFullKiiThingID(kiiAppID,kiiThingID);

		List<GlobalThingInfo> list= jdbcTemplate.query(sql,new Object[]{fullKiiThingID},getRowMapper());

		if(list.size()==0){
			return null;
		}else{
			return list.get(0);
		}

	}

	public void updateState(String state,String fullKiiThingID){

		super.doUpdate("update global_thing set status = ? where full_kii_thing_id = ? ",state,fullKiiThingID);

	}
	
	public GlobalThingInfo getThingByVendorThingID(String vendorThingID) {
		List<GlobalThingInfo> list = super.findBySingleField(GlobalThingInfo.VANDOR_THING_ID, vendorThingID);
		if(list.size() > 0){
			return list.get(0);
		}else{
			return null;
		}
	}

	public void updateKiiThingID(String vendorID, String fullKiiThingID) {
		super.doUpdate("update global_thing set full_kii_thing_id = ? where vendor_thing_id = ? ",fullKiiThingID,vendorID);
	}
	
	public List<String> findAllThingTypes() {
		String sql = "SELECT DISTINCT "+ GlobalThingInfo.THING_TYPE +" FROM " + this.getTableName();
		
		if(AuthInfoStore.getTeamID() != null){
			sql += " INNER JOIN rel_team_thing r ON id_global_thing=r.thing_id WHERE r.team_id = " +AuthInfoStore.getTeamID();
		}
		
		List<String> rows = jdbcTemplate.queryForList(sql, null, String.class);

		return rows;
	}



	public List<Map<String, Object>> findAllThingTypesWithThingCount() {
		String sql = "SELECT "+ GlobalThingInfo.THING_TYPE +" as type, COUNT(1) as count FROM " + this.getTableName();
		
		if(AuthInfoStore.getTeamID() != null){
			sql += " INNER JOIN rel_team_thing r ON id_global_thing=r.thing_id WHERE r.team_id = " +AuthInfoStore.getTeamID();
		}
		
		sql += " GROUP BY " + GlobalThingInfo.THING_TYPE;

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[0]);

		return rows;
	}
	
	public List<GlobalThingInfo> getThingByType(String type) {
		StringBuilder sql = new StringBuilder("SELECT g.* from " + this.getTableName() +" g ");
		StringBuilder where = new StringBuilder(" WHERE g."+GlobalThingInfo.THING_TYPE+" = ? ");
		List<Object> params = new ArrayList<Object>();
		params.add(type);
		if(AuthInfoStore.getTeamID() != null){
			sql.append(" INNER JOIN rel_team_thing r ON id_global_thing=r.thing_id WHERE r.team_id = " +AuthInfoStore.getTeamID());
			params.add(AuthInfoStore.getTeamID());
		}
		sql.append(where);
		List<GlobalThingInfo> rows = jdbcTemplate.query(sql.toString(), params.toArray(new Object[params.size()]) ,getRowMapper() );
		return rows;
	}

	
	
	public List<GlobalThingInfo> findThingByTag(String tagName) {
		StringBuilder sql = new StringBuilder("SELECT g.* from " + this.getTableName() +" g ");
		StringBuilder where = new StringBuilder(" WHERE t.full_tag_name = ? ");
		List<Object> params = new ArrayList<Object>();
		params.add(tagName);
		if(AuthInfoStore.getTeamID() != null){
			sql.append(" INNER JOIN rel_team_thing rt ON g.id_global_thing=rt.thing_id ");
			where.append(" AND rt.team_id = ? ");
			params.add(AuthInfoStore.getTeamID());
		}
		sql.append(" INNER JOIN rel_thing_tag r ON g.id_global_thing=r.thing_id ");
		sql.append(" INNER JOIN tag_index t ON t.tag_id=r.tag_id ");
		sql.append(where);
		List<GlobalThingInfo> rows = jdbcTemplate.query(sql.toString(), params.toArray(new Object[params.size()]) ,getRowMapper() );
	    return rows;
	}
}
