package com.kii.beehive.portal.web.controller;

import com.kii.beehive.business.manager.TagThingManager;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.TagUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.TeamTagRelationDao;
import com.kii.beehive.portal.jdbc.entity.*;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.exception.BeehiveUnAuthorizedException;
import com.kii.beehive.portal.web.exception.PortalException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Beehive API - Thing API
 * <p>
 * refer to doc "Beehive API - Tech Design" section "Thing API" for details
 */
@RestController
@RequestMapping(path = "/tags", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {
        MediaType.APPLICATION_JSON_UTF8_VALUE})
public class TagController extends AbstractController {

    @Autowired
    private TagIndexDao tagIndexDao;

    @Autowired
    private TeamTagRelationDao teamTagRelationDao;

    @Autowired
    private TagThingManager tagThingManager;

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
        if (!tagThingManager.isTagCreator(toBeRemoved)) {
            throw new BeehiveUnAuthorizedException("Current user is not the creator of the tag.");
        }
        tagThingManager.removeTag(toBeRemoved);

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
        return tagThingManager.findUserLocations(getLoginUserID(), parentLocation);
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
     * POST /{tagIDs}/users/{userIDs}
     *
     * @param tagIds
     * @param userIds
     */
    @RequestMapping(path = "/{tagIDs}/users/{userIDs}", method = {RequestMethod.POST})
    public void bindTagToUser(@PathVariable("tagIDs") String tagIds, @PathVariable("userIDs") String userIds) {
        if (Strings.isBlank(tagIds) || Strings.isBlank(userIds)) {
            throw new PortalException("RequiredFieldsMissing", "tagIDs or userIDs is empty", HttpStatus
                    .BAD_REQUEST);
        }
        List<String> tagIDList = parseAndCheckTagIds(tagIds);
        List<String> userIDList = Arrays.asList(userIds.split(","));
        Set<String> nonExistUsers = userManager.checkNonExistingUserID(userIDList);
        if (null != nonExistUsers && !nonExistUsers.isEmpty()) {
            throw new PortalException("Requested user group doesn't exist", "Invalid user id(s): [" +
                    listToString(nonExistUsers) + "]", HttpStatus.BAD_REQUEST);
        }
        tagThingManager.bindTagToUser(tagIDList, userIDList);
    }

    /**
     * Unbind tags to users.
     * POST /{tagIDs}/users/{userIDs}
     *
     * @param tagIds
     * @param userIds
     */
    @RequestMapping(path = "/{tagIDs}/users/{userIDs}", method = {RequestMethod.DELETE})
    public void unbindTagFromUser(@PathVariable("tagIDs") String tagIds, @PathVariable("userIDs") String userIds) {
        if (Strings.isBlank(tagIds) || Strings.isBlank(userIds)) {
            throw new PortalException("RequiredFieldsMissing", "tagIDs or userIDs is empty", HttpStatus
                    .BAD_REQUEST);
        }
        List<String> tagIDList = parseAndCheckTagIds(tagIds);
        List<String> userIDList = Arrays.asList(userIds.split(","));
        Set<String> nonExistUsers = userManager.checkNonExistingUserID(userIDList);
        if (null != nonExistUsers && !nonExistUsers.isEmpty()) {
            throw new PortalException("Requested user group doesn't exist", "Invalid user id(s): [" +
                    listToString(nonExistUsers) + "]", HttpStatus.BAD_REQUEST);
        }
        tagThingManager.unbindTagFromUser(tagIDList, userIDList);
    }

    /**
     * 绑定tag及usergroup
     * POST /tags/{tagIDs}/userGroups/{userGroupIDs}
     * <p>
     * refer to doc "Beehive API - Thing API" for request/response details
     */
    @RequestMapping(path = "/{tagIDs}/userGroups/{userGroupIDs}", method = {RequestMethod.POST})
    public void bindTagToUserGroup(@PathVariable("tagIDs") String tagIDs, @PathVariable("userGroupIDs") String
            userGroupIDs) {
        if (Strings.isBlank(tagIDs) || Strings.isBlank(userGroupIDs)) {
            throw new PortalException("RequiredFieldsMissing", "tagIDs or userGroupIDs is empty", HttpStatus
                    .BAD_REQUEST);
        }
        List<String> tagIDList = parseAndCheckTagIds(tagIDs);
        List<String> userGroupIDList = Arrays.asList(userGroupIDs.split(","));
        getUserGroups(userGroupIDList);
        tagThingManager.bindTagToUserGroup(tagIDList, userGroupIDList);
    }

