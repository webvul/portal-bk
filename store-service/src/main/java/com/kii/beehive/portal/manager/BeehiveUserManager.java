package com.kii.beehive.portal.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.business.service.KiiUserService;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.StringRandomTools;
import com.kii.beehive.portal.entitys.PermissionTree;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.exception.UserExistException;
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


//	@Autowired
//	private TeamDao teamDao;


	public PermissionTree getUsersPermissonTree(String userID){

		return ruleService.getUserPermissionTree(userID);
	}


	public void createAdmin(String adminName,String password){

		BeehiveJdbcUser admin=new BeehiveJdbcUser();

		admin.setUserName(adminName);
		String hashedPwd=admin.getHashedPwd(password);
		admin.setUserPassword(hashedPwd);
		admin.setRoleName("admin");

		userDao.insert(admin);

		String loginID=kiiUserService.addBeehiveUser(admin,admin.getUserPassword());

		admin.setKiiUserID(loginID);

		userDao.updateEntityAllByID(admin);

	}

	public Map<String,Object> addUser(BeehiveJdbcUser user) {


		BeehiveJdbcUser existsUser=userDao.getUserByLoginId(user);

		if(existsUser!=null){
			throw new UserExistException(user,existsUser);
		}

		if(StringUtils.isBlank(user.getRoleName())){
			user.setRoleName("commUser");
		}

		user.setEnable(false);
		user=userDao.addUser(user);

		String loginID=kiiUserService.addBeehiveUser(user,user.getDefaultPassword());

		user.setKiiUserID(loginID);

		String token= StringRandomTools.getRandomStr(6);

		user.setActivityToken(user.getHashedPwd(token));

		userDao.updateEntityAllByID(user);

		Map<String,Object> result=new HashMap<>();

		result.put("user",user);
		result.put("activityToken",token);


		return result;
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



	public void deleteUser(Long userID) {
//		checkTeam(userID);
		BeehiveJdbcUser user = userDao.getUserByID(userID);

		groupUserRelationDao.delete(userID, null);

		kiiUserService.disableBeehiveUser(user);

		userDao.deleteUser(userID);


	}

	public BeehiveJdbcUser getUserByIDDirectly(String userID){

		BeehiveJdbcUser user= userDao.getUserByUserID(userID);
		if(user==null){
			throw new UserNotExistException(userID);
		}
		return user;
	}



//	public BeehiveJdbcUser getUserByID(String userID) {
//
//
////		checkTeam(userID);
//
//
//	}


	public void updateUser(BeehiveJdbcUser user,String userID){

//		checkTeam(userID);

		BeehiveJdbcUser oldUser=userDao.getUserByUserID(userID);

		if(oldUser==null){
			throw new UserNotExistException(userID);
		}

		userDao.updateEntityByID(user,oldUser.getId());


	}

	/**
	 *
	 */




	public List<BeehiveJdbcUser> simpleQueryUser(Map<String, Object> queryMap) {

		if (queryMap.isEmpty()) {
			return userDao.getAllUsers();
		} else {
			return userDao.getUsersBySimpleQuery(queryMap);
		}
	}


	private void checkTeam(Long userID){
		if(AuthInfoStore.isTeamIDExist()){
			TeamUserRelation tur = teamUserRelationDao.findByTeamIDAndUserID(AuthInfoStore.getTeamID(), userID);
			if(tur == null){
				throw new UnauthorizedException(UnauthorizedException.NOT_IN_CURR_TEAM);
			}
		}
	}
	
	
	public void updateUserSign(String userID, boolean b) {


		int i=userDao.updateEnableSign(userID,b);
		if(i==0){
			throw new UserNotExistException(userID);
		}
	}


	public void disableUser(String userID){

		int i=userDao.updateEnableSign(userID,false);
		if(i==0){
			throw new UserNotExistException(userID);
		}
		authService.removeTokenByUserID(userID);

	}



	public void removeUser(String userID){
		BeehiveJdbcUser  user=userDao.getUserByUserID(userID);
		if(user==null){
			throw new UserNotExistException(userID);
		}

		userDao.hardDeleteByID(user.getId());

		authService.removeTokenByUserID(user.getUserID());

		BeehiveArchiveUser  archiveUser=new BeehiveArchiveUser(user);

		archiveUserDao.insert(archiveUser);

	}
	

}
