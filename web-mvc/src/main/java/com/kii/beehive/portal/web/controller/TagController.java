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

/**
 * Beehive API - Thing API
 *
 * refer to doc "Tech Design - Beehive API" section "Thing API" for details
 */
@RestController
@RequestMapping(path = "/tags",  consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
public class TagController {
	
	@Autowired
	private TagIndexDao tagIndexDao;

	/**
	 * Beehive API - Thing API
	 * 列出所有tag
	 * GET /tags/all
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @return
     */
	@RequestMapping(path="/all",method={RequestMethod.GET})
	public List<TagIndex> getAllTag(){
		return tagIndexDao.getAllTag();
	}

	/**
	 * Beehive API - Thing API
	 * 创建tag
	 * POST /tags
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 * refer to doc "Tech Design - Beehive API", section "Create/Update Tag (创建/更新tag)" for more details
	 *
	 * @param input
     */
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

	/**
	 * Beehive API - Thing API
	 * 移除tag
	 * DELETE /tags/{tagName}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 * refer to doc "Tech Design - Beehive API", section "Delete Tag (移除tag)" for more details
	 *
	 * @param tagName
     */
	@RequestMapping(path="/{tagName}",method={RequestMethod.DELETE})
	public void removeTag(@PathVariable("tagName") String tagName){
		
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

	/**
	 * Beehive API - Thing API
	 * 查询tag
	 * GET /tags/tag/{tagName ...}/{operation}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 * refer to doc "Tech Design - Beehive API", section "Inquire Tag (查询tag)" for more details
	 *
	 * @param tagName
	 * @return
     */
	@RequestMapping(path = "/tag/{tagName}/{operation}", method = {RequestMethod.GET})
	public List<TagIndex> getTag(@PathVariable("tagName") String tagName, @PathVariable("operation") String operation) {
		// TODO
		return null;
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
