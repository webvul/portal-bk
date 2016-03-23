package com.kii.beehive.business.manager;

import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.CollectUtils;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.exception.ObjectNotFoundException;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    private UserGroupDao usergroupDao;

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


    /**
     * create or update the thing including the location and custom tags
     *
     * @param thingInfo
     * @param location
     * @param tagList
     * @return
     */
    public Long createThing(GlobalThingInfo thingInfo, String location, Collection<String> tagList) {

        KiiAppInfo kiiAppInfo = appInfoDao.getAppInfoByID(thingInfo.getKiiAppID());

        // check whether Kii App ID is existing
        if (kiiAppInfo == null) {
            EntryNotFoundException ex = new EntryNotFoundException(thingInfo.getKiiAppID());
            ex.setMessage("AppID not exist");
            throw ex;
        }

        // check whether Kii App ID is Master App
        if (kiiAppInfo.getMasterApp()) {
            EntryNotFoundException ex = new EntryNotFoundException(thingInfo.getKiiAppID());
            ex.setMessage("Can't user Master AppID");
            throw ex;
        }

		/*Set<TagIndex> tagSet=new HashSet<>();

		tagList.forEach((str)->{
			tagSet.add(TagIndex.generCustomTagIndex(str));
		});*/

        long thingID = globalThingDao.saveOrUpdate(thingInfo);

        // set location tag and location tag-thing relation
        if (Strings.isBlank(location)) {
            location = DEFAULT_LOCATION;
        }
        this.saveOrUpdateThingLocation(thingID, location);

        // set custom tag and custom tag-thing relation
        /*for(TagIndex tag:tagSet){
            if(!Strings.isBlank(tag.getDisplayName())){
				Long tagID = null;
				List<TagIndex>  list = tagIndexDao.findTagByTagTypeAndName(tag.getTagType().toString(), tag.getDisplayName());
				if( list.size() == 0) {
					tagID = tagIndexDao.saveOrUpdate(tag);
				}else{
					tagID = list.get(0).getId();
				}

				eventBus.onTagChangeFire(tag.getFullTagName(), Collections.singletonList(new Long(thingID)),true);
				tagThingRelationDao.saveOrUpdate(new TagThingRelation(tagID,thingID));
			}
		}*/

        return thingID;
    }

    public List<String> findThingTypeByTagIDs(Collection<String> tagIDs) {
        StringBuilder sb = new StringBuilder();
        for (String tagID : tagIDs) {
            List<TagIndex> tagList = this.tagIndexDao.findTag(Long.parseLong(tagID), null, null);
            if (tagList.size() > 0) {
                if (sb.length() > 0) sb.append(",");
                sb.append(tagID);
            }
        }

        List<String> result = new ArrayList<String>();
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

    public void bindTagToThing(List<TagIndex> tags, List<GlobalThingInfo> things) {
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

    public void bindTagToUserGroup(Collection<String> tagIDs, Collection<String> userGroupIDs) {
        List<TagIndex> tagList = this.findTagList(tagIDs);
        List<UserGroup> userGroupList = usergroupDao.findByIDs(userGroupIDs.stream().map(Long::valueOf).
                collect(Collectors.toList()));
        if (null != userGroupList) {
            userGroupList.forEach(userGroup -> {
                tagList.forEach(tagIndex -> {
                    TagGroupRelation relation = tagGroupRelationDao.findByTagIDAndUserGroupID(tagIndex.getId(),
                            userGroup.getId());
                    if (null == relation) {
                        tagGroupRelationDao.insert(new TagGroupRelation(tagIndex.getId(), userGroup.getId(), "1"));
                    }
                });
            });
        }
    }

    public void bindCustomTagToThing(Collection<String> displayNames, Collection<Long> globalThingIDs) {

        List<TagIndex> tagIndexList = this.findCustomTagList(displayNames);

        for (Long globalThingID : globalThingIDs) {
            GlobalThingInfo thing = globalThingDao.findByID(globalThingID);
            if (thing == null) {
                log.warn("Thing is null, ThingId = " + globalThingID);
            } else {
                for (TagIndex tag : tagIndexList) {
                    TagThingRelation ttr = tagThingRelationDao.findByThingIDAndTagID(globalThingID, tag.getId());
                    if (ttr == null) {
                        tagThingRelationDao.insert(new TagThingRelation(tag.getId(), globalThingID));
                    }
                }
            }
        }
    }

    public void unbindTagFromThing(List<TagIndex> tags, List<GlobalThingInfo> things) {
        tags.forEach(tag -> {
            things.forEach(thing -> {
                tagThingRelationDao.delete(tag.getId(), thing.getId());
            });
        });
    }

    public void unbindTagToUserGroup(Collection<String> tagIDs, Collection<String> userGroupIDs) {
        List<TagIndex> tagList = this.findTagList(tagIDs);
        List<UserGroup> userGroupList = usergroupDao.findByIDs(userGroupIDs.stream().map(Long::valueOf).
                collect(Collectors.toList()));
        if (null != userGroupList) {
            userGroupList.forEach(userGroup -> {
                tagList.forEach(tagIndex -> tagGroupRelationDao.delete(tagIndex.getId(), userGroup.getId()));
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

    public void unbindCustomTagToThing(Collection<String> displayNames, Collection<Long> globalThingIDs) {

        List<TagIndex> tagIndexList = this.findCustomTagList(displayNames);

        for (Long globalThingID : globalThingIDs) {
            GlobalThingInfo thing = globalThingDao.findByID(globalThingID);
            if (thing == null) {
                log.warn("Thing is null, ThingId = " + globalThingID);
            } else {
                for (TagIndex tag : tagIndexList) {
                    tagThingRelationDao.delete(tag.getId(), globalThingID);
                }
            }
        }
    }

    public void removeTag(TagIndex tag) {
        tagUserRelationDao.deleteByTagId(tag.getId());
        tagThingRelationDao.delete(tag.getId(), null);
        tagIndexDao.deleteByID(tag.getId());
    }

    public void removeThing(GlobalThingInfo thing) {
        tagThingRelationDao.delete(null, thing.getId());
        globalThingDao.deleteByID(thing.getId());

        // remove thing from Kii Cloud
        this.removeThingFromKiiCloud(thing);
    }

    /**
     * remove thing from Kii Cloud
     *
     * @param thingInfo
     */
    private void removeThingFromKiiCloud(GlobalThingInfo thingInfo) {

        log.debug("removeThingFromKiiCloud: " + thingInfo);

        String fullKiiThingID = thingInfo.getFullKiiThingID();

        // if thing not onboarding yet
        if (Strings.isBlank(fullKiiThingID)) {
            return;
        }

        try {
            thingIFInAppService.removeThing(fullKiiThingID);
        } catch (com.kii.extension.sdk.exception.ObjectNotFoundException e) {
            log.error("not found in Kii Cloud, full kii thing id: " + fullKiiThingID, e);
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
        TagIndex tagIndex = CollectUtils.getFirst(list);
        if (tagIndex == null) {
            tagIndex = new TagIndex(TagType.Location, location, null);
            long tagID = tagIndexDao.saveOrUpdate(tagIndex);
            tagIndex.setId(tagID);
        }

        // get tag-thing relation
        TagThingRelation relation = tagThingRelationDao.findByThingIDAndTagID(globalThingID, tagIndex.getId());

        if (relation == null) {
            relation = new TagThingRelation(tagIndex.getId(), globalThingID);
            tagThingRelationDao.insert(relation);
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

        return tagIndexDao.findTagByGlobalThingID(globalThingID);
    }

    public boolean isTagOwner(TagIndex tag) {
        TagUserRelation tur = tagUserRelationDao.find(tag.getId(), AuthInfoStore.getUserID());
        if (tur != null) {
            return true;
        } else {
            List<UserGroup> userGroupList = usergroupDao.findUserGroup(AuthInfoStore.getUserID(), null, null);
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

    public boolean isThingCreator(GlobalThingInfo thing) {
        if (thing.getCreateBy().equals(AuthInfoStore.getUserID())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isThingOwner(GlobalThingInfo thing) {
        ThingUserRelation tur = thingUserRelationDao.find(thing.getId(), AuthInfoStore.getUserID());
        if (tur != null) {
            return true;
        } else {
            List<UserGroup> userGroupList = usergroupDao.findUserGroup(AuthInfoStore.getUserID(), null, null);
            for (UserGroup ug : userGroupList) {
                ThingUserGroupRelation tgr = thingUserGroupRelationDao.find(thing.getId(), ug.getId());
                if (tgr != null) return true;
            }
            return false;
        }
    }


    private List<TagIndex> findTagList(Collection<String> tagIDs) {
        List<TagIndex> tagList = new ArrayList<TagIndex>();
        for (String tagID : tagIDs) {
            TagIndex tag = tagIndexDao.findByID(tagID);
            if (tag != null) {
                if (this.isTagCreator(tag) || this.isTagOwner(tag)) {
                    tagList.add(tag);
                } else {
                    tagIDs.remove(tagID);
                }
            } else {
                log.warn("Tag is null, TagId = " + tagID);
            }
        }
        return tagList;
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

    private List<TagIndex> findCustomTagList(Collection<String> displayNames) {
        List<TagIndex> tagList = new ArrayList<TagIndex>();
        for (String displayName : displayNames) {
            List<TagIndex> list = tagIndexDao.findTagByTagTypeAndName(TagType.Custom.toString(), displayName);
            TagIndex tag = CollectUtils.getFirst(list);
            if (tag != null) {
                tagList.add(tag);
            } else {
                log.warn("Custom Tag is null, displayName = " + displayName);
            }
        }
        return tagList;
    }

    public void bindTagToUser(List<String> tagIDList, List<String> userIDList) {
        List<TagIndex> tagList = findTagList(tagIDList);
        List<BeehiveUser> users = userDao.getUserByIDs(userIDList);
        if (null != users) {
            users.forEach(user -> {
                tagList.forEach(tagIndex -> {
                    TagUserRelation relation = tagUserRelationDao.find(tagIndex.getId(), user.getKiiLoginName());
                    if (null == relation) {
                        relation = new TagUserRelation();
                        relation.setUserId(user.getKiiLoginName());
                        relation.setTagId(tagIndex.getId());
                        tagUserRelationDao.insert(relation);
                    }
                });
            });
        }
    }

    public void unbindTagFromUser(List<String> tagIDList, List<String> userIDList) {
        List<TagIndex> tagList = findTagList(tagIDList);
        List<BeehiveUser> users = userDao.getUserByIDs(userIDList);
        if (null != users) {
            users.forEach(user -> {
                tagList.forEach(tagIndex -> tagUserRelationDao.deleteByTagIdAndUserId(tagIndex.getId(), user.getId()));
            });
        }
    }

    public List<GlobalThingInfo> getThings(List<String> thingIDList) throws ObjectNotFoundException {
        List<Long> thingIds = thingIDList.stream().filter(Pattern.compile("^[0-9]+$").asPredicate()).map(Long::valueOf)
                .collect(Collectors.toList());
        List<GlobalThingInfo> things = globalThingDao.getThingsByIDArray(thingIds);
        if (null == things || !things.stream().map(GlobalThingInfo::getId).map(Object::toString).
                collect(Collectors.toSet()).containsAll(thingIDList)) {
            thingIds.removeAll(things.stream().map(GlobalThingInfo::getId).collect(Collectors.toList()));
            throw new ObjectNotFoundException("Invalid thing id(s): [" + CollectUtils.collectionToString(thingIds) +
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
            throw new ObjectNotFoundException("Invalid tag id(s): [" + CollectUtils.collectionToString(tagIds) + "]");
        }
        return tagIndexes;
    }
}
