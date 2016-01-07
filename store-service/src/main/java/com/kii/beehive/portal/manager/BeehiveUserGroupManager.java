package com.kii.beehive.portal.manager;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.service.BeehiveUserDao;
import com.kii.beehive.portal.service.BeehiveUserGroupDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.store.entity.BeehiveUserGroup;

@Component
public class BeehiveUserGroupManager {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BeehiveUserGroupDao beehiveUserGroupDao;

    @Autowired
    private BeehiveUserDao beehiveUserDao;

    @Autowired
    private AppInfoDao appInfoDao;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * check whether the param userGroupID existing or not
     * @param userGroupID
     * @return
     */
    public boolean checkUserGroupIDExist(String userGroupID) {
        BeehiveUserGroup userGroup = beehiveUserGroupDao.getUserGroupByID(userGroupID);

        return userGroup != null;
    }

    public boolean checkUserGroupNameExist(String userGroupName) {
        BeehiveUserGroup userGroup = getUserGroupByName(userGroupName);

        return userGroup != null;
    }

    public BeehiveUserGroup getUserGroupByName(String userGroupName) {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("userGroupName", userGroupName);

        List<BeehiveUserGroup> list = beehiveUserGroupDao.getUserGroupsBySimpleQuery(queryMap);
        if(list != null && list.size() > 0) {
            return list.get(0);
        }

        return null;
    }

    /**
     * search user group by simple query
     * @param queryMap
     */
    public BeehiveUserGroup getUserGroupBySimpleQuery(Map<String,Object> queryMap, boolean includeUserData) {

        logger.debug("Start getUserGroupBySimpleQuery(Map<String,Object> queryMap, boolean includeUserData)");
        logger.debug("queryMap:" + queryMap);
        logger.debug("includeUserData:" + includeUserData);

        // get user group
        List<BeehiveUserGroup> groupList = null;
        if(queryMap == null || queryMap.isEmpty()) {
        	groupList =  beehiveUserGroupDao.getAll();
        }else{
        	groupList =  beehiveUserGroupDao.getUserGroupsBySimpleQuery(queryMap);
        }
        

        if(groupList == null || groupList.isEmpty()) {
            return null;
        }

        BeehiveUserGroup userGroup = groupList.get(0);

        // if includeUserData is true, get the user entities and add into user group
        Set<String> userIDs = userGroup.getUsers();
        if(includeUserData && userIDs != null && !userIDs.isEmpty()) {

            // get user entities
            List<String> userIDList = new ArrayList<>();
            userIDs.forEach(userID->{
                userIDList.add((String)userID);
            });

            List<BeehiveUser> userList = beehiveUserDao.getUserByIDs(userIDList);
            userGroup.setBeehiveUserList(userList);
        }

        logger.debug("End getUserGroupBySimpleQuery(Map<String,Object> queryMap, boolean includeUserData): " + userGroup);

        return userGroup;
    }

    /**
     * get all the user groups
     * @return
     */
    public List<BeehiveUserGroup> getUserGroupAll() {
        return beehiveUserGroupDao.getAll();
    }

    /**
     * add users to groups
     * @param beehiveUserIDs
     * @param beehiveUserGroupIDs
     * @param party3rdID
     */
    public void addUsersToGroups(List<String> beehiveUserIDs, List<String> beehiveUserGroupIDs, String party3rdID) {

        logger.debug("Start addUsersToGroups(List<String> beehiveUserIDs, List<String> beehiveUserGroupIDs, String party3rdID)");
        logger.debug("beehiveUserIDs:" + beehiveUserIDs);
        logger.debug("beehiveUserGroupIDs:" + beehiveUserGroupIDs);
        logger.debug("party3rdID:" + party3rdID);

        List<BeehiveUser> users = beehiveUserDao.getUserByIDs(beehiveUserIDs);

        users.stream().forEach((user) -> {
            Set<String> existGroupIDs = user.getGroups();
            existGroupIDs.addAll(beehiveUserGroupIDs);

            beehiveUserDao.updateUserGroups(user.getAliUserID(), existGroupIDs);
        });

        List<BeehiveUserGroup> groups = beehiveUserGroupDao.getUserGroupByIDs(beehiveUserGroupIDs);

        groups.stream().forEach((group) -> {
            Set<String> existUserIDs = group.getUserIDs();
            existUserIDs.addAll(beehiveUserIDs);

            beehiveUserGroupDao.updateUsers(group.getUserGroupID(), existUserIDs);
        });


        logger.debug("End addUsersToGroups(List<String> beehiveUserIDs, List<String> beehiveUserGroupIDs, String party3rdID)");

    }

