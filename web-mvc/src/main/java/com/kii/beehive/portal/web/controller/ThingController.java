package com.kii.beehive.portal.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.business.manager.TagThingManager;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.TeamThingRelationDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.portal.jdbc.entity.TeamThingRelation;
import com.kii.beehive.portal.web.entity.ThingRestBean;
import com.kii.beehive.portal.web.exception.PortalException;

/**
 * Beehive API - Thing API
 *
 * refer to doc "Beehive API - Tech Design" section "Thing API" for details
 *
 */
@RestController
@RequestMapping(path="/things",consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class ThingController extends AbstractController{

	@Autowired
	private TagThingManager thingTagManager;
//
	@Autowired
	private GlobalThingSpringDao globalThingDao;
	
	@Autowired
	private TeamThingRelationDao teamThingRelationDao;

	@Autowired
	private ThingIFInAppService  thingIFService;

	
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
	public ThingRestBean getThingByGlobalID(@PathVariable("globalThingID") Long globalThingID) {

		// get thing
		GlobalThingInfo thing =  globalThingDao.findByID(globalThingID);
		if(thing == null) {
			throw new PortalException("Thing Not Found", "Thing with globalThingID:" + globalThingID + " Not Found", HttpStatus.NOT_FOUND);
		}else if(this.isTeamIDExist()){
			TeamThingRelation ttr = teamThingRelationDao.findByTeamIDAndThingID(this.getLoginTeamID(), thing.getId());
			if(ttr == null){
				throw new PortalException("Thing Not Found", "Thing with globalThingID:" + globalThingID + " Not Found", HttpStatus.NOT_FOUND);
			}
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
	 * POST /things
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
		
		if(isTeamIDExist()){
    		teamThingRelationDao.saveOrUpdate(new TeamThingRelation(getLoginTeamID(), thingID));
    	}

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
	 * POST /things/{globalThingIDs}/tags/{tagID...}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingIDs
     */
	@RequestMapping(path="/{globalThingIDs}/tags/{tagIDs}",method={RequestMethod.POST})
	public void addThingTag(@PathVariable("globalThingIDs") String globalThingIDs,@PathVariable("tagIDs") String tagIDs){
		
		List<String> thingIDList = Arrays.asList(globalThingIDs.split(","));
		List<String> tagIDList = Arrays.asList(tagIDs.split(","));
		thingTagManager.bindTagToThing(tagIDList, thingIDList);

		List<Long> tagIDsInLong=tagIDList.stream().mapToLong(id->Long.parseLong(id)).boxed().collect(Collectors.toList());

		thingIFService.onTagIDsChangeFire(tagIDsInLong,true);
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
		List<String> thingIDList = Arrays.asList(globalThingIDs.split(","));
		List<String> tagIDList = Arrays.asList(tagIDs.split(","));
		thingTagManager.unbindTagToThing(tagIDList, thingIDList);

		List<Long> tagIDsInLong=tagIDList.stream().mapToLong(id->Long.parseLong(id)).boxed().collect(Collectors.toList());

		thingIFService.onTagIDsChangeFire(tagIDsInLong,false);
	}

	/**
	 * 绑定设备及custom tag
	 * POST /things/{globalThingID ...}/tags/custom/{tagName ...}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingIDs
	 * @param displayNames
	 */
	@RequestMapping(path="/{globalThingIDs}/tags/custom/{displayNames}",method={RequestMethod.POST})
	public void addThingCustomTag(@PathVariable("globalThingIDs") String globalThingIDs,@PathVariable("displayNames") String displayNames){

		List<String> list = Arrays.asList(globalThingIDs.split(","));
		List<Long> globalThingIDList = new ArrayList<>();
		for(String id : list) {
			globalThingIDList.add(Long.valueOf(id));
		}

		List<String> displayNameList = Arrays.asList(displayNames.split(","));
		thingTagManager.bindCustomTagToThing(displayNameList, globalThingIDList);

		displayNameList.forEach(name->{
			String fullName=TagType.Custom.getTagName(name);
			thingIFService.onTagChangeFire(fullName,true);
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

		List<String> list = Arrays.asList(globalThingIDs.split(","));
		List<Long> globalThingIDList = new ArrayList<>();
		for(String id : list) {
			globalThingIDList.add(Long.valueOf(id));
		}

		List<String> displayNameList = Arrays.asList(displayNames.split(","));
		thingTagManager.unbindCustomTagToThing(displayNameList, globalThingIDList);

		displayNameList.forEach(name->{
			String fullName=TagType.Custom.getTagName(name);
			thingIFService.onTagChangeFire(fullName,false);
		});
	}
	
	/**
	 * 绑定设备及team
	 * POST /things/{globalThingIDs}/teams/{teamID}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingIDs
     */
	@RequestMapping(path="/{globalThingIDs}/teams/{teamIDs}",method={RequestMethod.POST})
	public void addThingTeam(@PathVariable("globalThingIDs") String globalThingIDs,@PathVariable("teamIDs") String teamIDs){
		
		List<String> thingIDList = Arrays.asList(globalThingIDs.split(","));
		List<String> teamIDList = Arrays.asList(teamIDs.split(","));
		thingTagManager.bindTeamToThing(teamIDList, thingIDList);
	}
	
	/**
	 * 解除绑定设备及team
	 * DELETE /things/{globalThingIDs}/teams/{teamIDs...}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingIDs
     */
	@RequestMapping(path="/{globalThingIDs}/teams/{teamIDs}",method={RequestMethod.DELETE},consumes={"*"})
	public void removeThingTeam(@PathVariable("globalThingIDs") String globalThingIDs,@PathVariable("teamIDs") String teamIDs){
		List<String> thingIDList = Arrays.asList(globalThingIDs.split(","));
		List<String> teamIDList = Arrays.asList(teamIDs.split(","));
		thingTagManager.unbindTeamToThing(teamIDList, thingIDList);
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