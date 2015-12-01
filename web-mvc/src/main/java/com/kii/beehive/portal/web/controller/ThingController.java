package com.kii.beehive.portal.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.manager.ThingManager;
import com.kii.beehive.portal.service.GlobalThingDao;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.TagType;
import com.kii.beehive.portal.web.constant.ErrorCode;
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
	private ThingManager thingManager;
	
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
		
		GlobalThingInfo thing =  globalThingDao.getThingInfoByID(globalThingID);
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
	public Map<String,String> createThing(@RequestBody ThingInput input){

		input.verifyInput();

		
		GlobalThingInfo thingInfo = new GlobalThingInfo();

		BeanUtils.copyProperties(input,thingInfo);

		String thingID=thingManager.createThing(thingInfo,input.getInputTags());
		
		Map<String,String> map=new HashMap<>();
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
	@RequestMapping(path="/{globalThingID}",method={RequestMethod.DELETE})
	public void removeThing(@PathVariable("globalThingID") String globalThingID){
		
		GlobalThingInfo orig =  globalThingDao.getThingInfoByID(globalThingID);
		
		thingManager.removeThings(orig);
		return;
	}

	/**
	 * 绑定设备及tag
	 * PUT /things/{globalThingID...}/tags/{tagName ...}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingID
	 * @param tagName
     */
	@RequestMapping(path="/{globalThingID}/tags/custom/{tagName}",method={RequestMethod.PUT})
	public void addThingTag(@PathVariable("globalThingID") String globalThingID,@PathVariable("tagName") String tagName){
		
		String[] thingIDs = globalThingID.split(",");
		List<String> tagIDs = CollectionUtils.arrayToList(tagName.split(","));

		List<String> tags=tagIDs.stream().map((s)-> TagType.Custom.getTagName(s)).collect(Collectors.toList());

		thingManager.bindTagToThing(tags, Arrays.asList(thingIDs));
		return ;
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
	@RequestMapping(path="/{globalThingID}/tags/custom/{tagName}",method={RequestMethod.DELETE})
	public void removeThingTag(@PathVariable("globalThingID") String globalThingID,@PathVariable("tagName") String tagName){
		thingManager.unbindTagToThing(TagType.Custom.getTagName(tagName),globalThingID);
		return;
	}



}