    /**
     * remove users from groups
     * @param beehiveUserIDs
     * @param beehiveUserGroupIDs
     * @param party3rdID
     */
    public void removeUsersFromGroups(List<String> beehiveUserIDs, List<String> beehiveUserGroupIDs, String party3rdID) {

        logger.debug("Start removeUsersFromGroups(List<String> beehiveUserIDs, List<String> beehiveUserGroupIDs, String party3rdID)");
        logger.debug("beehiveUserIDs:" + beehiveUserIDs);
        logger.debug("beehiveUserGroupIDs:" + beehiveUserGroupIDs);
        logger.debug("party3rdID:" + party3rdID);

        List<BeehiveUser> users = beehiveUserDao.getUserByIDs(beehiveUserIDs);

        users.stream().forEach((user) -> {
            Set<String> existGroupIDs = user.getGroups();
            existGroupIDs.removeAll(beehiveUserGroupIDs);

            beehiveUserDao.updateUserGroups(user.getAliUserID(), existGroupIDs);
        });

        List<BeehiveUserGroup> groups = beehiveUserGroupDao.getUserGroupByIDs(beehiveUserGroupIDs);

        groups.stream().forEach((group) -> {
            Set<String> existUserIDs = group.getUserIDs();
            existUserIDs.removeAll(beehiveUserIDs);

            beehiveUserGroupDao.updateUsers(group.getUserGroupID(), existUserIDs);
        });

        logger.debug("End removeUsersFromGroups(List<String> beehiveUserIDs, List<String> beehiveUserGroupIDs, String party3rdID)");
    }

    /**
     * create user group in Beehive (table BeehiveUserGroup)
     * if there is user info under the user group, add the group to these user info(table BeehiveUser)
     *
     * @param userGroup
     * @param party3rdID
     */
    public String createUserGroup(BeehiveUserGroup userGroup, String party3rdID) {

        logger.debug("Start createUserGroup(BeehiveUserGroup userGroup, String party3rdID)");
        logger.debug("userGroup:" + userGroup);
        logger.debug("party3rdID:" + party3rdID);

        // create user group
        String userGroupID = beehiveUserGroupDao.createUserGroup(userGroup);

        // check and update user
        this.checkUsersChange(userGroupID, null, userGroup.getUserIDs(), party3rdID);

        logger.debug("End createUserGroup(BeehiveUserGroup userGroup, String party3rdID)");
        logger.debug("userGroupID:" + userGroupID);

        return userGroupID;
    }

    /**
     * update user group in Beehive (table BeehiveUserGroup)
     * if any change on the relation between user and user group, update in user info too(table BeehiveUser)
     *
     * @param userGroup
     * @param party3rdID
     */
    public void updateUserGroup(BeehiveUserGroup userGroup, String party3rdID) {

        logger.debug("Start updateUserGroup(BeehiveUserGroup userGroup, String party3rdID)");
        logger.debug("userGroup:" + userGroup);
        logger.debug("party3rdID:" + party3rdID);

        // get the exist list of userIDs under the group
        BeehiveUserGroup existGroup = beehiveUserGroupDao.getUserGroupByID(userGroup.getUserGroupID());
        Set<String> existUserIDs = new HashSet<String>();
        if (existGroup != null && existGroup.getUsers() != null) {
            existUserIDs = existGroup.getUserIDs();
        }

        // check and update user
        this.checkUsersChange(userGroup.getUserGroupID(), existUserIDs, userGroup.getUserIDs(), party3rdID);

        // update user group
        beehiveUserGroupDao.updateUserGroup(userGroup.getUserGroupID(), userGroup);

        logger.debug("End updateUserGroup(BeehiveUserGroup userGroup, String party3rdID)");

    }

