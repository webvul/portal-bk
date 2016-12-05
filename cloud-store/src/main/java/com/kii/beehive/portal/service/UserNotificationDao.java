package com.kii.beehive.portal.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.UserNotification;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.context.UserTokenBindTool;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.entity.ScopeType;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="master",appBindSource="propAppBindTool",bindUser=true )
@Component
public class UserNotificationDao extends AbstractDataAccess<UserNotification>{

	@Autowired
	private UserTokenBindTool bindTool;

	@Override
	protected BucketInfo getBucketInfo() {

		return new BucketInfo("userNotification", ScopeType.Me,"me");
	}


}
