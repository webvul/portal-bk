package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.Date;
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
		Object[] paramArr = new String[params.size()];
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
			globalThingInfo.setId((int)row.get(GlobalThingInfo.ID_GLOBAL_THING));
			globalThingInfo.setVendorThingID((String)row.get(GlobalThingInfo.VANDOR_THING_ID));
			globalThingInfo.setKiiAppID((String)row.get(GlobalThingInfo.KII_APP_ID));
			globalThingInfo.setType((String)row.get(GlobalThingInfo.THING_TYPE));
			globalThingInfo.setStatus((String)row.get(GlobalThingInfo.STATUS));
			globalThingInfo.setCustom((String)row.get(GlobalThingInfo.CUSTOM_INFO));
			globalThingInfo.setCreateBy((String)row.get(GlobalThingInfo.CREATE_BY));
			globalThingInfo.setCreateDate((Date)row.get(GlobalThingInfo.CREATE_DATE));
			globalThingInfo.setModifyBy((String)row.get(GlobalThingInfo.MODIFY_BY));
			globalThingInfo.setModifyDate((Date)row.get(GlobalThingInfo.CREATE_DATE));
			list.add(globalThingInfo);
		}
		return list;
	}


}
