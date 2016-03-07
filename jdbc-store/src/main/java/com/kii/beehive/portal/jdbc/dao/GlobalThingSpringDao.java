package com.kii.beehive.portal.jdbc.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;

@Repository
public class GlobalThingSpringDao extends SpringBaseDao<GlobalThingInfo> {
	@Override
	protected String getTableName() {
		return GlobalThingDao.TABLE_NAME;
	}

	@Override
	protected String getKey() {
		return GlobalThingInfo.ID_GLOBAL_THING;
	}

	@Override
	protected Class getEntityCls() {
		return GlobalThingInfo.class;
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
				"  (SELECT r.thing_id from  rel_thing_tag r  "
				+ "INNER JOIN tag_index t ON t.tag_id=r.tag_id "
				+ " WHERE t.full_tag_name in (:names) group by r.thing_id having  count(r.tag_id) = :count )";
		Map<String,Object> params=new HashMap<>();
		params.put("names",tagCollect);
		params.put("count",tagCollect.size());

		return new HashSet<>(namedJdbcTemplate.query(sql,params,getRowMapper()));

	}

	public Set<GlobalThingInfo> queryThingByUnionTags(List<String> tagCollect,String type){

		String sql = "SELECT g.* "
				+ "FROM  global_thing  g "
				+ "INNER JOIN rel_thing_tag r ON g.id_global_thing=r.thing_id "
				+ "INNER JOIN tag_index t ON t.tag_id=r.tag_id "
				+ " WHERE t.full_tag_name in (:names) and g.thing_type = :type ";

		Map<String,Object> params=new HashMap<>();
		params.put("names",tagCollect);
		params.put("type",type);
		return new HashSet<>(namedJdbcTemplate.query(sql,params,getRowMapper()));

	}

	public Set<GlobalThingInfo>  queryThingByIntersectionTags(List<String> tagCollect,String type){

		String sql = "select th.* from global_thing th where th.id_global_thing in  " +
				"  (SELECT  r.thing_id from rel_thing_tag r  "
				+ "INNER JOIN tag_index t ON t.tag_id=r.tag_id "
				+ " WHERE t.full_tag_name in (:names) group by r.thing_id having  count(r.tag_id) = :count )" +
				"  and  th.thing_type = :type ";
		Map<String,Object> params=new HashMap<>();

		params.put("names",tagCollect);
		params.put("count",tagCollect.size());
		params.put("type",type);

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

		String sql = "SELECT g.* "
				+ "FROM global_thing g "
				+ " WHERE g.vendor_thing_id  = ? ";

		List<GlobalThingInfo> list= jdbcTemplate.query(sql,new Object[]{vendorThingID},getRowMapper());

		if(list.size()==0){
			return null;
		}else{
			return list.get(0);
		}

	}
	
	
	public void updateKiiThingID(String vendorID, String fullKiiThingID) {

		super.doUpdate("update global_thing set full_kii_thing_id = ? where vendor_thing_id = ? ",fullKiiThingID,vendorID);


	}
	
	public List<GlobalThingInfo> getAllThing(PagerTag pager) {

		String sql = "SELECT g.* "
				+ "FROM global_thing g ";


		List<GlobalThingInfo> list= super.queryWithPage(sql,new Object[]{},pager);

		pager.setStartRow(pager.getStartRow()+list.size());

		return list;

	}
}