    /**
     * delete user group in Beehive (table BeehiveUserGroup)
     * if there is user info under the user group, add the group to these user info(table BeehiveUser)
     *
     * @param userGroupID
     * @param party3rdID
     */
    public void deleteUserGroup(String userGroupID, String party3rdID) {

        logger.debug("Start deleteUserGroup(BeehiveUserGroup userGroup, String party3rdID)");
        logger.debug("userGroup:" + userGroupID);
        logger.debug("party3rdID:" + party3rdID);


        // get the exist list of userIDs under the group
        BeehiveUserGroup existGroup = beehiveUserGroupDao.getUserGroupByID(userGroupID);
        Set<String> existUserIDs = new HashSet<String>();
        if (existGroup != null && existGroup.getUsers() != null) {
            existUserIDs = existGroup.getUserIDs();
        }

        // check and update user change
        this.checkUsersChange(userGroupID, existUserIDs, null, party3rdID);

        // delete user group
        beehiveUserGroupDao.deleteUserGroup(userGroupID);

        logger.debug("End deleteUserGroup(BeehiveUserGroup userGroup, String party3rdID)");

    }

    /**
     * check whether any change on the users under the user group,
     * if there is, update the user info too (table BeehiveUser)
     *
     * @param userGroupID
     * @param existUserIDs the existing set of users under the user group
     * @param newUserIDs  new set of users under the user group
     * @param party3rdID
     */
    private void checkUsersChange(String userGroupID, Set<String> existUserIDs, Set<String> newUserIDs, String party3rdID) {

        if(existUserIDs == null) {
            existUserIDs = new HashSet<String>();
        }

        if (newUserIDs == null) {
            newUserIDs = new HashSet<String>();
        }

        if (newUserIDs.containsAll(existUserIDs) && existUserIDs.containsAll(newUserIDs)) {
            logger.debug("no change on relation bwtween user and user group:" + userGroupID);
            return;
        }

        // get the users(ID) to remove user group
        Set<String> userIDsToRemoveFromGroup = new HashSet<String>();
        userIDsToRemoveFromGroup.addAll(existUserIDs);
        userIDsToRemoveFromGroup.removeAll(newUserIDs);

        logger.debug("userIDsToRemoveFromGroup:" + userIDsToRemoveFromGroup);

        // get the users(ID) to add user group
        Set<String> userIDsToAddToGroup = new HashSet<String>();
        userIDsToAddToGroup.addAll(newUserIDs);
        userIDsToAddToGroup.removeAll(existUserIDs);

        logger.debug("userIDsToAddToGroup:" + userIDsToAddToGroup);

        // get the users(ID) to add or remove user group
        List<String> userIDsToUpdateGroup = new ArrayList<String>();
        userIDsToUpdateGroup.addAll(userIDsToRemoveFromGroup);
        userIDsToUpdateGroup.addAll(userIDsToAddToGroup);

        List<BeehiveUser> beehiveUserList = beehiveUserDao.getUserByIDs(userIDsToUpdateGroup);

        // update the user group info into table BeehiveUser
        beehiveUserList.stream().forEach((beehiveUser) -> {
            String tempId = beehiveUser.getAliUserID();
            Set<String> tempGroups = beehiveUser.getGroups();
            if(tempGroups == null) {
                tempGroups = new HashSet<String>();
            }

            // add or remove the user group from user
            if (userIDsToAddToGroup.contains(tempId)) {
                tempGroups.add(userGroupID);
            } else if (userIDsToRemoveFromGroup.contains(tempId)) {
                tempGroups.remove(userGroupID);
            }

            beehiveUserDao.updateUserGroups(tempId, tempGroups);
        });


    }

}
