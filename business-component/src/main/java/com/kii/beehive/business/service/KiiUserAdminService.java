package com.kii.beehive.business.service;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.KiiUser;
import com.kii.extension.sdk.service.UserService;

@Component
@BindAppByName(appName="master",bindAdmin=true)
public class KiiUserAdminService {



	@Autowired
	private UserService userService;


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


	public void removeBeehiveUser(String kiiUserID) {


		userService.removeUserByID(kiiUserID);
	}

	public void disableBeehiveUser(BeehiveJdbcUser user) {
		userService.disableUser(user.getKiiUserID());
	}


	public void enableBeehiveUser(String kiiUserID) {

		userService.enableUser(kiiUserID);
	}
}
