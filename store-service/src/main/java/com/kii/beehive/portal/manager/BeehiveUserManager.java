package com.kii.beehive.portal.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.kii.beehive.business.service.KiiUserService;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.StringRandomTools;
import com.kii.beehive.portal.entitys.PermissionTree;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.helper.RuleSetService;
import com.kii.beehive.portal.jdbc.dao.GroupUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.TeamDao;
import com.kii.beehive.portal.jdbc.dao.TeamGroupRelationDao;
import com.kii.beehive.portal.jdbc.dao.TeamUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.UserGroupDao;
import com.kii.beehive.portal.jdbc.entity.Team;
import com.kii.beehive.portal.jdbc.entity.TeamUserRelation;
import com.kii.beehive.portal.service.BeehiveUserDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;

@Component
@Transactional
public class BeehiveUserManager {




	@Autowired
	protected TeamUserRelationDao teamUserRelationDao;

	@Autowired
	protected TeamGroupRelationDao teamGroupRelationDao;

	@Autowired
	private KiiUserService kiiUserService;

	@Autowired
	private BeehiveUserDao userDao;

	@Autowired
	private RuleSetService ruleService;



	@Autowired
	private UserGroupDao userGroupDao;

	@Autowired
	private GroupUserRelationDao groupUserRelationDao;


	@Autowired
	private TeamDao teamDao;




	public PermissionTree getUsersPermissonTree(String userID){

		return ruleService.getUserPermissionTree(userID);
	}


	public void createAdmin(String adminName,String password){

		BeehiveUser  admin=new BeehiveUser();

		admin.setUserName(adminName);
		String hashedPwd=admin.getHashedPwd(password);
		admin.setUserPassword(hashedPwd);

		userDao.addEntity(admin);

		String loginID=kiiUserService.addBeehiveUser(admin,admin.getUserPassword());

		admin.setKiiUserID(loginID);

		userDao.updateEntity(admin,admin.getId());

	}

	public Map<String,Object>  addUser(BeehiveUser user,String teamName) {


		BeehiveUser existsUser=userDao.getUserByName(user.getUserName());

		if(existsUser!=null){
			throw new IllegalArgumentException("the username had existed,please change a loginName or email or phone Number");
		}

		userDao.addKiiEntity(user);

		String loginID=kiiUserService.addBeehiveUser(user,user.getDefaultPassword());

		user.setKiiUserID(loginID);

		String token= StringRandomTools.getRandomStr(6);

		user.setActivityToken(user.getHashedPwd(token));

		userDao.updateEntity(user,user.getId());

		Map<String,Object> result=new HashMap<>();

		if(!StringUtils.isEmpty(teamName)){
			Long teamID=addTeam(teamName,user.getId());
			result.put("teamID",teamID);
		}

		result.put("userID",user.getId());
		result.put("activityToken",token);


		return result;
	}


	private Long addTeam(String teamName,String userID){

			List<Team> teamList = teamDao.findTeamByTeamName(teamName);

			Long teamID=null;
			if(teamList.isEmpty()){
				Team t = new Team();
				t.setName(teamName);
				teamID = teamDao.saveOrUpdate(t);
				TeamUserRelation tur = new TeamUserRelation(teamID, userID, 1);
				teamUserRelationDao.saveOrUpdate(tur);

			}else if(teamList.size()==1){// user add to team
				teamID = teamList.get(0).getId();
				TeamUserRelation tur = new TeamUserRelation(teamID, userID, 0);
				teamUserRelationDao.saveOrUpdate(tur);
			}else{
				throw new IllegalArgumentException();
			}
			return teamID;

	}



	public void deleteUser(String userID) {
		checkTeam(userID);
		BeehiveUser user = userDao.getUserByID(userID);

		groupUserRelationDao.delete(userID, null);

		kiiUserService.disableBeehiveUser(user);

		userDao.deleteUser(userID);


	}

	public BeehiveUser getUserByIDDirectly(String userID){
		return userDao.getUserByID(userID);
	}



	public BeehiveUser getUserByID(String userID) {

		checkTeam(userID);
		return userDao.getUserByID(userID);
	}


	public void updateUser(BeehiveUser user,String userID){

//		checkTeam(userID);
		userDao.updateEntity(user,userID);


	}


	public List<BeehiveUser> simpleQueryUser(Map<String, Object> queryMap) {

		if (queryMap.isEmpty()) {
			return userDao.getAllUsers();
		} else {
			return userDao.getUsersBySimpleQuery(queryMap);
		}
	}


	private void checkTeam(String userID){
		if(AuthInfoStore.isTeamIDExist()){
			TeamUserRelation tur = teamUserRelationDao.findByTeamIDAndUserID(AuthInfoStore.getTeamID(), userID);
			if(tur == null){
				throw new UnauthorizedException(UnauthorizedException.NOT_IN_CURR_TEAM);
			}
		}
	}
	



}
