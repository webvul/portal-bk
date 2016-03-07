package com.kii.beehive.portal.service;

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.KiiUser;
import com.kii.extension.sdk.entity.LoginInfo;
import com.kii.extension.sdk.exception.BadUserNameException;
import com.kii.extension.sdk.exception.UserNotFoundException;
import com.kii.extension.sdk.service.UserService;


/**
 * Tech Design - Beehive API
 * User Sync Util to Kii Master App (同步用户信息到Kii Master(Federated) App)
 *
 * refer to doc "Tech Design - Beehive API" for details
 */
@Component
@BindAppByName(appName="master")
public class KiiUserSyncDao {

	@Autowired
	private UserService userService;

	/**
	 * important:
	 * as the existing user with the same login name will be removed,
	 * this API is supposed to be called only when initialize the environment
	 */
	public String addDefaultOwner(String name,String pwd){



		try {
			LoginInfo info = userService.login(name, pwd);
			return info.getUserID();
		}catch(UserNotFoundException | BadUserNameException e){
			KiiUser user=new KiiUser();

			user.setLoginName(name);
			user.setPassword(pwd);
			return userService.createUser(user);
		}

	}

	public void addBeehiveUser(BeehiveUser beehiveUser){

		KiiUser user=new KiiUser();

		user.setDisplayName(beehiveUser.getUserName());

		if(!StringUtils.isEmpty(beehiveUser.getAliUserID())) {
			user.setLoginName(beehiveUser.getAliUserID());
		}else{
			String loginName= new String(Hex.encodeHex(beehiveUser.getUserName().getBytes(Charsets.UTF_8)));
			user.setLoginName(loginName);
		}

		String pwd=DigestUtils.sha1Hex(user.getLoginName()+"_beehive");
		user.setPassword(pwd);

		String kiiUserID = userService.createUser(user);

		beehiveUser.setKiiUserID(kiiUserID);
		beehiveUser.setKiiLoginName(user.getLoginName());

		if(StringUtils.isEmpty(beehiveUser.getAliUserID())){
			beehiveUser.setAliUserID(kiiUserID);
		}
	}

	public String bindToUser(BeehiveUser user){


		String userID=user.getKiiLoginName();

		String pwd=DigestUtils.sha1Hex(user.getKiiLoginName()+"_beehive");

		LoginInfo loginInfo=userService.login(userID,pwd);

		return loginInfo.getToken();
	}

	public void removeBeehiveUser(String kiiUserID) {

		userService.removeUserByID(kiiUserID);
	}

	public void disableBeehiveUser(BeehiveUser user) {

		userService.disableUser(user.getKiiUserID());

	}


	public void enableUser(String kiiUserID) {

		userService.enableUser(kiiUserID);
	}


}
