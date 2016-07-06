package com.kii.beehive.business.manager;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.exception.DuplicateException;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.jdbc.dao.BeehiveUserJdbcDao;
import com.kii.beehive.portal.jdbc.dao.GroupUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.TeamDao;
import com.kii.beehive.portal.jdbc.dao.TeamGroupRelationDao;
import com.kii.beehive.portal.jdbc.dao.TeamUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.UserGroupDao;
import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;
import com.kii.beehive.portal.jdbc.entity.GroupUserRelation;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.Team;
import com.kii.beehive.portal.jdbc.entity.TeamGroupRelation;
import com.kii.beehive.portal.jdbc.entity.TeamUserRelation;
import com.kii.beehive.portal.jdbc.entity.UserGroup;


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
	private TagIndexDao  tagDao;

	@Autowired
	private BeehiveUserJdbcDao userDao;



	@Autowired
	protected TeamGroupRelationDao teamGroupRelationDao;

	@Autowired
	protected TeamUserRelationDao teamUserRelationDao;




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

	public Long createUserGroup(UserGroup userGroup, Long loginUserID) {
		// create user group

		List<UserGroup> userGroupList = userGroupDao.findUserGroupByName(userGroup.getName());

		if (userGroupList.size() > 0) {
			throw new DuplicateException(userGroup.getName(),"UserGroup");
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

	public Long updateUserGroup(UserGroup userGroup, Long loginUserID) {
		List<UserGroup> orgiList = userGroupDao.findUserGroup(loginUserID, userGroup.getId(), null);
		if (orgiList.size() == 0) {
			throw new EntryNotFoundException(userGroup.getId().toString(),"UserGroup");
		}

		List<UserGroup> userGroupList = userGroupDao.findUserGroupByName(userGroup.getName());
		if (userGroupList.size() > 0 && userGroupList.get(0).getId() != userGroup.getId()) {
			throw new DuplicateException(userGroup.getName(),"UserGroup");
		}

		UserGroup orgi = orgiList.get(0);
		if (!orgi.getCreateBy().equals(loginUserID)) {
			UnauthorizedException excep= new UnauthorizedException(UnauthorizedException.NOT_GROUP_CREATER);
			excep.addParam("group",orgi.getName());
			excep.addParam("currUser",String.valueOf(loginUserID));
			throw excep;
		}
		orgi.setName(userGroup.getName());
		orgi.setDescription(userGroup.getDescription());
		orgi.setModifyDate(new Date());
		orgi.setModifyBy(String.valueOf(loginUserID));
		Long userGroupID = userGroupDao.saveOrUpdate(orgi);
		return userGroupID;
	}


	public void deleteUserGroup(Long userGroupID) {

		UserGroup orig = userGroupDao.findByID(userGroupID);

		if (orig == null) {
			throw  EntryNotFoundException.userGroupNotFound(userGroupID);
		}

		if (!orig.getCreateBy().equals(String.valueOf(AuthInfoStore.getUserIDInLong()))) {
			throw new UnauthorizedException(UnauthorizedException.USERGROUP_NO_PRIVATE);
		}
		groupUserRelationDao.delete(null, userGroupID);
		userGroupDao.deleteByID(userGroupID);
	}


	public UserGroup  getUserGroupDetail(Long userGroupID){

		isGroupOfUser(AuthInfoStore.getUserIDInLong(), userGroupID);


		List<BeehiveJdbcUser>  userList=userDao.findUserIDByUserGroupID(userGroupID);

		UserGroup ug = userGroupDao.findByID(userGroupID);

		if(ug==null){
			throw  EntryNotFoundException.userGroupNotFound(userGroupID);
		}
		ug.setUserList(userList);

		return ug;
	}

	public List<TagIndex> getTagIndexList(Long userGroupID){

		isGroupOfUser(AuthInfoStore.getUserIDInLong(), userGroupID);


		return tagDao.getTagListByGroupID(userGroupID);
//
//		List<Long> tagIDList = new ArrayList<Long>();
//
//
//
//		List<TagGroupRelation> relList = tagGroupRelationDao.findByUserGroupID(userGroupID);
//
//		if (relList.size() > 0) {
//				relList.forEach(tgr -> tagIDList.add(tgr.getTagID()));
//				tagList = tagIndexDao.findByIDs(tagIDList);
//		}
//
//		if (tagList == null) {
//			throw new PortalException(ErrorCode.USERGROUP_NO_PRIVATE, HttpStatus.NOT_FOUND);
//		}

	}
	/**
	 * add users to user group
	 *
	 * @param userIDList  the already existing userIDs under the user group will not be added again
	 * @param userGroupID
	 */
	public void addUserToUserGroup(List<String> userIDList, Long userGroupID) {


		UserGroup ug = this.userGroupDao.findByID(userGroupID);

		if (!ug.getCreateBy().equals(AuthInfoStore.getUserID())) {
			UnauthorizedException  excep= new UnauthorizedException(UnauthorizedException.NOT_GROUP_CREATER);
			excep.addParam("group",ug.getName());
			excep.addParam("currUser",AuthInfoStore.getUserID());
			throw excep;
		}

		List<BeehiveJdbcUser> userList = userDao.getUserByUserIDs(userIDList);

		Set<Long> existingSet = new HashSet<>(groupUserRelationDao.findUserGroupIds(userGroupID));

		List<GroupUserRelation> relationList = new ArrayList<>();

		userList.stream().filter(u->!existingSet.contains(u.getId())).forEach((u)->{

			relationList.add(new GroupUserRelation(u.getId(), userGroupID));

		});

		groupUserRelationDao.batchInsert(relationList);
	}




	private  void isGroupOfUser(Long loginUserID, Long userGroupID) {

		List<UserGroup> checkAuth = userGroupDao.findUserGroup(loginUserID, userGroupID, null);

		if (checkAuth.size() != 1) {

			throw new UnauthorizedException(UnauthorizedException.USERGROUP_NO_PRIVATE);
		}
	}

	public void removeUserFromGroup(Long userGroupID,List<String> userIDs){
		UserGroup ug = this.userGroupDao.findByID(userGroupID);
		if (ug == null) {
			throw new UnauthorizedException(UnauthorizedException.USERGROUP_NO_PRIVATE);

		} else if (!ug.getCreateBy().equals(String.valueOf(AuthInfoStore.getUserIDInLong()))) {

			throw new UnauthorizedException(UnauthorizedException.NOT_GROUP_CREATER);

		} else {

			if (userIDs.contains(AuthInfoStore.getUserID())) {
					throw new UnauthorizedException(UnauthorizedException.USERGROUP_NO_PRIVATE);
			}
			List<Long> ids=userDao.getUserByUserIDs(userIDs).stream().mapToLong(BeehiveJdbcUser::getId).boxed().collect(Collectors.toList());

			groupUserRelationDao.deleteUsers(ids, userGroupID);

		}
	}


	public List<UserGroup>  findUserGroup(){

		return userGroupDao.findUserGroup(AuthInfoStore.getUserIDInLong(), null, null);

	}

	public List<UserGroup>  findAll(){

		return userGroupDao.findAll();

	}
	

	
	
	/**
	 * return the non existing userIDs
	 *
	 * @param userIDs
	 * @return
	 */
//	public Set<Long> checkNonExistingUserID(Collection<Long> userIDs) {
//
//		if (userIDs == null) {
//			return new HashSet<String>();
//		}
//
//		// get the existing user IDs
//		List<BeehiveUser> beehiveUserList = userDao.getUserByIDs(userIDs);
//		Set<String> existingUserIDList = new HashSet<>();
//		for (BeehiveUser user : beehiveUserList) {
//			existingUserIDList.add(user.getId());
//		}
//
//		// get the non existing user IDs
//		Set<Long> nonExistingUserIDs = new HashSet<>();
//		nonExistingUserIDs.addAll(userIDs);
//		nonExistingUserIDs.removeAll(existingUserIDList);
//
//		return nonExistingUserIDs;
//	}

	/**
	 * validate whether the userIDs in param "userIDList" existing
	 * if any userID not existing, throw UserNotExistException
	 *
	 * @param userIDs
	 */
//	public void validateUserIDExisting(Set<Long> userIDs) {
//
//		Set<Long> nonExistingUserIDList = this.checkNonExistingUserID(userIDs);
//
//		if (nonExistingUserIDList != null && !nonExistingUserIDList.isEmpty()) {
//			StringBuffer buffer = new StringBuffer();
//
//			for (Long nonExistingUserID : nonExistingUserIDList) {
//				buffer.append(nonExistingUserID).append(",");
//
//			}
//			buffer.deleteCharAt(buffer.length() - 1);
//
//			throw new UserNotExistException(buffer.toString());
//		}
//	}



}
