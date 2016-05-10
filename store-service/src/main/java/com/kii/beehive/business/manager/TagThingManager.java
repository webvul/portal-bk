package com.kii.beehive.business.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.CollectUtils;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.GroupUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.TagGroupRelationDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.TagThingRelationDao;
import com.kii.beehive.portal.jdbc.dao.TagUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.TeamDao;
import com.kii.beehive.portal.jdbc.dao.TeamTagRelationDao;
import com.kii.beehive.portal.jdbc.dao.TeamThingRelationDao;
import com.kii.beehive.portal.jdbc.dao.ThingUserGroupRelationDao;
import com.kii.beehive.portal.jdbc.dao.ThingUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.UserGroupDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.GroupUserRelation;
import com.kii.beehive.portal.jdbc.entity.TagGroupRelation;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagThingRelation;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.portal.jdbc.entity.TagUserRelation;
import com.kii.beehive.portal.jdbc.entity.Team;
import com.kii.beehive.portal.jdbc.entity.TeamTagRelation;
import com.kii.beehive.portal.jdbc.entity.TeamThingRelation;
import com.kii.beehive.portal.jdbc.entity.ThingUserGroupRelation;
import com.kii.beehive.portal.jdbc.entity.ThingUserRelation;
import com.kii.beehive.portal.jdbc.entity.UserGroup;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.service.BeehiveUserDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.store.entity.KiiAppInfo;
import com.kii.extension.sdk.exception.ObjectNotFoundException;

@Component
@Transactional
public class TagThingManager {
	public final static String DEFAULT_LOCATION = "Unknown";
	private Logger log = LoggerFactory.getLogger(TagThingManager.class);
	@Autowired
	private GlobalThingSpringDao globalThingDao;

	@Autowired
	private TagIndexDao tagIndexDao;

	@Autowired
	private TagThingRelationDao tagThingRelationDao;

	@Autowired
	private TeamDao teamDao;

	@Autowired
	private TeamThingRelationDao teamThingRelationDao;

	@Autowired
	private TagGroupRelationDao tagGroupRelationDao;

	@Autowired
	private TagUserRelationDao tagUserRelationDao;

	@Autowired
	private AppInfoDao appInfoDao;

	@Autowired
	private ThingUserGroupRelationDao thingUserGroupRelationDao;

	@Autowired
	private ThingUserRelationDao thingUserRelationDao;

	@Autowired
	private TeamTagRelationDao teamTagRelationDao;

	@Autowired
	private ThingIFInAppService thingIFInAppService;

	@Autowired
	private BeehiveUserDao userDao;

	@Autowired
	private UserGroupDao userGroupDao;

	@Autowired
	private GroupUserRelationDao groupUserRelationDao;


	/**
	 * create or update the thing including the location and custom tags
	 *
	 * @param thingInfo
	 * @param location
	 * @param tagList
	 * @return
	 */
	public Long createThing(GlobalThingInfo thingInfo, String location, Collection<String> tagList)
			throws ObjectNotFoundException, UnauthorizedException {

		KiiAppInfo kiiAppInfo = appInfoDao.getAppInfoByID(thingInfo.getKiiAppID());

		// check whether Kii App ID is existing
		if (kiiAppInfo == null) {
			throw  EntryNotFoundException.appNotFound(thingInfo.getKiiAppID());
		}

		// check whether Kii App ID is Master App
		if (kiiAppInfo.getMasterApp()) {
			throw  EntryNotFoundException.thingNotFound(thingInfo.getId());
		}


		long thingID = globalThingDao.saveOrUpdate(thingInfo);

		// set location tag and location tag-thing relation
		if (Strings.isBlank(location)) {
			location = DEFAULT_LOCATION;
		}


		if (thingInfo.getId() == null) { //create
			this.saveOrUpdateThingLocation(thingID, location);

			ThingUserRelation relation = new ThingUserRelation();
			relation.setUserId(thingInfo.getCreateBy());
			relation.setThingId(thingID);
			thingUserRelationDao.saveOrUpdate(relation);
		}

		if (null != AuthInfoStore.getTeamID()) {
			teamThingRelationDao.saveOrUpdate(new TeamThingRelation(AuthInfoStore.getTeamID(), thingID));
		}
		return thingID;
	}

