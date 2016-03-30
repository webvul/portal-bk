package com.kii.beehive.business.manager;

import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.CollectUtils;
import com.kii.beehive.portal.exception.ObjectNotFoundException;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.jdbc.dao.*;
import com.kii.beehive.portal.jdbc.entity.*;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.service.BeehiveUserDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.store.entity.KiiAppInfo;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.kii.beehive.portal.common.utils.CollectUtils.collectionToString;

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
			throw new ObjectNotFoundException("AppID doesn't exist");
		}

		// check whether Kii App ID is Master App
		if (kiiAppInfo.getMasterApp()) {
			throw new UnauthorizedException("Can't use Master AppID to create thing");
		}


		long thingID = globalThingDao.saveOrUpdate(thingInfo);

		// set location tag and location tag-thing relation
		if (Strings.isBlank(location)) {
			location = DEFAULT_LOCATION;
		}
		this.saveOrUpdateThingLocation(thingID, location);

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
			throw new ObjectNotFoundException("Requested tag doesn't exist or is not accessible. TagIds: " +
					collectionToString(targetTagIds));
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

	public void bindTagsToThings(List<TagIndex> tags, List<GlobalThingInfo> things) throws UnauthorizedException {
		if (!isTagCreator(tags)) {
			throw new UnauthorizedException("Current user is not the creator of the tag(s).");
		}
		if (!isThingCreator(things)) {
			throw new UnauthorizedException("Current user is not the creator of the thing(s).");
		}
		tags.forEach(tagIndex -> {
			things.forEach(thing -> {
				TagThingRelation relation = tagThingRelationDao.findByThingIDAndTagID(thing.getId(), tagIndex.getId());
				if (null == relation) {
					tagThingRelationDao.insert(new TagThingRelation(tagIndex.getId(), thing.getId()));
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

	public void bindTagsToUserGroups(List<TagIndex> tags, List<UserGroup> userGroups) throws UnauthorizedException {
		if (!isTagCreator(tags)) {
			throw new UnauthorizedException("Current user is not the creator of the tag.");
		}
		if (null != userGroups) {
			userGroups.forEach(userGroup -> {
				tags.forEach(tagIndex -> {
					TagGroupRelation relation = tagGroupRelationDao.findByTagIDAndUserGroupID(tagIndex.getId(),
							userGroup.getId());
					if (null == relation) {
						tagGroupRelationDao.insert(new TagGroupRelation(tagIndex.getId(), userGroup.getId(), "1"));
					}
				});
			});
		}
	}

	public void unbindThingsFromTags(List<TagIndex> tags, List<GlobalThingInfo> things) throws UnauthorizedException {
		if (!isTagCreator(tags)) {
			throw new UnauthorizedException("Current user is not the creator of the tag(s).");
		}
		if (!isThingCreator(things)) {
			throw new UnauthorizedException("Current user is not the creator of the thing(s).");
		}
		tags.forEach(tag -> {
			things.forEach(thing -> {
				tagThingRelationDao.delete(tag.getId(), thing.getId());
			});
		});
	}

	public void unbindTagsFromUserGroups(List<TagIndex> tags, List<UserGroup> userGroups) throws UnauthorizedException {
		if (!isTagCreator(tags)) {
			throw new UnauthorizedException("Current user is not the creator of the tag.");
		}
		if (null != userGroups) {
			userGroups.forEach(userGroup -> {
				tags.forEach(tagIndex -> tagGroupRelationDao.delete(tagIndex.getId(), userGroup.getId()));
			});
		}
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

	public void removeTag(TagIndex tag) {
		tagUserRelationDao.deleteByTagId(tag.getId());
		tagGroupRelationDao.delete(tag.getId(), null);
		tagThingRelationDao.delete(tag.getId(), null);
		tagIndexDao.deleteByID(tag.getId());
	}

	public void removeThing(GlobalThingInfo thing) throws ObjectNotFoundException {
		tagThingRelationDao.delete(null, thing.getId());
		thingUserRelationDao.deleteByThingId(thing.getId());
		thingUserGroupRelationDao.deleteByThingId(thing.getId());
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
			throw new ObjectNotFoundException("not found in Kii Cloud, full kii thing id: " + fullKiiThingID);
		}
	}

	public List<String> findLocations(String parentLocation) {

		return tagIndexDao.findLocations(parentLocation);

	}

	public List<String> findUserLocations(String userId, String parentLocation) {

		return tagIndexDao.findUserLocations(userId, parentLocation);

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
	private void saveOrUpdateThingLocation(Long globalThingID, String location) {

		// get location tag
		List<TagIndex> list = tagIndexDao.findTagByTagTypeAndName(TagType.Location.toString(), location);
		Long tagId;
		if (null == list || list.isEmpty()) {
			tagId = tagIndexDao.saveOrUpdate(new TagIndex(TagType.Location, location, null));
		} else {
			tagId = list.get(0).getId();
		}

		// get tag-thing relation
		if (null == tagThingRelationDao.findByThingIDAndTagID(globalThingID, tagId)) {
			tagThingRelationDao.insert(new TagThingRelation(tagId, globalThingID));
		}

	}

	public GlobalThingInfo findThingByVendorThingID(String vendorThingID) {
		List<GlobalThingInfo> list = globalThingDao.findBySingleField(GlobalThingInfo.VANDOR_THING_ID, vendorThingID);
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.get(0);
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

	public void bindTagsToUsers(List<TagIndex> tags, List<BeehiveUser> users) throws UnauthorizedException {
		if (!isTagCreator(tags)) {
			throw new UnauthorizedException("Current user is not the creator of the tag.");
		}
		if (null != users) {
			users.forEach(user -> {
				tags.forEach(tagIndex -> {
					TagUserRelation relation = tagUserRelationDao.find(tagIndex.getId(), user.getKiiLoginName());
					if (null == relation) {
						tagUserRelationDao.insert(new TagUserRelation(tagIndex.getId(), user.getKiiLoginName()));
					}
				});
			});
		}
	}

	public void unbindTagsFromUsers(List<TagIndex> tags, List<BeehiveUser> users) throws UnauthorizedException {
		if (!isTagCreator(tags)) {
			throw new UnauthorizedException("Current user is not the creator of the tag.");
		}
		if (null != users) {
			users.forEach(user -> {
				tags.forEach(tagIndex -> tagUserRelationDao.deleteByTagIdAndUserId(tagIndex.getId(),
						user.getKiiLoginName()));
			});
		}
	}

	public void bindThingsToUsers(List<GlobalThingInfo> things, List<BeehiveUser> users) throws UnauthorizedException {
		if (!isThingCreator(things)) {
			throw new UnauthorizedException("Current user is not the creator of the thing(s).");
		}
		if (null != users) {
			users.forEach(user -> {
				things.forEach(thing -> {
					ThingUserRelation relation = thingUserRelationDao.find(thing.getId(), user.getKiiLoginName());
					if (null == relation) {
						relation = new ThingUserRelation();
						relation.setThingId(thing.getId());
						relation.setUserId(user.getKiiLoginName());
						thingUserRelationDao.insert(relation);
					}
				});
			});
		}
	}


	public void unbindThingsFromUsers(List<GlobalThingInfo> things, List<BeehiveUser> users) throws
			UnauthorizedException {
		if (!isThingCreator(things)) {
			throw new UnauthorizedException("Current user is not the creator of the thing(s).");
		}
		if (null != users) {
			users.forEach(user -> {
				things.forEach(thing -> thingUserRelationDao.deleteByThingIdAndUserId(thing.getId(),
						user.getKiiLoginName()));
			});
		}
	}

	public void bindThingsToUserGroups(List<GlobalThingInfo> things, List<UserGroup> userGroups) throws
			UnauthorizedException {
		if (!isThingCreator(things)) {
			throw new UnauthorizedException("Current user is not the creator of thing(s).");
		}
		if (null != userGroups) {
			userGroups.forEach(userGroup -> {
				things.forEach(thing -> {
					ThingUserGroupRelation relation = thingUserGroupRelationDao.find(thing.getId(), userGroup.getId());
					if (null == relation) {
						thingUserGroupRelationDao.insert(new ThingUserGroupRelation(thing.getId(), userGroup.getId()));
					}
				});
			});
		}
	}

	public void unbindThingsFromUserGroups(List<GlobalThingInfo> things, List<UserGroup> userGroups) throws
			UnauthorizedException {
		if (!isThingCreator(things)) {
			throw new UnauthorizedException("Current user is not the creator of thing(s).");
		}
		if (null != userGroups) {
			userGroups.forEach(userGroup -> {
				things.forEach(thing -> thingUserGroupRelationDao.deleteByThingIdAndUserGroupId(thing.getId(),
						userGroup.getId()));
			});
		}
	}

	public List<GlobalThingInfo> getThings(List<String> thingIDList) throws ObjectNotFoundException {
		List<Long> thingIds = thingIDList.stream().filter(Pattern.compile("^[0-9]+$").asPredicate()).map(Long::valueOf)
				.collect(Collectors.toList());
		List<GlobalThingInfo> things = globalThingDao.findByIDs(thingIds);
		if (null == things || !things.stream().map(GlobalThingInfo::getId).map(Object::toString).
				collect(Collectors.toSet()).containsAll(thingIDList)) {
			thingIDList.removeAll(things.stream().map(GlobalThingInfo::getId).map(Object::toString).
					collect(Collectors.toList()));
			throw new ObjectNotFoundException("Invalid thing id(s): [" + collectionToString(thingIDList) +
					"]");
		}
		return things;
	}

	public List<TagIndex> getTagIndexes(List<String> tagIDList) throws ObjectNotFoundException {
		List<Long> tagIds = tagIDList.stream().filter(Pattern.compile("^[0-9]+$").asPredicate()).map(Long::valueOf)
				.collect(Collectors.toList());
		List<TagIndex> tagIndexes = tagIndexDao.findByIDs(tagIds);
		if (null == tagIndexes || !tagIndexes.stream().map(TagIndex::getId).map(Object::toString).
				collect(Collectors.toSet()).containsAll(tagIDList)) {
			tagIds.removeAll(tagIndexes.stream().map(TagIndex::getId).collect(Collectors.toList()));
			throw new ObjectNotFoundException("Invalid tag id(s): [" + collectionToString(tagIds) + "]");
		}
		return tagIndexes;
	}

	public List<TagIndex> getTagIndexes(Collection<String> displayNames, TagType tagType) throws
			ObjectNotFoundException {
		List<TagIndex> tags = new ArrayList();
		for (String name : displayNames) {
			List<TagIndex> list = tagIndexDao.findTagByTagTypeAndName(tagType.name(), name);
			if (null == list || list.isEmpty()) {
				throw new ObjectNotFoundException("Requested tag name " + name + ", type " + tagType +
						" doesn't exist");
			}
			tags.add(CollectUtils.getFirst(list));
		}
		return tags;
	}

	public List<BeehiveUser> getUsers(List<String> userIDList) throws ObjectNotFoundException {
		List<BeehiveUser> users = userDao.getUserByIDs(userIDList);
		if (null == users || !users.stream().map(BeehiveUser::getKiiLoginName).collect(Collectors.toSet())
				.containsAll(userIDList)) {
			userIDList.removeAll(users.stream().map(BeehiveUser::getKiiLoginName).collect(Collectors.toList()));
			throw new ObjectNotFoundException("Invalid user id(s): [" + collectionToString(userIDList) +
					"]");
		}
		return users;
	}

	public List<UserGroup> getUserGroups(List<String> userGroupIDList) throws ObjectNotFoundException {
		List<Long> userGroupIds = userGroupIDList.stream().filter(Pattern.compile("^[0-9]+$").asPredicate())
				.map(Long::valueOf).collect(Collectors.toList());
		List<UserGroup> userGroups = userGroupDao.findByIDs(userGroupIds);
		if (null == userGroups || !userGroups.stream().map(UserGroup::getId).map(Object::toString).
				collect(Collectors.toSet()).containsAll(userGroupIDList)) {
			userGroupIds.removeAll(userGroups.stream().map(UserGroup::getId).collect(Collectors.toList()));
			throw new ObjectNotFoundException("Invalid user group id(s): [" + collectionToString
					(userGroupIds) + "]");
		}
		return userGroups;
	}

	public List<GlobalThingInfo> getAccessibleThingsByType(String thingType, String user) {
		return globalThingDao.findByIDsAndType(getAccessibleThingIds(user), thingType).orElse(Collections.emptyList());
	}

	public List<Map<String, Object>> getTypesOfAccessibleThingsWithCount(String user) {
		return globalThingDao.findThingTypesWithThingCount(getAccessibleThingIds(user)).orElse(Collections.emptyList());
	}

	public List<String> getTypesOfAccessibleThingsByTagFullName(String user, Set<String> fullTagNames)
			throws ObjectNotFoundException {
		List<Long> tagIds = tagUserRelationDao.findTagIds(user).orElse(Collections.emptyList());
		tagIds = tagIndexDao.findTagIdsByIDsAndFullname(tagIds, fullTagNames).orElse(Collections.emptyList());
		if (tagIds.size() != fullTagNames.size()) {
			throw new ObjectNotFoundException("Some requested tags don't exist or are not accessible");
		}
		return globalThingDao.findByIDs(tagThingRelationDao.findThingIds(tagIds).orElse(Collections.emptyList())).
				stream().map(GlobalThingInfo::getType).collect(Collectors.toList());
	}

	private Set<Long> getAccessibleThingIds(String user) {
		Set<Long> thingIds = new HashSet();
		List<UserGroup> userGroupList = userGroupDao.findUserGroup(user, null, null);
		if (null != userGroupList) {
			userGroupList.forEach(userGroup -> thingIds.addAll(thingUserGroupRelationDao.findThingIds(
					userGroup.getId()).orElse(Collections.emptyList())));
		}
		thingIds.addAll(thingUserRelationDao.findThingIds(user).orElse(Collections.emptyList()));
		tagUserRelationDao.findByUserId(user).orElse(Collections.emptyList()).forEach(tagUserRelation ->
				thingIds.addAll(tagThingRelationDao.findThingIds(tagUserRelation.getTagId()).
						orElse(Collections.emptyList())));
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

		throw new ObjectNotFoundException("Requested thing doesn't exist or is not accessible");
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

	public List<TagIndex> getAccessibleTagsByFullTagName(String userId, String fullTagNames) {
		List<String> fullTagNameList = Arrays.asList(fullTagNames.split(","));
		List<Long> tagIds1 = tagUserRelationDao.findTagIds(userId, fullTagNameList).
				orElse(Collections.emptyList());
		List<Long> tagIds2 = tagGroupRelationDao.findTagIds(userId, fullTagNameList).
				orElse(Collections.emptyList());
		List<Long> allIds = new ArrayList(tagIds1);
		allIds.addAll(tagIds2);
		return tagIndexDao.findByIDs(allIds);
	}

	public List<GlobalThingInfo> getThingsByTagIds(Set<Long> tagIds) {
		if (null == tagIds || tagIds.isEmpty()) {
			return Collections.emptyList();
		}
		return globalThingDao.findByIDs(tagThingRelationDao.findThingIds(tagIds).
				orElse(Collections.emptyList()));
	}

	public List<String> getUsersOfThing(Long thingId) {
		List<Long> tagIds = tagThingRelationDao.findTagIds(thingId).orElse(Collections.emptyList());
		Set<Long> groupIds = new HashSet(tagGroupRelationDao.findUserGroupIdsByTagIds(tagIds).
				orElse(Collections.emptyList()));
		groupIds.addAll(thingUserGroupRelationDao.findUserGroupIds(thingId).orElse(Collections.emptyList()));
		Set<String> users = groupUserRelationDao.findUserIds(groupIds).orElse(Collections.emptyList()).stream().
				collect(Collectors.toSet());
		users.addAll(thingUserRelationDao.findUserIds(thingId));
		users.addAll(tagUserRelationDao.findUserIds(tagIds).orElse(Collections.emptyList()));
		return users.stream().collect(Collectors.toList());
	}

	public List<Long> getUserGroupsOfThing(Long thingId) {
		List<Long> tagIds = tagThingRelationDao.findTagIds(thingId).orElse(Collections.emptyList());
		Set<Long> groupIds = new HashSet(tagGroupRelationDao.findUserGroupIdsByTagIds(tagIds).
				orElse(Collections.emptyList()));
		groupIds.addAll(thingUserGroupRelationDao.findUserGroupIds(thingId).orElse(Collections.emptyList()));
		return groupIds.stream().collect(Collectors.toList());
	}

	public List<GlobalThingInfo> getAccessibleThingsByUserId(String userId) {
		return globalThingDao.findByIDs(getAccessibleThingIds(userId));
	}

	public List<GlobalThingInfo> getAccessibleThingsByUserGroupId(Long userGroupId) {
		List<Long> tagIds = tagGroupRelationDao.findTagIdsByUserGroupId(userGroupId).orElse(Collections.emptyList());
		Set<Long> thingIds = tagThingRelationDao.findThingIds(tagIds).orElse(Collections.emptyList()).stream().
				collect(Collectors.toSet());
		thingIds.addAll(thingUserGroupRelationDao.findThingIds(userGroupId).orElse(Collections.emptyList()));
		return globalThingDao.findByIDs(thingIds);
	}
}
