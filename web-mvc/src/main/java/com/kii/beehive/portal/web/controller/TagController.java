package com.kii.beehive.portal.web.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.portal.jdbc.entity.UserGroup;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;

/**
 * Beehive API - Thing API
 * <p>
 * refer to doc "Beehive API - Tech Design" section "Thing API" for details
 */
@RestController
@RequestMapping(value = "/tags", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {
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
	@RequestMapping(value = "/custom", method = {RequestMethod.POST})
	public Map<String, Object> createTag(@RequestBody TagIndex tag) {
		String displayName = tag.getDisplayName();
		if (Strings.isBlank(displayName)) {
			PortalException excep= new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,  HttpStatus.BAD_REQUEST);
			excep.addParam("field","displayName");
			throw excep;
		}

		if (null != tag.getId()) {

				TagIndex existedTag = thingTagManager.getTagIndexes(Arrays.asList(tag.getId().toString())).get(0);
				if (!thingTagManager.isTagCreator(existedTag)) {
					PortalException excep= new PortalException(ErrorCode.TAG_NO_PRIVATE,
							HttpStatus.BAD_REQUEST);
					excep.addParam("tagName",existedTag.getDisplayName());
					throw excep;
				}

		} else if (thingTagManager.isTagDisplayNamePresent(AuthInfoStore.getTeamID(), TagType.Custom, displayName)) {
			PortalException excep= new PortalException(ErrorCode.TAG_NO_PRIVATE,
					HttpStatus.BAD_REQUEST);
			excep.addParam("tagName",displayName);
			throw excep;
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
	@RequestMapping(value = "/custom/{displayName}", method = {RequestMethod.DELETE}, consumes = {"*"})
	public void removeTag(@PathVariable("displayName") String displayName) {

		if (Strings.isBlank(displayName)) {
			PortalException excep= new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,  HttpStatus.BAD_REQUEST);
			excep.addParam("field","displayName");
			throw excep;		}

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
	@RequestMapping(value = "/search", method = {RequestMethod.GET}, consumes = {"*"})
	public List<TagIndex> findTags(@RequestParam(value = "tagType", required = false) String tagType,
								   @RequestParam(value = "displayName", required = false) String displayName) {
		return thingTagManager.getAccessibleTagsByTagTypeAndName(AuthInfoStore.getUserIDInLong(),
				StringUtils.capitalize(tagType), displayName);
	}

	/*@RequestMapping(value = "/{tagName}/operation/{operation}", method = { RequestMethod.GET })
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
	@RequestMapping(value = "/locations/{parentLocation}", method = {RequestMethod.GET}, consumes = {"*"})
	public List<String> findLocations(@PathVariable("parentLocation") String parentLocation) {
		return thingTagManager.getAccessibleTagsByUserIdAndLocations(AuthInfoStore.getUserIDInLong(), parentLocation).stream().
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
	@RequestMapping(value = "/locations", method = {RequestMethod.GET}, consumes = {"*"})
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
	@RequestMapping(value = "/{fullNames}/users/{userIDs}", method = {RequestMethod.POST})
	public void bindTagToUser(@PathVariable("fullNames") String fullNames, @PathVariable("userIDs") String userIds) {
//		if (Strings.isBlank(fullNames) || Strings.isBlank(userIds)) {
//			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, HttpStatus
//					.BAD_REQUEST);
//		}
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
	@RequestMapping(value = "/{fullNames}/users/{userIDs}", method = {RequestMethod.DELETE}, consumes = {"*"})
	public void unbindTagFromUser(@PathVariable("fullNames") String fullNames, @PathVariable("userIDs") String userIds) {
//		if (Strings.isBlank(fullNames) || Strings.isBlank(userIds)) {
//			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, HttpStatus
//					.BAD_REQUEST);
//		}
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
	@RequestMapping(value = "/{fullNames}/userGroups/{userGroupIDs}", method = {RequestMethod.POST})
	public void bindTagToUserGroup(@PathVariable("fullNames") String fullNames, @PathVariable("userGroupIDs") String
			userGroupIDs) {
//		if (Strings.isBlank(fullNames) || Strings.isBlank(userGroupIDs)) {
//			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,  HttpStatus
//					.BAD_REQUEST);
//		}

		List<Long> tagIds = getCreatedTagIds(fullNames);
		Set<Long> groupIds = getUserGroupIds(userGroupIDs);
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
	@RequestMapping(value = "/{fullNames}/userGroups/{userGroupIDs}", method = {RequestMethod.DELETE}, consumes = {"*"})
	public void unbindTagsFromUserGroups(@PathVariable("fullNames") String fullNames, @PathVariable("userGroupIDs") String userGroupIDs) {
//		if (Strings.isBlank(fullNames) || Strings.isBlank(userGroupIDs)) {
//			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,  HttpStatus
//					.BAD_REQUEST);
//		}
		List<Long> tagIds = getCreatedTagIds(fullNames);


		Set<Long> groupIds = super.getUserGroupIds(userGroupIDs);
		thingTagManager.unbindTagsFromUserGroups(tagIds, groupIds);
	}

	/**
	 * GET /tags/user
	 *
	 * @return a list of tags which the user can access
	 */
	@RequestMapping(value = "/user", method = RequestMethod.GET, consumes = {"*"})
	public List<TagIndex> getTagsByUser() {
			return thingTagManager.getAccessibleTagsByUserId(AuthInfoStore.getUserIDInLong());
	}

	/**
	 * GET /tags/{fullTagName}/users
	 *
	 * @param fullTagName
	 * @return a list of users who can access the tags
	 */
	@RequestMapping(value = "/{fullTagName}/users", method = RequestMethod.GET, consumes = {"*"})
	public List<BeehiveJdbcUser> getUsersByFullTagName(@PathVariable("fullTagName") String fullTagName) {
		return  thingTagManager.getUsersOfAccessibleTags(AuthInfoStore.getUserIDInLong(), fullTagName);
	}

	/**
	 * GET /tags/userGroup/{userGroupID}
	 *
	 * @param userGroupId
	 * @return a list of tags which the user group can access
	 */
	@RequestMapping(value = "/userGroup/{userGroupID}", method = RequestMethod.GET, consumes = {"*"})
	public List<TagIndex> getTagsByUserGroup(@PathVariable("userGroupID") Long userGroupId) {
		return thingTagManager.getAccessibleTagsByUserGroupId(userGroupId);
	}

	/**
	 * GET /tags/{fullTagName}/userGroups
	 *
	 * @param fullTagName
	 * @return a list of user groups which can access the tags
	 */
	@RequestMapping(value = "/{fullTagName}/userGroups", method = RequestMethod.GET, consumes = {"*"})
	public List<UserGroup> getUserGroupsByFullTagName(@PathVariable("fullTagName") String fullTagName) {
		List<Long> userGroupIds = thingTagManager.getUserGroupsOfAccessibleTags(AuthInfoStore.getUserIDInLong(), fullTagName);
		return thingTagManager.getUserGroupsByIds(userGroupIds);

	}
}

