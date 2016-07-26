package com.kii.beehive.portal.web.controller;

import java.util.ArrayList;
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
import com.kii.beehive.portal.web.entity.ThingIDsForReportWithDoubleGroup;
import com.kii.beehive.portal.web.entity.ThingIDsForReportWithGroup;

@RestController
@RequestMapping( consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {
		MediaType.APPLICATION_JSON_UTF8_VALUE})
public class ThingLocationQueryController {


	@Autowired
	private LocationQueryManager locationQueryManager;

	@RequestMapping(value="/reports/thingQuery/groupByType",method = RequestMethod.POST)
	public List<ThingIDsForReportWithGroup>   queryByThingLocationWithType(@RequestBody ThingLocQuery query){


		Map<String,ThingLocationDao.ThingIDs>  map= locationQueryManager.doQueryWithGroup(query,true);

		List<ThingIDsForReportWithGroup>  idList=new ArrayList<>();

		map.forEach((k,v)->{

			ThingIDsForReportWithGroup group=new ThingIDsForReportWithGroup();
			group.setGroupName(k);
			group.setThingIDArray(v);

			idList.add(group);
		});

		return idList;

	}

	@RequestMapping(value="/reports/thingQuery/groupByLocationTag",method = RequestMethod.POST)
	public List<ThingIDsForReportWithGroup> queryByThingLocationWithLocation(@RequestBody ThingLocQuery query){


		Map<String,ThingLocationDao.ThingIDs>  map= locationQueryManager.doQueryWithGroup(query,false);

		List<ThingIDsForReportWithGroup>  idList=new ArrayList<>();

		map.forEach((k,v)->{

			ThingIDsForReportWithGroup group=new ThingIDsForReportWithGroup();
			group.setGroupName(k);
			group.setThingIDArray(v);

			idList.add(group);
		});

		return idList;


	}

	@RequestMapping(value="/reports/thingQuery",method = RequestMethod.POST)
	public List<String>  queryByThingLocation(@RequestBody ThingLocQuery query){
			return locationQueryManager.doQueryForReport(query);
	}

	@RequestMapping(value="/reports/thingQuery/groupByAll",method = RequestMethod.POST)
	public  List<ThingIDsForReportWithDoubleGroup> queryByThingLocationWithGroup(@RequestBody ThingLocQuery query){
		Map<String,Map<String,ThingLocationDao.ThingIDs>>  map= locationQueryManager.doQueryForReportWithAllGroup(query);

		List<ThingIDsForReportWithDoubleGroup>  idList=new ArrayList<>();

		map.forEach((k,v)->{

			List<ThingIDsForReportWithGroup>  list=new ArrayList<>();
			v.forEach((kk,vv)->{
				ThingIDsForReportWithGroup group=new ThingIDsForReportWithGroup();
				group.setGroupName(kk);
				group.setThingIDArray(vv);

				list.add(group);
			});

			ThingIDsForReportWithDoubleGroup topGroup=new ThingIDsForReportWithDoubleGroup();
			topGroup.setGroupName(k);
			topGroup.setSubGroupArray(list);

			idList.add(topGroup);
		});

		return idList;
	}


	//query thing by rel thing

	@RequestMapping(value="/things/{thingID}/relThingsInLocTag",method = RequestMethod.GET,consumes = {MediaType.ALL_VALUE})
	public List<GlobalThingInfo> getRelLocThings(@PathVariable("thingID") Long thingID){

		return locationQueryManager.getRelThing(thingID,new ThingLocQuery());
	}



	@RequestMapping(value="/things/{thingID}/relThingsInLocTag/type/{type}/locationTag/{location}",method = RequestMethod.GET,consumes = {MediaType.ALL_VALUE})
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





}
