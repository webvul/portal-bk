package com.kii.beehive.portal.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.manager.GatewayCallinManager;
import com.kii.beehive.portal.web.entity.AddLocInput;
import com.kii.beehive.portal.web.entity.ThingRestBean;


@RestController
@RequestMapping(value = "/gatewayServer", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {
		MediaType.APPLICATION_JSON_UTF8_VALUE})
public class GatewayServerController {



	@Autowired
	private GatewayCallinManager manager;


	@RequestMapping(value = "/initNodeLocation", method = {RequestMethod.POST})
	public Map<String, Long> createEndNodeThing(@RequestBody AddLocInput  input ) {


		input.verify();


		List<String> locList=input.getLocList();
		if(locList.contains(input.getDefaultLoc())){
			locList.add(input.getDefaultLoc());
		}

		Long thingID=manager.createEndNode(input.getVendorThingID(),locList,input.getGatewayVendorThingID());

		Map<String, Long> map = new HashMap<>();
		map.put("globalThingID", thingID);
		return map;
	}


	@RequestMapping(value="/fillEndNode",method = RequestMethod.POST)
	public void updateLocListToVendorThing(@RequestBody ThingRestBean input){

		input.verifyInputForEndnode();

		manager.updateThing(input.getThingInfo());

	}



}
