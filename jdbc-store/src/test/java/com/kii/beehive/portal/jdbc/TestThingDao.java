package com.kii.beehive.portal.jdbc;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.jdbc.dao.GlobalThingDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;

public class TestThingDao extends TestTemplate{

	@Autowired
	private GlobalThingDao dao;
	
	private GlobalThingInfo  thing=new GlobalThingInfo();
	
	@Before
	public void init(){
		thing.setVendorThingID("demo_vendor_thing_id");
		thing.setKiiAppID("appID");
		thing.setCustom("custom");
		thing.setType("type");
		thing.setStatus("this is a test about long text,we don't know the final required,so....");
		long id=dao.saveOrUpdate(thing);
		thing.setId(id);
	}
	
	@Test
	public void testFoo(){
		dao.test();
	}

	@Test
	public void testFindByID(){

		GlobalThingInfo  entity=dao.findByID(thing.getId());
		assertEquals(thing.getVendorThingID(),entity.getVendorThingID());
		assertEquals(thing.getKiiAppID(),entity.getKiiAppID());
		assertEquals(thing.getCustom(),entity.getCustom());
		assertEquals(thing.getType(),entity.getType());
		assertEquals(thing.getStatus(),entity.getStatus());

	}
	
	@Test
	public void testFindByIDs(){
		thing.setVendorThingID("demo_vendor_thing_id2");
		thing.setKiiAppID("appID2");
		thing.setStatus("1");
		long id2=dao.saveOrUpdate(thing);
		
		List<GlobalThingInfo>  list=dao.findByIDs(new Object[]{thing.getId(),id2});
		assertEquals(2,list.size());
	}
	
	@Test
	public void testDelete(){
		dao.deleteByID(thing.getId());
		GlobalThingInfo  entity=dao.findByID(thing.getId());
		assertNull(entity);
	}
	
	@Test
	public void testIsIdExist(){
		boolean b = dao.IsIdExist(thing.getId());
		assertTrue(b);
	}
	
	@Test
	public void testGetThingByVendorThingID(){
		GlobalThingInfo  entity = dao.getThingByVendorThingID(thing.getVendorThingID());
		assertEquals(thing.getVendorThingID(),entity.getVendorThingID());
		assertEquals(thing.getKiiAppID(),entity.getKiiAppID());
		assertEquals(thing.getCustom(),entity.getCustom());
		assertEquals(thing.getType(),entity.getType());
		assertEquals(thing.getStatus(),entity.getStatus());
	}
}