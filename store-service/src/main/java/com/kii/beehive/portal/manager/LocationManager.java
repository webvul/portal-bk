package com.kii.beehive.portal.manager;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.portal.jdbc.dao.ThingLocationDao;
import com.kii.beehive.portal.jdbc.dao.ThingLocationRelDao;
import com.kii.beehive.portal.service.LocationDao;
import com.kii.beehive.portal.service.SubLocInfo;
import com.kii.beehive.portal.store.entity.LocationInfo;

@Component
@Transactional
public class LocationManager {



	@Autowired
	private ThingLocationRelDao relDao;

	@Autowired
	private ThingLocationDao thingLocDao;

	@Autowired
	private LocationDao locDao;

	public void generalRoot(SubLocInfo  locInfo){

		locDao.generTopLocation(locInfo);

	}

	public void generalSubBranch(String upperLevel,SubLocInfo  locInfo){

		locDao.generSubLevelInUpper(upperLevel,locInfo);

	}

	public List<LocationInfo>  getLowLocation(String location){
		return locDao.getLowLocation(location);
	}

	public List<LocationInfo> getAllLowLocation(String location){
		return locDao.getAllLowLocation(location);
	}

	public List<LocationInfo> getUpperLocation(String location){
		return locDao.getPathToLoc(location);
	}


	public List<LocationInfo> getTopLevel() {

		return locDao.getTopLocation();
	}
	//=========================

	public List<LocationInfo> getThingRelLocations(Long thingID){

		List<String>  locations=relDao.getRelation(thingID);

		if(locations.isEmpty()){
			return new ArrayList<>();
		}

		return locDao.getEntitys(locations.toArray(new String[0]));
	}


	//=========================

	public void addRelation(Long thingID,List<String> locList){

		relDao.addRelation(thingID,locList);

	}


	public void removeRelation(Long thingID,List<String> locList){

		relDao.removeRelation(thingID,locList);

	}


	public void updateRelation(Long thingID,List<String> locList){

		relDao.clearAllRelation(thingID);

		relDao.addRelation(thingID,locList);

	}

	public void clearRelation(Long thingID){


		relDao.clearAllRelation(thingID);

	}
	

}
