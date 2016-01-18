package com.kii.beehive.portal.jdbc;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.dao.GlobalThingDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.TagThingRelationDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.Permission;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagThingRelation;
import com.kii.beehive.portal.jdbc.entity.TagType;

public class TestThingDao extends TestTemplate{

	@Autowired
	private GlobalThingDao dao;
	
	@Autowired
	private TagIndexDao tagIndexDao;
	
	@Autowired
	private TagThingRelationDao tagThingRelationDao;
	
	private GlobalThingInfo  thing=new GlobalThingInfo();
	
	@Before
	public void init(){
		thing.setVendorThingID("demo_vendor_thing_id");
		thing.setKiiAppID("appID");
		thing.setCustom("custom");
		thing.setType("type");
		thing.setStatus("this is a test about long text,we don't know the final required,so....");

		String fullKiiThingID= ThingIDTools.joinFullKiiThingID("abcdefghijk","appID");
		thing.setFullKiiThingID(fullKiiThingID);
		long id=dao.saveOrUpdate(thing);
		thing.setId(id);
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
		GlobalThingInfo thing2 = new GlobalThingInfo();
		thing2.setVendorThingID("demo_vendor_thing_id2");
		thing2.setKiiAppID("appID2");
		thing2.setStatus("1");

		long id2=dao.saveOrUpdate(thing2);

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
	public void testGetThingByVendorThingID(){
		GlobalThingInfo  entity = dao.getThingByVendorThingID(thing.getVendorThingID());
		assertEquals(thing.getVendorThingID(),entity.getVendorThingID());
		assertEquals(thing.getKiiAppID(),entity.getKiiAppID());
		assertEquals(thing.getCustom(),entity.getCustom());
		assertEquals(thing.getType(),entity.getType());
		assertEquals(thing.getStatus(),entity.getStatus());
	}

	@Test
	public void testUpdate(){
		thing.setVendorThingID("demo_vendor_thing_id_update");
		thing.setCustom("customUpdate");
		thing.setType("typeUpdate");
		thing.setStatus("Update");
		dao.update(thing);
		GlobalThingInfo  entity=dao.findByID(thing.getId());
		assertEquals(thing.getVendorThingID(),entity.getVendorThingID());
		assertEquals(thing.getKiiAppID(),entity.getKiiAppID());
		assertEquals(thing.getCustom(),entity.getCustom());
		assertEquals(thing.getType(),entity.getType());
		assertEquals(thing.getStatus(),entity.getStatus());
	}
	
	@Test
	public void testFindAllThingTypes() {
		List<String> list = dao.findAllThingTypes();
		assertTrue(list.size() > 0);
	}
	
	@Test
	public void testFindAllThingTypesWithThingCount() {
		List<Map<String, Object>> list = dao.findAllThingTypesWithThingCount();
		assertTrue(list.size() > 0);
	}
	
	@Test
	public void testGetThingByType() {
		List<GlobalThingInfo> list = dao.getThingByType(thing.getType());
		GlobalThingInfo  entity=list.get(0);
		assertEquals(thing.getVendorThingID(),entity.getVendorThingID());
		assertEquals(thing.getKiiAppID(),entity.getKiiAppID());
		assertEquals(thing.getCustom(),entity.getCustom());
		assertEquals(thing.getType(),entity.getType());
		assertEquals(thing.getStatus(),entity.getStatus());
	}
	
	/*@Test
	public void testFindThingByTag() {
		TagIndex  tag =new TagIndex();
		tag.setDisplayName("DisplayNameTest");
		tag.setTagType(TagType.Custom);
		tag.setDescription("DescriptionTest");
		long id=tagIndexDao.saveOrUpdate(tag);
		tag.setId(id);
		
		TagThingRelation rel =new TagThingRelation();
		rel.setTagID(tag.getId());
		rel.setThingID(thing.getId());
		tagThingRelationDao.saveOrUpdate(rel);
		
		List<GlobalThingInfo> list = dao.findThingByTag(tag.getDisplayName());
		assertTrue(list.size() > 0);
		GlobalThingInfo  entity=list.get(0);
		assertEquals(thing.getVendorThingID(),entity.getVendorThingID());
		assertEquals(thing.getKiiAppID(),entity.getKiiAppID());
		assertEquals(thing.getCustom(),entity.getCustom());
		assertEquals(thing.getType(),entity.getType());
		assertEquals(thing.getStatus(),entity.getStatus());
	}*/
}
