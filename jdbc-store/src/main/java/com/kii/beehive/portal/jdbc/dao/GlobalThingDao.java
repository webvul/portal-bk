package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;

@Repository
public class GlobalThingDao extends BaseDao<GlobalThingInfo>{


	public static final String TABLE_NAME = "global_thing";

	public void test(){
		jdbcTemplate.execute("select sysdate() from dual");
	}

	public List<String> findAllThingTypes() {
		String sql = "SELECT DISTINCT thing_type FROM " + this.getTableName();

		List<String> rows = jdbcTemplate.queryForList(sql, null, String.class);

		return rows;
	}

//	public GlobalThingInfo getThingByVendorThingID(String vendorThingID) {
//		List<GlobalThingInfo> list = super.findBySingleField(GlobalThingInfo.VANDOR_THING_ID, vendorThingID);
//		if(list.size() > 0){
//			return list.get(0);
//		}else{
//			return null;
//		}
//	}

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

	public List<GlobalThingInfo>  queryThingByUnionTags(List<String> tagCollect){

		String sql = "SELECT g.* "
				+ "FROM global_thing g "
				+ "INNER JOIN rel_thing_tag r ON g.id_global_thing=r.thing_id "
				+ "INNER JOIN tag_index t ON t.tag_id=r.tag_id "
				+ " WHERE t.full_tag_name in (:names) ";

		return namedJdbcTemplate.query(sql, Collections.singletonMap("names",tagCollect),getRowMapper());

	}

	public List<GlobalThingInfo>  queryThingByIntersectionTags(List<String> tagCollect){

		String sql = "select * from global_thing th where th.id_global_thing in  " +
				  "  (SELECT g.id_global_thing "
				+ "FROM global_thing g "
				+ "INNER JOIN rel_thing_tag r ON g.id_global_thing=r.thing_id "
				+ "INNER JOIN tag_index t ON t.tag_id=r.tag_id "
				+ " WHERE t.full_tag_name in (:names) group by g.id_global_thing having  count(r.tag_id) = :count )";
        Map<String,Object> params=new HashMap<>();
		params.put("names",tagCollect);
		params.put("count",tagCollect.size());

		return namedJdbcTemplate.query(sql,params,getRowMapper());

	}

	public GlobalThingInfo getThingByKiiThingID(String kiiThingID) {

		String sql = "SELECT g.* "
				+ "FROM global_thing g "
				+ " WHERE g.kii_app_id  = ? ";

		List<GlobalThingInfo> list= jdbcTemplate.query(sql,new Object[]{kiiThingID},getRowMapper());

		if(list.size()==0){
			return null;
		}else{
			return list.get(0);
		}

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

	public List<GlobalThingInfo> findThingByTag(String tagName) {
		String sql = "SELECT g.* from global_thing  "
					+ "INNER JOIN rel_thing_tag r ON g.id_global_thing=r.thing_id "
					+ "INNER JOIN tag_index t ON t.tag_id=r.tag_id "
					+ " WHERE t.full_tag_name= ? ";

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, tagName );
	    return mapToList(rows);
	}

	@Override
	protected Class<GlobalThingInfo> getEntityCls() {
		return GlobalThingInfo.class;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}


	@Override
	public String getKey() {
		return GlobalThingInfo.ID_GLOBAL_THING;
	}


	@Override
	public List<GlobalThingInfo> mapToList(List<Map<String, Object>> rows) {
		List<GlobalThingInfo> list = new ArrayList<GlobalThingInfo>();
		for (Map<String, Object> row : rows) {
			GlobalThingInfo globalThingInfo = new GlobalThingInfo();
			globalThingInfo.setId((Integer)row.get(GlobalThingInfo.ID_GLOBAL_THING));
			globalThingInfo.setVendorThingID((String)row.get(GlobalThingInfo.VANDOR_THING_ID));
			globalThingInfo.setKiiAppID((String)row.get(GlobalThingInfo.KII_APP_ID));
			globalThingInfo.setType((String)row.get(GlobalThingInfo.THING_TYPE));
			globalThingInfo.setStatus((String)row.get(GlobalThingInfo.STATUS));
			globalThingInfo.setCustom((String)row.get(GlobalThingInfo.CUSTOM_INFO));

			mapToListForDBEntity(globalThingInfo, row);

			list.add(globalThingInfo);
		}
		return list;

	}
	
	@Override
	public long update(GlobalThingInfo entity) {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ").append(this.getTableName()).append(" SET ");
		sql.append(GlobalThingInfo.VANDOR_THING_ID).append("=?, ");
		sql.append(GlobalThingInfo.KII_APP_ID).append("=?, ");
		sql.append(GlobalThingInfo.THING_TYPE).append("=?, ");
		sql.append(GlobalThingInfo.STATUS).append("=?, ");
		sql.append(GlobalThingInfo.CUSTOM_INFO).append("=?, ");
		sql.append(GlobalThingInfo.CREATE_DATE).append("=?, ");
		sql.append(GlobalThingInfo.CREATE_BY).append("=?, ");
		sql.append(GlobalThingInfo.MODIFY_DATE).append("=?, ");
		sql.append(GlobalThingInfo.MODIFY_BY).append("=? ");
		sql.append("WHERE ").append(GlobalThingInfo.ID_GLOBAL_THING).append("=? ");
		
        return jdbcTemplate.update(sql.toString(), entity.getVendorThingID(),
		        		entity.getKiiAppID(),
		        		entity.getType(),
		        		entity.getStatus(),
		        		entity.getCustom(),
		        		entity.getCreateDate(),
		        		entity.getCreateBy(),
		        		entity.getModifyDate(),
		        		entity.getModifyBy(),
		        		entity.getId());
	}


}
