//package com.kii.beehive.portal.jdbc;
//
//import static junit.framework.TestCase.assertEquals;
//import static org.junit.Assert.assertNull;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.kii.beehive.portal.jdbc.dao.GroupPermissionRelationDao;
//import com.kii.beehive.portal.jdbc.dao.PermissionDao;
//import com.kii.beehive.portal.jdbc.dao.SourceDao;
//import com.kii.beehive.portal.jdbc.dao.UserGroupDao;
//import com.kii.beehive.portal.jdbc.entity.GroupPermissionRelation;
//import com.kii.beehive.portal.jdbc.entity.Permission;
//import com.kii.beehive.portal.jdbc.entity.Source;
//import com.kii.beehive.portal.jdbc.entity.SourceType;
//import com.kii.beehive.portal.jdbc.entity.UserGroup;
//
//public class TestGroupPermissionRelationDao extends TestTemplate {
//
////	@Autowired
////	private GroupPermissionRelationDao dao;
//
//	@Autowired
//	private UserGroupDao userGroupDao;
//
//	@Autowired
//	private SourceDao sourceDao;
//
//	@Autowired
//	private PermissionDao permissionDao;
//
////	private GroupPermissionRelation rel = new GroupPermissionRelation();
//	private UserGroup userGroup = new UserGroup();
//	private Permission permission = new Permission();
//	private Source source = new Source();
//
//	@Before
//	public void init() {
//
//		userGroup.setName("NameTest");
//		userGroup.setDescription("DescriptionTest");
//		long id = userGroupDao.saveOrUpdate(userGroup);
//		userGroup.setId(id);
//
//		source.setName("SourceNameTest");
//		source.setType(SourceType.Web);
//		long id2 = sourceDao.saveOrUpdate(source);
//		source.setId(id2);
//
//		permission.setName("NameTest");
//		permission.setSourceID(source.getId());
//		permission.setAction("ActionTest");
//		permission.setDescription("DescriptionTest");
//		long id3 = permissionDao.saveOrUpdate(permission);
//		permission.setId(id3);
//
//		rel.setUserGroupID(userGroup.getId());
//		rel.setPermissionID(permission.getId());
//		long id4 = dao.insert(rel);
//		rel.setId(id4);
//	}
//
//	@Test
//	public void testFindByID() {
//		GroupPermissionRelation entity = dao.findByID(rel.getId());
//		assertEquals(rel.getPermissionID(), entity.getPermissionID());
//		assertEquals(rel.getUserGroupID(), entity.getUserGroupID());
//	}
//
//	@Test
//	public void testFindByPermissionIDAndUserGroupID() {
//		GroupPermissionRelation entity = dao.findByPermissionIDAndUserGroupID(permission.getId(), userGroup.getId());
//		assertEquals(rel.getPermissionID(), entity.getPermissionID());
//		assertEquals(rel.getUserGroupID(), entity.getUserGroupID());
//
//		entity = dao.findByPermissionIDAndUserGroupID(permission.getId(), null);
//		assertNull(entity);
//		entity = dao.findByPermissionIDAndUserGroupID(null, userGroup.getId());
//		assertNull(entity);
//		entity = dao.findByPermissionIDAndUserGroupID(null, null);
//		assertNull(entity);
//	}
//
//	@Test
//	public void testDelete() {
//		dao.delete(permission.getId(), userGroup.getId());
//		GroupPermissionRelation entity = dao.findByID(rel.getId());
//		assertNull(entity);
//	}
//
//	@Test
//	public void testDeleteByUserGroupID() {
//		dao.delete(permission.getId(), null);
//		GroupPermissionRelation entity = dao.findByID(rel.getId());
//		assertNull(entity);
//	}
//
//	@Test
//	public void testDeleteByPermissionID() {
//		dao.delete(null, userGroup.getId());
//		GroupPermissionRelation entity = dao.findByID(rel.getId());
//		assertNull(entity);
//	}
//
//	@Test
//	public void testDeleteNull() {
//		dao.delete(null, null);
//	}
//
//}
