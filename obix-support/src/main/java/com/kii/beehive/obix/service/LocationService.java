package com.kii.beehive.obix.service;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.obix.dao.DemoThingStatusDao;
import com.kii.beehive.obix.dao.DemoLocationDao;
import com.kii.beehive.obix.store.LocationInfo;

@Component
public class LocationService {



	@Autowired
	private DemoLocationDao locDao;

	@Autowired
	private DemoThingStatusDao  thingDao;

	@Autowired
	private ThingService  thService;


	public List<LocationInfo> getRootLoc(){

		List<LocationInfo>  locList=new ArrayList<>();

		locDao.getTopLocation().forEach(s->{
			LocationInfo  loc=new LocationInfo();
			loc.setLocation(s);
			loc.setParent(null);

			List<String> subLoc=locDao.getChildLoc(s);
			loc.setSubLocations(subLoc);

			locList.add(loc);

		});

		return locList;

	}
	
	
	public LocationInfo getLocationInfo(String locStr) {

		LocationInfo  loc=new LocationInfo();
		loc.setLocation(locStr);
		loc.setParent(locDao.getParentLoc(locStr));
		loc.setSubLocations(locDao.getChildLoc(locStr));


		thService.getThingInfoByLoc(locStr).forEach(loc::addEntity);

		thService.getPointInfoByLoc(locStr).forEach(loc::addEntity);

		return loc;

	}
}
