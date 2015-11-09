package com.kii.beehive.portal.store;

import static junit.framework.TestCase.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.manager.ThingManager;
import com.kii.beehive.portal.service.GlobalThingDao;
import com.kii.beehive.portal.service.TagIndexDao;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.TagIndex;

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

		thingDao.addKiiEntity(thing);

		thing.setId("002");
		thing.setAppID("b");

		thingDao.addKiiEntity(thing);
		
		thing.setId("003");
		thing.setAppID("c");

		thingDao.addKiiEntity(thing);


		TagIndex tag=new TagIndex();

		tag.setId("sys-demo");

		tagIndexDao.addKiiEntity(tag);

		tag.setId("sys-hello");

		tagIndexDao.addKiiEntity(tag);

	}

	@Test
	public void addTag() throws Exception{
		TagIndex tag = tagIndexDao.getObjectByID("sys-demo");
		String json1=mapper.writeValueAsString(tag);
		System.out.println(json1);
		
		thingManager.bindTagToThing("sys-demo","001");
		
		tag = tagIndexDao.getObjectByID("sys-demo");
		String json=mapper.writeValueAsString(tag);
		System.out.println(json);
		
		assertEquals(1,tag.getGlobalThings().size());
		assertEquals(1,tag.getAppIDs().size());
		
	}

	@Test
	public void addTags() throws Exception{

		thingManager.bindTagToThing(new String[]{"sys-demo","sys-hello"},new String[]{"001","002","003"});
		
		TagIndex tag = tagIndexDao.getObjectByID("sys-hello");
		assertEquals(2,tag.getGlobalThings().size());
		
		tag = tagIndexDao.getObjectByID("sys-hello");
		assertEquals(2,tag.getGlobalThings().size());
		String json=mapper.writeValueAsString(tag);
		System.out.println(json);
	}



	@After
	public void cleanData(){

//		thingDao.removeEntity("001");
//		tagDao.removeEntity("sys-demo");
	}

}
