package com.kii.beehive.portal.store;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kii.beehive.portal.store.repositories.LandLordRepository;
import com.kii.beehive.portal.store.repositories.ThingInfoRepository;
import com.kii.beehive.portal.store.repositories.TokenRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:com/kii/beehive/portal/store/testStoreContext.xml" })
public class TestInit {


	@Autowired
	private LandLordRepository  landLordRep;

	@Autowired
	private TokenRepository tokenRep;

	@Autowired
	private ThingInfoRepository thingRep;


	@Test
	public void  testInit(){


	}
}
