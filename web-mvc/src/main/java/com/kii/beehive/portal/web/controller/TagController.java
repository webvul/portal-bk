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

import com.kii.beehive.portal.service.TagIndexDao;
import com.kii.beehive.portal.store.entity.TagIndex;
import com.kii.beehive.portal.web.help.PortalException;

@RestController
@RequestMapping(path = "/tags",  consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
public class TagController {
	
	@Autowired
	private TagIndexDao tagIndexDao;
	
	@RequestMapping(path="/all",method={RequestMethod.GET})
	public List<TagIndex> getAllTag(){
		return tagIndexDao.getAllTag();
	}
	
	@RequestMapping(path="/",method={RequestMethod.POST})
	public void createTag(@RequestBody TagIndex input){
		if(input == null){
			throw new PortalException();//no body
		}
		
		if(Strings.isBlank(input.getTagType())){
			throw new PortalException();//paramter missing
		}
		
		if(Strings.isBlank(input.getDisplayName())){
			throw new PortalException();//paramter missing
		}
		
		tagIndexDao.addTagIndex(input);
	}
	
	@RequestMapping(path="/{tagName}",method={RequestMethod.DELETE})
	public void removeThing(@PathVariable("tagName") String tagName){
		
		if(Strings.isBlank(tagName)){
			throw new PortalException();//paramter missing
		}
		
		TagIndex orig =  tagIndexDao.getTagIndexByID(tagName);
		
		if(orig == null){
			//not found object
			throw new PortalException();
		}
		
		tagIndexDao.removeTagByID(orig.getId());
	}

	@RequestMapping(path = "/tags/{tagName}", method = {RequestMethod.GET})
	public List<TagIndex> getThingsByTagArray(@PathVariable("tagName") String tagName) {
		if(Strings.isBlank(tagName)){
			throw new PortalException();//paramter missing
		}
		
		List<TagIndex> list = tagIndexDao.findTagIndexByTagNameArray(tagName.split(","));
		return list;

	}

}
