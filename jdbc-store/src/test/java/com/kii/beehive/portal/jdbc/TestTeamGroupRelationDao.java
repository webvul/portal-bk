package com.kii.beehive.portal.jdbc;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.jdbc.dao.TeamDao;
import com.kii.beehive.portal.jdbc.dao.TeamGroupRelationDao;
import com.kii.beehive.portal.jdbc.dao.UserGroupDao;
import com.kii.beehive.portal.jdbc.entity.Team;
import com.kii.beehive.portal.jdbc.entity.TeamGroupRelation;
import com.kii.beehive.portal.jdbc.entity.UserGroup;

public class TestTeamGroupRelationDao extends TestTemplate {

	@Autowired
	private TeamGroupRelationDao dao;

	@Autowired
	private UserGroupDao userGroupDao;
	
	@Autowired
	private TeamDao teamDao;


	private TeamGroupRelation rel = new TeamGroupRelation();
	private UserGroup userGroup = new UserGroup();
	private Team team = new Team();

	@Before
	public void init() {

		userGroup.setName("NameTest");
		userGroup.setDescription("DescriptionTest");
		long id = userGroupDao.saveOrUpdate(userGroup);
		userGroup.setId(id);
		
		team.setName("TeamTest");
		long teamId = teamDao.saveOrUpdate(team);
		team.setId(teamId);

		rel.setUserGroupID(userGroup.getId());
		rel.setTeamID(team.getId());
		long id4 = dao.insert(rel);
		rel.setId(id4);
	}

	@Test
	public void testFindByID() {
		TeamGroupRelation entity = dao.findByID(rel.getId());
		assertEquals(rel.getTeamID(), entity.getTeamID());
		assertEquals(rel.getUserGroupID(), entity.getUserGroupID());
	}

	@Test
	public void testFindByUserIDAndUserGroupID() {
		TeamGroupRelation entity = dao.findByTeamIDAndUserGroupID(rel.getTeamID(), userGroup.getId());
		assertEquals(rel.getTeamID(), entity.getTeamID());
		assertEquals(rel.getUserGroupID(), entity.getUserGroupID());

		entity = dao.findByTeamIDAndUserGroupID(rel.getTeamID(), null);
		assertNull(entity);
		entity = dao.findByTeamIDAndUserGroupID(null, userGroup.getId());
		assertNull(entity);
		entity = dao.findByTeamIDAndUserGroupID(null, null);
		assertNull(entity);
	}

	@Test
	public void testDelete() {
		dao.delete(rel.getTeamID(), userGroup.getId());
		TeamGroupRelation entity = dao.findByID(rel.getId());
		assertNull(entity);
	}

	@Test
	public void testDeleteByUserGroupID() {
		dao.delete(rel.getTeamID(), null);
		TeamGroupRelation entity = dao.findByID(rel.getId());
		assertNull(entity);
	}

	@Test
	public void testDeleteByUserID() {
		dao.delete(null, userGroup.getId());
		TeamGroupRelation entity = dao.findByID(rel.getId());
		assertNull(entity);
	}

	@Test
	public void testDeleteNull() {
		dao.delete(null, null);
	}

}
