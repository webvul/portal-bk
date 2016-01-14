package com.kii.beehive.portal.jdbc;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.jdbc.dao.GroupUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.UserGroupDao;
import com.kii.beehive.portal.jdbc.entity.GroupUserRelation;
import com.kii.beehive.portal.jdbc.entity.UserGroup;

public class TestGroupUserRelationDao extends TestTemplate {

	@Autowired
	private GroupUserRelationDao dao;

	@Autowired
	private UserGroupDao userGroupDao;


	private GroupUserRelation rel = new GroupUserRelation();
	private UserGroup userGroup = new UserGroup();

	@Before
	public void init() {

		userGroup.setName("NameTest");
		userGroup.setDescription("DescriptionTest");
		long id = userGroupDao.saveOrUpdate(userGroup);
		userGroup.setId(id);


		rel.setUserGroupID(userGroup.getId());
		rel.setUserID("UserTest");
		long id4 = dao.saveOrUpdate(rel);
		rel.setId(id4);
	}

	@Test
	public void testFindByID() {
		GroupUserRelation entity = dao.findByID(rel.getId());
		assertEquals(rel.getUserID(), entity.getUserID());
		assertEquals(rel.getUserGroupID(), entity.getUserGroupID());
	}

	@Test
	public void testFindByUserIDAndUserGroupID() {
		GroupUserRelation entity = dao.findByUserIDAndUserGroupID(rel.getUserID(), userGroup.getId());
		assertEquals(rel.getUserID(), entity.getUserID());
		assertEquals(rel.getUserGroupID(), entity.getUserGroupID());

		entity = dao.findByUserIDAndUserGroupID(rel.getUserID(), null);
		assertNull(entity);
		entity = dao.findByUserIDAndUserGroupID(null, userGroup.getId());
		assertNull(entity);
		entity = dao.findByUserIDAndUserGroupID(null, null);
		assertNull(entity);
	}

	@Test
	public void testDelete() {
		dao.delete(rel.getUserID(), userGroup.getId());
		GroupUserRelation entity = dao.findByID(rel.getId());
		assertNull(entity);
	}

	@Test
	public void testDeleteByUserGroupID() {
		dao.delete(rel.getUserID(), null);
		GroupUserRelation entity = dao.findByID(rel.getId());
		assertNull(entity);
	}

	@Test
	public void testDeleteByUserID() {
		dao.delete(null, userGroup.getId());
		GroupUserRelation entity = dao.findByID(rel.getId());
		assertNull(entity);
	}

	@Test
	public void testDeleteNull() {
		dao.delete(null, null);
	}

}