	public List<String> getThingTypesOfAccessibleThingsByTagIds(String userId, Collection<String> tagIDs)
			throws ObjectNotFoundException {
		Set<Long> targetTagIds = tagIDs.stream().filter(Pattern.compile("^[0-9]+$").asPredicate()).
				map(Long::valueOf).collect(Collectors.toSet());
		List<Long> accessibleTagIds = tagUserRelationDao.findAccessibleTagIds(userId, targetTagIds).
				orElse(Collections.emptyList());
		if (!accessibleTagIds.stream().map(Object::toString).collect(Collectors.toSet()).containsAll(tagIDs)) {
			tagIDs.stream().collect(Collectors.toSet()).removeAll(accessibleTagIds.stream().
					map(Object::toString).collect(Collectors.toSet()));
			throw EntryNotFoundException.existsNullTag(tagIDs);
		}

		StringBuilder sb = new StringBuilder();
		for (String tagID : tagIDs) {
			if (0 != sb.length()) {
				sb.append(",");
			}
			sb.append(tagID);
		}

		List<String> result = new ArrayList();
		if (sb.length() > 0) {
			List<Map<String, Object>> typeList = globalThingDao.findThingTypeBytagIDs(sb.toString());
			if (typeList.size() > 0) {
				typeList.forEach(map -> {
					result.add(map.get("type").toString());
				});
			}
		}
		return result;
	}

	public void bindTagsToThings(List<Long> tagIds, List<Long> thingIds) {
		tagIds.forEach(tagId -> {
			thingIds.forEach(thingId -> {
				TagThingRelation relation = tagThingRelationDao.findByThingIDAndTagID(thingId, tagId);
				if (null == relation) {
					tagThingRelationDao.insert(new TagThingRelation(tagId, thingId));
				}
			});
		});
	}

	public void bindTeamToThing(Collection<String> teamIDs, Collection<String> thingIDs) {
		List<Team> teamList = this.findTeamList(teamIDs);

		for (String thingID : thingIDs) {
			GlobalThingInfo thing = globalThingDao.findByID(thingID);
			if (thing == null) {
				log.warn("Thing is null, ThingId = " + thingID);
			} else {
				for (Team team : teamList) {
					TeamThingRelation ttr = teamThingRelationDao.findByTeamIDAndThingID(team.getId(), thing.getId());
					if (ttr == null) {
						teamThingRelationDao.insert(new TeamThingRelation(team.getId(), thing.getId()));
					}
				}
			}
		}
	}

	public void bindTagsToUserGroups(List<Long> tagIds, List<Long> userGroupIds) {
		tagIds.forEach(tagId -> userGroupIds.forEach(groupId -> {
			TagGroupRelation relation = tagGroupRelationDao.findByTagIDAndUserGroupID(tagId, groupId);
			if (null == relation) {
				tagGroupRelationDao.insert(new TagGroupRelation(tagId, groupId, "1"));
			}
		}));
	}

	public void unbindTagsFromThings(List<Long> tagIds, List<Long> thingIds) {
		tagIds.forEach(tagId -> {
			thingIds.forEach(thingId -> {
				tagThingRelationDao.delete(tagId, thingId);
			});
		});
	}

	public void unbindTagsFromUserGroups(List<Long> tagsId, List<Long> userGroupIds) {
		tagsId.forEach(tagId -> userGroupIds.forEach(groupId -> tagGroupRelationDao.delete(tagId, groupId)));
	}

