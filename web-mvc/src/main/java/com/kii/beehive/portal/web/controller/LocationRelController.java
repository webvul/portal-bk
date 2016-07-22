package com.kii.beehive.portal.web.controller;


import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.manager.LocationManager;
import com.kii.beehive.portal.service.SubLocInfo;
import com.kii.beehive.portal.store.entity.LocationInfo;

@RestController
@RequestMapping( consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {
		MediaType.APPLICATION_JSON_UTF8_VALUE})
public class LocationRelController {



	@Autowired
	private LocationManager manager;


	@RequestMapping(value="/things/{thingID}/location/{location}",method = RequestMethod.PUT,consumes = {MediaType.ALL_VALUE})
	public void addLocationInThing(@PathVariable("thingID") long thingID,@PathVariable("location") String location){

		manager.updateRelation(thingID, Collections.singletonList(location));

	}

	@RequestMapping(value="/things/{thingID}/location/{location}",method = RequestMethod.DELETE,consumes = {MediaType.ALL_VALUE})
	public void removeLocationFromThing(@PathVariable("thingID") long thingID,@PathVariable("location") String location){

		manager.removeRelation(thingID, Collections.singletonList(location));

	}


	@RequestMapping(value="/things/{thingID}/location",method = RequestMethod.PATCH)
	public void addLocListToThing(@PathVariable("thingID") long thingID, @RequestBody List<String> locList){

		manager.addRelation(thingID,locList);

	}

	@RequestMapping(value="/things/{thingID}/location",method = RequestMethod.GET,consumes = {MediaType.ALL_VALUE})
	public List<LocationInfo> getLocListFromThing(@PathVariable("thingID") long thingID){

		return manager.getThingRelLocations(thingID);

	}


	@RequestMapping(value="/things/{thingID}/removeLoc",method = RequestMethod.POST)
	public void removeLocListFromThing(@PathVariable("thingID") long thingID, @RequestBody List<String> locList){

		manager.removeRelation(thingID, locList);

	}


	@RequestMapping(value="/things/{thingID}/location",method = RequestMethod.PUT)
	public void updateLocListToThing(@PathVariable("thingID") long thingID, @RequestBody List<String> locList){


		manager.updateRelation(thingID, locList);

	}

	//======================

	@RequestMapping(value="/locations/{location}/subLevel",method = RequestMethod.GET,consumes = {MediaType.ALL_VALUE})
	public List<LocationInfo> getLowLevelLocations(@PathVariable("location") String location){
		return manager.getLowLocation(location);

	}

	@RequestMapping(value="/locations/{location}/parent",method = RequestMethod.GET,consumes = {MediaType.ALL_VALUE})
	public List<LocationInfo> getUpperLevelLocation(@PathVariable("location") String location){
		return manager.getUpperLocation(location);

	}

	@RequestMapping(value="/locations/{location}/allSubLocation",method = RequestMethod.GET,consumes = {MediaType.ALL_VALUE})
	public List<LocationInfo> getAllLowLevelLocations(@PathVariable("location") String location){

		return manager.getAllLowLocation(location);

	}


	//====================

	@RequestMapping(value="/locations/generBuilder",method = RequestMethod.POST)
	public void generalRootLocation(@RequestBody SubLocInfo builders){

		manager.generalRoot(builders);


	}

	@RequestMapping(value="/locations/gener/{upperLevel}",method = RequestMethod.POST)
	public void generalLowerLevelLocation(@PathVariable("upperLevel")String upperLevel,@RequestBody SubLocInfo builders){


		manager.generalSubBranch(upperLevel,builders);

	}






}
