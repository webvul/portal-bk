package com.kii.beehive.portal.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.service.BeehiveUserDao;
import com.kii.beehive.portal.service.BeehiveUserGroupDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.store.entity.BeehiveUserGroup;

@Component
public class UserGroupManager {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BeehiveUserGroupDao beehiveUserGroupDao;

    @Autowired
    private BeehiveUserDao beehiveUserDao;



    @Autowired
    private AppInfoDao appInfoDao;



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
            Set<String> existUserIDs = group.getUsers();
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
            Set<String> existUserIDs = group.getUsers();
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

        // generate user group id
        String userGroupID = DigestUtils.sha1Hex(userGroup.getUserGroupName());
        userGroup.setUserGroupID(userGroupID);

        // check and update user change
        // important:
        // this has to go before the user group update(table BeehiveUserGroup),
        // to get the exist list of users under the user group
        this.checkUsersChange(userGroup.getUserGroupID(), userGroup.getUsers(), party3rdID);

        beehiveUserGroupDao.createUserGroup(userGroup);

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

        // check and update user change
        // important:
        // this has to go before the user group update(table BeehiveUserGroup),
        // to get the exist list of users under the user group
        this.checkUsersChange(userGroup.getUserGroupID(), userGroup.getUsers(), party3rdID);

        beehiveUserGroupDao.updateUserGroup(userGroup.getUserGroupID(), userGroup);

        logger.debug("End updateUserGroup(BeehiveUserGroup userGroup, String party3rdID)");

    }

    /**
     * delete user group in Beehive (table BeehiveUserGroup)
     * if there is user info under the user group, add the group to these user info(table BeehiveUser)
     *
     * @param userGroup
     * @param party3rdID
     */
    public void deleteUserGroup(BeehiveUserGroup userGroup, String party3rdID) {

        logger.debug("Start deleteUserGroup(BeehiveUserGroup userGroup, String party3rdID)");
        logger.debug("userGroup:" + userGroup);
        logger.debug("party3rdID:" + party3rdID);

        // check and update user change
        // important:
        // this has to go before the user group update(table BeehiveUserGroup),
        // to get the exist list of users under the user group
        this.checkUsersChange(userGroup.getUserGroupID(), null, party3rdID);

        beehiveUserGroupDao.deleteUserGroup(userGroup.getUserGroupID());

        logger.debug("End deleteUserGroup(BeehiveUserGroup userGroup, String party3rdID)");

    }

    /**
     * check whether any change on the users under the user group,
     * if there is, update the user info too (table BeehiveUser)
     *
     * @param userGroupID
     * @param userIDs  new set of users under the user group
     * @param party3rdID
     */
    private void checkUsersChange(String userGroupID, Set<String> userIDs, String party3rdID) {

        if (userIDs == null) {
            userIDs = new HashSet<String>();
        }

        // get the exist list of user ids in DB
        BeehiveUserGroup existGroup = beehiveUserGroupDao.getUserGroupByID(userGroupID);
        Set<String> existUserIDs = new HashSet<String>();
        if (existGroup != null && existGroup.getUsers() != null) {
            existUserIDs = existGroup.getUsers();
        }

        if (userIDs.containsAll(existUserIDs) && existUserIDs.containsAll(userIDs)) {
            logger.debug("no change on relation bwtween user and user group:" + userGroupID);
            return;
        }

        // get the users(ID) to remove user group
        Set<String> userIDsToRemoveFromGroup = new HashSet<String>();
        userIDsToRemoveFromGroup.addAll(existUserIDs);
        userIDsToRemoveFromGroup.removeAll(userIDs);

        logger.debug("userIDsToRemoveFromGroup:" + userIDsToRemoveFromGroup);

        // get the users(ID) to add user group
        Set<String> userIDsToAddToGroup = new HashSet<String>();
        userIDsToAddToGroup.addAll(userIDs);
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
