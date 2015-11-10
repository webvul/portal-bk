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
import com.kii.beehive.portal.store.entity.TagIndex;

@RestController
@RequestMapping(path = "/tags",  consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
public class TagController {
	
	@Autowired
	private ThingManager thingManager;

	@RequestMapping(path="/",method={RequestMethod.POST})
	public void createTag(@RequestBody TagIndex input){
		if(input == null){
			//no body
		}
		
		if(Strings.isEmpty(input.getTagType())){
			//paramter missing
		}
		
		if(Strings.isEmpty(input.getDisplayName())){
			//paramter missing
		}
		
		thingManager.createTag(input);
	}


	@RequestMapping(path = "/tag/{tagName}", method = {RequestMethod.GET})
	public TagIndex getThingsByTag(@PathVariable("tagName") String tagName) {
		if(Strings.isEmpty(tagName)){
			//paramter missing
		}
		
		TagIndex tagIndex = thingManager.findTagIndexByTagName(tagName);
		return tagIndex;

	}



	@RequestMapping(path = "/express/{tagExpress}", method = {RequestMethod.GET})
	public List<GlobalThingInfo> getThingByTagExpress(@PathVariable("tagName") String tagName){

		return null;

	}






}
