package com.kii.beehive.portal.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.kii.beehive.business.event.KiiCloudEventBus;
import com.kii.beehive.portal.manager.TagThingManager;
import com.kii.beehive.portal.jdbc.dao.GlobalThingDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.portal.web.entity.ThingRestBean;
import com.kii.beehive.portal.web.help.PortalException;

/**
 * Beehive API - Thing API
 *
 * refer to doc "Beehive API - Tech Design" section "Thing API" for details
 *
 */
@RestController
@RequestMapping(path="/things",consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class ThingController {

	@Autowired
	private TagThingManager thingTagManager;
	
	@Autowired
	private GlobalThingDao globalThingDao;

	@Autowired
	private KiiCloudEventBus eventBus;


	
	
	/**
	 * @deprecated this is internal API for Kii only
	 *
	 * 查询所有设备
	 * GET /things/all
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
     * @return
     */
	@RequestMapping(path = "/all", method = {RequestMethod.GET})
	public ResponseEntity<List<ThingRestBean>> getThingsByAll() {
		List<GlobalThingInfo> list = globalThingDao.findAll();
		List<ThingRestBean> resultList = new ArrayList<>();
		if(list != null) {
			for (GlobalThingInfo thingInfo : list) {
				ThingRestBean input = new ThingRestBean();
				BeanUtils.copyProperties(thingInfo,input);
				resultList.add(input);
			}
		}

		return new ResponseEntity<>(resultList, HttpStatus.OK);
	}
	
	/**
	 * type下的所有设备
	 * GET /things/types/{type}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
     * @return
     */
	@RequestMapping(path = "/types/{type}", method = {RequestMethod.GET})
	public ResponseEntity<List<ThingRestBean>> getThingsByType(@PathVariable("type") String type) {
		List<GlobalThingInfo> list = globalThingDao.getThingByType(type);
		List<ThingRestBean> resultList = new ArrayList<>();
		if(list != null) {
			for (GlobalThingInfo thingInfo : list) {
				ThingRestBean input = new ThingRestBean();
				BeanUtils.copyProperties(thingInfo,input);
				resultList.add(input);
			}
		}

		return new ResponseEntity<>(resultList, HttpStatus.OK);
	}
	
	/**
	 * 所有设备的type
	 * GET /things/types
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
     * @return
     */
	@RequestMapping(path = "/types", method = {RequestMethod.GET})
	public ResponseEntity<List<Map<String, Object>>> getAllType() {
		List<Map<String, Object>> list = globalThingDao.findAllThingTypesWithThingCount();

		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	/**
	 * 查询设备（globalThingID）
	 * GET /things/{globalThingID}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingID
	 * @return
	 */
	@RequestMapping(path = "/{globalThingID}", method = {RequestMethod.GET})
	public ThingRestBean getThingByGlobalID(@PathVariable("globalThingID") String globalThingID) {

		// get thing
		GlobalThingInfo thing =  globalThingDao.findByID(globalThingID);
		if(thing == null) {
			throw new PortalException("Thing Not Found", "Thing with globalThingID:" + globalThingID + " Not Found", HttpStatus.NOT_FOUND);
		}

		// get tag
		List<TagIndex> tagIndexList = thingTagManager.findTagIndexByGlobalThingID(globalThingID);

		// set thing into output
		ThingRestBean thingRestBean = new ThingRestBean();
		BeanUtils.copyProperties(thing, thingRestBean);

		// set location and custom tags into output
		String location = null;
		Set<String> customDisplayNameList = new HashSet<>();
		for(TagIndex tag : tagIndexList) {
			TagType tagType = tag.getTagType();
			if(tagType == TagType.Location) {
				location = tag.getDisplayName();
			} else if(tagType == TagType.Custom){
				customDisplayNameList.add(tag.getDisplayName());
			}
		}
		thingRestBean.setLocation(location);
		thingRestBean.setInputTags(customDisplayNameList);

		return thingRestBean;
	}


	/**
	 * 创建设备信息
	 * POST /things/
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param input
     */
	@RequestMapping(path="",method={RequestMethod.POST})
	public Map<String,Long> createThing(@RequestBody ThingRestBean input){

		input.verifyInput();
		
		GlobalThingInfo thingInfo = new GlobalThingInfo();

		BeanUtils.copyProperties(input,thingInfo);

		Long thingID = thingTagManager.createThing(thingInfo, input.getLocation(), input.getInputTags());

//		input.getInputTags().

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
			throw new PortalException("Thing Not Found", "Thing with globalThingID:" + globalThingID + " Not Found", HttpStatus.NOT_FOUND);
		}
		
		thingTagManager.removeThing(orig);
	}

	/**
	 * 绑定设备及tag
	 * PUT /things/{globalThingIDs}/tags/{tagID...}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingIDs
     */
	@RequestMapping(path="/{globalThingIDs}/tags/{tagIDs}",method={RequestMethod.PUT})
	public void addThingTag(@PathVariable("globalThingIDs") String globalThingIDs,@PathVariable("tagIDs") String tagIDs){
		
		List<String> thingIDList = CollectionUtils.arrayToList(globalThingIDs.split(","));
		List<String> tagIDList = CollectionUtils.arrayToList(tagIDs.split(","));
		thingTagManager.bindTagToThing(tagIDList, thingIDList);

		eventBus.onTagIDsChangeFire(tagIDList,true);
	}

	/**
	 * 解除绑定设备及tag
	 * DELETE /things/{globalThingIDs}/tags/{tagID...}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingIDs
     */
	@RequestMapping(path="/{globalThingIDs}/tags/{tagIDs}",method={RequestMethod.DELETE},consumes={"*"})
	public void removeThingTag(@PathVariable("globalThingIDs") String globalThingIDs,@PathVariable("tagIDs") String tagIDs){
		List<String> thingIDList = CollectionUtils.arrayToList(globalThingIDs.split(","));
		List<String> tagIDList = CollectionUtils.arrayToList(tagIDs.split(","));
		thingTagManager.unbindTagToThing(tagIDList, thingIDList);

		eventBus.onTagIDsChangeFire(tagIDList,false);

	}

	/**
	 * 绑定设备及custom tag
	 * PUT /things/{globalThingID ...}/tags/custom/{tagName ...}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingIDs
	 * @param displayNames
	 */
	@RequestMapping(path="/{globalThingIDs}/tags/custom/{displayNames}",method={RequestMethod.PUT})
	public void addThingCustomTag(@PathVariable("globalThingIDs") String globalThingIDs,@PathVariable("displayNames") String displayNames){

		List<String> list = CollectionUtils.arrayToList(globalThingIDs.split(","));
		List<Long> globalThingIDList = new ArrayList<>();
		for(String id : list) {
			globalThingIDList.add(Long.valueOf(id));
		}

		List<String> displayNameList = CollectionUtils.arrayToList(displayNames.split(","));
		thingTagManager.bindCustomTagToThing(displayNameList, globalThingIDList);

		displayNameList.forEach(name->{
			String fullName=TagType.Custom.getTagName(name);
			eventBus.onTagChangeFire(fullName,true);
		});
	}

	/**
	 * 解除绑定设备及custom tag
	 * DELETE /things/{globalThingID ...}/tags/custom/{tagName ...}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingIDs
	 * @param displayNames
	 */
	@RequestMapping(path="/{globalThingIDs}/tags/custom/{displayNames}",method={RequestMethod.DELETE},consumes={"*"})
	public void removeThingCustomTag(@PathVariable("globalThingIDs") String globalThingIDs,@PathVariable("displayNames") String displayNames){

		List<String> list = CollectionUtils.arrayToList(globalThingIDs.split(","));
		List<Long> globalThingIDList = new ArrayList<>();
		for(String id : list) {
			globalThingIDList.add(Long.valueOf(id));
		}

		List<String> displayNameList = CollectionUtils.arrayToList(displayNames.split(","));
		thingTagManager.unbindCustomTagToThing(displayNameList, globalThingIDList);

		displayNameList.forEach(name->{
			String fullName=TagType.Custom.getTagName(name);
			eventBus.onTagChangeFire(fullName,false);
		});
	}

	/**
	 * 查询tag下的设备
	 * GET /things/search?tagType={tagType}&displayName={displayName}
	 * tags和location信息不会被返回
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
     * @return
     */
	@RequestMapping(path = "/search", method = {RequestMethod.GET})
	public ResponseEntity<List<ThingRestBean>> getThingsByTagExpress(@RequestParam(value="tagType", required = false) String tagType,
																		@RequestParam(value="displayName", required = false) String displayName) {
		List<GlobalThingInfo> list = null;

		if(Strings.isBlank(tagType) && Strings.isBlank(displayName)){
			list = globalThingDao.findAll();
		}else{
			list = globalThingDao.findThingByTag(StringUtils.capitalize(tagType)+"-"+displayName);
		}

		List<ThingRestBean> resultList = new ArrayList<>();
		if(list != null) {
			for (GlobalThingInfo thingInfo : list) {
				ThingRestBean input = new ThingRestBean();
				BeanUtils.copyProperties(thingInfo,input);
				resultList.add(input);
			}
		}

		return new ResponseEntity<>(resultList, HttpStatus.OK);
	}


}