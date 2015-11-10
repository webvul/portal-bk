package com.kii.beehive.portal.store;

import static junit.framework.TestCase.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.manager.ThingManager;
import com.kii.beehive.portal.service.GlobalThingDao;
import com.kii.beehive.portal.service.TagIndexDao;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.TagIndex;
import com.kii.beehive.portal.store.entity.TagType;

public class TestGlobalThingDao extends TestInit {
	
	
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
		thing.setKiiAppID("a");

		thingDao.addKiiEntity(thing);

		thing.setId("002");
		thing.setKiiAppID("b");

		thingDao.addKiiEntity(thing);
		
		thing.setId("003");
		thing.setKiiAppID("c");

		thingDao.addKiiEntity(thing);


		TagIndex tag=new TagIndex();
		tag.setTagType(TagType.System.toString());
		tag.setDisplayName("demo1");
		tagIndexDao.addKiiEntity(tag);

		tag.setTagType(TagType.System.toString());
		tag.setDisplayName("demo2");
		tagIndexDao.addKiiEntity(tag);
	}
	
	@Test
	public void addTag() throws Exception{
		TagIndex tag=new TagIndex();
		tag.setTagType(TagType.System.toString());
		tag.setDisplayName("demo1");
		
		thingManager.bindTagToThing(tag.getId(),"001");
		
		//tag = tagIndexDao.getObjectByID(tag.getId());
		//System.out.println(tag.toString());
		
		assertEquals(1,tag.getGlobalThings().size());
		assertEquals(1,tag.getKiiAppIDs().size());
		
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
		
		TagIndex tag = tagIndexDao.getObjectByID(tag1.getId());
		assertEquals(2,tag.getGlobalThings().size());
		assertEquals(2,tag.getKiiAppIDs().size());
		
		tag = tagIndexDao.getObjectByID(tag2.getId());
		assertEquals(2,tag.getGlobalThings().size());
		assertEquals(2,tag.getKiiAppIDs().size());
		
		//String json=mapper.writeValueAsString(tag);
		//System.out.println(json);
	}



	@After
	public void cleanData(){

//		thingDao.removeEntity("001");
//		tagDao.removeEntity("sys-demo");
	}

}
