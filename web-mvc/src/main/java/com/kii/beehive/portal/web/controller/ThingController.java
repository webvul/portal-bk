package com.kii.beehive.portal.web.controller;

import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.manager.ThingManager;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;

@RestController
@RequestMapping(path="/things",consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
public class ThingController {


	@Autowired
	private ThingManager thingManger;
	
	@RequestMapping(path="/",method={RequestMethod.GET})
	public List<GlobalThingInfo> getThing(){
		return thingManger.findGlobalThing();
	}

	@RequestMapping(path="/",method={RequestMethod.POST})
	public void createThing(@RequestBody GlobalThingInfo input){
		if(Strings.isEmpty(input.getVendorThingID())){
			//paramter missing
		}
		
		if(Strings.isEmpty(input.getGlobalThingID())){
			//paramter missing
		}
		thingManger.createThing(input);
	}


	@RequestMapping(path="/{thingID}/tags/{tagName}",method={RequestMethod.PUT})
	public void addThingTag(@PathVariable("thingID") String thingID,@PathVariable("tagName") String tagName){

		thingManger.bindTagToThing(tagName,thingID);

	}

	@RequestMapping(path="/{thingID}/tag/{tagName}/",method={RequestMethod.DELETE})
	public void removeThingTag(@PathVariable("thingID") String thingID,@PathVariable("tagName") String tagName){


	}
}