    /**
     * 解除绑定tag及usergroup
     * DELETE /tags/{tagIDs}/userGroups/{userGroupIDs}
     * <p>
     * refer to doc "Beehive API - Thing API" for request/response details
     *
     * @param tagIDs
     */
    @RequestMapping(path = "/{tagIDs}/userGroups/{userGroupIDs}", method = {RequestMethod.DELETE}, consumes = {"*"})
    public void unbindTagFromUserGroup(@PathVariable("tagIDs") String tagIDs, @PathVariable("userGroupIDs") String userGroupIDs) {
        if (Strings.isBlank(tagIDs) || Strings.isBlank(userGroupIDs)) {
            throw new PortalException("RequiredFieldsMissing", "tagIDs or userGroupIDs is empty", HttpStatus
                    .BAD_REQUEST);
        }
        List<String> tagIDList = parseAndCheckTagIds(tagIDs);
        List<String> userGroupIDList = Arrays.asList(userGroupIDs.split(","));
        getUserGroups(userGroupIDList);
        tagThingManager.unbindTagToUserGroup(tagIDList, userGroupIDList);
    }

    private String listToString(Collection<?> collection) {
        StringBuilder sb = new StringBuilder();
        collection.forEach(data -> {
            if (0 != sb.length()) {
                sb.append(", ");
            }
            sb.append(data);
        });
        return sb.toString();
    }

    private List<TagIndex> getTagIndexes(List<String> tagIDList) throws PortalException {
        List<Long> tagIds = tagIDList.stream().filter(Pattern.compile("^[0-9]+$").asPredicate()).map(Long::valueOf)
                .collect(Collectors.toList());
        List<TagIndex> tagIndexes = tagIndexDao.findByIDs(tagIds);
        if (null == tagIndexes || !tagIndexes.stream().map(TagIndex::getId).map(Object::toString).
                collect(Collectors.toSet()).containsAll(tagIDList)) {
            tagIds.removeAll(tagIndexes.stream().map(TagIndex::getId).collect(Collectors.toList()));
            throw new PortalException("Requested tag doesn't exist", "Invalid tag id(s): [" + listToString(tagIds) +
                    "]", HttpStatus.BAD_REQUEST);
        }
        return tagIndexes;
    }

    private List<UserGroup> getUserGroups(List<String> userGroupIDList) throws PortalException {
        List<Long> userGroupIds = userGroupIDList.stream().filter(Pattern.compile("^[0-9]+$").asPredicate())
                .map(Long::valueOf).collect(Collectors.toList());
        List<UserGroup> userGroups = userGroupDao.findByIDs(userGroupIds);
        if (null == userGroups || !userGroups.stream().map(UserGroup::getId).map(Object::toString).
                collect(Collectors.toSet()).containsAll(userGroupIDList)) {
            userGroupIds.removeAll(userGroups.stream().map(UserGroup::getId).collect(Collectors.toList()));
            throw new PortalException("Requested user group doesn't exist", "Invalid user group id(s): [" +
                    listToString(userGroupIds) + "]", HttpStatus.BAD_REQUEST);
        }
        return userGroups;
    }

    private List<String> parseAndCheckTagIds(String tagIds) {
        List<String> tagIDList = Arrays.asList(tagIds.split(","));
        List<TagIndex> tagIndexes = getTagIndexes(tagIDList);
        tagIndexes.forEach(index -> {
            if (!tagThingManager.isTagCreator(index)) {
                throw new BeehiveUnAuthorizedException("Current user is not the creator of the tag.");
            }
        });
        return tagIDList;
    }
}
