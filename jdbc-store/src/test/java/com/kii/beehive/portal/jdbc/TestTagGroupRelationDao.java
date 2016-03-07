package com.kii.beehive.portal.jdbc;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.jdbc.dao.TagGroupRelationDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.UserGroupDao;
import com.kii.beehive.portal.jdbc.entity.TagGroupRelation;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.portal.jdbc.entity.UserGroup;

public class TestTagGroupRelationDao extends TestTemplate {

	@Autowired
	private TagGroupRelationDao dao;

	@Autowired
	private UserGroupDao userGroupDao;
	
	@Autowired
	private TagIndexDao tagIndexDao;


	private TagGroupRelation rel = new TagGroupRelation();
	private UserGroup userGroup = new UserGroup();
	private TagIndex  tag =new TagIndex();

	@Before
	public void init() {

		userGroup.setName("NameTest");
		userGroup.setDescription("DescriptionTest");
		long id = userGroupDao.saveOrUpdate(userGroup);
		userGroup.setId(id);
		
		tag.setDisplayName("DisplayNameTest");
		tag.setTagType(TagType.Custom);
		tag.setDescription("DescriptionTest");
		tag.setFullTagName(TagType.Custom.getTagName("DisplayNameTest"));
		long tagId=tagIndexDao.saveOrUpdate(tag);
		tag.setId(tagId);

		rel.setUserGroupID(userGroup.getId());
		rel.setTagID(tag.getId());
		rel.setType("READONLY");
		long id4 = dao.insert(rel);
		rel.setId(id4);
	}

	@Test
	public void testFindByID() {
		TagGroupRelation entity = dao.findByID(rel.getId());
		assertEquals(rel.getTagID(), entity.getTagID());
		assertEquals(rel.getUserGroupID(), entity.getUserGroupID());
	}

	@Test
	public void testFindByUserIDAndUserGroupID() {
		TagGroupRelation entity = dao.findByTagIDAndUserGroupID(rel.getTagID(), userGroup.getId());
		assertEquals(rel.getTagID(), entity.getTagID());
		assertEquals(rel.getUserGroupID(), entity.getUserGroupID());

		entity = dao.findByTagIDAndUserGroupID(rel.getTagID(), null);
		assertNull(entity);
		entity = dao.findByTagIDAndUserGroupID(null, userGroup.getId());
		assertNull(entity);
		entity = dao.findByTagIDAndUserGroupID(null, null);
		assertNull(entity);
	}

	@Test
	public void testDelete() {
		dao.delete(rel.getTagID(), userGroup.getId());
		TagGroupRelation entity = dao.findByID(rel.getId());
		assertNull(entity);
	}

	@Test
	public void testDeleteByUserGroupID() {
		dao.delete(rel.getTagID(), null);
		TagGroupRelation entity = dao.findByID(rel.getId());
		assertNull(entity);
	}

	@Test
	public void testDeleteByUserID() {
		dao.delete(null, userGroup.getId());
		TagGroupRelation entity = dao.findByID(rel.getId());
		assertNull(entity);
	}

	@Test
	public void testDeleteNull() {
		dao.delete(null, null);
	}

}
