package com.kii.beehive.portal.jdbc;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.TeamDao;
import com.kii.beehive.portal.jdbc.dao.TeamThingRelationDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.Team;
import com.kii.beehive.portal.jdbc.entity.TeamThingRelation;

public class TestTeamThingRelationDao extends TestTemplate {

	@Autowired
	private TeamThingRelationDao dao;

	@Autowired
	private GlobalThingSpringDao globalThingDao;
	
	@Autowired
	private TeamDao teamDao;


	private TeamThingRelation rel = new TeamThingRelation();
	private GlobalThingInfo  thing=new GlobalThingInfo();
	private Team team = new Team();

	@Before
	public void init() {

		thing.setVendorThingID("demo_vendor_thing_id");
		thing.setKiiAppID("appID");
		thing.setCustom("custom");
		thing.setType("type");
		thing.setStatus("1");
		long thingID=globalThingDao.saveOrUpdate(thing);
		thing.setId(thingID);
		
		team.setName("TeamTest");
		long teamId = teamDao.saveOrUpdate(team);
		team.setId(teamId);

		rel.setThingID(thing.getId());
		rel.setTeamID(team.getId());
		long id4 = dao.insert(rel);
		rel.setId(id4);
	}

	@Test
	public void testFindByID() {
		TeamThingRelation entity = dao.findByID(rel.getId());
		assertEquals(rel.getTeamID(), entity.getTeamID());
		assertEquals(rel.getThingID(), entity.getThingID());
	}

	@Test
	public void testFindByUserIDAndUserGroupID() {
		TeamThingRelation entity = dao.findByTeamIDAndThingID(rel.getTeamID(), thing.getId());
		assertEquals(rel.getTeamID(), entity.getTeamID());
		assertEquals(rel.getThingID(), entity.getThingID());

		entity = dao.findByTeamIDAndThingID(rel.getTeamID(), null);
		assertNull(entity);
		entity = dao.findByTeamIDAndThingID(null, thing.getId());
		assertNull(entity);
		entity = dao.findByTeamIDAndThingID(null, null);
		assertNull(entity);
	}

	@Test
	public void testDelete() {
		dao.delete(rel.getTeamID(), thing.getId());
		TeamThingRelation entity = dao.findByID(rel.getId());
		assertNull(entity);
	}

	@Test
	public void testDeleteByUserGroupID() {
		dao.delete(rel.getTeamID(), null);
		TeamThingRelation entity = dao.findByID(rel.getId());
		assertNull(entity);
	}

	@Test
	public void testDeleteByUserID() {
		dao.delete(null, thing.getId());
		TeamThingRelation entity = dao.findByID(rel.getId());
		assertNull(entity);
	}

	@Test
	public void testDeleteNull() {
		dao.delete(null, null);
	}

}
