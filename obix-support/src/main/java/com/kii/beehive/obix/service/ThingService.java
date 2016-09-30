package com.kii.beehive.obix.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.obix.dao.DemoThingStatusDao;
import com.kii.beehive.obix.dao.ThingSchemaDao;
import com.kii.beehive.obix.store.PointInfo;
import com.kii.beehive.obix.store.ThingInfo;
import com.kii.beehive.obix.store.beehive.Thing;
import com.kii.beehive.obix.store.beehive.ThingSchema;

@Component
public class ThingService {


	@Autowired
	private DemoThingStatusDao thingDao;



	@Autowired
	private ThingSchemaDao schemaDao;



	public ThingInfo getFullThingInfo(String thingID){


		Thing thing=thingDao.getThingByID(thingID);

		ThingSchema  schema=schemaDao.getThingSchemaByName(thing.getSchema());

		ThingInfo info=new ThingInfo(schema,thing.getStatus());

		info.setName(thing.getThingID());

		return info;

	}

	public List<ThingInfo>  getThingInfoByLoc(String loc){

		return thingDao.getThingIDByLoc(loc).stream().map(this::getFullThingInfo).collect(Collectors.toList());

	}

	public List<PointInfo>  getPointInfoByLoc(String loc){

		String thLoc= StringUtils.substringBeforeLast(loc,"-");

		return getThingInfoByLoc(thLoc).stream()
				.flatMap((th)->th.getPointCollect().stream())
				.filter((p-> p.getLocation().equals(loc))).collect(Collectors.toList());

	}

	public PointInfo getPointInfo(String thingID,String pointName){


		return getFullThingInfo(thingID).getPointCollect()
				.stream().filter(p->p.getFieldName().equals(pointName)).findAny().get();

	}



	private void setPointInfo(String thingID,PointInfo point){


		thingDao.setThingStatus(thingID,point.getFieldName(),point.getValue());
	}

}
