package com.kii.beehive.portal.manager;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.service.BeehiveUserDao;
import com.kii.beehive.portal.service.KiiUserSyncDao;
import com.kii.beehive.portal.store.entity.BeehiveGuestLog;
import com.kii.beehive.portal.store.entity.BeehiveUser;

@Component
public class UserManager {

	@Autowired
	private BeehiveUserDao userDao;

	@Autowired
	private KiiUserSyncDao kiiUserDao;


	@Autowired
	private AppInfoDao appInfoDao;

	public String addUser(BeehiveUser user){


		String pwd= DigestUtils.sha1Hex(user.getUserName() + "_beehive");

		String kiiUserID=kiiUserDao.addBeehiveUser(user,pwd);

		user.setKiiUserID(kiiUserID);

		userDao.createUser(user);

		return kiiUserID;

	}


	public void removeUser(String userID){

		userDao.deleteUser(userID);

		kiiUserDao.removeBeehiveUser(userID);
	}



}
