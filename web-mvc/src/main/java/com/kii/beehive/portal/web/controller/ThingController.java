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
import com.kii.beehive.portal.service.GlobalThingDao;
import com.kii.beehive.portal.service.TagIndexDao;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.TagIndex;
import com.kii.beehive.portal.web.help.PortalException;

@RestController
@RequestMapping(path="/things",consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
public class ThingController {


	@Autowired
	private ThingManager thingManager;
	
	@Autowired
	private TagIndexDao tagIndexDao;
	
	@Autowired
	private GlobalThingDao globalThingDao;
	
	@RequestMapping(path="/all",method={RequestMethod.GET})
	public List<GlobalThingInfo> getThing(){
		return globalThingDao.getAllThing();
	}

	@RequestMapping(path="/",method={RequestMethod.POST})
	public void createThing(@RequestBody GlobalThingInfo input){
		if(input == null){
			throw new PortalException();//no body
		}
		
		if(Strings.isEmpty(input.getVendorThingID())){
			throw new PortalException();//paramter missing
		}
		
		if(Strings.isEmpty(input.getGlobalThingID())){
			throw new PortalException();//paramter missing
		}
		
		
		thingManager.createThing(input);
	}
	
	@RequestMapping(path="/{thingID}",method={RequestMethod.DELETE})
	public void removeThing(@PathVariable("thingID") String thingID){
		
		if(Strings.isEmpty(thingID)){
			throw new PortalException();//paramter missing
		}
		
		GlobalThingInfo orig =  globalThingDao.getThingInfoByID(thingID);
		
		if(orig == null){
			throw new PortalException();//not found object
		}
		
		globalThingDao.removeGlobalThingByID(orig.getId());
	}
	


	@RequestMapping(path="/{thingID}/tags/{tagName}",method={RequestMethod.PUT})
	public void addThingTag(@PathVariable("thingID") String thingID,@PathVariable("tagName") String tagName){
		thingManager.bindTagToThing(tagName,thingID);
	}

	@RequestMapping(path="/{thingID}/tag/{tagName}/",method={RequestMethod.DELETE})
	public void removeThingTag(@PathVariable("thingID") String thingID,@PathVariable("tagName") String tagName){
		thingManager.unbindTagToThing(tagName,thingID);
	}
	
	@RequestMapping(path = "/tag/{tagName}", method = {RequestMethod.GET})
	public List<GlobalThingInfo> getThingsByTag(@PathVariable("tagName") String tagName) {
		if(Strings.isEmpty(tagName)){
			throw new PortalException();//paramter missing
		}
		
		TagIndex tagIndex = tagIndexDao.getTagIndexByID(tagName);
		
		if(tagIndex == null){
			throw new PortalException();// not found object
		}
		
		if(tagIndex.getGlobalThings().size() == 0){
			throw new PortalException();// no GlobalThings
		}
		List<GlobalThingInfo> list = globalThingDao.getThingsByIDs(tagIndex.getGlobalThings().toArray(new String[tagIndex.getGlobalThings().size()]));
		return list;

	}
}