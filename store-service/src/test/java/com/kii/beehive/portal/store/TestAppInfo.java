package com.kii.beehive.portal.store;

import static junit.framework.TestCase.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.service.DemoCrossAppDao;
import com.kii.beehive.portal.store.entity.KiiAppInfo;
import com.kii.extension.sdk.entity.AppInfo;

public class TestAppInfo extends TestInit {

	@Autowired
	private AppInfoDao appDao;

	@Autowired
	private DemoCrossAppDao crossDao;

	@Before
	public void before(){


	}

	@Test
	public void testAppInfo(){

		appDao.init();

		AppInfo appInfo=appDao.getAppInfo("app1");

//		KiiAppInfo info=new KiiAppInfo();
//		info.setAppName("foo");
//		appDao.addEntity(info);

	}

	@Test
	public void testDynamicApp(){

		DemoCrossAppDao.FooEntity foo=new DemoCrossAppDao.FooEntity();
		foo.setName("hello");

		String appName1="test-slave-1";
		String appName2="test-slave-2";

		String id1=crossDao.addData(appName1,foo);

		String id2=crossDao.addData(appName2,foo);

		DemoCrossAppDao.FooEntity newFoo=crossDao.getData(appName1,id1);

		assertEquals(newFoo.getName(),"hello");


	}
}
