package com.kii.beehive.portal.web.controller;

import com.kii.beehive.portal.exception.ObjectNotFoundException;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.portal.jdbc.entity.UserGroup;
import com.kii.beehive.portal.jdbc.entity.BeehiveUser;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.exception.BeehiveUnAuthorizedException;
import com.kii.beehive.portal.web.exception.PortalException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Beehive API - Thing API
 * <p>
 * refer to doc "Beehive API - Tech Design" section "Thing API" for details
 */
@RestController
@RequestMapping(path = "/tags", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {
		MediaType.APPLICATION_JSON_UTF8_VALUE})
public class TagController extends AbstractThingTagController {

	/**
	 * 创建tag
	 * POST /tags/custom
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details refer
	 * to doc "Tech Design - Beehive API", section
	 * "Create/Update Tag (创建/更新tag)" for more details
	 */
	@RequestMapping(path = "/custom", method = {RequestMethod.POST})
	public Map<String, Object> createTag(@RequestBody TagIndex tag) {
		String displayName = tag.getDisplayName();
		if (Strings.isBlank(displayName)) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "DisplayName is empty",
					HttpStatus.BAD_REQUEST);
		}

		if (null != tag.getId()) {
			try {
				TagIndex existedTag = thingTagManager.getTagIndexes(Arrays.asList(tag.getId().toString())).get(0);
				if (!thingTagManager.isTagCreator(existedTag)) {
					throw new BeehiveUnAuthorizedException("Requested tag isn't created by current user");
				}
			} catch (ObjectNotFoundException e) {
				throw new PortalException(HttpStatus.BAD_REQUEST + "", "Requested tag doesn't exists",
						HttpStatus.BAD_REQUEST);
			}
		} else if (thingTagManager.isTagDisplayNamePresent(getLoginTeamID(), TagType.Custom, displayName)) {
			throw new PortalException(HttpStatus.BAD_REQUEST + "", "Requested displayName already exists",
					HttpStatus.BAD_REQUEST);
		}
		tag.setTagType(TagType.Custom);
		tag.setFullTagName(TagType.Custom.getTagName(displayName));
		long tagID = thingTagManager.createTag(tag);


		Map<String, Object> map = new HashMap<>();
		map.put("id", tagID);
		map.put("tagName", tag.getFullTagName());
		return map;
	}

	/**
	 * 移除tag
	 * DELETE /tags/custom/{displayName}
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details refer
	 * to doc "Tech Design - Beehive API", section "Delete Tag (移除tag)" for more
	 * details
	 */
	@RequestMapping(path = "/custom/{displayName}", method = {RequestMethod.DELETE}, consumes = {"*"})
	public void removeTag(@PathVariable("displayName") String displayName) {

		if (Strings.isBlank(displayName)) {
			throw new PortalException("RequiredFieldsMissing", "displayName is empty", HttpStatus.BAD_REQUEST);
		}

		getCreatedTagIds(TagType.Custom, displayName).forEach(id -> thingTagManager.removeTag(id));
	}

	/**
	 * 查询tag
	 * GET /tags/search?tagType={tagType}&displayName={displayName}
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details refer
	 * to doc "Tech Design - Beehive API", section "Inquire Tag (查询tag)" for
	 * more details
	 *
	 * @return
	 */
	@RequestMapping(path = "/search", method = {RequestMethod.GET})
	public List<TagIndex> findTags(@RequestParam(value = "tagType", required = false) String tagType,
								   @RequestParam(value = "displayName", required = false) String displayName) {
		return thingTagManager.getAccessibleTagsByTagTypeAndName(getLoginUserID(),
				StringUtils.capitalize(tagType), displayName);
	}

	/*@RequestMapping(path = "/{tagName}/operation/{operation}", method = { RequestMethod.GET })
	public List<GlobalThingInfo> getThingsByTagExpress(@PathVariable("tagName") String tagName,
			@PathVariable("operation") String operation) {

		List<GlobalThingInfo> list = this.thingManager.findThingByTagName(tagName.split(","), operation);

		return list;
	}*/

	/**
	 * 查询位置信息
	 * GET /tags/locations/{parentLocation}
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @return
	 */
	@RequestMapping(path = "/locations/{parentLocation}", method = {RequestMethod.GET}, consumes = {"*"})
	public List<String> findLocations(@PathVariable("parentLocation") String parentLocation) {
		return thingTagManager.getAccessibleTagsByUserIdAndLocations(getLoginUserID(), parentLocation).stream().
				map(TagIndex::getDisplayName).collect(Collectors.toList());
	}

	/**
	 * 查询位置信息(所有)
	 * GET /tags/locations/{parentLocation}
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @return
	 */
	@RequestMapping(path = "/locations", method = {RequestMethod.GET}, consumes = {"*"})
	public List<String> findAllLocations() {
		return findLocations("");
	}


	/**
	 * Bind tags to users.
	 * POST /{fullNames}/users/{userIDs}
	 *
	 * @param fullNames
	 * @param userIds
	 */
	@RequestMapping(path = "/{fullNames}/users/{userIDs}", method = {RequestMethod.POST})
	public void bindTagToUser(@PathVariable("fullNames") String fullNames, @PathVariable("userIDs") String userIds) {
		if (Strings.isBlank(fullNames) || Strings.isBlank(userIds)) {
			throw new PortalException("RequiredFieldsMissing", "tagIDs or userIDs is empty", HttpStatus
					.BAD_REQUEST);
		}
		List<Long> tagIds = getCreatedTagIds(fullNames);
		Set<String> userIdSet = getUserIds(userIds);
		thingTagManager.bindTagsToUsers(tagIds, userIdSet);
	}

	/**
	 * Unbind tags to users.
	 * POST /{fullNames}/users/{userIDs}
	 *
	 * @param fullNames
	 * @param userIds
	 */
	@RequestMapping(path = "/{fullNames}/users/{userIDs}", method = {RequestMethod.DELETE})
	public void unbindTagFromUser(@PathVariable("fullNames") String fullNames, @PathVariable("userIDs") String userIds) {
		if (Strings.isBlank(fullNames) || Strings.isBlank(userIds)) {
			throw new PortalException("RequiredFieldsMissing", "tagIDs or userIDs is empty", HttpStatus
					.BAD_REQUEST);
		}
		List<Long> tagIds = getCreatedTagIds(fullNames);
		Set<String> userIdSet = getUserIds(userIds);
		thingTagManager.unbindTagsFromUsers(tagIds, userIdSet);
	}

	/**
	 * 绑定tag及usergroup
	 * POST /tags/{fullNames}/userGroups/{userGroupIDs}
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 */
	@RequestMapping(path = "/{fullNames}/userGroups/{userGroupIDs}", method = {RequestMethod.POST})
	public void bindTagToUserGroup(@PathVariable("fullNames") String fullNames, @PathVariable("userGroupIDs") String
			userGroupIDs) {
		if (Strings.isBlank(fullNames) || Strings.isBlank(userGroupIDs)) {
			throw new PortalException("RequiredFieldsMissing", "tagIDs or userGroupIDs is empty", HttpStatus
					.BAD_REQUEST);
		}

		List<Long> tagIds = getCreatedTagIds(fullNames);
		List<Long> groupIds = getUserGroupIds(userGroupIDs);
		thingTagManager.bindTagsToUserGroups(tagIds, groupIds);
	}

	/**
	 * 解除绑定tag及usergroup
	 * DELETE /tags/{fullNames}/userGroups/{userGroupIDs}
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param fullNames
	 */
	@RequestMapping(path = "/{fullNames}/userGroups/{userGroupIDs}", method = {RequestMethod.DELETE}, consumes = {"*"})
	public void unbindTagsFromUserGroups(@PathVariable("fullNames") String fullNames, @PathVariable("userGroupIDs") String userGroupIDs) {
		if (Strings.isBlank(fullNames) || Strings.isBlank(userGroupIDs)) {
			throw new PortalException("RequiredFieldsMissing", "tagIDs or userGroupIDs is empty", HttpStatus
					.BAD_REQUEST);
		}
		List<Long> tagIds = getCreatedTagIds(fullNames);
		List<Long> groupIds = getUserGroupIds(userGroupIDs);
		thingTagManager.unbindTagsFromUserGroups(tagIds, groupIds);
	}

	/**
	 * GET /tags/user/{userID}
	 *
	 * @param userId
	 * @return a list of tags which the user can access
	 */
	@RequestMapping(value = "/user/{userID}", method = RequestMethod.GET)
	public List<TagIndex> getTagsByUser(@PathVariable("userID") String userId) {
		return thingTagManager.getAccessibleTagsByUserId(userId);
	}

	/**
	 * GET /tags/{fullTagName}/users
	 *
	 * @param fullTagName
	 * @return a list of users who can access the tags
	 */
	@RequestMapping(value = "/{fullTagName}/users", method = RequestMethod.GET)
	public List<BeehiveUser> getUsersByFullTagName(@PathVariable("fullTagName") String fullTagName) {
		List<String> userId = thingTagManager.getUsersOfAccessibleTags(getLoginUserID(), fullTagName);
		try {
			return thingTagManager.getUsers(userId);
		} catch (ObjectNotFoundException e) {
			throw new PortalException(e.getMessage(), e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * GET /tags/userGroup/{userGroupID}
	 *
	 * @param userGroupId
	 * @return a list of tags which the user group can access
	 */
	@RequestMapping(value = "/userGroup/{userGroupID}", method = RequestMethod.GET)
	public List<TagIndex> getTagsByUserGroup(@PathVariable("userGroupID") Long userGroupId) {
		return thingTagManager.getAccessibleTagsByUserGroupId(userGroupId);
	}

	/**
	 * GET /tags/{fullTagName}/userGroups
	 *
	 * @param fullTagName
	 * @return a list of user groups which can access the tags
	 */
	@RequestMapping(value = "/{fullTagName}/userGroups", method = RequestMethod.GET)
	public List<UserGroup> getUserGroupsByFullTagName(@PathVariable("fullTagName") String fullTagName) {
		List<Long> userGroupIds = thingTagManager.getUserGroupsOfAccessibleTags(getLoginUserID(), fullTagName);
		try {
			return thingTagManager.getUserGroupsByIds(userGroupIds);
		} catch (ObjectNotFoundException e) {
			throw new PortalException(e.getMessage(), e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
}

