package com.kii.beehive.portal.manager;


import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.helper.LocationTreeService;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.ThingLocationDao;
import com.kii.beehive.portal.jdbc.dao.ThingLocationRelDao;
import com.kii.beehive.portal.service.LocationDao;
import com.kii.beehive.portal.service.SubLocInfo;
import com.kii.beehive.portal.store.entity.LocationInfo;
import com.kii.beehive.portal.store.entity.LocationTree;
import com.kii.beehive.portal.store.entity.LocationType;

@Component
@Transactional
public class LocationManager {



	@Autowired
	private ThingLocationRelDao relDao;

	@Autowired
	private ThingLocationDao thingLocDao;

	@Autowired
	private LocationDao locDao;


	@Autowired
	private GlobalThingSpringDao thingDao;
	
	@Autowired
	private LocationTreeService  treeCache;
	
	
	@PostConstruct
	public void init(){
		
		treeCache.refreshTree();
	}


	public void generalRoot(SubLocInfo  locInfo){

		locDao.generTopLocation(locInfo);
		treeCache.refreshTree();

	}

	public void generalSubBranch(String upperLevel,SubLocInfo  locInfo){

		locDao.generSubLevelInUpper(upperLevel,locInfo);
		treeCache.refreshTree();

	}

	public List<LocationInfo>  getLowLocation(String location){
		return locDao.getLowLocation(location);
	}

	public List<LocationInfo> getAllLowLocation(String location){
		return locDao.getAllLowLocation(location);
	}

	public List<LocationInfo> getUpperLocation(String location){
		if(LocationType.getTypeByLocation(location)==LocationType.building){
			return new ArrayList<>();
		}
		return locDao.getPathToLoc(location);
	}


	public List<LocationInfo> getTopLevel() {

		return locDao.getTopLocation();
	}
	//=========================

	public List<LocationInfo> getThingRelLocations(Long thingID){
		verifyThingID(thingID);

		List<String>  locations=relDao.getRelation(thingID);

		if(locations.isEmpty()){
			return new ArrayList<>();
		}

		return locDao.getEntitys(locations.toArray(new String[0]));
	}


	//=========================

	public void addRelation(Long thingID,List<String> locList){
		verifyThingID(thingID);

		LinkedList list=new LinkedList(locList);
		List<String>  relList=relDao.getRelation(thingID);

		list.removeAll(relList);
		relDao.addRelation(thingID,list);

	}


	public void removeRelation(Long thingID,List<String> locList){
		verifyThingID(thingID);

		relDao.removeRelation(thingID,locList);

	}


	public void updateRelation(Long thingID,List<String> locList){
		verifyThingID(thingID);

		relDao.clearAllRelation(thingID);

		relDao.addRelation(thingID,locList);

	}

	public void clearRelation(Long thingID){
		verifyThingID(thingID);

		relDao.clearAllRelation(thingID);

	}

	private void verifyThingID(Long thingID){

		try {
			thingDao.existEntity(thingID);
		}catch(EmptyResultDataAccessException ex){
			throw new EntryNotFoundException(String.valueOf(thingID),"thing");
		}
	}

	
	public LocationTree getFullTree(){
		return  treeCache.getLocationTree();
	}

}
