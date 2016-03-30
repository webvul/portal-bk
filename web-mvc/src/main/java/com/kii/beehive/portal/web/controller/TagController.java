package com.kii.beehive.portal.web.controller;

import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.TagUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.TeamTagRelationDao;
import com.kii.beehive.portal.jdbc.entity.*;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.exception.BeehiveUnAuthorizedException;
import com.kii.beehive.portal.web.exception.PortalException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Beehive API - Thing API
 * <p>
 * refer to doc "Beehive API - Tech Design" section "Thing API" for details
 */
@RestController
@RequestMapping(path = "/tags", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {
		MediaType.APPLICATION_JSON_UTF8_VALUE})
public class TagController extends AbstractThingTagController {

	@Autowired
	private TagIndexDao tagIndexDao;

	@Autowired
	private TeamTagRelationDao teamTagRelationDao;

	@Autowired
	private TagUserRelationDao tagUserRelationDao;

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

		tag.setTagType(TagType.Custom);
		List<TagIndex> tagList = tagIndexDao.findTagByTagTypeAndName(tag.getTagType().name(), tag.getDisplayName());
		if (tagList.size() > 0) {//update
			TagIndex old = tagList.get(0);
			old.setDescription(tag.getDescription());
			tag = old;
		}
		tag.setFullTagName(TagType.Custom.getTagName(displayName));
		long tagID = tagIndexDao.saveOrUpdate(tag);

		if (isTeamIDExist()) {
			teamTagRelationDao.saveOrUpdate(new TeamTagRelation(getLoginTeamID(), tagID));
		}

		tagUserRelationDao.saveOrUpdate(new TagUserRelation(tagID, getLoginUserID()));

		Map<String, Object> map = new HashMap<>();
		map.put("id", tagID);
		map.put("tagName", TagType.Custom.getTagName(tag.getDisplayName()));
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

		List<TagIndex> orig = tagIndexDao.findTagByTagTypeAndName(TagType.Custom.toString(), displayName);

		if (orig.size() == 0) {
			throw new PortalException("Tag Not Found", "Tag with displayName:" + displayName + " Not Found",
					HttpStatus.NOT_FOUND);
		}

		TagIndex toBeRemoved = orig.get(0);
		if (!thingTagManager.isTagCreator(toBeRemoved)) {
			throw new BeehiveUnAuthorizedException("Current user is not the creator of the tag.");
		}
		thingTagManager.removeTag(toBeRemoved);

//		eventBus.onTagChangeFire();
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

		List<TagIndex> list = tagIndexDao.findUserTagByTypeAndName(getLoginUserID(),
				StringUtils.capitalize(tagType), displayName);
		return list;

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
		return thingTagManager.findUserLocations(getLoginUserID(), parentLocation);
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
		List<TagIndex> tagIndexes = getTags(Arrays.asList(fullNames.split(",")));
		List<BeehiveUser> users = getUsers(userIds);
		try {
			thingTagManager.bindTagsToUsers(tagIndexes, users);
		} catch (UnauthorizedException e) {
			throw new BeehiveUnAuthorizedException(e.getMessage());
		}
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
		List<TagIndex> tagIndexes = getTags(Arrays.asList(fullNames.split(",")));
		List<BeehiveUser> users = getUsers(userIds);
		try {
			thingTagManager.unbindTagsFromUsers(tagIndexes, users);
		} catch (UnauthorizedException e) {
			throw new BeehiveUnAuthorizedException(e.getMessage());
		}
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

		List<TagIndex> tagIndexes = getTags(Arrays.asList(fullNames.split(",")));

		List<UserGroup> userGroups = getUserGroups(userGroupIDs);
		try {
			thingTagManager.bindTagsToUserGroups(tagIndexes, userGroups);
		} catch (UnauthorizedException e) {
			throw new BeehiveUnAuthorizedException(e.getMessage());
		}
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
	public void unbindTagFromUserGroup(@PathVariable("fullNames") String fullNames, @PathVariable("userGroupIDs") String userGroupIDs) {
		if (Strings.isBlank(fullNames) || Strings.isBlank(userGroupIDs)) {
			throw new PortalException("RequiredFieldsMissing", "tagIDs or userGroupIDs is empty", HttpStatus
					.BAD_REQUEST);
		}
		List<TagIndex> tagIndexes = getTags(Arrays.asList(fullNames.split(",")));
		List<UserGroup> userGroups = getUserGroups(userGroupIDs);
		try {
			thingTagManager.unbindTagsFromUserGroups(tagIndexes, userGroups);
		} catch (UnauthorizedException e) {
			throw new BeehiveUnAuthorizedException(e.getMessage());
		}
	}
}
