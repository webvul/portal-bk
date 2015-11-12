package com.kii.beehive.portal.store;

import static junit.framework.TestCase.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.manager.AppInfoManager;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.service.DemoCrossAppDao;
import com.kii.beehive.portal.store.entity.KiiAppInfo;
import com.kii.extension.sdk.entity.AppInfo;

public class TestAppInfo extends TestInit {

	@Autowired
	private AppInfoManager appDao;

	@Autowired
	private DemoCrossAppDao crossDao;

	@Before
	public void before(){


	}

	@Test
	public void testFillAppInfo(){


		appDao.initDataWithDevPortal("steven.jiang@kii.com","1qaz2wsx");
	}

	@Test
	public void createRelation(){

		appDao.setMasterSalve("master-test","portal");
	}

	@Test
	public void testOAuth(){



	}
}
