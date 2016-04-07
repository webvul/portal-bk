package com.kii.beehive.business.manager;

import com.kii.beehive.business.helper.SyncMsgService;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.exception.DuplicateException;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.exception.InvalidAuthException;
import com.kii.beehive.portal.exception.UserNotExistException;
import com.kii.beehive.portal.jdbc.dao.*;
import com.kii.beehive.portal.jdbc.entity.GroupUserRelation;
import com.kii.beehive.portal.jdbc.entity.Team;
import com.kii.beehive.portal.jdbc.entity.TeamGroupRelation;
import com.kii.beehive.portal.jdbc.entity.UserGroup;
import com.kii.beehive.portal.service.ArchiveBeehiveUserDao;
import com.kii.beehive.portal.service.BeehiveUserDao;
import com.kii.beehive.portal.service.KiiUserSyncDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.store.entity.CustomProperty;
import com.kii.extension.sdk.exception.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserManager {

	private Logger logger = LoggerFactory.getLogger(UserManager.class);

	@Autowired
	private ArchiveBeehiveUserDao archiveUserDao;

	@Autowired
	private BeehiveUserDao userDao;

	@Autowired
	private UserGroupDao userGroupDao;

	@Autowired
	private TeamDao teamDao;

	@Autowired
	private GroupUserRelationDao groupUserRelationDao;

	@Autowired
	private GroupPermissionRelationDao groupPermissionRelationDao;

	@Autowired
	protected TeamGroupRelationDao teamGroupRelationDao;

	@Autowired
	protected PermissionDao permissionDao;

	@Autowired
	private KiiUserSyncDao kiiUserDao;

	@Autowired
	private BeehiveUserDao beehiveUserDao;

	@Autowired
	private SyncMsgService msgService;


	public String addUser(BeehiveUser user) {

		BeehiveUser archiveUser = archiveUserDao.queryInArchive(user);

		//old user restore
		if (archiveUser != null) {

			archiveUserDao.removeArchive(archiveUser.getId());
			kiiUserDao.enableUser(archiveUser.getKiiUserID());

		} else {

			kiiUserDao.addBeehiveUser(user);

		}

		String id = userDao.createUser(user);

		msgService.addInsertMsg(id, user);
		return id;
	}


	public void updateUser(BeehiveUser user, String userID) {


		try {
			userDao.updateUser(user, userID);

		} catch (ObjectNotFoundException e) {
			throw new UserNotExistException(userID);
		}
		msgService.addUpdateMsg(userID, user);


	}

	public void updateCustomProp(String userID, Map<String, Object> customProps) {

		BeehiveUser user = new BeehiveUser();
		user.setCustomFields(new CustomProperty(customProps));
		try {
			userDao.updateUser(user, userID);

		} catch (ObjectNotFoundException e) {
			throw new UserNotExistException(userID);
		}
		msgService.addUpdateMsg(userID, user);

	}

	public List<BeehiveUser> simpleQueryUser(Map<String, Object> queryMap) {

		if (queryMap.isEmpty()) {
			return userDao.getAllUsers();
		} else {

			Map<String, Object> map = new HashMap<>();
			queryMap.forEach((k, v) -> {
				if (k.startsWith("custom.")) {
					String newK = k.replace(".", "-");
					map.put(newK, v);
				} else {
					map.put(k, v);
				}
			});

			return userDao.getUsersBySimpleQuery(map);
		}
	}

	public Long createUserGroup(UserGroup userGroup, String loginUserID) {
		// create user group

		List<UserGroup> userGroupList = userGroupDao.findUserGroupByName(userGroup.getName());

		if (userGroupList.size() > 0) {
			throw new DuplicateException(userGroup.getName());
		}

		Long userGroupID = userGroupDao.saveOrUpdate(userGroup);
		GroupUserRelation gur = new GroupUserRelation(loginUserID, userGroupID);
		groupUserRelationDao.insert(gur);

		if (AuthInfoStore.getTeamID() != null) {
			TeamGroupRelation tgr = new TeamGroupRelation(AuthInfoStore.getTeamID(), userGroupID);
			teamGroupRelationDao.insert(tgr);
		}

		return userGroupID;
	}

	public Long updateUserGroup(UserGroup userGroup, String loginUserID) {
		List<UserGroup> orgiList = userGroupDao.findUserGroup(loginUserID, userGroup.getId(), null);
		if (orgiList.size() == 0) {
			throw new EntryNotFoundException(userGroup.getId().toString());
		}

		List<UserGroup> userGroupList = userGroupDao.findUserGroupByName(userGroup.getName());
		if (userGroupList.size() > 0 && userGroupList.get(0).getId() != userGroup.getId()) {
			throw new DuplicateException(userGroup.getName());
		}

		UserGroup orgi = orgiList.get(0);
		if (!orgi.getCreateBy().equals(loginUserID)) {
			throw new InvalidAuthException(orgi.getCreateBy(), loginUserID);
		}
		orgi.setName(userGroup.getName());
		orgi.setDescription(userGroup.getDescription());
		orgi.setModifyDate(new Date());
		orgi.setModifyBy(loginUserID);
		Long userGroupID = userGroupDao.saveOrUpdate(orgi);
		return userGroupID;
	}


	public void deleteUserGroup(Long userGroupID) {
		groupUserRelationDao.delete(null, userGroupID);
		userGroupDao.deleteByID(userGroupID);
	}

	/**
	 * add users to user group
	 *
	 * @param userIDList  the already existing userIDs under the user group will not be added again
	 * @param userGroupID
	 */
	public void addUserToUserGroup(List<String> userIDList, Long userGroupID) {

		List<BeehiveUser> userList = beehiveUserDao.getUserByIDs(userIDList);
		if (userList.size() == 1) {
			List<UserGroup> orgiList = userGroupDao.findUserGroup(userList.get(0).getKiiLoginName(), userGroupID, null);
			if (orgiList.size() == 0) {
				GroupUserRelation gur = new GroupUserRelation(userList.get(0).getKiiLoginName(), userGroupID);
				groupUserRelationDao.insert(gur);
			}
		} else if (userList.size() > 1) {
			List<String> existingUserIDList = groupUserRelationDao.findUserIDByUserGroupID(userGroupID);

			List<String> userIDListToInsert = new ArrayList<>();
			userList.forEach(beehiveUser -> userIDListToInsert.add(beehiveUser.getKiiLoginName()));
			userIDListToInsert.removeAll(existingUserIDList);

			List<GroupUserRelation> relationList = new ArrayList<>();
			userIDListToInsert.forEach(newUserId -> relationList.add(new GroupUserRelation(newUserId, userGroupID)));

			groupUserRelationDao.batchInsert(relationList);
		}
	}

	public void deleteUser(String userID) {

		BeehiveUser user = userDao.getUserByID(userID);

		//this.removeUserFromUserGroup(userID, user.getGroups());

		groupUserRelationDao.delete(userID, null);

		kiiUserDao.disableBeehiveUser(user);
		archiveUserDao.archive(user);

		userDao.deleteUser(userID);

		msgService.addDeleteMsg(userID);

	}

	public BeehiveUser getUserByID(String userID) {
		return userDao.getUserByID(userID);
	}

	/**
	 * return the non existing userIDs
	 *
	 * @param userIDs
	 * @return
	 */
	public Set<String> checkNonExistingUserID(Collection<String> userIDs) {

		if (userIDs == null) {
			return new HashSet<String>();
		}

		// get the existing user IDs
		List<BeehiveUser> beehiveUserList = userDao.getUserByIDs(new ArrayList<>(userIDs));
		Set<String> existingUserIDList = new HashSet<>();
		for (BeehiveUser user : beehiveUserList) {
			existingUserIDList.add(user.getAliUserID());
		}

		// get the non existing user IDs
		Set<String> nonExistingUserIDs = new HashSet<>();
		nonExistingUserIDs.addAll(userIDs);
		nonExistingUserIDs.removeAll(existingUserIDList);

		return nonExistingUserIDs;
	}

	/**
	 * validate whether the userIDs in param "userIDList" existing
	 * if any userID not existing, throw UserNotExistException
	 *
	 * @param userIDs
	 */
	public void validateUserIDExisting(Set<String> userIDs) {

		Set<String> nonExistingUserIDList = this.checkNonExistingUserID(userIDs);

		if (nonExistingUserIDList != null && !nonExistingUserIDList.isEmpty()) {
			StringBuffer buffer = new StringBuffer();

			for (String nonExistingUserID : nonExistingUserIDList) {
				buffer.append(nonExistingUserID).append(",");

			}
			buffer.deleteCharAt(buffer.length() - 1);

			throw new UserNotExistException(buffer.toString());
		}
	}

	public Team getTeamByID(String userID) {
		List<Team> teamList = teamDao.findTeamByUserID(userID);
		if (teamList != null && teamList.size() > 0) {
			return teamList.get(0);
		} else {
			return null;
		}
	}
}
