package com.kii.beehive.portal.jdbc;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.jdbc.dao.GroupPermissionRelationDao;
import com.kii.beehive.portal.jdbc.dao.GroupUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.PermissionDao;
import com.kii.beehive.portal.jdbc.dao.SourceDao;
import com.kii.beehive.portal.jdbc.dao.UserGroupDao;
import com.kii.beehive.portal.jdbc.entity.GroupPermissionRelation;
import com.kii.beehive.portal.jdbc.entity.GroupUserRelation;
import com.kii.beehive.portal.jdbc.entity.Permission;
import com.kii.beehive.portal.jdbc.entity.Source;
import com.kii.beehive.portal.jdbc.entity.SourceType;
import com.kii.beehive.portal.jdbc.entity.UserGroup;

public class TestUserGroupDao extends TestTemplate {

	@Autowired
	private UserGroupDao dao;
	@Autowired
	private GroupUserRelationDao groupUserRelationDao;
	
	@Autowired
	private GroupPermissionRelationDao groupPermissionRelationDao;

	@Autowired
	private SourceDao sourceDao;

	@Autowired
	private PermissionDao permissionDao;
	
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
		dao.saveOrUpdate(userGroup);
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

		//List<UserGroup> list = dao.findByIDs(new Object[] { userGroup.getId(), id2 });
		List<Long> ids = new ArrayList<>();
		ids.add(userGroup.getId());
		ids.add(id2 );
		List<UserGroup> list = dao.findByIDs(ids);
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
	
	@Test
	public void testFindUserGroupByUserID() {
		GroupUserRelation rel = new GroupUserRelation();
		
		rel.setUserGroupID(userGroup.getId());
		rel.setUserID("UserTest");
		groupUserRelationDao.saveOrUpdate(rel);
		
		List<UserGroup> list = dao.findUserGroup(rel.getUserID(), null , null);
		assertTrue(list.size() > 0);
		
		list = dao.findUserGroup(rel.getUserID(), userGroup.getId() , null);
		assertTrue(list.size() > 0);
		UserGroup ug = list.get(0);
		assertEquals(userGroup.getName(), ug.getName());
		assertEquals(userGroup.getDescription(), ug.getDescription());
		
		list = dao.findUserGroup(rel.getUserID(), null , userGroup.getName());
		assertTrue(list.size() > 0);
		
		list = dao.findUserGroup(rel.getUserID(), userGroup.getId() , userGroup.getName());
		assertTrue(list.size() > 0);
		
		list = dao.findUserGroup(null, null , null);
		assertNull(list);
	}
	
	@Test
	public void testFindUserGroupByPermissionID() {
		GroupPermissionRelation rel = new GroupPermissionRelation();
		Permission permission = new Permission();
		Source source = new Source();
		
		source.setName("SourceNameTest");
		source.setType(SourceType.Web);
		long id2 = sourceDao.saveOrUpdate(source);
		source.setId(id2);

		permission.setName("NameTest");
		permission.setSourceID(source.getId());
		permission.setAction("ActionTest");
		permission.setDescription("DescriptionTest");
		long id3 = permissionDao.saveOrUpdate(permission);
		permission.setId(id3);

		rel.setUserGroupID(userGroup.getId());
		rel.setPermissionID(permission.getId());
		groupPermissionRelationDao.saveOrUpdate(rel);
		
		List<UserGroup> list = dao.findUserGroup(permission.getId(), null);
		assertTrue(list.size() > 0);
		
		list = dao.findUserGroup(permission.getId(), userGroup.getId());
		assertTrue(list.size() > 0);
		UserGroup ug = list.get(0);
		assertEquals(userGroup.getName(), ug.getName());
		assertEquals(userGroup.getDescription(), ug.getDescription());
		
		list = dao.findUserGroup(null, null);
		assertNull(list);
	}
	
	

}
