package com.kii.beehive.business.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
public class KiiUserService {

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




	public BeehiveUser addBeehiveUser(BeehiveUser beehiveUser){

		KiiUser user=new KiiUser();

		user.setDisplayName(beehiveUser.getUserName());

		user.setLoginName(beehiveUser.getUserName());

		String pwd=getPassword(beehiveUser);
		user.setPassword(pwd);

		String kiiUserID = userService.createUser(user);

		beehiveUser.setKiiUserID(kiiUserID);
		beehiveUser.setKiiLoginName(user.getLoginName());

		return beehiveUser;
	}

	private String getPassword(BeehiveUser  user){

		return DigestUtils.sha1Hex(user.getKiiLoginName()+"_username_"+user.getId()+"_beehive");
	}

	public String bindToUser(BeehiveUser user){

		LoginInfo loginInfo=userService.login(user.getKiiLoginName(),getPassword(user));

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
