package com.kii.beehive.portal.store;

import static junit.framework.TestCase.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.manager.ThingManager;
import com.kii.beehive.portal.service.GlobalThingDao;
import com.kii.beehive.portal.service.TagIndexDao;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.TagIndex;
import com.kii.beehive.portal.store.entity.TagType;

public class TestGlobalThingDao extends TestInit {
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private GlobalThingDao thingDao;

	@Autowired
	private ThingManager thingManager;

	@Autowired
	private TagIndexDao tagIndexDao;

	@Before
	public void addData(){


		GlobalThingInfo thing=new GlobalThingInfo();
		thing.setId("001");
		thing.setAppID("a");


		thingDao.addThingInfo(thing);

		thing.setId("002");
		thing.setAppID("b");

		thingDao.addThingInfo(thing);
		
		thing.setId("003");
		thing.setAppID("c");

		thingDao.addThingInfo(thing);


		TagIndex tag=new TagIndex();
		tag.setTagType(TagType.System.toString());
		tag.setDisplayName("demo1");
		tagIndexDao.addTagIndex(tag);

		tag.setTagType(TagType.System.toString());
		tag.setDisplayName("demo2");
		tagIndexDao.addTagIndex(tag);
	}

	@Test
	public void addTag() throws Exception{
		TagIndex tag=new TagIndex();
		tag.setTagType(TagType.System.toString());
		tag.setDisplayName("demo1");
		
		thingManager.bindTagToThing(tag.getId(),"001");
		
		//tag = tagIndexDao.getObjectByID(tag.getId());
		//String json=mapper.writeValueAsString(tag);
		//System.out.println(json);
		
		assertEquals(1,tag.getGlobalThings().size());
		assertEquals(1,tag.getAppIDs().size());
		
	}

	@Test
	public void addTags() throws Exception{
		TagIndex tag1=new TagIndex();
		tag1.setTagType(TagType.System.toString());
		tag1.setDisplayName("demo1");
		
		TagIndex tag2=new TagIndex();
		tag2.setTagType(TagType.System.toString());
		tag2.setDisplayName("demo2");
		
		thingManager.bindTagToThing(new String[]{tag1.getId(),tag2.getId()},new String[]{"001","002"});
		
		TagIndex tag = tagIndexDao.getTagIndexByID(tag1.getId());
		assertEquals(2,tag.getGlobalThings().size());
		assertEquals(2,tag.getAppIDs().size());
		
		tag = tagIndexDao.getTagIndexByID(tag2.getId());
		assertEquals(2,tag.getGlobalThings().size());
		assertEquals(2,tag.getAppIDs().size());
		
		//String json=mapper.writeValueAsString(tag);
		//System.out.println(json);
	}



	@After
	public void cleanData(){

//		thingDao.removeEntity("001");
//		tagDao.removeEntity("sys-demo");
	}

}
