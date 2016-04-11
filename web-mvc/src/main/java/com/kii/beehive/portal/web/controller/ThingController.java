package com.kii.beehive.portal.web.controller;

import static java.util.Arrays.asList;

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
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.exception.ObjectNotFoundException;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.portal.jdbc.entity.UserGroup;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.entity.ThingRestBean;
import com.kii.beehive.portal.web.exception.BeehiveUnAuthorizedException;
import com.kii.beehive.portal.web.exception.PortalException;
import com.kii.extension.sdk.entity.thingif.EndNodeOfGateway;

/**
 * Beehive API - Thing API
 * <p>
 * refer to doc "Beehive API - Tech Design" section "Thing API" for details
 */
@RestController
@RequestMapping(value = "/things", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE},
		produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class ThingController extends AbstractThingTagController {
	@Autowired
	private ThingIFInAppService thingIFService;

	/**
	 * GET /things/{globalThingID}/users
	 *
	 * @param globalThingID
	 * @return a list of user ids who can access the device.
	 */
	@RequestMapping(value = "/{globalThingID}/users", method = RequestMethod.GET)
	public List<BeehiveUser> getUsersByThing(@PathVariable("globalThingID") Long globalThingID) {
		try {
			return thingTagManager.getUsersOfAccessibleThing(AuthInfoStore.getUserID(), globalThingID);
		} catch (ObjectNotFoundException e) {
			throw new PortalException(e.getMessage(), e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * GET /things/user/{userID}
	 *
	 * @param userId
	 * @return a list of devices which the user can access
	 */
	@RequestMapping(value = "/user/{userID}", method = RequestMethod.GET)
	public List<ThingRestBean> getThingsByUser(@PathVariable("userID") String userId) {
		return toThingRestBean(thingTagManager.getAccessibleThingsByUserId(userId));
	}

	/**
	 * GET /things/{globalThingID}/userGroups
	 *
	 * @param globalThingID
	 * @return a list of user group id which can access the device.
	 */
	@RequestMapping(value = "/{globalThingID}/userGroups", method = RequestMethod.GET)
	public List<UserGroup> getUserGroupIdsByThing(@PathVariable("globalThingID") Long globalThingID) {
		try {
			return thingTagManager.getUserGroupsOfAccessibleThing(AuthInfoStore.getUserID(), globalThingID);
		} catch (ObjectNotFoundException e) {
			throw new PortalException(e.getMessage(), e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * GET /things/user/{userID}
	 *
	 * @param userGroupId
	 * @return a list of devices which the user can access
	 */
	@RequestMapping(value = "/userGroup/{userGroupID}", method = RequestMethod.GET)
	public List<ThingRestBean> getThingsByUserGroup(@PathVariable("userGroupID") Long userGroupId) {
		return toThingRestBean(thingTagManager.getAccessibleThingsByUserGroupId(userGroupId));
	}


	/**
	 * type下的所有设备
	 * GET /things/types/{type}
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @return
	 */

	@RequestMapping(value = "/types/{type}", method = {RequestMethod.GET})
	public List<ThingRestBean> getThingsByType(@PathVariable("type") String type) {
		List<GlobalThingInfo> thingList = thingTagManager.getAccessibleThingsByType(type, AuthInfoStore.getUserID());
		List<ThingRestBean> resultList = this.toThingRestBean(thingList);
		return resultList;
	}

	/**
	 * 所有设备的type
	 * GET /things/types
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @return
	 */
	@RequestMapping(value = "/types", method = {RequestMethod.GET})
	public List<Map<String, Object>> getAllType() {
		return thingTagManager.getTypesOfAccessibleThingsWithCount(AuthInfoStore.getUserID());
	}


	/**
	 * tagID查thingType
	 * GET /things/types/tagID/{tagIDs}
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 */
	@RequestMapping(value = "/types/tagID/{tagIDs}", method = {RequestMethod.GET})
	public List<String> getThingTypeByTagIDs(@PathVariable("tagIDs") String tagIDs) {
		List<String> result;
		try {
			result = thingTagManager.getThingTypesOfAccessibleThingsByTagIds(AuthInfoStore.getUserID(),
					asList(tagIDs.split(",")));
		} catch (ObjectNotFoundException e) {
			throw new PortalException("Requested tag doesn't exist or is not accessible", e.getMessage(),
					HttpStatus.BAD_REQUEST);
		}
		return result;
	}

	/**
	 * 查询指定tag下所有的设备类型
	 * GET /things/types/fulltagname/{fullTagNames}
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @return
	 */
	@RequestMapping(value = "/types/fulltagname/{fullTagNames}", method = {RequestMethod.GET})
	public List<String> getThingTypeByTagFullName(@PathVariable("fullTagNames") String fullTagNames) {
		try {
			return thingTagManager.getTypesOfAccessibleThingsByTagFullName(AuthInfoStore.getUserID(),
					asList(fullTagNames.split(",")).stream().collect(Collectors.toSet()));
		} catch (ObjectNotFoundException e) {
			throw new PortalException("Some requested tags don't exist or is not accessible", e.getMessage(),
					HttpStatus.BAD_REQUEST);
		}
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
	@RequestMapping(value = "/{globalThingID}", method = {RequestMethod.GET})
	public ThingRestBean getThingByGlobalID(@PathVariable("globalThingID") Long globalThingID) {
		GlobalThingInfo thingInfo;
		try {
			thingInfo = thingTagManager.getAccessibleThingById(AuthInfoStore.getUserID(), globalThingID);
		} catch (ObjectNotFoundException e) {
			throw new PortalException("Requested thing doesn't exist or isn't accessible", e.getMessage(),
					HttpStatus.BAD_REQUEST);
		}

		// set thing into output
		ThingRestBean thingRestBean = new ThingRestBean();
		BeanUtils.copyProperties(thingInfo, thingRestBean);

		// set location and custom tags into output
		String location = null;
		Set<String> customDisplayNameList = new HashSet<>();
		// get tag
		List<TagIndex> tagIndexList = thingTagManager.findTagIndexByGlobalThingID(globalThingID);
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
	@RequestMapping(value = "", method = {RequestMethod.POST})
	public Map<String, Long> createThing(@RequestBody ThingRestBean input) {

		input.verifyInput();

		GlobalThingInfo thingInfo = null;

		if (input.getId() == null) {
			thingInfo = new GlobalThingInfo();
		} else {
			try {
				thingInfo = thingTagManager.getAccessibleThingById(AuthInfoStore.getUserID(), input.getId());
			} catch (ObjectNotFoundException e) {
				throw new PortalException("Requested thing doesn't exist or isn't accessible", e.getMessage(),
						HttpStatus.BAD_REQUEST);
			}
		}

		thingInfo.setVendorThingID(input.getVendorThingID());
		thingInfo.setKiiAppID(input.getKiiAppID());
		thingInfo.setType(input.getType());
		thingInfo.setStatus(input.getStatus());
		thingInfo.setCustom(input.getCustom());


		List<GlobalThingInfo> gList = thingTagManager.getThingsByVendorThingIds(Arrays.asList(thingInfo.getVendorThingID()));

		if (!gList.isEmpty() && !gList.get(0).getId().equals(input.getId())) {
			throw new PortalException("DuplicateObject", " Duplicate VenderID : " + thingInfo.getVendorThingID(),
					HttpStatus.BAD_REQUEST);
		}

		Long thingID;
		try {
			thingID = thingTagManager.createThing(thingInfo, input.getLocation(), input.getInputTags());
		} catch (ObjectNotFoundException e) {
			throw new PortalException(e.getMessage(), e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (UnauthorizedException e) {
			throw new BeehiveUnAuthorizedException(e.getMessage());
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
	@RequestMapping(value = "/{globalThingID}", method = {RequestMethod.DELETE}, consumes = {"*"})
	public void removeThing(@PathVariable("globalThingID") Long globalThingID) {
		try {
			thingTagManager.removeThing(getCreatedThings(globalThingID.toString()).get(0));
		} catch (ObjectNotFoundException e) {
			throw new PortalException("Requested thing doesn't exist", e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}


	/**
	 * 绑定设备及tag
	 * POST /things/{globalThingIDs}/tags/{fullNames...}
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingIDs
	 */
	@RequestMapping(value = "/{globalThingIDs}/tags/{fullNames}", method = {RequestMethod.POST})
	public void bindThingsToTags(@PathVariable("globalThingIDs") String globalThingIDs,
								 @PathVariable("fullNames") String fullTagNames) {
		if (Strings.isBlank(globalThingIDs) || Strings.isBlank(fullTagNames)) {
			throw new PortalException("RequiredFieldsMissing", "globalThingIDs or tagIDs is empty", HttpStatus
					.BAD_REQUEST);
		}
		List<Long> thingIds = getCreatedThingIds(globalThingIDs);
		List<Long> tagIds = getCreatedTagIds(fullTagNames);
		thingTagManager.bindTagsToThings(tagIds, thingIds);
		thingIFService.onTagIDsChangeFire(tagIds, true);
	}


	/**
	 * 解除绑定设备及tag
	 * DELETE /things/{globalThingIDs}/tags/{fullNames...}
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingIDs
	 */
	@RequestMapping(value = "/{globalThingIDs}/tags/{fullNames}", method = {RequestMethod.DELETE}, consumes = {"*"})
	public void unbindThingsFromTags(@PathVariable("globalThingIDs") String globalThingIDs,
									 @PathVariable("fullNames") String fullTagNames) {
		if (Strings.isBlank(globalThingIDs) || Strings.isBlank(fullTagNames)) {
			throw new PortalException("RequiredFieldsMissing", "globalThingIDs or tagIDs is empty", HttpStatus
					.BAD_REQUEST);
		}
		List<Long> thingIds = getCreatedThingIds(globalThingIDs);
		List<Long> tagIds = getCreatedTagIds(fullTagNames);
		thingTagManager.unbindTagsFromThings(tagIds, thingIds);
		thingIFService.onTagIDsChangeFire(tagIds, false);
	}

	/**
	 * Bind things(devices) to user groups
	 * POST /{globalThingIDs}/userGroups/{userGroupIDs}
	 *
	 * @param globalThingIDs
	 * @param userGroupIDs
	 */
	@RequestMapping(value = "/{globalThingIDs}/userGroups/{userGroupIDs}", method = {RequestMethod.POST})
	public void bindThingsToUserGroups(@PathVariable("globalThingIDs") String globalThingIDs, @PathVariable
			("userGroupIDs")
			String userGroupIDs) {
		if (Strings.isBlank(globalThingIDs) || Strings.isBlank(userGroupIDs)) {
			throw new PortalException("RequiredFieldsMissing", "globalThingIDs or userGroupIDs is empty", HttpStatus
					.BAD_REQUEST);
		}
		List<Long> thingIds = getCreatedThingIds(globalThingIDs);
		List<Long> userGroupIds = getUserGroupIds(userGroupIDs);
		thingTagManager.bindThingsToUserGroups(thingIds, userGroupIds);
	}

	/**
	 * Unbind things(devices) from user groups
	 * POST /{globalThingIDs}/userGroups/{userGroupIDs}
	 *
	 * @param globalThingIDs
	 * @param userGroupIDs
	 */
	@RequestMapping(value = "/{globalThingIDs}/userGroups/{userGroupIDs}", method = {RequestMethod.DELETE}, consumes =
			{"*"})
	public void unbindThingsFromUserGroups(@PathVariable("globalThingIDs") String globalThingIDs, @PathVariable
			("userGroupIDs")
			String userGroupIDs) {
		if (Strings.isBlank(globalThingIDs) || Strings.isBlank(userGroupIDs)) {
			throw new PortalException("RequiredFieldsMissing", "globalThingIDs or userGroupIDs is empty", HttpStatus
					.BAD_REQUEST);
		}
		List<Long> thingIds = getCreatedThingIds(globalThingIDs);
		List<Long> userGroupIds = getUserGroupIds(userGroupIDs);
		thingTagManager.unbindThingsFromUserGroups(thingIds, userGroupIds);
	}

	/**
	 * Bind things(devices) to users
	 * POST /{globalThingIDs}/users/{userIDs}
	 *
	 * @param globalThingIDs thing id list, separated by single comma character
	 * @param userIDs        user id list, separated by single comma character
	 */
	@RequestMapping(value = "/{globalThingIDs}/users/{userIDs}", method = {RequestMethod.POST})
	public void bindThingsToUsers(@PathVariable("globalThingIDs") String globalThingIDs, @PathVariable("userIDs")
			String userIDs) {
		if (Strings.isBlank(globalThingIDs) || Strings.isBlank(userIDs)) {
			throw new PortalException("RequiredFieldsMissing", "globalThingIDs or userIDs is empty", HttpStatus
					.BAD_REQUEST);
		}
		List<Long> thingIds = getCreatedThingIds(globalThingIDs);
		Set<String> userIds = getUserIds(userIDs);
		thingTagManager.bindThingsToUsers(thingIds, userIds);
	}

	/**
	 * Unbind things(devices) from users
	 * DELETE /{globalThingIDs}/users/{userIDs}
	 *
	 * @param globalThingIDs thing id list, separated by single comma character
	 * @param userIDs        user id list, separated by single comma character
	 */
	@RequestMapping(value = "/{globalThingIDs}/users/{userIDs}", method = {RequestMethod.DELETE}, consumes = {"*"})
	public void unbindThingsFromUsers(@PathVariable("globalThingIDs") String globalThingIDs, @PathVariable("userIDs")
			String userIDs) {
		if (Strings.isBlank(globalThingIDs) || Strings.isBlank(userIDs)) {
			throw new PortalException("RequiredFieldsMissing", "globalThingIDs or userIDs is empty", HttpStatus
					.BAD_REQUEST);
		}
		List<Long> thingIds = getCreatedThingIds(globalThingIDs);
		Set<String> userIds = getUserIds(userIDs);
		thingTagManager.unbindThingsFromUsers(thingIds, userIds);
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
	@RequestMapping(value = "/{globalThingIDs}/tags/custom/{displayNames}", method = {RequestMethod.POST})
	public void bindThingsToCustomTags(@PathVariable("globalThingIDs") String globalThingIDs,
									   @PathVariable("displayNames") String displayNames) {
		if (Strings.isBlank(globalThingIDs) || Strings.isBlank(displayNames)) {
			throw new PortalException("RequiredFieldsMissing", "globalThingIDs or displayNames is empty", HttpStatus
					.BAD_REQUEST);
		}
		List<Long> thingIds = getCreatedThingIds(globalThingIDs);
		List<Long> tagIds = getCreatedTagIds(TagType.Custom, displayNames);
		thingTagManager.bindTagsToThings(tagIds, thingIds);
		thingIFService.onTagIDsChangeFire(tagIds, true);
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
	@RequestMapping(value = "/{globalThingIDs}/tags/custom/{displayNames}", method = {RequestMethod.DELETE}, consumes = {"*"})
	public void unbindThingsFromCustomTags(@PathVariable("globalThingIDs") String globalThingIDs,
										   @PathVariable("displayNames") String displayNames) {
		if (Strings.isBlank(globalThingIDs) || Strings.isBlank(displayNames)) {
			throw new PortalException("RequiredFieldsMissing", "globalThingIDs or displayNames is empty", HttpStatus
					.BAD_REQUEST);
		}
		List<Long> thingIds = getCreatedThingIds(globalThingIDs);
		List<Long> tagIds = getCreatedTagIds(TagType.Custom, displayNames);
		thingTagManager.unbindTagsFromThings(tagIds, thingIds);
		thingIFService.onTagIDsChangeFire(tagIds, false);
	}

	/**
	 * 绑定设备及team
	 * POST /things/{globalThingIDs}/teams/{teamID}
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingIDs
	 */
	@RequestMapping(value = "/{globalThingIDs}/teams/{teamIDs}", method = {RequestMethod.POST})
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
	@RequestMapping(value = "/{globalThingIDs}/teams/{teamIDs}", method = {RequestMethod.DELETE}, consumes = {"*"})
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
	@RequestMapping(value = "/search", method = {RequestMethod.GET})
	public List<ThingRestBean> getThingsByTagExpress(@RequestParam(value = "tagType") String tagType,
													 @RequestParam(value = "displayName") String displayName) {
		List<TagIndex> tagIndexes = thingTagManager.getAccessibleTagsByTagTypeAndName(AuthInfoStore.getUserID(),
				StringUtils.capitalize(tagType), displayName);

		List<GlobalThingInfo> thingInfos = thingTagManager.getThingsByTagIds(tagIndexes.stream().map(TagIndex::getId).
				collect(Collectors.toSet()));

		List<ThingRestBean> resultList = this.toThingRestBean(thingInfos);

		return resultList;
	}

	/**
	 * 查询gateway下的设备
	 * GET /things/{globalThingID}/endnodes
	 * <p>
	 *
	 * @param globalThingID
	 * @return
	 */
	@RequestMapping(value = "/{globalThingID}/endnodes", method = {RequestMethod.GET})
	public ResponseEntity<List<ThingRestBean>> getGatewayEndnodes(@PathVariable("globalThingID") Long globalThingID) {

		// get gateway info

		GlobalThingInfo gatewayInfo = getCreatedThings(globalThingID.toString()).get(0);

		// check whether onboarding is done
		String fullKiiThingID = gatewayInfo.getFullKiiThingID();

		if (Strings.isBlank(fullKiiThingID)) {
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
		}

		// get vendor thing id list of endnodes
		List<EndNodeOfGateway> endNodes = thingIFService.getAllEndNodesOfGateway(fullKiiThingID);
		List<String> vendorThingIDList = new ArrayList();
		for (EndNodeOfGateway endNodeOfGateway : endNodes) {
			vendorThingIDList.add(endNodeOfGateway.getVendorThingID());
		}

		// get thing info of endnodes
		List<GlobalThingInfo> globalThingInfoList = thingTagManager.getThingsByVendorThingIds(vendorThingIDList);
		List<ThingRestBean> resultList = this.toThingRestBean(globalThingInfoList);

		return new ResponseEntity(resultList, HttpStatus.OK);
	}

	private List<ThingRestBean> toThingRestBean(List<GlobalThingInfo> list) {
		List<ThingRestBean> resultList = new ArrayList<>();
		if (list != null) {
			list.forEach(thingInfo -> {
				ThingRestBean input = new ThingRestBean();
				BeanUtils.copyProperties(thingInfo, input);
				resultList.add(input);
			});
		}

		return resultList;
	}

}