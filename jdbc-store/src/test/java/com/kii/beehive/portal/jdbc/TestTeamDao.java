package com.kii.beehive.portal.jdbc;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.jdbc.dao.TeamDao;
import com.kii.beehive.portal.jdbc.entity.Team;

public class TestTeamDao extends TestTemplate {

	@Autowired
	private TeamDao dao;

	private Team team = new Team();

	@Before
	public void init() {
		team.setName("TeamNameTest");
		long id = dao.saveOrUpdate(team);
		team.setId(id);
	}

	@Test
	public void testFindByID() {
		Team entity = dao.findByID(team.getId());
		assertEquals(team.getName(), entity.getName());

	}

	@Test
	public void testUpdate() {
		team.setName("TeamNameUpdate");
		dao.saveOrUpdate(team);
		Team entity = dao.findByID(team.getId());
		assertEquals("TeamNameUpdate", entity.getName());

	}

	@Test
	public void testFindByIDs() {
		Team team2 = new Team();
		team2.setName("TeamNameTest2");
		long id2 = dao.saveOrUpdate(team2);
		List<Long> ids = new ArrayList<>();
		ids.add(team.getId());
		ids.add(id2);
		List<Team> list = dao.findByIDs(ids);

		assertEquals(2, list.size());
	}

	@Test
	public void testDelete() {
		dao.deleteByID(team.getId());
		Team entity = dao.findByID(team.getId());
		assertNull(entity);
	}

	@Test
	public void testIsIdExist() {
		boolean b = dao.IsIdExist(team.getId());
		assertTrue(b);
	}

}
