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


		thingDao.addThingInfo(thing);

		thing.setId("002");
		thing.setKiiAppID("b");

		thingDao.addThingInfo(thing);
		
		thing.setId("003");
		thing.setKiiAppID("c");

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
		
		tag = tagIndexDao.getTagIndexByID(tag.getId());
		//System.out.println(tag.getGlobalThings());
		//System.out.println(tag.getKiiAppIDs());
		
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
		
		TagIndex tag = tagIndexDao.getTagIndexByID(tag1.getId());
		assertEquals(2,tag.getGlobalThings().size());
		assertEquals(2,tag.getKiiAppIDs().size());
		
		tag = tagIndexDao.getTagIndexByID(tag2.getId());
		assertEquals(2,tag.getGlobalThings().size());
		assertEquals(2,tag.getKiiAppIDs().size());
		
		//String json=mapper.writeValueAsString(tag);
		//System.out.println(json);
	}
	
	@Test
	public void removeTag() throws Exception{
		TagIndex tag=new TagIndex();
		tag.setTagType(TagType.System.toString());
		tag.setDisplayName("demo1");
		
		thingManager.bindTagToThing(tag.getId(),"001");
		
		//tag = tagIndexDao.getObjectByID(tag.getId());
		//System.out.println(tag.getGlobalThings());
		//System.out.println(tag.getKiiAppIDs());
		
		thingManager.unbindTagToThing(tag.getId(), "001");
		tag = tagIndexDao.getTagIndexByID(tag.getId());
		//System.out.println(tag.getGlobalThings());
		//System.out.println(tag.getKiiAppIDs());
		
		assertEquals(0,tag.getGlobalThings().size());
		assertEquals(0,tag.getKiiAppIDs().size());
	}



	@After
	public void cleanData(){

//		thingDao.removeEntity("001");
//		tagDao.removeEntity("sys-demo");
	}

}
