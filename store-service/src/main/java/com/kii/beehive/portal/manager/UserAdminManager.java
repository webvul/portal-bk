package com.kii.beehive.portal.manager;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.business.service.KiiUserAdminService;
import com.kii.beehive.portal.common.utils.StringRandomTools;
import com.kii.beehive.portal.exception.UserExistException;
import com.kii.beehive.portal.exception.UserNotExistException;
import com.kii.beehive.portal.helper.AuthInfoService;
import com.kii.beehive.portal.jdbc.dao.BeehiveArchiveUserDao;
import com.kii.beehive.portal.jdbc.dao.BeehiveUserJdbcDao;
import com.kii.beehive.portal.jdbc.dao.GroupUserRelationDao;
import com.kii.beehive.portal.jdbc.entity.BeehiveArchiveUser;
import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;

@Component
@Transactional
public class UserAdminManager {



	@Autowired
	private KiiUserAdminService kiiUserAdminService;


	@Autowired
	private GroupUserRelationDao groupUserRelationDao;

	@Autowired
	private BeehiveUserJdbcDao userDao;


	@Autowired
	private AuthInfoService authService;

	@Autowired
	private BeehiveArchiveUserDao archiveUserDao;
	
	
	public Map<String,Object> resetActivate(String userID) {
		
		BeehiveJdbcUser user = userDao.getUserByUserID(userID);
		
		String token = StringRandomTools.getRandomStr(6);
		
		user.setActivityToken(user.getHashedPwd(token));
		
		userDao.updateEntityAllByID(user);
		
		Map<String, Object> result = new HashMap<>();
		
		result.put("user", user);
		result.put("activityToken", token);
		
		return result;
	}


	public Map<String, Object> addUser(BeehiveJdbcUser user) {


		List<BeehiveJdbcUser> existsUser = userDao.getUserByLoginId(user);

		if (existsUser.size()>0) {
			throw new UserExistException(user, existsUser.get(0));
		}

		if (StringUtils.isBlank(user.getRoleName())) {
			user.setRoleName("commUser");
		}

		user.setEnable(false);
		user = userDao.addUser(user);

		String loginID = kiiUserAdminService.addBeehiveUser(user, user.getDefaultPassword());

		user.setKiiUserID(loginID);

		String token = StringRandomTools.getRandomStr(6);

		user.setActivityToken(user.getHashedPwd(token));

		userDao.updateEntityAllByID(user);

		Map<String, Object> result = new HashMap<>();

		result.put("user", user);
		result.put("activityToken", token);


		return result;
	}


	public void updateUser(BeehiveJdbcUser user, String userID) {

//		checkTeam(userID);

		BeehiveJdbcUser oldUser = userDao.getUserByUserID(userID);

		if (oldUser == null) {
			throw new UserNotExistException(userID);
		}

		userDao.updateEntityByID(user, oldUser.getId());


	}


	public void disableUser(String userID) {

		int i = userDao.updateEnableSign(userID, false);
		if (i == 0) {
			throw new UserNotExistException(userID);
		}


		authService.removeTokenByUserID(userID);

	}


	public void removeUser(Long userID) {
		BeehiveJdbcUser user = userDao.getUserByID(userID);
		if (user == null) {
			throw new UserNotExistException(String.valueOf(userID));
		}

		userDao.hardDeleteByID(user.getId());

		groupUserRelationDao.delete(userID, null);

		authService.removeTokenByUserID(user.getUserID());

		kiiUserAdminService.removeBeehiveUser(user.getKiiUserID());

		BeehiveArchiveUser archiveUser = new BeehiveArchiveUser(user);

		archiveUserDao.insert(archiveUser);

	}

	public void updateUserSign(String userID, boolean b) {


		int i = userDao.updateEnableSign(userID, b);
		if (i == 0) {
			throw new UserNotExistException(userID);
		}
	}

	public BeehiveJdbcUser getUserByUserID(String userID) {

		BeehiveJdbcUser user = userDao.getUserByUserID(userID);
		if (user == null) {
			throw new UserNotExistException(String.valueOf(userID));
		}
		return user;
	}
	
	

}
