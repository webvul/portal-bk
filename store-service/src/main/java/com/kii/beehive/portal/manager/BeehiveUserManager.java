package com.kii.beehive.portal.manager;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.business.service.KiiUserService;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.entitys.PermissionTree;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.exception.UserNotExistException;
import com.kii.beehive.portal.helper.AuthInfoService;
import com.kii.beehive.portal.helper.RuleSetService;
import com.kii.beehive.portal.jdbc.dao.BeehiveArchiveUserDao;
import com.kii.beehive.portal.jdbc.dao.BeehiveUserJdbcDao;
import com.kii.beehive.portal.jdbc.dao.GroupUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.TeamGroupRelationDao;
import com.kii.beehive.portal.jdbc.dao.TeamUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.UserGroupDao;
import com.kii.beehive.portal.jdbc.entity.BeehiveArchiveUser;
import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;
import com.kii.beehive.portal.jdbc.entity.TeamUserRelation;

@Component
@Transactional
public class BeehiveUserManager {


	@Autowired
	private AuthInfoService authService;

	@Autowired
	protected TeamUserRelationDao teamUserRelationDao;

	@Autowired
	protected TeamGroupRelationDao teamGroupRelationDao;

	@Autowired
	private KiiUserService kiiUserService;

	@Autowired
	private BeehiveUserJdbcDao userDao;

	@Autowired
	private BeehiveArchiveUserDao archiveUserDao;

	@Autowired
	private RuleSetService ruleService;

	@Autowired
	private UserGroupDao userGroupDao;

	@Autowired
	private GroupUserRelationDao groupUserRelationDao;



	public PermissionTree getUsersPermissonTree(Long userID) {

		return ruleService.getUserPermissionTree(userID);
	}


	public void createAdmin(String adminName, String password) {

		BeehiveJdbcUser admin = new BeehiveJdbcUser();

		admin.setUserName(adminName);
		String hashedPwd = admin.getHashedPwd(password);
		admin.setUserPassword(hashedPwd);
		admin.setRoleName("admin");

		userDao.insert(admin);

		String loginID = kiiUserService.addBeehiveUser(admin, admin.getUserPassword());

		admin.setKiiUserID(loginID);

		userDao.updateEntityAllByID(admin);

	}



//	private Long addTeam(String teamName,Long userID){
//
//			List<Team> teamList = teamDao.findTeamByTeamName(teamName);
//
//			Long teamID=null;
//			if(teamList.isEmpty()){
//				Team t = new Team();
//				t.setName(teamName);
//				teamID = teamDao.saveOrUpdate(t);
//				TeamUserRelation tur = new TeamUserRelation(teamID, userID, 1);
//				teamUserRelationDao.saveOrUpdate(tur);
//
//			}else if(teamList.size()==1){// user add to team
//				teamID = teamList.get(0).getId();
//				TeamUserRelation tur = new TeamUserRelation(teamID, userID, 0);
//				teamUserRelationDao.saveOrUpdate(tur);
//			}else{
//				throw new IllegalArgumentException();
//			}
//			return teamID;
//
//	}




	public BeehiveJdbcUser getUserByIDDirectly(Long userID) {

		BeehiveJdbcUser user = userDao.getUserByID(userID);
		if (user == null) {
			throw new UserNotExistException(String.valueOf(userID));
		}
		return user;
	}


	public BeehiveJdbcUser getUserByUserID(String userID) {

		BeehiveJdbcUser user = userDao.getUserByUserID(userID);
		if (user == null) {
			throw new UserNotExistException(String.valueOf(userID));
		}
		return user;
	}
	public BeehiveJdbcUser getUserByFaceUserID(String faceUserID) {

		BeehiveJdbcUser user = userDao.getUserByFaceUserID(faceUserID);
		if (user == null) {
			throw new UserNotExistException(String.valueOf(faceUserID));
		}
		return user;
	}


	public void updateUser(BeehiveJdbcUser user, String userID) {

//		checkTeam(userID);

		BeehiveJdbcUser oldUser = userDao.getUserByUserID(userID);

		if (oldUser == null) {
			throw new UserNotExistException(userID);
		}

		userDao.updateEntityByID(user, oldUser.getId());


	}


	public List<BeehiveJdbcUser> simpleQueryUser(Map<String, Object> queryMap) {

		if (queryMap.isEmpty()) {
			return userDao.getAllUsers();
		} else {
			return userDao.getUsersBySimpleQuery(queryMap);
		}
	}


	private void checkTeam(Long userID) {
		if (AuthInfoStore.isTeamIDExist()) {
			TeamUserRelation tur = teamUserRelationDao.findByTeamIDAndUserID(AuthInfoStore.getTeamID(), userID);
			if (tur == null) {
				throw new UnauthorizedException(UnauthorizedException.NOT_IN_CURR_TEAM);
			}
		}
	}


	public void removeUser(Long userID) {
		BeehiveJdbcUser user = userDao.getUserByID(userID);
		if (user == null) {
			throw new UserNotExistException(String.valueOf(userID));
		}

		userDao.hardDeleteByID(user.getId());

		groupUserRelationDao.delete(userID, null);

		authService.removeTokenByUserID(user.getUserID());

		kiiUserService.removeBeehiveUser(user);

		BeehiveArchiveUser archiveUser = new BeehiveArchiveUser(user);

		archiveUserDao.insert(archiveUser);

	}


}
