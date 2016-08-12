package com.kii.beehive.portal.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.jdbc.dao.ThingLocQuery;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.manager.LocationManager;
import com.kii.beehive.portal.manager.LocationQueryManager;
import com.kii.beehive.portal.service.SubLocInfo;
import com.kii.beehive.portal.store.entity.LocationInfo;


@RestController
@RequestMapping( path="/locationTags",consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {
		MediaType.APPLICATION_JSON_UTF8_VALUE})
public class LocationController {



	@Autowired
	private LocationManager manager;


	@Autowired
	private LocationQueryManager locationQueryManager;

	@RequestMapping(value="/{location}/subLevel",method = RequestMethod.GET,consumes = {MediaType.ALL_VALUE})
	public List<LocationInfo> getLowLevelLocations(@PathVariable("location") String location){
		return manager.getLowLocation(location);

	}

	@RequestMapping(value="/{location}/parent",method = RequestMethod.GET,consumes = {MediaType.ALL_VALUE})
	public List<LocationInfo> getTopLevelLocation(@PathVariable("location") String location){


		return manager.getUpperLocation(location);

	}

	@RequestMapping(value="/{location}/allSubLocation",method = RequestMethod.GET,consumes = {MediaType.ALL_VALUE})
	public List<LocationInfo> getAllLowLevelLocations(@PathVariable("location") String location){

		return manager.getAllLowLocation(location);

	}


	@RequestMapping(value="/topLevel",method = RequestMethod.GET,consumes = {MediaType.ALL_VALUE})
	public List<LocationInfo> getUpperLevelLocation(){
		return manager.getTopLevel();

	}


	//====================

	@RequestMapping(value="/generBuilder",method = RequestMethod.POST)
	public void generalRootLocation(@RequestBody SubLocInfo builders){

		manager.generalRoot(builders);


	}

	@RequestMapping(value="/gener/{upperLevel}",method = RequestMethod.POST)
	public void generalLowerLevelLocation(@PathVariable("upperLevel")String upperLevel,@RequestBody SubLocInfo builders){



		manager.generalSubBranch(upperLevel,builders);

	}

	//==============query thing by loc

	@RequestMapping(value="/{location}/things",method = RequestMethod.GET,consumes = {MediaType.ALL_VALUE})
	public List<GlobalThingInfo> getRelThingsByLocation(@PathVariable("location") String location){


		ThingLocQuery query=new ThingLocQuery();
		query.setIncludeSub(false);
		query.setLocation(location);
		query.setType(null);

		return locationQueryManager.getThingsByLocation(query);

	}




	@RequestMapping(value="/{location}/allThings",method = RequestMethod.GET,consumes = {MediaType.ALL_VALUE})
	public List<GlobalThingInfo> getRelThingsInChildByLocation(@PathVariable("location") String location){


		ThingLocQuery  query=new ThingLocQuery();
		query.setIncludeSub(true);
		query.setLocation(location);
		query.setType(null);

		return locationQueryManager.getThingsByLocation(query);
	}


	@RequestMapping(value="/{location}/things/{type}",method = RequestMethod.GET,consumes = {MediaType.ALL_VALUE})
	public List<GlobalThingInfo> getRelThingsByLocationAndType(@PathVariable("location") String location,@PathVariable("type") String type){


		ThingLocQuery  query=new ThingLocQuery();
		query.setIncludeSub(false);
		query.setLocation(location);
		query.setType(type);

		return locationQueryManager.getThingsByLocation(query);

	}




	@RequestMapping(value="/{location}/allThings/{type}",method = RequestMethod.GET,consumes = {MediaType.ALL_VALUE})
	public List<GlobalThingInfo> getRelThingsInChildByLocationAndType(@PathVariable("location") String location,@PathVariable("type") String type){


		ThingLocQuery  query=new ThingLocQuery();
		query.setIncludeSub(true);
		query.setLocation(location);
		query.setType(type);

		return locationQueryManager.getThingsByLocation(query);

	}




}
