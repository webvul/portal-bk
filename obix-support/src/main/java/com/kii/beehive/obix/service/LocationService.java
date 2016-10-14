package com.kii.beehive.obix.service;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.obix.helper.ThingStatusService;
import com.kii.beehive.obix.store.LocationView;
import com.kii.beehive.portal.service.LocationDao;
import com.kii.beehive.portal.store.entity.LocationInfo;

@Component
@Transactional
public class LocationService {



	@Autowired
	private LocationDao locDao;

	@Autowired
	private ThingStatusService thingService;



	public List<LocationView> getRootLoc(){

		return locDao.getTopLocation().stream().map(LocationView::new).collect(Collectors.toList());

	}
	
	
	public LocationView getLocationInfo(String locStr) {


		LocationInfo loc=locDao.getObjectByID(locStr);

		LocationView view =new LocationView(loc);

		thingService.getThingInfoByLoc(locStr).forEach(view::addEntity);

//		thingService.getPointInfoByLoc(locStr).forEach(view::addEntity);

		return view;

	}
}
