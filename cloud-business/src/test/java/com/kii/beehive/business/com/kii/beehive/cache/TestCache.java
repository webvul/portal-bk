package com.kii.beehive.business.com.kii.beehive.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:com/kii/beehive/business/testStoreContext.xml" })
public class TestCache {

	@Autowired
	private Demo demo;



	@Test
	public void test() throws InterruptedException {

		int i=0;
		while(true){


			System.out.println(demo.getFoo(i));

			Thread.sleep(1000);


		}


	}
}
