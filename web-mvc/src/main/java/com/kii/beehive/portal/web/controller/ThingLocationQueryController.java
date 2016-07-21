package com.kii.beehive.portal.web.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.jdbc.dao.ThingLocQuery;
import com.kii.beehive.portal.jdbc.dao.ThingLocationDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.manager.LocationQueryManager;

@RestController
@RequestMapping( consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {
		MediaType.APPLICATION_JSON_UTF8_VALUE})

public class ThingLocationQueryController {


	@Autowired
	private LocationQueryManager locationQueryManager;

	@RequestMapping(value="/reports/thingQuery/groupByType",method = RequestMethod.POST)
	public Map<String,ThingLocationDao.ThingIDs> queryByThingLocationWithType(@RequestBody ThingLocQuery query){

			return locationQueryManager.doQueryWithGroup(query,true);


	}

	@RequestMapping(value="/reports/thingQuery/groupByLocation",method = RequestMethod.POST)
	public Map<String,ThingLocationDao.ThingIDs> queryByThingLocationWithLocation(@RequestBody ThingLocQuery query){

		return locationQueryManager.doQueryWithGroup(query,false);



	}

	@RequestMapping(value="/reports/thingQuery",method = RequestMethod.POST)
	public List<String>  queryByThingLocation(@RequestBody ThingLocQuery query){
			return locationQueryManager.doQueryForReport(query);
	}

	@RequestMapping(value="/reports/thingQuery/groupByAll",method = RequestMethod.POST)
	public  Map<String,Map<String,ThingLocationDao.ThingIDs>> queryByThingLocationWithGroup(@RequestBody ThingLocQuery query){
		return locationQueryManager.doQueryForReportWithAllGroup(query);
	}


	//query thing by rel thing

	@RequestMapping(value="/things/{thingID}/relThingsInLoc",method = RequestMethod.GET,consumes = {MediaType.ALL_VALUE})
	public List<GlobalThingInfo> getRelLocThings(@PathVariable("thingID") Long thingID){

		return locationQueryManager.getRelThing(thingID,new ThingLocQuery());
	}



	@RequestMapping(value="/things/{thingID}/relThingsInLoc/type/{type}/location/{location}",method = RequestMethod.GET,consumes = {MediaType.ALL_VALUE})
	public List<GlobalThingInfo> getRelLocThingsByTypeLocations(@PathVariable("thingID") Long thingID,@PathVariable("type") String type,@PathVariable("location") String location){

		ThingLocQuery query=new ThingLocQuery();
		if(!"*".equals(type)) {
			query.setType(type);
		}
		if(!"*".equals(location)) {
			query.setLocation(location);
		}
		query.setIncludeSub(false);

		return locationQueryManager.getRelThing(thingID,query);
	}



	//==============query thing by loc

	@RequestMapping(value="/locations/{location}/things",method = RequestMethod.GET,consumes = {MediaType.ALL_VALUE})
	public List<GlobalThingInfo> getRelThingsByLocation(@PathVariable("location") String location){


		ThingLocQuery  query=new ThingLocQuery();
		query.setIncludeSub(false);
		query.setLocation(location);
		query.setType(null);

		return locationQueryManager.getThingsByLocation(query);

	}




	@RequestMapping(value="/locations/{location}/allThings",method = RequestMethod.GET,consumes = {MediaType.ALL_VALUE})
	public List<GlobalThingInfo> getRelThingsInChildByLocation(@PathVariable("location") String location){


		ThingLocQuery  query=new ThingLocQuery();
		query.setIncludeSub(true);
		query.setLocation(location);
		query.setType(null);

		return locationQueryManager.getThingsByLocation(query);
	}


	@RequestMapping(value="/locations/{location}/things/{type}",method = RequestMethod.GET,consumes = {MediaType.ALL_VALUE})
	public List<GlobalThingInfo> getRelThingsByLocationAndType(@PathVariable("location") String location,@PathVariable("type") String type){


		ThingLocQuery  query=new ThingLocQuery();
		query.setIncludeSub(false);
		query.setLocation(location);
		query.setType(type);

		return locationQueryManager.getThingsByLocation(query);

	}




	@RequestMapping(value="/locations/{location}/allThings/{type}",method = RequestMethod.GET,consumes = {MediaType.ALL_VALUE})
	public List<GlobalThingInfo> getRelThingsInChildByLocationAndType(@PathVariable("location") String location,@PathVariable("type") String type){


		ThingLocQuery  query=new ThingLocQuery();
		query.setIncludeSub(true);
		query.setLocation(location);
		query.setType(type);

		return locationQueryManager.getThingsByLocation(query);

	}


}
