package com.kii.beehive.portal.manager;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.entitys.ThingID;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
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



	@Autowired
	private GlobalThingSpringDao thingDao;


	public List<ThingID> doQueryForReport(ThingLocQuery query){

		return thingLocDao.getThingsByLocation(query,AuthInfoStore.getUserID()).stream().map(ThingID::new).collect(Collectors.toList());

	}

	public Map<String,ThingLocationDao.ThingIDs> doQueryWithGroup(ThingLocQuery query, boolean withType){
		return thingLocDao.getIDsByTypeGroup(query,AuthInfoStore.getUserID(),withType);
	}

	public Map<String,Map<String,ThingLocationDao.ThingIDs>> doQueryForReportWithAllGroup(ThingLocQuery query){

			return thingLocDao.getIDsByLocationAndTypeGroup(query,AuthInfoStore.getUserID());
	}


	//===========================


	public List<GlobalThingInfo>  getRelThing(Long thingID,ThingLocQuery query){
		verifyThingID(thingID);
		return thingLocDao.getRelationThingsByThingLocatoin(thingID,AuthInfoStore.getUserID(),query);
	}

	//===========================
	public List<GlobalThingInfo>  getThingsByLocation(ThingLocQuery query){

		return thingLocDao.getThingsByLocation(query, AuthInfoStore.getUserID());
	}

	private void verifyThingID(Long thingID){

		try {
			thingDao.existEntity(thingID);
		}catch(EmptyResultDataAccessException ex){
			throw new EntryNotFoundException(String.valueOf(thingID),"thing");
		}
	}

}
