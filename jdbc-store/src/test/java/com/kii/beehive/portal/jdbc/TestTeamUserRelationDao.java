package com.kii.beehive.portal.jdbc;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.jdbc.dao.TeamDao;
import com.kii.beehive.portal.jdbc.dao.TeamUserRelationDao;
import com.kii.beehive.portal.jdbc.entity.Team;
import com.kii.beehive.portal.jdbc.entity.TeamUserRelation;

public class TestTeamUserRelationDao extends TestTemplate {

	@Autowired
	private TeamUserRelationDao dao;

	@Autowired
	private TeamDao teamDao;


	private TeamUserRelation rel = new TeamUserRelation();
	private Team team = new Team();

	@Before
	public void init() {

		
		team.setName("TeamTest");
		long teamId = teamDao.saveOrUpdate(team);
		team.setId(teamId);

		rel.setUserID("UserTest");
		rel.setTeamID(team.getId());
		rel.setVaild(1);
		long id4 = dao.insert(rel);
		rel.setId(id4);
	}

	@Test
	public void testFindByID() {
		TeamUserRelation entity = dao.findByID(rel.getId());
		assertEquals(rel.getTeamID(), entity.getTeamID());
		assertEquals(rel.getUserID(), entity.getUserID());
	}

	@Test
	public void testFindByUserIDAndUserGroupID() {
		TeamUserRelation entity = dao.findByTeamIDAndUserID(rel.getTeamID(), rel.getUserID());
		assertEquals(rel.getTeamID(), entity.getTeamID());
		assertEquals(rel.getUserID(), entity.getUserID());

		entity = dao.findByTeamIDAndUserID(rel.getTeamID(), null);
		assertNull(entity);
		entity = dao.findByTeamIDAndUserID(null, rel.getUserID());
		assertNull(entity);
		entity = dao.findByTeamIDAndUserID(null, null);
		assertNull(entity);
	}

	@Test
	public void testDelete() {
		dao.delete(rel.getTeamID(), rel.getUserID());
		TeamUserRelation entity = dao.findByID(rel.getId());
		assertNull(entity);
	}

	@Test
	public void testDeleteByUserGroupID() {
		dao.delete(rel.getTeamID(), null);
		TeamUserRelation entity = dao.findByID(rel.getId());
		assertNull(entity);
	}

	@Test
	public void testDeleteByUserID() {
		dao.delete(null, rel.getUserID());
		TeamUserRelation entity = dao.findByID(rel.getId());
		assertNull(entity);
	}

	@Test
	public void testDeleteNull() {
		dao.delete(null, null);
	}

}
