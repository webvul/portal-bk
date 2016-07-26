package com.kii.beehive.portal.jdbc;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.jdbc.dao.GroupUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.TagGroupRelationDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.UserGroupDao;
import com.kii.beehive.portal.jdbc.entity.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestTagGroupRelationDao extends TestTemplate {

	@Autowired
	private UserGroupDao userGroupDao;

	@Autowired
	private TagIndexDao tagIndexDao;


	private TagGroupRelation rel = new TagGroupRelation();
	private UserGroup userGroup = new UserGroup();
	private TagIndex tag = new TagIndex();

	@Autowired
	private TagGroupRelationDao tagGroupRelationDao;

	@Autowired
	private GroupUserRelationDao groupUserRelationDao;

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
		long tagId = tagIndexDao.saveOrUpdate(tag);
		tag.setId(tagId);

		rel.setUserGroupID(userGroup.getId());
		rel.setTagID(tag.getId());
		rel.setType("READONLY");
		long id4 = tagGroupRelationDao.insert(rel);
		rel.setId(id4);
	}

	@Test
	public void testFindByID() {
		TagGroupRelation entity = tagGroupRelationDao.findByID(rel.getId());
		assertEquals(rel.getTagID(), entity.getTagID());
		assertEquals(rel.getUserGroupID(), entity.getUserGroupID());
	}

	@Test
	public void testFindByUserIDAndUserGroupID() {
		TagGroupRelation entity = tagGroupRelationDao.findByTagIDAndUserGroupID(rel.getTagID(), userGroup.getId());
		assertEquals(rel.getTagID(), entity.getTagID());
		assertEquals(rel.getUserGroupID(), entity.getUserGroupID());

		entity = tagGroupRelationDao.findByTagIDAndUserGroupID(rel.getTagID(), null);
		assertNull(entity);
		entity = tagGroupRelationDao.findByTagIDAndUserGroupID(null, userGroup.getId());
		assertNull(entity);
		entity = tagGroupRelationDao.findByTagIDAndUserGroupID(null, null);
		assertNull(entity);
	}

	@Test
	public void testDelete() {
		tagGroupRelationDao.delete(rel.getTagID(), userGroup.getId());
		TagGroupRelation entity = tagGroupRelationDao.findByID(rel.getId());
		assertNull(entity);
	}

	@Test
	public void testDeleteByUserGroupID() {
		tagGroupRelationDao.delete(rel.getTagID(), null);
		TagGroupRelation entity = tagGroupRelationDao.findByID(rel.getId());
		assertNull(entity);
	}

	@Test
	public void testDeleteByUserID() {
		tagGroupRelationDao.delete(null, userGroup.getId());
		TagGroupRelation entity = tagGroupRelationDao.findByID(rel.getId());
		assertNull(entity);
	}

	@Test
	public void testDeleteNull() {
		tagGroupRelationDao.delete(null, null);
	}

	@Test
	public void testFindTagIdsFilterBy() throws Exception {
		List<Long> allTagIds = new ArrayList();
		AuthInfoStore.setAuthInfo(123456L);

		TagIndex tag = new TagIndex();
		for (int i = 1; i <= 3; ++i) {
			tag.setDisplayName("Tag " + i);
			tag.setTagType(TagType.Custom);
			tag.setDescription("Description");
			tag.setFullTagName(TagType.Custom.getTagName(tag.getDisplayName()));
			allTagIds.add(tagIndexDao.saveOrUpdate(tag));
		}

		for (int i = 1; i <= 3; ++i) {
			tag.setDisplayName("Location " + i);
			tag.setTagType(TagType.Location);
			tag.setDescription("Description");
			tag.setFullTagName(TagType.Location.getTagName(tag.getDisplayName()));
			allTagIds.add(tagIndexDao.saveOrUpdate(tag));
		}

		for (int i = 1; i <= 3; ++i) {
			tag.setDisplayName("Location " + i);
			tag.setTagType(TagType.System);
			tag.setDescription("Description");
			tag.setFullTagName(TagType.System.getTagName(tag.getDisplayName()));
			allTagIds.add(tagIndexDao.saveOrUpdate(tag));
		}

		UserGroup group = new UserGroup();
		group.setName("group");
		Long userGroupId = userGroupDao.saveOrUpdate(group);

		allTagIds.forEach(id -> {
			TagGroupRelation relation = new TagGroupRelation();
			relation.setTagID(id);
			relation.setUserGroupID(userGroupId);
			relation.setType("test type");
			tagGroupRelationDao.saveOrUpdate(relation);
		});

		GroupUserRelation gurelation = new GroupUserRelation();
		gurelation.setBeehiveUserID(101l);
		gurelation.setUserGroupID(userGroupId);
		groupUserRelationDao.saveOrUpdate(gurelation);

		Set<Long> tagIds = tagGroupRelationDao.findTagIdsByUserId(101l, null, null).
				stream().collect(Collectors.toSet());
		assertEquals("Number of ids doesn't match", 9, tagIds.size());
		assertTrue("Ids don't match", tagIds.containsAll(allTagIds));

		tagIds = tagGroupRelationDao.findTagIdsByUserId(102l, null, null).
				stream().collect(Collectors.toSet());
		assertTrue("Should not find any ids", tagIds.isEmpty());

		tagIds = tagGroupRelationDao.findTagIdsByUserId(101l, TagType.Location.name(), null).
				stream().collect(Collectors.toSet());
		Assert.assertEquals("Should have 3 ids", 3, tagIds.size());
		assertTrue("Ids don't match", tagIds.containsAll(allTagIds.subList(3, 6)));

		tagIds = tagGroupRelationDao.findTagIdsByUserId(101l, null, "Location 1").
				stream().collect(Collectors.toSet());
		Assert.assertEquals("Should have 2 ids", 2, tagIds.size());
		assertTrue("Ids don't match", tagIds.contains(allTagIds.get(3)) && tagIds.contains(allTagIds.get(6)));

		tagIds = tagGroupRelationDao.findTagIdsByUserId(101l, TagType.Location.name(), "Location 1").
				stream().collect(Collectors.toSet());
		Assert.assertEquals("Should have 1 id", 1, tagIds.size());
		Assert.assertEquals("Id doesn't match", allTagIds.get(3), tagIds.iterator().next());

		List<String> tagNameList = new ArrayList<String>();
		tagNameList.add(TagType.Location.getTagName("Location 1"));
		tagIds = tagGroupRelationDao.findTagIdsByUserIdAndFullTagName(101l, tagNameList).
				orElse(Collections.emptyList()).stream().collect(Collectors.toSet());
		Assert.assertEquals("Should have 1 id", 1, tagIds.size());
		Assert.assertEquals("Id doesn't match", allTagIds.get(3), tagIds.iterator().next());
	}

	@Test
	public void testFindUserGroupIdsByTagIds() throws Exception {
		List<Long> groupIds = tagGroupRelationDao.findUserGroupIdsByTagIds(Arrays.asList(tag.getId())).
				orElse(Collections.emptyList());
		assertEquals(1, groupIds.size());
		assertEquals(userGroup.getId(), groupIds.get(0));
	}
}
