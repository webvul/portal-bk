package com.kii.beehive.portal.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.jdbc.dao.GlobalThingDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.ThingTagService;
import com.kii.beehive.portal.web.entity.ThingInput;
import com.kii.beehive.portal.web.help.PortalException;

/**
 * Beehive API - Thing API
 *
 * refer to doc "Tech Design - Beehive API" section "Thing API" for details
 */
@RestController
@RequestMapping(path="/things",consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class ThingController {


	@Autowired
	private ThingTagService thingTagService;
	
	@Autowired
	private GlobalThingDao globalThingDao;
	
	/**
	 * 查询设备
	 * GET /things/{globalThingID}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingID
	 * @return
	 */
	@RequestMapping(path = "/{globalThingID}", method = {RequestMethod.GET})
	public GlobalThingInfo getThingByGlobalID(@PathVariable("globalThingID") String globalThingID) {
		
		GlobalThingInfo thing =  globalThingDao.findByID(globalThingID);
		return thing;
	}


	/**
	 * 创建/更新设备信息
	 * POST /things/
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param input
     */
	@RequestMapping(path="",method={RequestMethod.POST})
	public Map<String,Long> createThing(@RequestBody ThingInput input){

		input.verifyInput();
		
		GlobalThingInfo thingInfo = new GlobalThingInfo();

		BeanUtils.copyProperties(input,thingInfo);

		Long thingID = thingTagService.createThing(thingInfo,input.getInputTags());
		
		Map<String,Long> map=new HashMap<>();
		map.put("globalThingID",thingID);
		return map;
	}

	/**
	 * 移除设备
	 * DELETE /things/{globalThingID}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingID
     */
	@RequestMapping(path="/{globalThingID}",method={RequestMethod.DELETE},consumes={"*"})
	public void removeThing(@PathVariable("globalThingID") Long globalThingID){
		
		GlobalThingInfo orig =  globalThingDao.findByID(globalThingID);
		
		if(orig == null){
			throw new PortalException("no body", "no body", HttpStatus.BAD_REQUEST);
		}
		
		thingTagService.removeThing(orig);
	}

	/**1043  1014,1022
	 * 绑定设备及tag
	 * PUT /things/{globalThingID...}/tags/{tagName ...}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingID
	 * @param tagName
     */
	@RequestMapping(path="/{globalThingID}/tags/{tagIDs}",method={RequestMethod.PUT})
	public void addThingTag(@PathVariable("globalThingID") Long globalThingID,@PathVariable("tagIDs") String tagIDs){
		
		List<String> tagIDList = CollectionUtils.arrayToList(tagIDs.split(","));
		thingTagService.bindTagToThing(tagIDList, globalThingID);
	}

	/**
	 * 解除绑定设备及tag
	 * DELETE /things/{globalThingID}/tags/{tagName}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingID
	 * @param tagName
     */
	@RequestMapping(path="/{globalThingID}/tags/{tagIDs}",method={RequestMethod.DELETE},consumes={"*"})
	public void removeThingTag(@PathVariable("globalThingID") Long globalThingID,@PathVariable("tagIDs") String tagIDs){
		List<String> tagIDList = CollectionUtils.arrayToList(tagIDs.split(","));
		thingTagService.unbindTagToThing(tagIDList, globalThingID);
	}


	/**
	 * 查询tag下的设备
	 * GET /things/tag/{tagName...}/operation/{operation}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param tagName
	 * @param operation
     * @return
     */
	@RequestMapping(path = "/search", method = {RequestMethod.GET})
	public ResponseEntity<List<GlobalThingInfo>> getThingsByTagExpress(@RequestParam(value="tagType", required = false) String tagType, 
																		@RequestParam(value="displayName", required = false) String displayName) {
		List<GlobalThingInfo> list = null;
		
		if(Strings.isBlank(tagType) && Strings.isBlank(displayName)){
			list = globalThingDao.findAll();
		}else{
			list = globalThingDao.findThingByTag(StringUtils.capitalize(tagType), displayName);
		}
		
		return new ResponseEntity<>(list, HttpStatus.OK);
	}


}