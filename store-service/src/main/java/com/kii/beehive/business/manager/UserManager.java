package com.kii.beehive.business.manager;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.StringRandomTools;
import com.kii.beehive.portal.exception.DuplicateException;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.exception.InvalidAuthException;
import com.kii.beehive.portal.exception.UserNotExistException;
import com.kii.beehive.portal.jdbc.dao.BeehiveUserDao;
import com.kii.beehive.portal.jdbc.dao.GroupPermissionRelationDao;
import com.kii.beehive.portal.jdbc.dao.GroupUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.PermissionDao;
import com.kii.beehive.portal.jdbc.dao.TeamDao;
import com.kii.beehive.portal.jdbc.dao.TeamGroupRelationDao;
import com.kii.beehive.portal.jdbc.dao.UserGroupDao;
import com.kii.beehive.portal.jdbc.entity.BeehiveUser;
import com.kii.beehive.portal.jdbc.entity.GroupUserRelation;
import com.kii.beehive.portal.jdbc.entity.Team;
import com.kii.beehive.portal.jdbc.entity.TeamGroupRelation;
import com.kii.beehive.portal.jdbc.entity.TeamUserRelation;
import com.kii.beehive.portal.jdbc.entity.UserGroup;
import com.kii.beehive.business.service.KiiUserService;
import com.kii.beehive.portal.store.entity.PortalSyncUser;

@Component
@Transactional
public class UserManager {


	private Logger logger = LoggerFactory.getLogger(UserManager.class);

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
	private KiiUserService kiiUserService;

	@Autowired
	private BeehiveUserDao  userDao;


	@Autowired
	protected TeamGroupRelationDao teamGroupRelationDao;


	public BeehiveUser addUser(BeehiveUser user) {

		userDao.insert(user);

		user=kiiUserService.addBeehiveUser(user);

		String token= StringRandomTools.getRandomStr(6);

		user.setActivityToken(token);

		userDao.updateEntityByID(user,user.getUserID());

		return user;
	}


	private void addTeamInfo(String teamName,Long userID){

		//create team
		if(!Strings.isBlank(teamName)){
			List<Team> teamList = teamDao.findTeamByTeamName(teamName);
			Long teamID = null;
			if(teamList.size() == 0){//create team and user add to team
				Team t = new Team();
				t.setName(teamName);
				teamID = teamDao.saveOrUpdate(t);
				TeamUserRelation tur = new TeamUserRelation(teamID, userID, 1);
				teamUserRelationDao.saveOrUpdate(tur);

			}else{// user add to team
				teamID = teamList.get(0).getId();
				TeamUserRelation tur = new TeamUserRelation(teamID, userID, 0);
				teamUserRelationDao.saveOrUpdate(tur);
			}
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

		List<BeehiveUser> userList = userDao.getUserByIDs(userIDList);
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

		userDao.deleteUser(userID);


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
