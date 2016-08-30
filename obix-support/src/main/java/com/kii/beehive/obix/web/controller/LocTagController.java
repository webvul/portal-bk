package com.kii.beehive.obix.web.controller;


import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.obix.service.LocationService;
import com.kii.beehive.obix.store.LocationInfo;
import com.kii.beehive.obix.store.PointInfo;
import com.kii.beehive.obix.store.ThingInfo;

@RestController
@RequestMapping(path="/site",method= RequestMethod.GET,consumes = {MediaType.ALL_VALUE},produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class LocTagController {


	@Autowired
	private LocationService  locService;



	@RequestMapping(path="/**")
	public LocationInfo getRootLoc(HttpServletRequest request){

		return locService.getRootLoc();


	}


	@RequestMapping(path="/**/equips/{thingID}")
	public ThingInfo getThingInfo(@PathVariable("thingID") String thingID){

		return locService.getThingInfo(thingID);


	}

	@RequestMapping(path="/**/equips/{thingID}/{pointID}")
	public PointInfo getThingInfo(@PathVariable("thingID")String thingID,@PathVariable("thingID")String pointID){


		return locService.getPointInfo(thingID,pointID);

	}

}
