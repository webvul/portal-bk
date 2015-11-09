package com.kii.beehive.portal.web.controller;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.manager.ThingManager;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.TagIndex;

@RestController
@RequestMapping(path = "/tags",  consumes = {"application/json"}, produces = {"application/json"})
public class TagController {
	
	@Autowired
	private ThingManager thingManger;

	@RequestMapping(path="/",method={RequestMethod.POST})
	public Map<String,String> createTag(@RequestBody TagIndex input){
		thingManger.createTag(input);
		return null;

	}


	@RequestMapping(path = "/tag/{tagName}", method = {RequestMethod.GET})
	public TagIndex getThingsByTag(@PathVariable("tagName") String tagName) {
		TagIndex tagIndex = thingManger.findTagIndexByTagName(tagName);
		return tagIndex;

	}



	@RequestMapping(path = "/express/{tagExpress}", method = {RequestMethod.GET})
	public List<GlobalThingInfo> getThingByTagExpress(@PathVariable("tagName") String tagName){

		return null;

	}






}
