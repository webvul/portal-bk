package com.kii.beehive.portal.jdbc;

import static junit.framework.TestCase.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.jdbc.dao.GlobalThingDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingEntity;

public class TestThingDao extends TestTemplate{

	@Autowired
	private GlobalThingDao dao;

	@Test
	public void testFoo(){

		dao.test();
	}
//
	@Test
	public void testInsert(){

		GlobalThingEntity  thing=new GlobalThingEntity();
		thing.setVendorThingID("demo_vendor_thing_id");
		thing.setKiiAppID("appID");
		thing.setStatus("this is a test about long text,we don't know the final required,so....");

		dao.insertThing(thing);

	}

	@Test
	public void testInsertAndGet(){

		GlobalThingEntity  thing=new GlobalThingEntity();
		thing.setVendorThingID("demo_vendor_thing_id");
		thing.setKiiAppID("appID");
		thing.setStatus("this is a test about long text,we don't know the final required,so....");

		long id=dao.insertThing(thing);

		GlobalThingEntity  entity=dao.getThingByID(id);

		assertEquals(thing.getStatus(),entity.getStatus());

	}
}
