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
import com.kii.beehive.portal.service.TagIndexDao;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.TagIndex;
import com.kii.beehive.portal.web.help.PortalException;

@RestController
@RequestMapping(path = "/tags",  consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
public class TagController {
	
	@Autowired
	private ThingManager thingManager;
	
	@Autowired
	private TagIndexDao tagIndexDao;
	
	@RequestMapping(path="/all",method={RequestMethod.GET})
	public List<TagIndex> getThing(){
		return tagIndexDao.getAllThing();
	}
	
	@RequestMapping(path="/",method={RequestMethod.POST})
	public void createTag(@RequestBody TagIndex input){
		if(input == null){
			throw new PortalException();//no body
		}
		
		if(Strings.isEmpty(input.getTagType())){
			throw new PortalException();//paramter missing
		}
		
		if(Strings.isEmpty(input.getDisplayName())){
			throw new PortalException();//paramter missing
		}
		
		tagIndexDao.addTagIndex(input);
	}
	
	@RequestMapping(path="/{tagID}",method={RequestMethod.DELETE})
	public void removeThing(@PathVariable("tagID") String tagID){
		
		if(Strings.isEmpty(tagID)){
			throw new PortalException();//paramter missing
		}
		
		TagIndex orig =  tagIndexDao.getTagIndexByID(tagID);
		
		if(orig == null){
			//not found object
			throw new PortalException();
		}
		
		tagIndexDao.removeTagByID(orig.getId());
	}


	@RequestMapping(path = "/tag/{tagName}", method = {RequestMethod.GET})
	public TagIndex getThingsByTag(@PathVariable("tagName") String tagName) {
		if(Strings.isEmpty(tagName)){
			throw new PortalException();//paramter missing
			
		}
		
		TagIndex tagIndex = tagIndexDao.getTagIndexByID(tagName);
		return tagIndex;

	}

	@RequestMapping(path = "/tags/{tagName}", method = {RequestMethod.GET})
	public List<TagIndex> getThingsByTagArray(@PathVariable("tagName") String tagName) {
		if(Strings.isEmpty(tagName)){
			throw new PortalException();//paramter missing
		}
		
		List<TagIndex> list = thingManager.findTagIndexByQuery(tagName.split(","));
		return list;

	}

	@RequestMapping(path = "/express/{tagExpress}", method = {RequestMethod.GET})
	public List<GlobalThingInfo> getThingByTagExpress(@PathVariable("tagName") String tagName){

		return null;

	}






}
