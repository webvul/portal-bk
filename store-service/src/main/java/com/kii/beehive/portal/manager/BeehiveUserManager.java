package com.kii.beehive.portal.manager;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.business.service.KiiUserService;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.StringRandomTools;
import com.kii.beehive.portal.exception.ObjectNotFoundException;
import com.kii.beehive.portal.jdbc.dao.GroupUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.TeamUserRelationDao;
import com.kii.beehive.portal.jdbc.entity.TeamUserRelation;
import com.kii.beehive.portal.service.BeehiveUserDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;

@Component
@Transactional
public class BeehiveUserManager {




	@Autowired
	protected TeamUserRelationDao teamUserRelationDao;
	@Autowired
	private KiiUserService kiiUserService;

	@Autowired
	private BeehiveUserDao userDao;


	@Autowired
	private GroupUserRelationDao groupUserRelationDao;




	public Map<String,Object>  addUser(BeehiveUser user) {


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

		result.put("userID",user.getId());
		result.put("activityToken",token);

		return result;
	}


	public void deleteUser(String userID) {
		checkTeam(userID);
		BeehiveUser user = userDao.getUserByID(userID);

		//this.removeUserFromUserGroup(userID, user.getGroups());

		groupUserRelationDao.delete(userID, null);

		kiiUserService.disableBeehiveUser(user);

		userDao.deleteUser(userID);


	}

	public BeehiveUser getUserByID(String userID) {

		checkTeam(userID);
		return userDao.getUserByID(userID);
	}


	public void updateUser(BeehiveUser user,String userID){

		checkTeam(userID);
		userDao.updateEntity(user,userID);


	}


	private void checkTeam(String userID){
		if(AuthInfoStore.isTeamIDExist()){
			TeamUserRelation tur = teamUserRelationDao.findByTeamIDAndUserID(AuthInfoStore.getTeamID(), userID);
			if(tur == null){
				throw new ObjectNotFoundException( "userID:" + userID + " Not Found");
			}
		}
	}
	



}
