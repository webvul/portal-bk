package com.kii.beehive.portal.web.controller;


import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.manager.LocationManager;
import com.kii.beehive.portal.store.entity.LocationInfo;

@RestController
@RequestMapping( consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {
		MediaType.APPLICATION_JSON_UTF8_VALUE})
public class LocationRelController {



	@Autowired
	private LocationManager manager;


	@RequestMapping(value="/thing/{thingID}/location/{location}",method = RequestMethod.PUT)
	public void addLocationInThing(@PathVariable("thingID") long thingID,@PathVariable("location") String location){

		manager.updateRelation(thingID, Collections.singletonList(location));

	}

	@RequestMapping(value="/thing/{thingID}/location/{location}",method = RequestMethod.DELETE,consumes = {MediaType.ALL_VALUE})
	public void removeLocationFromThing(@PathVariable("thingID") long thingID,@PathVariable("location") String location){

		manager.removeRelation(thingID, Collections.singletonList(location));

	}


	@RequestMapping(value="/thing/{thingID}/location",method = RequestMethod.PATCH)
	public void addLocListToThing(@PathVariable("thingID") long thingID, @RequestBody List<String> locList){

		manager.addRelation(thingID,locList);

	}

	@RequestMapping(value="/thing/{thingID}/location",method = RequestMethod.GET,consumes = {MediaType.ALL_VALUE})
	public List<LocationInfo> getLocListFromThing(@PathVariable("thingID") long thingID){

		manager


	}


	@RequestMapping(value="/thing/{thingID}/removeLoc",method = RequestMethod.POST)
	public void removeLocListFromThing(@RequestBody List<String> locList){


	}


	@RequestMapping(value="/thing/{thingID}/location",method = RequestMethod.PUT)
	public void updateLocListToThing(@RequestBody List<String> locList){


	}


	@RequestMapping(value="/location/{location}/subLevel",method = RequestMethod.GET,consumes = {MediaType.ALL_VALUE})
	public List<LocationInfo> getLowLevelLocations(){

	}

	@RequestMapping(value="/location/{location}/parent",method = RequestMethod.GET,consumes = {MediaType.ALL_VALUE})
	public LocationInfo getUpperLevelLocations(){

	}

	@RequestMapping(value="/location/{location}/subTree",method = RequestMethod.GET,consumes = {MediaType.ALL_VALUE})
	public Map<String,LocationInfo> getLowLevelLocations(){

	}




}
