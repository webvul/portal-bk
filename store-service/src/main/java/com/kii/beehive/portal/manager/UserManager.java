package com.kii.beehive.portal.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.kii.beehive.portal.notify.UserSyncNotifier;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.service.ArchiveBeehiveUserDao;
import com.kii.beehive.portal.service.BeehiveUserDao;
import com.kii.beehive.portal.service.BeehiveUserGroupDao;
import com.kii.beehive.portal.service.KiiUserSyncDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.store.entity.BeehiveUserGroup;
import com.kii.extension.sdk.exception.KiiCloudException;


@Component
public class UserManager {

	private Logger logger= LoggerFactory.getLogger(UserManager.class);

	@Autowired
	private ArchiveBeehiveUserDao archiveUserDao;

	@Autowired
	private BeehiveUserDao userDao;

	@Autowired
	private BeehiveUserGroupDao userGroupDao;

	@Autowired
	private KiiUserSyncDao kiiUserDao;

//	@Autowired
//	private UserSyncNotifier userSyncNotifier;

//	@Autowired
//	private AppInfoDao appInfoDao;



	public String addUser(BeehiveUser user){

		if(StringUtils.isEmpty(user.getUserName())){
			throw new KiiCloudException();
		}

		String pwd= DigestUtils.sha1Hex(user.getUserName() + "_beehive");

		String kiiUserID=kiiUserDao.addBeehiveUser(user,pwd);

		user.setKiiUserID(kiiUserID);

		logger.debug("kiiUserID:" + kiiUserID);

		// create user in table BeehiveUser
		userDao.createUser(user);

		// notify the other device suppliers of the user info change in async way
//		userSyncNotifier.notifyDeviceSuppliersAsync(user.getParty3rdID(),
//				beehiveUserID, UserSyncNotifier.CHANGE_TYPE_CREATE);

		return kiiUserID;
	}



	public void updateUser(BeehiveUser user) {

//		if(StringUtils.isEmpty(user.getUserName())){
//			throw new KiiCloudException();
//		}

//		this.checkUserGroupsChange(user.getBeehiveUserID(), user.getGroups());
		userDao.updateUser(user);

	}

	public void updateCustomProp(String userID,Map<String,Object> customProps){

		userDao.updateUserCustomFields(userID, customProps);

	}

	public List<BeehiveUser> simpleQueryUser(Map<String,Object> queryMap){

		if(queryMap.isEmpty()){
			return userDao.getAllUsers();
		}else {
			return userDao.getUsersBySimpleQuery(queryMap);
		}
	}


	public void deleteUser(String userID) {



		BeehiveUser user = userDao.getUserByID(userID);
		archiveUserDao.archive(user);

		kiiUserDao.disableBeehiveUser(user);

//		this.checkUserGroupsChange(user.getBeehiveUserID(), null);

		userDao.deleteUser(userID);

	}

	/**
	 * check whether any change on the user groups under the user,
	 * if there is, update the user group info too (table BeehiveUserGroup)
	 *
	 * @param userID
	 * @param groupIDs  new set of user groups under the user
	 */
	private void checkUserGroupsChange(String userID, Set<String> groupIDs) {


		BeehiveUser existUser = userDao.getUserByID(userID);

		Set<String> existGroupIDs = new HashSet<String>();
		if (existUser != null && existUser.getGroups() != null) {
			existGroupIDs = existUser.getGroups();
		}

		if (groupIDs.containsAll(existGroupIDs) && existGroupIDs.containsAll(groupIDs)) {
			logger.debug("no change on relation bwtween user group and user:" + userID);
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

		List<BeehiveUserGroup> userGroupList = userGroupDao.getUserGroupByIDs(userIDsToUpdateGroup);

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

			userGroupDao.updateUsers(tempId, tempUsers);
		});

	}


	public BeehiveUser getUserByID(String userID) {
		return userDao.getUserByID(userID);
	}
}
