package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.kii.beehive.portal.common.utils.StrTemplate;
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

		String fullSql= StrTemplate.gener(sql,TABLE_NAME);

		return query(fullSql,params.toArray());
	}

}
