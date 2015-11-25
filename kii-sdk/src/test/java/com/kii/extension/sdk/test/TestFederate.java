package com.kii.extension.sdk.test;


import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.factory.LocalPropertyBindTool;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.context.TokenBindToolResolver;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.SiteType;
import com.kii.extension.sdk.service.AppMasterSalveService;
import com.kii.extension.sdk.service.DataService;
import com.kii.extension.sdk.service.FederatedAuthService;
import com.kii.extension.sdk.service.UserService;

public class TestFederate extends TestTemplate {

	@Autowired
	private LocalPropertyBindTool  bindTool;

	@Autowired
	private AppBindToolResolver resolver;

	@Autowired
	private TokenBindToolResolver tokenResolver;


	@Autowired
	private FederatedAuthService  service;

	@Autowired
	private UserService userService;

	@Autowired
	private DataService dataService;

	@Autowired
	private AppMasterSalveService  masterSalveService;

	@Test
	public void testRegist(){

		AppInfo master=bindTool.getAppInfo("test-master");


		AppInfo salve=bindTool.getAppInfo("test-slave-1");

		masterSalveService.setMaster(master);
//		masterSalveService.setSalveApp(salve);

		masterSalveService.addSalveAppToMaster(master,salve);



	}

	@Test
	public void testLogin(){

		AppInfo salve=bindTool.getAppInfo("test-slave-1");
		AppInfo master=bindTool.getAppInfo("test-master");


		String url=service.getAuthUrl(salve);


		String code=service.generAuthRequest(url, SiteType.BH01A, "demo", "qwerty");


		resolver.setAppName("test-master");
//
//
//		LoginInfo login=userService.loginWithCode(code,salve.getClientID());
//
//		String token=login.getToken();
//
//		assertNotNull(token);
//
//		tokenResolver.bindToken(token);
//
//		resolver.setAppName("test-slave-1");
//		Map<String,String> obj=new HashMap<>();
//		obj.put("foo","bar");
//
//		dataService.createObject(obj,new BucketInfo("demo"));



	}


}
