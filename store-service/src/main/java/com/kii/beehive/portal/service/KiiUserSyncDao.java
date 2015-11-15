package com.kii.beehive.portal.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.annotation.AppBindParam;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.extension.sdk.entity.KiiUser;
import com.kii.extension.sdk.entity.LoginInfo;
import com.kii.extension.sdk.service.UserService;

import java.util.Set;

@Component
public class KiiUserSyncDao {

	@Autowired
	private UserService userService;

	public String addBeehiveUser(BeehiveUser beehiveUser,@AppBindParam String appName){

		KiiUser user=new KiiUser();

		String pwd= DigestUtils.sha1Hex(beehiveUser.getBeehiveUserID()+"_beehive");

		user.setPassword(pwd);

		user.setCustomProp("beehiveUserID", beehiveUser.getBeehiveUserID());


		return userService.createUser(user);


	}

	public String bindToUser(BeehiveUser user,@AppBindParam String appName){


		String userID=user.getKiiUserID();

		String pwd=DigestUtils.sha1Hex(user.getBeehiveUserID()+"_beehive");

		LoginInfo loginInfo=userService.login(userID,pwd);

		return loginInfo.getToken();
	}

	public void disableBeehiveUser(BeehiveUser user,  @AppBindParam String appName) {

		userService.disableUser(user.getKiiUserID());

	}


}
