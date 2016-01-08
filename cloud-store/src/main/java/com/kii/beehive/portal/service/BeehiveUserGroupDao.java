package com.kii.beehive.portal.service;

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

import com.kii.beehive.business.helper.SimpleQueryTool;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.store.entity.BeehiveUserGroup;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;


@BindAppByName(appName = "portal",appBindSource="propAppBindTool")
@Component
public class BeehiveUserGroupDao extends AbstractDataAccess<BeehiveUserGroup> {

	private Logger logger= LoggerFactory.getLogger(BeehiveUserGroupDao.class);

	@Autowired
	private SimpleQueryTool queryTool;

    public String createUserGroup(BeehiveUserGroup userGroup) {

        return super.addKiiEntity(userGroup);

    }

    public void updateUserGroup(String userGroupID, BeehiveUserGroup userGroup) {

        super.updateEntity(userGroup, userGroupID);

    }

    public void updateUsers(String userGroupID, Set<String> users){

        Map<String,Object> paramMap = new HashMap<String, Object>();
        paramMap.put("users", users);

        super.updateEntity(paramMap, userGroupID);

    }

    public void deleteUserGroup(String userGroupID) {
        super.removeEntity(userGroupID);
    }

    public BeehiveUserGroup getUserGroupByID(String userGroupID) {

        if(!checkExist(userGroupID)) {
            return null;
        }

		return super.getObjectByID(userGroupID);
    }

    public List<BeehiveUserGroup> getUserGroupByIDs(List<String> userGroupIDs) {

        return super.getEntitys(userGroupIDs.toArray(new String[0]));
    }

    public List<BeehiveUserGroup>  getUserGroupsBySimpleQuery(Map<String,Object> params){
        QueryParam query=queryTool.getEntitysByFields(params);

        return super.fullQuery(query);
    }

    @Override
    protected Class<BeehiveUserGroup> getTypeCls() {
        return BeehiveUserGroup.class;
    }

    @Override
    protected BucketInfo getBucketInfo() {
        return new BucketInfo("beehiveUserGroup");
    }



	/**
	 * check whether any change on the user groups under the user,
	 * if there is, update the user group info too (table BeehiveUserGroup)
	 *
	 * @param userID
	 * @param groupIDs  new set of user groups under the user
	 */
	public  void checkUserGroupsChange(BeehiveUser existUser, Set<String> groupIDs) {


//		BeehiveUser existUser = userDao.getUserByID(userID);

		String userID=existUser.getId();

		Set<String> existGroupIDs = new HashSet<String>();
		if (existUser != null && existUser.getGroups() != null) {
			existGroupIDs = existUser.getGroups();
		}

		if (groupIDs.containsAll(existGroupIDs) && existGroupIDs.containsAll(groupIDs)) {
			logger.debug("no change on relation bwtween user group and user:" + existUser.getId());
			return;
		}

		Set<String> groupIDsToRemoveUser = new HashSet<String>();
		groupIDsToRemoveUser.addAll(existGroupIDs);
		groupIDsToRemoveUser.removeAll(groupIDs);

		logger.debug("groupIDsToRemoveUser:" + groupIDsToRemoveUser);

		// get the user groups(ID) to add the user
		Set<String> groupIDsToAddUser = new HashSet<String>();
		groupIDsToAddUser.addAll(groupIDs);
		groupIDsToAddUser.removeAll(existGroupIDs);

		logger.debug("groupIDsToAddUser:" + groupIDsToAddUser);

		// get the user groups(ID) to add or remove the user
		List<String> userIDsToUpdateGroup = new ArrayList<String>();
		userIDsToUpdateGroup.addAll(groupIDsToRemoveUser);
		userIDsToUpdateGroup.addAll(groupIDsToAddUser);

		List<BeehiveUserGroup> userGroupList = getUserGroupByIDs(userIDsToUpdateGroup);

		// update the user info into table BeehiveUserGroup
		userGroupList.forEach((group) -> {
			String tempId = group.getId();
			Set<String> tempUsers = group.getUsers();

			// add or remove user from the user group
			if (groupIDsToAddUser.contains(tempId)) {
				tempUsers.add(userID);
			} else if (groupIDsToRemoveUser.contains(tempId)) {
				tempUsers.remove(userID);
			}

			updateUsers(tempId, tempUsers);
		});

	}
}
