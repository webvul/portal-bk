package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Repository;
import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.ThingGeo;

@Repository
public class ThingGeoDao extends SpringBaseDao<ThingGeo> {


	public static final String TABLE_NAME = "thing_geo";
	public static final String KEY = ThingGeo.ID;

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}


	@Override
	public String getKey() {
		return KEY;
	}

	public List<ThingGeo> getDuplicatedThingGeo(ThingGeo thingGeo) {

		Long globalThingID = thingGeo.getGlobalThingID();
		String vendorThingID = thingGeo.getVendorThingID();
		String aliThingID = thingGeo.getAliThingID();


		String sql="select * from ${0} where 1<>1 ";
		List<Object> params=new ArrayList<>();


		if(globalThingID!=null){
			sql+="  or " + ThingGeo.GLOBAL_THING_ID + " =  ? ";
			params.add(globalThingID);
		}
		if(vendorThingID!=null){
			sql+="  or " + ThingGeo.VENDOR_THING_ID + " =  ? ";
			params.add(vendorThingID);
		}
		if(aliThingID!=null){
			sql+="  or " + ThingGeo.ALI_THING_ID + " =  ? ";
			params.add(aliThingID);
		}

		// order by
		sql += " order by " + ThingGeo.BUILDING_ID + ", " + ThingGeo.FLOOR;

		String fullSql= StrTemplate.gener(sql,TABLE_NAME);

		return query(fullSql,params.toArray());

	}

	public List<ThingGeo> findByBuildingIDAndFloor(String buildingID, Integer floor) {

		String sql="select * from ${0} where 1=1 ";
		List<Object> params=new ArrayList<>();


		if(buildingID != null){
			sql+="  and " + ThingGeo.BUILDING_ID + " =  ? ";
			params.add(buildingID);
		}
		if(floor != null){
			sql+="  and " + ThingGeo.FLOOR + " =  ? ";
			params.add(floor);
		}

		// order by
		sql += " order by " + ThingGeo.BUILDING_ID + ", " + ThingGeo.FLOOR;

		String fullSql= StrTemplate.gener(sql,TABLE_NAME);

		return query(fullSql,params.toArray());
	}

	public int cleanGlobalthingID(String buildingID, Integer floor) {
		String sql="update ${0} set " + ThingGeo.GLOBAL_THING_ID + "=null where 1=1 ";
		List<Object> params=new ArrayList<>();


		if(buildingID != null){
			sql+="  and " + ThingGeo.BUILDING_ID + " =  ? ";
			params.add(buildingID);
		}
		if(floor != null){
			sql+="  and " + ThingGeo.FLOOR + " =  ? ";
			params.add(floor);
		}

		String fullSql= StrTemplate.gener(sql,TABLE_NAME);

		return this.jdbcTemplate.update(fullSql, params.toArray());
	}

	public int syncGlobalThingID(String buildingID, Integer floor) {

		String sql = "update ${0} geo inner join ${1} thing " +
				"set geo.${2} = thing.${3} " +
				"where thing.${4}=0 and geo.${5} = thing.${6} ";
		List<Object> params=new ArrayList<>();


		if(buildingID != null){
			sql+="  and geo." + ThingGeo.BUILDING_ID + " =  ? ";
			params.add(buildingID);
		}
		if(floor != null){
			sql+="  and geo." + ThingGeo.FLOOR + " =  ? ";
			params.add(floor);
		}

		String fullSql= StrTemplate.gener(sql,TABLE_NAME, GlobalThingSpringDao.TABLE_NAME,
				ThingGeo.GLOBAL_THING_ID, GlobalThingInfo.ID_GLOBAL_THING,
				GlobalThingInfo.IS_DELETED, ThingGeo.VENDOR_THING_ID, GlobalThingInfo.VANDOR_THING_ID);

		return this.jdbcTemplate.update(fullSql, params.toArray());
	}

	public int updateGlobalThingIDByVendorThingID(String vendorThingID, Long globalThingID) {

		if(Strings.isBlank(vendorThingID)) {
			return 0;
		}

		String sql = "update ${0} set ${1} = ? where ${2} = ?";
		List<Object> params=new ArrayList<>();
		params.add(globalThingID);
		params.add(vendorThingID);

		String fullSql= StrTemplate.gener(sql, TABLE_NAME, ThingGeo.GLOBAL_THING_ID, ThingGeo.VENDOR_THING_ID);

		return this.jdbcTemplate.update(fullSql, params.toArray());
	}

}
