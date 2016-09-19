package com.kii.beehive.obix.web.controller;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.kii.beehive.obix.service.LocationService;
import com.kii.beehive.obix.store.LocationInfo;

@RestController
@RequestMapping(path="/site",method= RequestMethod.GET,consumes = {MediaType.ALL_VALUE},produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class LocTagController {


	@Autowired
	private LocationService  locService;



	@RequestMapping(path="/**")
	public LocationInfo getRootLoc(WebRequest request){

		String url= StringUtils.substringAfter(request.getDescription(false),"=");

		return locService.getRootLoc();


	}



}
