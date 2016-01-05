package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;

@Repository
public class GlobalThingDao extends BaseDao<GlobalThingInfo>{


	public static final String TABLE_NAME = "global_thing";

	public void test(){
		jdbcTemplate.execute("select sysdate() from dual");
	}

	public List<String> findAllThingTypes() {
		String sql = "SELECT DISTINCT "+ GlobalThingInfo.THING_TYPE +" FROM " + this.getTableName();

		List<String> rows = jdbcTemplate.queryForList(sql, null, String.class);

		return rows;
	}

	public List<Map<String, Object>> findAllThingTypesWithThingCount() {
		String sql = "SELECT "+ GlobalThingInfo.THING_TYPE +" as type, COUNT(1) as count FROM " + this.getTableName()
				+ " GROUP BY " + GlobalThingInfo.THING_TYPE;

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[0]);

		return rows;
	}
	
	public List<GlobalThingInfo> getThingByType(String type) {
		List<GlobalThingInfo> list = super.findBySingleField(GlobalThingInfo.THING_TYPE, type);
			return list;
	}

	public GlobalThingInfo getThingByVendorThingID(String vendorThingID) {
		List<GlobalThingInfo> list = super.findBySingleField(GlobalThingInfo.VANDOR_THING_ID, vendorThingID);
		if(list.size() > 0){
			return list.get(0);
		}else{
			return null;
		}
	}

	public List<GlobalThingInfo> findThingByTag(String tagType,String displayName) {
		String sql = "SELECT g.id_global_thing,g.vendor_thing_id,g.kii_app_id,g.thing_type,g.custom_info,g.status,g.create_by,g.create_date,g.modify_by,g.modify_date "
					+ "FROM " + this.getTableName() +" g "
					+ "INNER JOIN rel_thing_tag r ON g.id_global_thing=r.thing_id " 
					+ "INNER JOIN tag_index t ON t.tag_id=r.tag_id "
					+ " WHERE ";
		
		StringBuilder where = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		if(!Strings.isBlank(tagType)){
			where.append(" t.tag_type = ? "); 
			params.add(tagType);
		}
		
		if(!Strings.isBlank(displayName)){
			if(where.length() > 0){
				where.append("AND");
			}
			where.append(" t.display_name = ? ");
			params.add(displayName);
		}
		Object[] paramArr = new Object[params.size()];
		paramArr = params.toArray(paramArr);
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql+where.toString(), paramArr);
	    return mapToList(rows);
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
		String[] columns = new String[]{
				GlobalThingInfo.ID_GLOBAL_THING,
				GlobalThingInfo.VANDOR_THING_ID,
				GlobalThingInfo.KII_APP_ID,
				GlobalThingInfo.THING_TYPE,
				GlobalThingInfo.STATUS,
				GlobalThingInfo.CUSTOM_INFO,
				GlobalThingInfo.CREATE_DATE,
				GlobalThingInfo.CREATE_BY,
				GlobalThingInfo.MODIFY_DATE,
				GlobalThingInfo.MODIFY_BY,
		};
		
        return super.update(entity, columns);
	}

}
