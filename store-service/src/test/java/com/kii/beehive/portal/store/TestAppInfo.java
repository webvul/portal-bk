package com.kii.beehive.portal.store;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.StoreServiceTestInit;
import com.kii.beehive.portal.service.DemoCrossAppDao;

@Ignore("just ")
public class TestAppInfo extends StoreServiceTestInit {



	@Autowired
	private DemoCrossAppDao crossDao;

	@Before
	public void before(){


	}

	@Test
	public void testOAuth(){



	}
}
