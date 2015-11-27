package com.kii.extension.sdk.test;


import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.factory.LocalPropertyBindTool;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.context.TokenBindToolResolver;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.entity.FederatedAuthResult;
import com.kii.extension.sdk.entity.SiteType;
import com.kii.extension.sdk.service.AppMasterSalveService;
import com.kii.extension.sdk.service.DataService;
import com.kii.extension.sdk.service.FederatedAuthService;
import com.kii.extension.sdk.service.UserService;

public class TestFederate extends TestTemplate {

	private Logger log= LoggerFactory.getLogger(TestFederate.class);

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

		String slaveApp="test-slave-1";
//		AppInfo salve=bindTool.getAppInfo(slaveApp);
////		AppInfo master=bindTool.getAppInfo("test-master");
//
//
//		String url=service.getAuthUrl(salve);
//
//
//		FederatedAuthResult result=service.generAuthRequest(url, SiteType.BH01A, "demo", "qwerty");
//
//		resolver.setAppName(slaveApp);
//
//		tokenResolver.bindToken(result.getAppAuthToken());

		service.loginSalveApp(slaveApp,"demo","qwerty");

		Map<String,String> obj=new HashMap<>();
		obj.put("foo","bar");

		dataService.createObject(obj,new BucketInfo("demo"));

//		log.info(code);

//		resolver.setAppName("test-master");



//		masterSalveService.
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



	}


}
