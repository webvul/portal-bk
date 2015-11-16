package com.kii.beehive.portal.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.annotation.AppBindParam;
import com.kii.beehive.portal.annotation.BindAppByName;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.extension.sdk.entity.KiiUser;
import com.kii.extension.sdk.entity.LoginInfo;
import com.kii.extension.sdk.exception.KiiCloudException;
import com.kii.extension.sdk.service.AbstractDataAccess;
import com.kii.extension.sdk.service.UserService;

@Component
@BindAppByName(appName="master")
public class KiiUserSyncDao {

	@Autowired
	private UserService userService;

	public String addBeehiveUser(BeehiveUser beehiveUser,String pwd){

		KiiUser user=new KiiUser();

		user.setPassword(pwd);

		user.setLoginName(beehiveUser.getUserName());


		return userService.createUser(user);

	}

	public String bindToUser(BeehiveUser user){


		String userID=user.getKiiUserID();

		String pwd=DigestUtils.sha1Hex(user.getBeehiveUserID()+"_beehive");

		LoginInfo loginInfo=userService.login(userID,pwd);

		return loginInfo.getToken();
	}

	public void removeBeehiveUser(String beehiveUserID){

		userService.removeUserByLoginName(beehiveUserID);
	}


}
