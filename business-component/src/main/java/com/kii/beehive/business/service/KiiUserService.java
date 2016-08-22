package com.kii.beehive.business.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.context.UserTokenBindTool;
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
@BindAppByName(appName="master",bindAdmin=false)
public class KiiUserService {

	@Autowired
	private UserService userService;

	@Autowired
	private UserTokenBindTool  tokenBind;

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




	public String  addBeehiveUser(BeehiveJdbcUser beehiveUser, String pwd){

		KiiUser user=new KiiUser();

		String displayName=beehiveUser.getDisplayName();

		if(StringUtils.isBlank(displayName)){
			displayName=beehiveUser.getUserName();
		}
		if(displayName.length()>50) {
			displayName=displayName.substring(0,50);
		}
		user.setDisplayName(displayName);


		user.setLoginName(beehiveUser.getKiiCloudLoginName());

		user.setPassword(pwd);

		String kiiUserID = userService.createUser(user);

		return kiiUserID;
	}



	public String bindToUser(BeehiveJdbcUser user, String pwd){

		LoginInfo loginInfo=userService.login(user.getKiiCloudLoginName(),pwd);

		tokenBind.bindToken(loginInfo.getToken());

		return loginInfo.getToken();
	}


	public void changePassword(String oldPwd,String newPwd){

		userService.changePassword(oldPwd,newPwd);

	}


	public KiiUser getKiiUser(String token) {
		tokenBind.bindToken(token);
		return userService.getUserDetail();
	}



	public void removeBeehiveUser(BeehiveJdbcUser  user) {
		LoginInfo loginInfo=userService.login(user.getKiiCloudLoginName(),user.getUserPassword());

		tokenBind.bindToken(loginInfo.getToken());


		userService.removeUserByID(user.getKiiUserID());
	}



//	public KiiUser getKiiUser() {
//		return userService.getUserDetail();
//	}
}
