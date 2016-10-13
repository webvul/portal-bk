package com.kii.beehive.obix.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.obix.dao.ThingSchemaDao;
import com.kii.beehive.obix.helper.ThingStatusService;
import com.kii.beehive.obix.store.PointInfo;
import com.kii.beehive.obix.store.ThingInfo;

@Component
@Transactional
public class ThingService {


	@Autowired
	private ThingStatusService thingService;



	@Autowired
	private ThingSchemaDao schemaDao;




	public ThingInfo getFullThingInfo(String thingID){

//
//		Thing thing=thingService.getThingByID(thingID);
//
//		ThingSchema schema=schemaDao.getThingSchemaByName(thing.getSchema());
//
//		ThingInfo info=new ThingInfo(schema,thing.getStatus());
//
//		info.setName(thing.getThingID());

		return thingService.getThingByID(thingID);

	}

	public List<PointInfo>  getPointInfoByLoc(String loc){

		String thLoc= StringUtils.substringBeforeLast(loc,"-");

		return thingService.getThingInfoByLoc(thLoc).stream()
				.flatMap((th)->th.getPointCollect().stream())
				.filter((p-> p.getLocation().equals(loc))).collect(Collectors.toList());

	}

	public PointInfo getPointInfo(String thingID,String pointName){


		return thingService.getThingByID(thingID).getPointCollect()
				.stream().filter(p->p.getFieldName().equals(pointName)).findAny().get();

	}



	private void setPointInfo(String thingID,PointInfo point){


		thingService.setThingStatus(thingID,point.getFieldName(),point.getValue());
	}

}
