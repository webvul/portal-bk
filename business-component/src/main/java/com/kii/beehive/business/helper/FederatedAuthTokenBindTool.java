package com.kii.beehive.business.helper;

import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.service.PortalSyncUserDao;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.context.TokenBindTool;
import com.kii.extension.sdk.service.FederatedAuthService;

public class FederatedAuthTokenBindTool implements TokenBindTool {


	@Autowired
	private AppBindToolResolver resolver;

	@Autowired
	private FederatedAuthService  authService;


	@Autowired
	private PortalSyncUserDao userDao;


	private ThreadLocal<String>  idLocal=new ThreadLocal<String>();

	public void setLoginInfo(String userID){

	}

	@Override
	public String getToken() {
		return null;
	}
}
