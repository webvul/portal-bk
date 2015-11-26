package com.kii.beehive.portal.web.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.manager.ThingManager;
import com.kii.beehive.portal.service.TagIndexDao;
import com.kii.beehive.portal.store.entity.TagIndex;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.help.PortalException;

/**
 * Beehive API - Thing API
 *
 * refer to doc "Tech Design - Beehive API" section "Thing API" for details
 */
@RestController
@RequestMapping(path = "/tags",  consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class TagController {
	
	@Autowired
	private TagIndexDao tagIndexDao;
	
	@Autowired
	private ThingManager thingManager;
	/**
	 * 列出所有tag
	 * GET /tags/all
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @return
     */
	@RequestMapping(path="/all",method={RequestMethod.GET})
	public ResponseEntity<List<TagIndex>> getAllTag(){
		List<TagIndex> list = tagIndexDao.getAllTag();
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	/**
	 * 创建tag
	 * POST /tags
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 * refer to doc "Tech Design - Beehive API", section "Create/Update Tag (创建/更新tag)" for more details
	 *
	 * @param input
     */
	@RequestMapping(path="",method={RequestMethod.POST})
	public Map<String,String> createTag(@RequestBody TagIndex input){
		if(input == null){
			throw new PortalException(ErrorCode.NO_BODY,"Body is null", HttpStatus.BAD_REQUEST);
		}
		
		if(Strings.isBlank(input.getTagType())){
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,"TagType is empty", HttpStatus.BAD_REQUEST);
		}
		
		if(Strings.isBlank(input.getDisplayName())){
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,"DisplayName is empty", HttpStatus.BAD_REQUEST);
		}
		
		tagIndexDao.addTagIndex(input);
		Map<String,String> map=new HashMap<>();
		map.put("tagName",input.getId());
		return map;
	}

	/**
	 * 移除tag
	 * DELETE /tags/{tagName}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 * refer to doc "Tech Design - Beehive API", section "Delete Tag (移除tag)" for more details
	 *
	 * @param tagName
     */
	@RequestMapping(path="/{tagName}",method={RequestMethod.DELETE})
	public ResponseEntity<String> removeTag(@PathVariable("tagName") String tagName){
		
		TagIndex orig =  tagIndexDao.getTagIndexByID(tagName);
		
		thingManager.removeTag(orig);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 查询tag
	 * GET /tags/tag/{tagName ...}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 * refer to doc "Tech Design - Beehive API", section "Inquire Tag (查询tag)" for more details
	 *
	 * @param tagName
	 * @return
     */

	@RequestMapping(path = "/tag/{tagName}", method = {RequestMethod.GET})
	public ResponseEntity<List<TagIndex>> getThingsByTagArray(@PathVariable("tagName") String tagName) {
		
		List<TagIndex> list = tagIndexDao.findTagIndexByTagNameArray(tagName.split(","));
		return new ResponseEntity<>(list, HttpStatus.OK);

	}

}
