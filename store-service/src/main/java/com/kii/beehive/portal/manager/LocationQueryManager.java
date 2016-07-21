package com.kii.beehive.portal.manager;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.portal.jdbc.dao.ThingLocQuery;
import com.kii.beehive.portal.jdbc.dao.ThingLocationDao;
import com.kii.beehive.portal.jdbc.dao.ThingLocationRelDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;

@Component
@Transactional
public class LocationQueryManager {


	@Autowired
	private ThingLocationRelDao  relDao;

	@Autowired
	private ThingLocationDao  thingLocDao;


	public List<String> doQueryForReport(ThingLocQuery query){

		return thingLocDao.getThingsByLocation(query).stream().map(GlobalThingInfo::getVendorThingID).collect(Collectors.toList());

	}

	public Map<String,ThingLocationDao.ThingIDs> doQueryWithGroup(ThingLocQuery query, boolean withType){
		return thingLocDao.getIDsByTypeGroup(query,withType);
	}

	public Map<String,Map<String,ThingLocationDao.ThingIDs>> doQueryForReportWithAllGroup(ThingLocQuery query){

			return thingLocDao.getIDsByLocationAndTypeGroup(query);
	}


	//===========================


	public List<GlobalThingInfo>  getRelThing(Long thingID,ThingLocQuery query){
		return thingLocDao.getRelationThingsByThingLocatoin(thingID,query);
	}

	//===========================
	public List<GlobalThingInfo>  getThingsByLocation(ThingLocQuery query){
		return thingLocDao.getThingsByLocation(query);
	}

}
