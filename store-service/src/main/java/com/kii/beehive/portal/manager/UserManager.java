package com.kii.beehive.portal.manager;

import java.util.*;

import com.kii.beehive.portal.notify.UserSyncNotifier;
import com.kii.beehive.portal.service.*;
import com.kii.beehive.portal.store.entity.BeehiveUserGroup;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.BeehiveUser;

import javax.annotation.PostConstruct;

@Component
public class UserManager {

	private Logger logger;

	@Autowired
	private ArchiveBeehiveUserDao archiveUserDao;

	@Autowired
	private BeehiveUserDao userDao;

	@Autowired
	private BeehiveUserGroupDao userGroupDao;

	@Autowired
	private KiiUserSyncDao kiiUserDao;

	@Autowired
	private UserSyncNotifier userSyncNotifier;

	@Autowired
	private AppInfoDao appInfoDao;

	@PostConstruct
	public void init() {
		logger = Logger.getLogger(this.getClass());
	}

	public String addUser(BeehiveUser user){

		logger.debug("Start addUser(BeehiveUser user)");
		logger.debug("user:" + user);

		// create user in Kii Master App
		String kiiUserID=kiiUserDao.addBeehiveUser(user,appInfoDao.getMasterAppInfo().getAppName());

		user.setKiiUserID(kiiUserID);

		logger.debug("kiiUserID:" + kiiUserID);

		String beehiveUserID = user.getBeehiveUserID();

		// check and update user group change
		//
		// important:
		// this has to go before the user update(table BeehiveUser),
		// to get the exist list of user groups under the user
		this.checkUserGroupsChange(beehiveUserID, user.getGroups());

		// create user in table BeehiveUser
		userDao.createUser(user);

		// notify the other device suppliers of the user info change in async way
		userSyncNotifier.notifyDeviceSuppliersAsync(user.getParty3rdID(),
				beehiveUserID, UserSyncNotifier.CHANGE_TYPE_CREATE);

		logger.debug("End addUser(BeehiveUser user)");
		logger.debug("beehiveUserID:" + beehiveUserID);
		return beehiveUserID;
	}

	public void updateUser(BeehiveUser user) {

		logger.debug("Start updateUser(BeehiveUser user)");
		logger.debug("user:" + user);

		// check and update user group change
		//
		// important:
		// this has to go before the user update(table BeehiveUser),
		// to get the exist list of user groups under the user
		this.checkUserGroupsChange(user.getBeehiveUserID(), user.getGroups());

		// update user in table BeehiveUser
		userDao.updateUser(user);

		// notify the other device suppliers of the user info change in async way
		userSyncNotifier.notifyDeviceSuppliersAsync(user.getParty3rdID(),
				user.getBeehiveUserID(), UserSyncNotifier.CHANGE_TYPE_UPDATE);

		logger.debug("End updateUser(BeehiveUser user)");
	}

	public void deleteUser(BeehiveUser user) {

		logger.debug("Start deleteUser(BeehiveUser user)");
		logger.debug("user:" + user);

		String beehiveUserID = user.getBeehiveUserID();

		// disable user in Kii Master App
		kiiUserDao.disableBeehiveUser(user, appInfoDao.getMasterAppInfo().getAppName());

		// archive the user to table ArchiveBeehiveUser
		user = userDao.getUserByID(beehiveUserID);
		archiveUserDao.archive(user);

		// check and update user group change
		//
		// important:
		// this has to go before the user update(table BeehiveUser),
		// to get the exist list of user groups under the user
		this.checkUserGroupsChange(user.getBeehiveUserID(), null);

		// remove the user from table BeehiveUser
		userDao.deleteUser(beehiveUserID);

		// notify the other device suppliers of the user info change in async way
		userSyncNotifier.notifyDeviceSuppliersAsync(user.getParty3rdID(),
				beehiveUserID, UserSyncNotifier.CHANGE_TYPE_DELETE);

		logger.debug("End deleteUser(BeehiveUser user)");
	}

	/**
	 * check whether any change on the user groups under the user,
	 * if there is, update the user group info too (table BeehiveUserGroup)
	 *
	 * @param userID
	 * @param groupIDs  new set of user groups under the user
	 */
	private void checkUserGroupsChange(String userID, Set<String> groupIDs) {

		if(groupIDs == null) {
			groupIDs = new HashSet<String>();
		}

		BeehiveUser existUser = userDao.getUserByID(userID);

		Set<String> existGroupIDs = new HashSet<String>();
		if (existUser != null && existUser.getGroups() != null) {
			existGroupIDs = existUser.getGroups();
		}


		if (groupIDs.containsAll(existGroupIDs) && existGroupIDs.containsAll(groupIDs)) {
			logger.debug("no change on relation bwtween user group and user:" + userID);
			return;
		}

		// get the user groups(ID) to remove the user
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
		userGroupList.stream().forEach((group) -> {
			String tempId = group.getUserGroupID();
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


}
