package com.kii.beehive.business;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kii.beehive.business.event.ListenerEnvInitService;
import com.kii.beehive.business.manager.AppInfoManager;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.store.entity.CallbackUrlParameter;
import com.kii.beehive.portal.store.entity.KiiAppInfo;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:com/kii/beehive/business/testComponentContext.xml" })
public class TestInit {


//	@Autowired
//	private LandLordRepository  landLordRep;
//
//	@Autowired
//	private TokenRepository tokenRep;
//
//	@Autowired
//	private ThingInfoRepository thingRep;



	@Autowired
	ListenerEnvInitService  envInitService;

	@Autowired
	AppInfoManager  appInfoManager;

	@Autowired
	private AppInfoDao  appDao;

	@Before
	public void init(){

		System.setProperty("spring.profile","local");

	}


	public static final String STATE_CHANGED = "stateChanged";
	public static final String THING_CREATED = "thingCreated";
	public static final String THING_CMD_RESPONSE="commandResponse";

	public static final String CALLBACK_URL="/callback";

	@Test
	public void  testInit(){

		KiiAppInfo appInfo=appDao.getAppInfoByID("ec08d20c");

		CallbackUrlParameter param = new CallbackUrlParameter();
		param.setStateChange(STATE_CHANGED);
		param.setThingCreated(THING_CREATED);
		param.setCommandResponse(THING_CMD_RESPONSE);


		param.setBaseUrl("localhost");

		envInitService.deployTrigger(appInfo.getAppInfo(),param);


	}
}
