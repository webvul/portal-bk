package com.kii.beehive.portal.store;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.manager.ThingManager;
import com.kii.beehive.portal.service.GlobalThingDao;
import com.kii.beehive.portal.service.TagIndexDao;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.TagThingIndex;

public class TestGlobalThingDao extends TestInit {

	@Autowired
	private GlobalThingDao thingDao;

	@Autowired
	private ThingManager mang;

	@Autowired
	private TagIndexDao tagDao;

	@Before
	public void addData(){


		GlobalThingInfo thing=new GlobalThingInfo();

		thing.setId("001");
		thing.setAppID("a");


		thingDao.addKiiEntity(thing);

		thing.setId("002");
		thing.setAppID("b");

		thingDao.addKiiEntity(thing);


		TagThingIndex tag=new TagThingIndex();

		tag.setId("sys-demo");

		tagDao.addKiiEntity(tag);

		tag.setId("sys-hello");

		tagDao.addKiiEntity(tag);

	}

	@Test
	public void addTag(){

		mang.bindTagToThing("sys-demo","001");

	}

	@Test
	public void addTags(){

		mang.bindTagToThing(new String[]{"sys-demo","sys-hello"},new String[]{"001","002"});


	}



	@After
	public void cleanData(){

//		thingDao.removeEntity("001");
//		tagDao.removeEntity("sys-demo");
	}

}
