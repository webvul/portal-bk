package com.kii.beehive.portal.jdbc;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.jdbc.dao.BeehiveUserJdbcDao;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.UserGroupDao;

public class TestSqlDao extends TestTemplate {


	@Autowired
	private GlobalThingSpringDao  thingDao;

	@Autowired
	private UserGroupDao  groupDao;

	@Autowired
	private TagIndexDao tagDao;

	List<Long> ids=new ArrayList<>();


	List<String> tags=new ArrayList<>();

	@PostConstruct
	public void init(){

		ids.add(100l);

		tags.add("a");
		tags.add("b");
	}


	@Autowired
	private BeehiveUserJdbcDao  userDao;
	@Test
	public void testUserDao(){

		userDao.findUserByTagID(100l);

		userDao.findUserByTagName("abc");

		userDao.findUserByTagRelThing(100l);

		userDao.findUserByTags(ids);

		userDao.findUserByThingID(100l);

		userDao.findUserIDByUserGroupID(100l);

		userDao.findUsersByGroups(ids);


	}


	@Test
	public void testTagDao(){

		tagDao.findTagByGlobalThingID(100l);

		tagDao.findTagIdsByCreatorAndFullTagNames(100l,tags);

		tagDao.findTagByFullTagName("abc");

		tagDao.findUserLocations(100l,"abc");

		tagDao.findUserTagByUserID(100l);


	}

	@Test
	public void testGroupDao(){

		groupDao.getAllGroupByRelTagRelThing(10l);

		groupDao.getAllGroupByRelThing(10l);


	}


	@Test
	public void testQueryThingByUser(){


		thingDao.findThingByGroupIDRelUserID(100l);

		thingDao.findThingByGroupIDRelUserIDWithThingID(100l,100l);

		thingDao.findThingByTag("test");

		thingDao.findThingByTagRelUserID(100l);

		thingDao.findThingByUserID(100l);

		thingDao.findThingByUserIDThingID(100l,100l);


		thingDao.findThingIdsByCreator(100l,ids);


		thingDao.findThingTypeByFullTagNames(tags);
		thingDao.findThingTypeBytagIDs("a,b,c");

	}


}
