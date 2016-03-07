package com.kii.beehive.portal.jdbc;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.jdbc.dao.GroupPermissionRelationDao;
import com.kii.beehive.portal.jdbc.dao.PermissionDao;
import com.kii.beehive.portal.jdbc.dao.SourceDao;
import com.kii.beehive.portal.jdbc.dao.UserGroupDao;
import com.kii.beehive.portal.jdbc.entity.GroupPermissionRelation;
import com.kii.beehive.portal.jdbc.entity.Permission;
import com.kii.beehive.portal.jdbc.entity.Source;
import com.kii.beehive.portal.jdbc.entity.SourceType;
import com.kii.beehive.portal.jdbc.entity.UserGroup;

public class TestPermissionDao extends TestTemplate {

	@Autowired
	private PermissionDao dao;
	
	@Autowired
	private UserGroupDao userGroupDao;
	
	@Autowired
	private GroupPermissionRelationDao groupPermissionRelationDao;
	
	@Autowired
	private SourceDao sourceDao;

	private Source source = new Source();
	private Permission permission = new Permission();

	@Before
	public void init() {
		source.setName("SourceNameTest");
		source.setType(SourceType.Web);
		long id = sourceDao.saveOrUpdate(source);
		source.setId(id);
		
		permission.setName("NameTest");
		permission.setSourceID(source.getId());
		permission.setAction("ActionTest");
		permission.setDescription("DescriptionTest");
		long id2 = dao.saveOrUpdate(permission);
		permission.setId(id2);
	}

	@Test
	public void testFindByID() {
		Permission entity = dao.findByID(permission.getId());
		assertEquals(entity.getName(), entity.getName());
		assertEquals(entity.getAction(), entity.getAction());
		assertEquals(permission.getDescription(), entity.getDescription());
	}

	@Test
	public void testUpdate() {
		permission.setName("PermissionNameUpdate");
		dao.updateEntityAllByID(permission);
		Permission entity = dao.findByID(permission.getId());
		assertEquals("PermissionNameUpdate", entity.getName());
		assertEquals(permission.getAction(), entity.getAction());
		assertEquals(permission.getDescription(), entity.getDescription());

	}

	@Test
	public void testFindByIDs() {
		Permission permission2 = new Permission();
		permission2.setName("PermissionNameTest2");
		permission2.setSourceID(source.getId());
		permission2.setAction("ActionTest2");
		permission2.setDescription("DescriptionTest2");
		long id2 = dao.saveOrUpdate(permission2);
		List<Long> ids = new ArrayList<>();
		ids.add(permission.getId());
		ids.add(id2);
		List<Permission> list = dao.findByIDs(ids);

		assertEquals(2, list.size());
	}

	@Test
	public void testDelete() {
		dao.deleteByID(permission.getId());
		Permission entity = dao.findByID(permission.getId());
		assertNull(entity);
	}

	@Test
	public void testIsIdExist() {
		boolean b = dao.IsIdExist(permission.getId());
		assertTrue(b);
	}
	
	@Test
	public void testFindAll() {
		List<Permission> list = dao.findAll();
		assertTrue(list.size() > 0);
		
		for(Permission p : list){
			assertNotNull(p.getSourceID());
			assertNotNull(p.getName());
			assertNotNull(p.getSourceName());
		}
	}
	
	@Test
	public void testFindByUserGroupID() {
		UserGroup userGroup = new UserGroup();
		userGroup.setName("NameTest");
		userGroup.setDescription("DescriptionTest");
		long id = userGroupDao.saveOrUpdate(userGroup);
		userGroup.setId(id);
		GroupPermissionRelation rel = new GroupPermissionRelation();
		rel.setUserGroupID(userGroup.getId());
		rel.setPermissionID(permission.getId());
		long id4 = groupPermissionRelationDao.insert(rel);
		rel.setId(id4);
		
		List<Permission> list = dao.findByUserGroupID(userGroup.getId());
		assertTrue(list.size() > 0);
	}

}