	public void unbindTeamToThing(Collection<String> teamIDs, Collection<String> thingIDs) {
		List<Team> teamList = this.findTeamList(teamIDs);

		for (String thingID : thingIDs) {
			GlobalThingInfo thing = globalThingDao.findByID(thingID);
			if (thing == null) {
				log.warn("Thing is null, ThingId = " + thingID);
			} else {
				for (Team team : teamList) {
					teamThingRelationDao.delete(team.getId(), thing.getId());
				}
			}
		}
	}

	public void removeTag(Long tagId) {
		tagIndexDao.deleteByID(tagId);
	}

	public void removeThing(GlobalThingInfo thing) throws ObjectNotFoundException {
		globalThingDao.deleteByID(thing.getId());

		// remove thing from Kii Cloud
		this.removeThingFromKiiCloud(thing);
	}

	/**
	 * remove thing from Kii Cloud
	 *
	 * @param thingInfo
	 */
	private void removeThingFromKiiCloud(GlobalThingInfo thingInfo) throws ObjectNotFoundException {

		log.debug("removeThingFromKiiCloud: " + thingInfo);

		String fullKiiThingID = thingInfo.getFullKiiThingID();

		// if thing not onboarding yet
		if (Strings.isBlank(fullKiiThingID)) {
			return;
		}

		try {
			thingIFInAppService.removeThing(fullKiiThingID);
		} catch (com.kii.extension.sdk.exception.ObjectNotFoundException e) {
			throw  EntryNotFoundException.thingNotFound(fullKiiThingID);
		}
	}

	public List<String> findLocations(String parentLocation) {

		return tagIndexDao.findLocations(parentLocation);

	}

	public List<TagIndex> getAccessibleTagsByUserIdAndLocations(String userId, String parentLocation) {
		// user -> tag
		Set<Long> tagIds = new HashSet(tagUserRelationDao.findTagIds(userId).orElse(Collections.emptyList()));
		tagIds.addAll(tagGroupRelationDao.findTagIdsByUserId(userId).orElse(Collections.emptyList()));
		return tagIndexDao.findTagsByTagIdsAndLocations(tagIds, parentLocation).orElse(Collections.emptyList());

	}

	public Long createTag(TagIndex tag) {
		Long tagID = tagIndexDao.saveOrUpdate(tag);

		if (AuthInfoStore.getTeamID() != null) {
			teamTagRelationDao.saveOrUpdate(new TeamTagRelation(AuthInfoStore.getTeamID(), tagID));
		}

		tagUserRelationDao.saveOrUpdate(new TagUserRelation(tagID, AuthInfoStore.getUserID()));

		return tagID;
	}


	/**
	 * save the thing-location relation
	 * - if location not existing, create it
	 * - if thing doesn't have location, create the thing-location relation
	 * - if thing already has location, update the thing-location relation (only one location is allowed for one thing)
	 *
	 * @param globalThingID
	 * @param location
	 */
	private void saveOrUpdateThingLocation(Long globalThingID, String location) throws UnauthorizedException {

		// get location tag
		List<TagIndex> list = tagIndexDao.findTagByTagTypeAndName(TagType.Location.toString(), location);
		Long tagId;
		if (null == list || list.isEmpty()) {
			tagId = this.createTag(new TagIndex(TagType.Location, location, null));
		} else {
			if (!list.get(0).getCreateBy().equals(AuthInfoStore.getUserID())) {
				throw new UnauthorizedException(UnauthorizedException.NOT_TAG_CREATER);
			}
			tagId = list.get(0).getId();
		}

		// get tag-thing relation
		if (null == tagThingRelationDao.findByThingIDAndTagID(globalThingID, tagId)) {
			tagThingRelationDao.insert(new TagThingRelation(tagId, globalThingID));
		}

	}

	public List<GlobalThingInfo> getThingsByVendorThingIds(Collection<String> vendorThingIds) {
		return globalThingDao.getThingsByVendorIDArray(vendorThingIds).orElse(Collections.emptyList());
	}

