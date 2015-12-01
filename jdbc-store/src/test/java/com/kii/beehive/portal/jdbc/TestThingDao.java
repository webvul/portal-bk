package com.kii.beehive.portal.jdbc;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.jdbc.dao.GlobalThingDao;

public class TestThingDao extends TestTemplate{

	@Autowired
	private GlobalThingDao dao;

	@Test
	public void testFoo(){

		dao.test();
	}
//
//	@Test
//	public void testInsert(){
//
//		dao.insertThing();
//	}
}
