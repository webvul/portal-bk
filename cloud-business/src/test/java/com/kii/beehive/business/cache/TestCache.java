package com.kii.beehive.business.cache;

import static junit.framework.TestCase.assertEquals;

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


		for(int i=0;i<5;i++){

			demo.addFoo(i,"str"+i);
		}

		assertEquals("str3",demo.getValue(3));

		demo.addFoo(10,"str10");


		assertEquals("str10",demo.getValue(10));

		assertEquals("str4",demo.getValue(4));

		demo.addFoo(4,"str40");

		assertEquals("str40",demo.getValue(4));

		demo.setZero("zero");

		assertEquals("zero",demo.getZero());



	}
}