	public List<TagIndex> findTagIndexByGlobalThingID(Long globalThingID) {
		return Optional.ofNullable(tagIndexDao.findTagByGlobalThingID(globalThingID)).orElse(Collections.emptyList());
	}

	public boolean isTagOwner(TagIndex tag) {
		TagUserRelation tur = tagUserRelationDao.find(tag.getId(), AuthInfoStore.getUserID());
		if (tur != null) {
			return true;
		} else {
			List<UserGroup> userGroupList = userGroupDao.findUserGroup(AuthInfoStore.getUserID(), null, null);
			for (UserGroup ug : userGroupList) {
				TagGroupRelation tgr = tagGroupRelationDao.findByTagIDAndUserGroupID(tag.getId(), ug.getId());
				if (tgr != null) return true;
			}
			return false;
		}
	}

	public boolean isTagCreator(TagIndex tag) {
		if (tag.getCreateBy().equals(AuthInfoStore.getUserID())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isTagCreator(Collection<TagIndex> tags) {
		for (TagIndex tag : tags) {
			if (!isTagCreator(tag))
				return false;
		}
		return true;
	}

	public boolean isThingCreator(GlobalThingInfo thing) {
		if (thing.getCreateBy().equals(AuthInfoStore.getUserID())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isThingCreator(Collection<GlobalThingInfo> things) {
		for (GlobalThingInfo thing : things) {
			if (!isThingCreator(thing)) {
				return false;
			}
		}
		return true;
	}

	public boolean isThingOwner(GlobalThingInfo thing) {
		ThingUserRelation tur = thingUserRelationDao.find(thing.getId(), AuthInfoStore.getUserID());
		if (tur != null) {
			return true;
		} else {
			List<UserGroup> userGroupList = userGroupDao.findUserGroup(AuthInfoStore.getUserID(), null, null);
			for (UserGroup ug : userGroupList) {
				ThingUserGroupRelation tgr = thingUserGroupRelationDao.find(thing.getId(), ug.getId());
				if (tgr != null) return true;
			}
			return false;
		}
	}

	private List<Team> findTeamList(Collection<String> teamIDs) {
		List<Team> teamList = new ArrayList<Team>();
		for (String teamID : teamIDs) {
			Team team = teamDao.findByID(teamID);
			if (team != null) {
				teamList.add(team);
			} else {
				log.warn("Team is null, TeamId = " + teamID);
			}
		}
		return teamList;
	}

	public void bindTagsToUsers(Collection<Long> tagIds, Collection<String> userIds) {
		userIds.forEach(userId -> tagIds.forEach(tagId -> {
			TagUserRelation relation = tagUserRelationDao.find(tagId, userId);
			if (null == relation) {
				tagUserRelationDao.insert(new TagUserRelation(tagId, userId));
			}
		}));
	}

	public void unbindTagsFromUsers(Collection<Long> tagIds, Collection<String> userIds) {
		userIds.forEach(userId -> tagIds.forEach(tagId -> tagUserRelationDao.deleteByTagIdAndUserId(tagId, userId)));
	}

	public void bindThingsToUsers(Collection<Long> thingIds, Collection<String> userIds) {
		thingIds.forEach(thingId -> userIds.forEach(userId -> {
			ThingUserRelation relation = thingUserRelationDao.find(thingId, userId);
			if (null == relation) {
				relation = new ThingUserRelation();
				relation.setThingId(thingId);
				relation.setUserId(userId);
				thingUserRelationDao.insert(relation);
			}
		}));
	}


	public void unbindThingsFromUsers(Collection<Long> thingIds, Collection<String> userIds) {
		thingIds.forEach(thingId -> userIds.forEach(userId ->
				thingUserRelationDao.deleteByThingIdAndUserId(thingId, userId)));
	}

	public void bindThingsToUserGroups(Collection<Long> thingIds, Collection<Long> userGroupIds) {
		thingIds.forEach(thingId -> userGroupIds.forEach(groupId -> {
			ThingUserGroupRelation relation = thingUserGroupRelationDao.find(thingId, groupId);
			if (null == relation) {
				thingUserGroupRelationDao.insert(new ThingUserGroupRelation(thingId, groupId));
			}
		}));
	}

	public void unbindThingsFromUserGroups(Collection<Long> thingIds, Collection<Long> userGroupIds) {
		thingIds.forEach(thingId -> userGroupIds.forEach(groupId ->
				thingUserGroupRelationDao.deleteByThingIdAndUserGroupId(thingId, groupId)));
	}

	public List<GlobalThingInfo> getThingsByIds(List<Long> thingIds) throws ObjectNotFoundException {
		Set<Long> idSet = new HashSet(thingIds);
		List<GlobalThingInfo> things = globalThingDao.findByIDs(thingIds);
		if (idSet.size() != things.size()) {
			things.forEach(thing -> idSet.remove(thing.getId()));
			throw EntryNotFoundException.thingNotFound(idSet);
		}
		if (null == things || things.isEmpty()) {
			throw EntryNotFoundException.thingNotFound(idSet);
		}
		return things;
	}

	public List<GlobalThingInfo> getThingsByIdStrings(List<String> thingIDList) throws ObjectNotFoundException {
		Set<String> idSet = new HashSet(thingIDList);
		List<Long> thingIds = thingIDList.stream().filter(Pattern.compile("^[0-9]+$").asPredicate()).map(Long::valueOf)
				.collect(Collectors.toList());
		if (idSet.size() != thingIds.size()) {
			thingIds.forEach(id -> idSet.remove(id.toString()));
			throw EntryNotFoundException.thingNotFound(idSet);
		}
		return getThingsByIds(thingIds);
	}

	public List<TagIndex> getTagIndexes(List<String> tagIDList) throws ObjectNotFoundException {
		List<Long> tagIds = tagIDList.stream().filter(Pattern.compile("^[0-9]+$").asPredicate()).map(Long::valueOf)
				.collect(Collectors.toList());
		List<TagIndex> tagIndexes = tagIndexDao.findByIDs(tagIds);
		if (null == tagIndexes || !tagIndexes.stream().map(TagIndex::getId).map(Object::toString).
				collect(Collectors.toSet()).containsAll(tagIDList)) {
			tagIds.removeAll(tagIndexes.stream().map(TagIndex::getId).collect(Collectors.toList()));
			throw EntryNotFoundException.existsNullTag(tagIds);
		}
		return tagIndexes;
	}

	public List<TagIndex> getTagIndexes(Collection<String> displayNames, TagType tagType) throws
			ObjectNotFoundException {
		List<TagIndex> tags = new ArrayList();
		for (String name : displayNames) {
			List<TagIndex> list = tagIndexDao.findTagByTagTypeAndName(tagType.name(), name);
			if (null == list || list.isEmpty()) {
				throw EntryNotFoundException.tagNameNotFound(name);

			}
			tags.add(CollectUtils.getFirst(list));
		}
		return tags;
	}

	public List<TagIndex> getTagFullNameIndexes(List<String> fullNames) throws
			ObjectNotFoundException, UnauthorizedException {
		List<TagIndex> tags = new ArrayList();
		for (String fullName : fullNames) {
			List<TagIndex> list = tagIndexDao.findTagByFullTagName(fullName);
			if (null == list || list.isEmpty()) {
				throw EntryNotFoundException.tagNameNotFound(fullName);
			} else if (!isTagCreator(CollectUtils.getFirst(list)) && !isTagOwner(CollectUtils.getFirst(list))) {
				throw new UnauthorizedException("NOT_TAG_CREATER");
			}
			tags.add(CollectUtils.getFirst(list));
		}
		return tags;
	}

	public List<BeehiveUser> getUsers(List<String> userIDList) throws ObjectNotFoundException {
		List<BeehiveUser> users = userDao.getUserByIDs(userIDList);
		if (null == users || !users.stream().map(BeehiveUser::getId).collect(Collectors.toSet())
				.containsAll(userIDList)) {
			userIDList.removeAll(users.stream().map(BeehiveUser::getId).collect(Collectors.toList()));
			throw EntryNotFoundException.userNotFound(userIDList);
		}
		return users;
	}

	public List<UserGroup> getUserGroupsByIds(List<Long> userGroupIds) throws ObjectNotFoundException {
		if (null == userGroupIds || userGroupIds.isEmpty()) {
			return Collections.emptyList();
		}
		List<UserGroup> userGroups = userGroupDao.findByIDs(userGroupIds);
		if (null == userGroups || !userGroups.stream().map(UserGroup::getId).collect(Collectors.toSet()).
				containsAll(userGroupIds)) {
			userGroupIds.removeAll(userGroups.stream().map(UserGroup::getId).collect(Collectors.toList()));
			throw  EntryNotFoundException.userGroupNotFound(userGroupIds);
		}
		return userGroups;
	}

	public List<Long> getUserGroupIds(List<String> userGroupIDList) throws ObjectNotFoundException {
		Set<Long> idSet = userGroupIDList.stream().filter(Pattern.compile("^[0-9]+$").asPredicate())
				.map(Long::valueOf).collect(Collectors.toSet());
		List<Long> userGroupIds = userGroupDao.findUserGroupIds(idSet).orElse(Collections.emptyList());
		if (idSet.size() != userGroupIds.size()) {
			idSet.removeAll(userGroupIds);
				throw  EntryNotFoundException.userGroupNotFound(userGroupIds);

		}
		return userGroupIds;
	}

	public List<GlobalThingInfo> getAccessibleThingsByType(String thingType, String user) {
		return globalThingDao.findByIDsAndType(getAccessibleThingIds(user), thingType).orElse(Collections.emptyList());
	}

	public List<Map<String, Object>> getTypesOfAccessibleThingsWithCount(String user) {
		return globalThingDao.findThingTypesWithThingCount(getAccessibleThingIds(user)).orElse(Collections.emptyList());
	}

	public List<String> getTypesOfAccessibleThingsByTagFullName(String user, Set<String> fullTagNames)
			 {
		List<Long> tagIds = tagUserRelationDao.findTagIds(user).orElse(Collections.emptyList());
		tagIds = tagIndexDao.findTagIdsByIDsAndFullname(tagIds, fullTagNames).orElse(Collections.emptyList());
		if (tagIds.size() != fullTagNames.size()) {
			throw EntryNotFoundException.existsNullTag(fullTagNames);
		}
		return globalThingDao.findByIDs(tagThingRelationDao.findThingIds(tagIds).orElse(Collections.emptyList())).
				stream().map(GlobalThingInfo::getType).collect(Collectors.toList());
	}

	private Set<Long> getAccessibleThingIds(String userId) {
		Set<Long> thingIds = new HashSet();
		// user -> user group -> thing
		thingIds.addAll(thingUserGroupRelationDao.findThingIds(groupUserRelationDao.findUserGroupIds(userId).
				orElse(Collections.emptyList())).orElse(Collections.emptyList()));
		// user -> thing
		thingIds.addAll(thingUserRelationDao.findThingIds(userId).orElse(Collections.emptyList()));
		// user -> tag -> thing
		thingIds.addAll(tagThingRelationDao.findThingIds(tagUserRelationDao.findTagIds(userId).
				orElse(Collections.emptyList())).orElse(Collections.emptyList()));
		return thingIds;
	}

	public GlobalThingInfo getAccessibleThingById(String userId, Long thingId) throws ObjectNotFoundException {
		if (null != thingUserRelationDao.find(thingId, userId) ||
				!thingUserGroupRelationDao.findByThingIdAndUserId(thingId, userId).isEmpty()) {
			GlobalThingInfo thingInfo = globalThingDao.findByID(thingId);
			if (null != thingInfo) {
				return thingInfo;
			}
		}

		throw EntryNotFoundException.thingNotFound(thingId);
	}

	public List<TagIndex> getAccessibleTagsByTagTypeAndName(String userId, String tagType, String displayName) {
		List<Long> tagIds1 = tagUserRelationDao.findTagIds(userId, tagType, displayName).
				orElse(Collections.emptyList());
		List<Long> tagIds2 = tagGroupRelationDao.findTagIdsByUserId(userId, tagType, displayName).
				orElse(Collections.emptyList());
		List<Long> allIds = new ArrayList(tagIds1);
		allIds.addAll(tagIds2);
		return tagIndexDao.findByIDs(allIds);
	}

	public List<Long> getCreatedTagIdsByTypeAndDisplayNames(String userId, TagType type, List<String> displayNames)
			throws ObjectNotFoundException {
		List<Long> result = tagIndexDao.getCreatedTagIdsByTypeAndDisplayNames(userId, type, displayNames).
				orElse(Collections.emptyList());
		if (result.isEmpty()) {
			throw EntryNotFoundException.userIDNotFound(userId);
		}
		return result;
	}

	public List<Long> getCreatedTagIdsByFullTagName(String userId, String fullTagNames)
			throws ObjectNotFoundException {
		List<String> fullTagNameList = Arrays.asList(fullTagNames.split(","));
		List<Long> result = tagIndexDao.findTagIdsByCreatorAndFullTagNames(userId, fullTagNameList).
				orElse(Collections.emptyList());
		if (result.isEmpty()) {
			throw EntryNotFoundException.userIDNotFound(userId);
		}
		return result;
	}

	public List<Long> getCreatedThingIds(String userId, List<Long> thingIds)
			throws ObjectNotFoundException {
		List<Long> result = globalThingDao.findThingIdsByCreator(userId, thingIds).orElse(Collections.emptyList());
		if (result.isEmpty()) {
			throw EntryNotFoundException.userIDNotFound(userId);
		}
		return result;
	}

	public List<TagIndex> getAccessibleTagsByFullTagName(String userId, String fullTagNames)
			throws ObjectNotFoundException {
		List<String> fullTagNameList = Arrays.asList(fullTagNames.split(","));
		List<Long> tagIds1 = tagUserRelationDao.findTagIds(userId, fullTagNameList).
				orElse(Collections.emptyList());
		List<Long> tagIds2 = tagGroupRelationDao.findTagIdsByUserIdAndFullTagName(userId, fullTagNameList).
				orElse(Collections.emptyList());
		List<Long> allIds = new ArrayList(tagIds1);
		allIds.addAll(tagIds2);
		if (allIds.isEmpty()) {
			throw EntryNotFoundException.userIDNotFound(userId);
		}
		return tagIndexDao.findByIDs(allIds);
	}

	public List<GlobalThingInfo> getThingsByTagIds(Set<Long> tagIds) {
		if (null == tagIds || tagIds.isEmpty()) {
			return Collections.emptyList();
		}
		return globalThingDao.findByIDs(tagThingRelationDao.findThingIds(tagIds).
				orElse(Collections.emptyList()));
	}

	public List<BeehiveUser> getUsersOfAccessibleThing(String userId, Long thingId) throws ObjectNotFoundException {
		getAccessibleThingById(userId, thingId);
		List<Long> tagIds = tagThingRelationDao.findTagIds(thingId).orElse(Collections.emptyList());
		Set<Long> groupIds = new HashSet(tagGroupRelationDao.findUserGroupIdsByTagIds(tagIds).
				orElse(Collections.emptyList()));
		groupIds.addAll(thingUserGroupRelationDao.findUserGroupIds(thingId).orElse(Collections.emptyList()));
		Set<String> users = groupUserRelationDao.findUserIds(groupIds).orElse(Collections.emptyList()).stream().
				collect(Collectors.toSet());
		users.addAll(thingUserRelationDao.findUserIds(thingId));
		users.addAll(tagUserRelationDao.findUserIds(tagIds).orElse(Collections.emptyList()));
		return userDao.getUserByIDs(users);
	}

	public List<UserGroup> getUserGroupsOfAccessibleThing(String userId, Long thingId) throws ObjectNotFoundException {
		getAccessibleThingById(userId, thingId);
		List<Long> tagIds = tagThingRelationDao.findTagIds(thingId).orElse(Collections.emptyList());
		Set<Long> groupIds = new HashSet(tagGroupRelationDao.findUserGroupIdsByTagIds(tagIds).
				orElse(Collections.emptyList()));
		groupIds.addAll(thingUserGroupRelationDao.findUserGroupIds(thingId).orElse(Collections.emptyList()));
		return userGroupDao.findByIDs(groupIds.stream().collect(Collectors.toList()));
	}

	public List<String> getUsersOfAccessibleTags(String userId, String fullTagName) {
		List<Long> tagIds = tagUserRelationDao.findTagIds(userId, Arrays.asList(fullTagName)).
				orElse(Collections.emptyList());
		return tagUserRelationDao.findUserIds(tagIds).orElse(Collections.emptyList());
	}

	public List<Long> getUserGroupsOfAccessibleTags(String userId, String fullTagName) {
		List<Long> tagIds = tagUserRelationDao.findTagIds(userId, Arrays.asList(fullTagName)).
				orElse(Collections.emptyList());
		return tagGroupRelationDao.findUserGroupIdsByTagIds(tagIds).orElse(Collections.emptyList());
	}

	public List<GlobalThingInfo> getAccessibleThingsByUserId(String userId) {
		return globalThingDao.findByIDs(getAccessibleThingIds(userId));
	}

	public List<GlobalThingInfo> getAccessibleThingsByUserGroupId(Long userGroupId) {
		GroupUserRelation gur = groupUserRelationDao.findByUserIDAndUserGroupID(AuthInfoStore.getUserID(), userGroupId);
		if (gur == null) {
			return null;
		}
		List<Long> tagIds = tagGroupRelationDao.findTagIdsByUserGroupId(userGroupId).orElse(Collections.emptyList());
		Set<Long> thingIds = tagThingRelationDao.findThingIds(tagIds).orElse(Collections.emptyList()).stream().
				collect(Collectors.toSet());
		thingIds.addAll(thingUserGroupRelationDao.findThingIds(userGroupId).orElse(Collections.emptyList()));
		return globalThingDao.findByIDs(thingIds);
	}

	public List<TagIndex> getAccessibleTagsByUserId(String userId) {
		Set<Long> tagIds = tagGroupRelationDao.findTagIdsByUserId(userId).orElse(Collections.emptyList()).stream().
				collect(Collectors.toSet());
		tagIds.addAll(tagUserRelationDao.findTagIds(userId).orElse(Collections.emptyList()));
		return tagIndexDao.findByIDs(tagIds);
	}

	public List<TagIndex> getAccessibleTagsByUserGroupId(Long userGroupId) {
		GroupUserRelation gur = groupUserRelationDao.findByUserIDAndUserGroupID(AuthInfoStore.getUserID(), userGroupId);
		if (gur == null) {
			return null;
		}
		return tagIndexDao.findByIDs(
				tagGroupRelationDao.findTagIdsByUserGroupId(userGroupId).orElse(Collections.emptyList()));
	}

	public boolean isTagDisplayNamePresent(Long teamId, TagType type, String displayName) {
		return !tagIndexDao.findTagIdsByTeamAndTagTypeAndName(teamId, type, displayName).
				orElse(Collections.emptyList()).isEmpty();
	}
}
