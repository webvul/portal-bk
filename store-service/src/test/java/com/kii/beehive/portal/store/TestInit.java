package com.kii.beehive.portal.store;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:com/kii/beehive/portal/store/testStoreContext.xml" })
public class TestInit {


//	@Autowired
//	private LandLordRepository  landLordRep;
//
//	@Autowired
//	private TokenRepository tokenRep;
//
//	@Autowired
//	private ThingInfoRepository thingRep;

	@Before
	public void init(){

	}

	@Test
	public void  testInit(){


	}
}
