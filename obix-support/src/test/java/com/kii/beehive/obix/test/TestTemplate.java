package com.kii.beehive.obix.test;


import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:com/kii/beehive/obix/appContext.xml" })
public class TestTemplate {



	@BeforeClass
	public  static void initEnv(){

		System.setProperty("spring.profile","local");

	}

}
