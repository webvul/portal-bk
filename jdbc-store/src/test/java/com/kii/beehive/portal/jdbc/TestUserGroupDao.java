package com.kii.beehive.portal.jdbc;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.jdbc.dao.UserGroupDao;
import com.kii.beehive.portal.jdbc.entity.UserGroup;

public class TestUserGroupDao extends TestTemplate {

	@Autowired
	private UserGroupDao dao;
	
	private UserGroup userGroup = new UserGroup();

	@Before
	public void init() {
		
		userGroup.setName("NameTest");
		userGroup.setDescription("DescriptionTest");
		long id = dao.saveOrUpdate(userGroup);
		userGroup.setId(id);
	}

	@Test
	public void testFindByID() {
		UserGroup entity = dao.findByID(userGroup.getId());
		assertEquals(entity.getName(), entity.getName());
		assertEquals(entity.getDescription(), entity.getDescription());
	}

	@Test
	public void testUpdate() {
		userGroup.setName("UserGroupNameUpdate");
		dao.update(userGroup);
		UserGroup entity = dao.findByID(userGroup.getId());
		assertEquals("UserGroupNameUpdate", entity.getName());
		assertEquals(userGroup.getDescription(), entity.getDescription());

	}

	@Test
	public void testFindByIDs() {
		UserGroup permission2 = new UserGroup();
		permission2.setName("UserGroupNameTest2");
		permission2.setDescription("DescriptionTest2");
		long id2 = dao.saveOrUpdate(permission2);

		List<UserGroup> list = dao.findByIDs(new Object[] { userGroup.getId(), id2 });

		assertEquals(2, list.size());
	}

	@Test
	public void testDelete() {
		dao.deleteByID(userGroup.getId());
		UserGroup entity = dao.findByID(userGroup.getId());
		assertNull(entity);
	}

	@Test
	public void testIsIdExist() {
		boolean b = dao.IsIdExist(userGroup.getId());
		assertTrue(b);
	}
	
	@Test
	public void testFindAll() {
		List<UserGroup> list = dao.findAll();
		assertTrue(list.size() > 0);
	}
	
	/*@Test
	public void testFindUserGroup() {
		List<UserGroup> list = dao.findByUserGroupID();
		System.out.println(list.size());
		assertTrue(list.size() > 0);
	}*/

}
