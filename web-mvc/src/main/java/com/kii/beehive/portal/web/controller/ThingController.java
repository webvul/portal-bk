package com.kii.beehive.portal.web.controller;

import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.exception.InvalidAuthException;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.TeamThingRelationDao;
import com.kii.beehive.portal.jdbc.entity.*;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.entity.ThingRestBean;
import com.kii.beehive.portal.web.exception.BeehiveUnAuthorizedException;
import com.kii.beehive.portal.web.exception.PortalException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * Beehive API - Thing API
 * <p>
 * refer to doc "Beehive API - Tech Design" section "Thing API" for details
 */
@RestController
@RequestMapping(path = "/things", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class ThingController extends AbstractThingTagController {

	//
	@Autowired
	private GlobalThingSpringDao globalThingDao;

	@Autowired
	private TagIndexDao tagIndexDao;

	@Autowired
	private TeamThingRelationDao teamThingRelationDao;

	@Autowired
	private ThingIFInAppService thingIFService;


	/**
	 * type下的所有设备
	 * GET /things/types/{type}
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @return
	 */

	@RequestMapping(path = "/types/{type}", method = {RequestMethod.GET})
	public ResponseEntity<List<ThingRestBean>> getThingsByType(@PathVariable("type") String type) {
		List<GlobalThingInfo> list = globalThingDao.getThingByType(type);
		List<ThingRestBean> resultList = new ArrayList<>();
		list.stream().filter(thingInfo -> thingTagManager.isThingCreator(thingInfo) || thingTagManager.isThingOwner(thingInfo)).forEach(thingInfo -> {
			ThingRestBean input = new ThingRestBean();
			BeanUtils.copyProperties(thingInfo, input);
			resultList.add(input);
		});

		return new ResponseEntity<>(resultList, HttpStatus.OK);
	}

	/**
	 * 所有设备的type
	 * GET /things/types
	 * <p>
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
	 * tagID查thingType
	 * GET /things/types/tagID/{tagIDs}
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 */
	@RequestMapping(path = "/types/tagID/{tagIDs}", method = {RequestMethod.GET})
	public ResponseEntity<List<String>> getThingTypeByTagIDs(@PathVariable("tagIDs") String tagIDs) {
		List<String> tagIDList = asList(tagIDs.split(","));

		List<String> result = thingTagManager.findThingTypeByTagIDs(tagIDList);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * 查询指定tag下所有的设备类型
	 * GET /things/types/fulltagname/{fullTagNames}
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @return
	 */
	@RequestMapping(path = "/types/fulltagname/{fullTagNames}", method = {RequestMethod.GET})
	public ResponseEntity<List<String>> getThingTypeByTagFullName(@PathVariable("fullTagNames") String fullTagNames) {
		List<String> fullTagNameList = asList(fullTagNames.split(","));

		for (String name : fullTagNameList) {
			List<TagIndex> tagList = tagIndexDao.findTagByFullTagName(name);
			if (tagList.size() > 0 && !thingTagManager.isTagCreator(tagList.get(0)) && !thingTagManager.isTagOwner(tagList.get(0))) {
				throw new BeehiveUnAuthorizedException("Current user is not the creator or owner of the tag.");
			} else {
				throw new PortalException("Tag Not Found", "Tag with fullTagName:" + name + " Not Found", HttpStatus.NOT_FOUND);
			}
		}

		List<String> result = globalThingDao.findThingTypeByFullTagNames(fullTagNameList);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * 查询设备（globalThingID）
	 * GET /things/{globalThingID}
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingID
	 * @return
	 */
	@RequestMapping(path = "/{globalThingID}", method = {RequestMethod.GET})
	public ThingRestBean getThingByGlobalID(@PathVariable("globalThingID") Long globalThingID) {

		// get thing
		GlobalThingInfo thing = globalThingDao.findByID(globalThingID);
		if (thing == null) {
			throw new PortalException("Thing Not Found", "Thing with globalThingID:" + globalThingID + " Not Found", HttpStatus.NOT_FOUND);
		} else if (this.isTeamIDExist()) {
			TeamThingRelation ttr = teamThingRelationDao.findByTeamIDAndThingID(this.getLoginTeamID(), thing.getId());
			if (ttr == null) {
				throw new PortalException("Thing Not Found", "Thing with globalThingID:" + globalThingID + " Not Found", HttpStatus.NOT_FOUND);
			}
		} else if (!thingTagManager.isThingCreator(thing) && thingTagManager.isThingOwner(thing)) {
			throw new BeehiveUnAuthorizedException("not creator or owner");
		}

		// get tag
		List<TagIndex> tagIndexList = thingTagManager.findTagIndexByGlobalThingID(globalThingID);

		// set thing into output
		ThingRestBean thingRestBean = new ThingRestBean();
		BeanUtils.copyProperties(thing, thingRestBean);

		// set location and custom tags into output
		String location = null;
		Set<String> customDisplayNameList = new HashSet<>();
		for (TagIndex tag : tagIndexList) {
			TagType tagType = tag.getTagType();
			if (tagType == TagType.Location) {
				location = tag.getDisplayName();
			} else if (tagType == TagType.Custom) {
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
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param input
	 */
	@RequestMapping(path = "", method = {RequestMethod.POST})
	public Map<String, Long> createThing(@RequestBody ThingRestBean input) {

		input.verifyInput();

		GlobalThingInfo thingInfo = new GlobalThingInfo();

		BeanUtils.copyProperties(input, thingInfo);

		Long thingID = thingTagManager.createThing(thingInfo, input.getLocation(), input.getInputTags());

		if (isTeamIDExist()) {
			teamThingRelationDao.saveOrUpdate(new TeamThingRelation(getLoginTeamID(), thingID));
		}

		Map<String, Long> map = new HashMap<>();
		map.put("globalThingID", thingID);
		return map;
	}


	/**
	 * 移除设备
	 * DELETE /things/{globalThingID}
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingID
	 */
	@RequestMapping(path = "/{globalThingID}", method = {RequestMethod.DELETE}, consumes = {"*"})
	public void removeThing(@PathVariable("globalThingID") Long globalThingID) {

		GlobalThingInfo orig = globalThingDao.findByID(globalThingID);
		if (orig == null) {
			throw new PortalException("Thing Not Found", "Thing with globalThingID:" + globalThingID + " Not Found", HttpStatus.NOT_FOUND);
		}

		if (thingTagManager.isThingCreator(orig)) {
			throw new InvalidAuthException(orig.getCreateBy(), getLoginUserID());
		}

		if (thingTagManager.isThingCreator(orig)) {
			thingTagManager.removeThing(orig);
		} else {
			throw new InvalidAuthException("not tag creator or thing creator");
		}


	}


	/**
	 * 绑定设备及tag
	 * POST /things/{globalThingIDs}/tags/{tagID...}
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingIDs
	 */
	@RequestMapping(path = "/{globalThingIDs}/tags/{tagIDs}", method = {RequestMethod.POST})
	public void addThingTag(@PathVariable("globalThingIDs") String globalThingIDs, @PathVariable("tagIDs") String
			tagIDs) {
		if (Strings.isBlank(globalThingIDs) || Strings.isBlank(tagIDs)) {
			throw new PortalException("RequiredFieldsMissing", "globalThingIDs or tagIDs is empty", HttpStatus
					.BAD_REQUEST);
		}
		List<GlobalThingInfo> things = getThings(globalThingIDs);
		List<TagIndex> tags = getTags(tagIDs);
		try {
			thingTagManager.bindTagToThing(tags, things);
		} catch (UnauthorizedException e) {
			throw new BeehiveUnAuthorizedException(e.getMessage());
		}
		thingIFService.onTagIDsChangeFire(tags.stream().map(TagIndex::getId).collect(Collectors.toList()), true);
	}


	/**
	 * 解除绑定设备及tag
	 * DELETE /things/{globalThingIDs}/tags/{tagID...}
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingIDs
	 */
	@RequestMapping(path = "/{globalThingIDs}/tags/{tagIDs}", method = {RequestMethod.DELETE}, consumes = {"*"})
	public void removeThingTag(@PathVariable("globalThingIDs") String globalThingIDs, @PathVariable("tagIDs") String
			tagIDs) {
		if (Strings.isBlank(globalThingIDs) || Strings.isBlank(tagIDs)) {
			throw new PortalException("RequiredFieldsMissing", "globalThingIDs or tagIDs is empty", HttpStatus
					.BAD_REQUEST);
		}
		List<GlobalThingInfo> things = getThings(globalThingIDs);
		List<TagIndex> tags = getTags(tagIDs);
		try {
			thingTagManager.unbindTagFromThing(tags, things);
		} catch (UnauthorizedException e) {
			throw new BeehiveUnAuthorizedException(e.getMessage());
		}
		thingIFService.onTagIDsChangeFire(tags.stream().map(TagIndex::getId).collect(Collectors.toList()), false);
	}

	/**
	 * Bind things(devices) to user groups
	 * POST /{globalThingIDs}/userGroups/{userGroupIDs}
	 *
	 * @param globalThingIDs
	 * @param userGroupIDs
	 */
	@RequestMapping(path = "/{globalThingIDs}/userGroups/{userGroupIDs}", method = {RequestMethod.POST})
	public void bindThingsToUserGroups(@PathVariable("globalThingIDs") String globalThingIDs, @PathVariable
			("userGroupIDs")
			String userGroupIDs) {
		if (Strings.isBlank(globalThingIDs) || Strings.isBlank(userGroupIDs)) {
			throw new PortalException("RequiredFieldsMissing", "globalThingIDs or userGroupIDs is empty", HttpStatus
					.BAD_REQUEST);
		}
		List<GlobalThingInfo> things = getThings(globalThingIDs);
		List<UserGroup> userGroups = getUserGroups(userGroupIDs);
		try {
			thingTagManager.bindThingToUserGroup(things, userGroups);
		} catch (UnauthorizedException e) {
			throw new BeehiveUnAuthorizedException(e.getMessage());
		}
	}

	/**
	 * Bind things(devices) to user groups
	 * POST /{globalThingIDs}/userGroups/{userGroupIDs}
	 *
	 * @param globalThingIDs
	 * @param userGroupIDs
	 */
	@RequestMapping(path = "/{globalThingIDs}/userGroups/{userGroupIDs}", method = {RequestMethod.DELETE}, consumes =
			{"*"})
	public void unbindThingsToUserGroups(@PathVariable("globalThingIDs") String globalThingIDs, @PathVariable
			("userGroupIDs")
			String userGroupIDs) {
		if (Strings.isBlank(globalThingIDs) || Strings.isBlank(userGroupIDs)) {
			throw new PortalException("RequiredFieldsMissing", "globalThingIDs or userGroupIDs is empty", HttpStatus
					.BAD_REQUEST);
		}
		List<GlobalThingInfo> things = getThings(globalThingIDs);
		List<UserGroup> userGroups = getUserGroups(userGroupIDs);
		try {
			thingTagManager.unbindThingFromUserGroup(things, userGroups);
		} catch (UnauthorizedException e) {
			throw new BeehiveUnAuthorizedException(e.getMessage());
		}
	}

	/**
	 * Bind things(devices) to users
	 * POST /{globalThingIDs}/users/{userIDs}
	 *
	 * @param globalThingIDs thing id list, separated by single comma character
	 * @param userIDs        user id list, separated by single comma character
	 */
	@RequestMapping(path = "/{globalThingIDs}/users/{userIDs}", method = {RequestMethod.POST})
	public void bindThingsToUsers(@PathVariable("globalThingIDs") String globalThingIDs, @PathVariable("userIDs")
			String userIDs) {
		if (Strings.isBlank(globalThingIDs) || Strings.isBlank(userIDs)) {
			throw new PortalException("RequiredFieldsMissing", "globalThingIDs or userIDs is empty", HttpStatus
					.BAD_REQUEST);
		}
		List<GlobalThingInfo> things = getThings(globalThingIDs);
		List<BeehiveUser> users = getUsers(userIDs);
		try {
			thingTagManager.bindThingToUser(things, users);
		} catch (UnauthorizedException e) {
			throw new BeehiveUnAuthorizedException(e.getMessage());
		}
	}

	/**
	 * Bind things(devices) to users
	 * DELETE /{globalThingIDs}/users/{userIDs}
	 *
	 * @param globalThingIDs thing id list, separated by single comma character
	 * @param userIDs        user id list, separated by single comma character
	 */
	@RequestMapping(path = "/{globalThingIDs}/users/{userIDs}", method = {RequestMethod.DELETE}, consumes = {"*"})
	public void unbindThingsFromUsers(@PathVariable("globalThingIDs") String globalThingIDs, @PathVariable("userIDs")
			String userIDs) {
		if (Strings.isBlank(globalThingIDs) || Strings.isBlank(userIDs)) {
			throw new PortalException("RequiredFieldsMissing", "globalThingIDs or userIDs is empty", HttpStatus
					.BAD_REQUEST);
		}
		List<GlobalThingInfo> things = getThings(globalThingIDs);
		List<BeehiveUser> users = getUsers(userIDs);
		try {
			thingTagManager.unbindThingFromUser(things, users);
		} catch (UnauthorizedException e) {
			throw new BeehiveUnAuthorizedException(e.getMessage());
		}
	}

	/**
	 * 绑定设备及custom tag
	 * POST /things/{globalThingID ...}/tags/custom/{tagName ...}
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingIDs
	 * @param displayNames
	 */
	@RequestMapping(path = "/{globalThingIDs}/tags/custom/{displayNames}", method = {RequestMethod.POST})
	public void addThingCustomTag(@PathVariable("globalThingIDs") String globalThingIDs, @PathVariable("displayNames") String displayNames) {

		List<String> list = asList(globalThingIDs.split(","));
		List<Long> globalThingIDList = new ArrayList<>();
		for (String id : list) {
			globalThingIDList.add(Long.valueOf(id));
		}

		List<String> displayNameList = asList(displayNames.split(","));
		thingTagManager.bindCustomTagToThing(displayNameList, globalThingIDList);

		displayNameList.forEach(name -> {
			String fullName = TagType.Custom.getTagName(name);
			thingIFService.onTagChangeFire(fullName, true);
		});
	}

	/**
	 * 解除绑定设备及custom tag
	 * DELETE /things/{globalThingID ...}/tags/custom/{tagName ...}
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingIDs
	 * @param displayNames
	 */
	@RequestMapping(path = "/{globalThingIDs}/tags/custom/{displayNames}", method = {RequestMethod.DELETE}, consumes = {"*"})
	public void removeThingCustomTag(@PathVariable("globalThingIDs") String globalThingIDs, @PathVariable("displayNames") String displayNames) {

		List<String> list = asList(globalThingIDs.split(","));
		List<Long> globalThingIDList = new ArrayList<>();
		for (String id : list) {
			globalThingIDList.add(Long.valueOf(id));
		}

		List<String> displayNameList = asList(displayNames.split(","));
		thingTagManager.unbindCustomTagToThing(displayNameList, globalThingIDList);

		displayNameList.forEach(name -> {
			String fullName = TagType.Custom.getTagName(name);
			thingIFService.onTagChangeFire(fullName, false);
		});
	}

	/**
	 * 绑定设备及team
	 * POST /things/{globalThingIDs}/teams/{teamID}
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingIDs
	 */
	@RequestMapping(path = "/{globalThingIDs}/teams/{teamIDs}", method = {RequestMethod.POST})
	public void addThingTeam(@PathVariable("globalThingIDs") String globalThingIDs, @PathVariable("teamIDs") String teamIDs) {

		List<String> thingIDList = asList(globalThingIDs.split(","));
		List<String> teamIDList = asList(teamIDs.split(","));
		thingTagManager.bindTeamToThing(teamIDList, thingIDList);
	}

	/**
	 * 解除绑定设备及team
	 * DELETE /things/{globalThingIDs}/teams/{teamIDs...}
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingIDs
	 */
	@RequestMapping(path = "/{globalThingIDs}/teams/{teamIDs}", method = {RequestMethod.DELETE}, consumes = {"*"})
	public void removeThingTeam(@PathVariable("globalThingIDs") String globalThingIDs, @PathVariable("teamIDs") String teamIDs) {
		List<String> thingIDList = asList(globalThingIDs.split(","));
		List<String> teamIDList = asList(teamIDs.split(","));
		thingTagManager.unbindTeamToThing(teamIDList, thingIDList);
	}

	/**
	 * 查询tag下的设备
	 * GET /things/search?tagType={tagType}&displayName={displayName}
	 * tags和location信息不会被返回
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @return
	 */
	@RequestMapping(path = "/search", method = {RequestMethod.GET})
	public ResponseEntity<List<ThingRestBean>> getThingsByTagExpress(@RequestParam(value = "tagType") String tagType,
																	 @RequestParam(value = "displayName") String displayName) {
		List<GlobalThingInfo> list = null;

		List<TagIndex> tagList = tagIndexDao.findTagByTagTypeAndName(StringUtils.capitalize(tagType), displayName);

		if (tagList.size() > 0) {
			TagIndex tag = tagList.get(0);
			if (thingTagManager.isTagCreator(tag) || thingTagManager.isTagOwner(tag)) {
				list = globalThingDao.findThingByTag(StringUtils.capitalize(tagType) + "-" + displayName);
			} else {
				throw new BeehiveUnAuthorizedException("loginUser isn't a tag creator or owner");
			}
		}

		List<ThingRestBean> resultList = new ArrayList<>();
		if (list != null) {
			for (GlobalThingInfo thingInfo : list) {
				ThingRestBean input = new ThingRestBean();
				BeanUtils.copyProperties(thingInfo, input);
				resultList.add(input);
			}
		}

		return new ResponseEntity<>(resultList, HttpStatus.OK);
	}
}