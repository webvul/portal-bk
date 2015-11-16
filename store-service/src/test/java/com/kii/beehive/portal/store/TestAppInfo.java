package com.kii.beehive.portal.store;

import static junit.framework.TestCase.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.helper.AppInfoService;
import com.kii.beehive.portal.service.DemoCrossAppDao;

public class TestAppInfo extends TestInit {

	@Autowired
	private AppInfoService appDao;

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

		appDao.setMasterSalve("test-master");
	}

	@Test
	public void testOAuth(){



	}
}
