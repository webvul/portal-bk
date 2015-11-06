package com.kii.beehive.portal.web.controller;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.kii.beehive.portal.common.utils.LocationsGeneral;
import com.kii.beehive.portal.manager.ThingManager;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.repositories.ThingInfoRepository;
import com.kii.beehive.portal.web.entity.ThingInput;

//@RestController
@RequestMapping(path="/things",consumes = {"application/json"}, produces = {"application/json"})
public class ThingController {


	@Autowired
	private ThingManager thingMang;

	@RequestMapping(path="/",method={RequestMethod.POST})
	public Map<String,String> createThing(@RequestBody ThingInput input){


		return null;


	}


	@RequestMapping(path="/{thingID}/tags/{tagName}",method={RequestMethod.PUT})
	public void addThingTag(@PathVariable("thingID") String thingID,@PathVariable("tagName") String tagName){



		thingMang.bindTagToThing(tagName,thingID);

	}

	@RequestMapping(path="/{thingID}/tag/{tagName}/",method={RequestMethod.DELETE})
	public void removeThingTag(@PathVariable("thingID") String thingID,@PathVariable("tagName") String tagName){


	}
}