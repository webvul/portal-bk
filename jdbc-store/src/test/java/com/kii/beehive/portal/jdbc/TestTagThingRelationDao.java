package com.kii.beehive.portal.jdbc;

import static junit.framework.TestCase.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.jdbc.dao.GlobalThingDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.TagThingRelationDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagThingRelation;
import com.kii.beehive.portal.jdbc.entity.TagType;

public class TestTagThingRelationDao extends TestTemplate{
	
	@Autowired
	private TagThingRelationDao dao;
	
	@Autowired
	private TagIndexDao tagIndexDao;
	
	@Autowired
	private GlobalThingDao globalThingDao;
	
	private TagThingRelation rel =new TagThingRelation();
	
	@Before
	public void init(){
		TagIndex  tag =new TagIndex();
		tag.setDisplayName("DisplayName");
		tag.setTagType(TagType.Custom);
		tag.setDescription("Description");
		long tagID=tagIndexDao.saveOrUpdate(tag);
		
		GlobalThingInfo  thing=new GlobalThingInfo();
		thing.setVendorThingID("demo_vendor_thing_id");
		thing.setKiiAppID("appID");
		thing.setCustom("custom");
		thing.setType("type");
		thing.setStatus("1");
		long thingID=globalThingDao.saveOrUpdate(thing);
		
		rel.setTagID(tagID);
		rel.setThingID(thingID);
		long id = dao.saveOrUpdate(rel);
		rel.setId(id);
	}
	
	@Test
	public void testFindByID(){
		TagThingRelation  entity=dao.findByID(rel.getId());
		assertEquals(rel.getTagID(),entity.getTagID());
		assertEquals(rel.getThingID(),entity.getThingID());
	}
	
	@Test
	public void testFindThingByTag(){
		List<GlobalThingInfo> gList = globalThingDao.findThingByTag(TagType.Custom.toString(), "DisplayName");
		System.out.println(gList.get(0));
		assertEquals(1,gList.size());
		
	}
	
	
}
