package com.kii.beehive.portal.jdbc;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.TagUserRelationDao;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.portal.jdbc.entity.TagUserRelation;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by hdchen on 3/18/16.
 */
public class TestTagUserRelationDao extends TestTemplate {
	@Autowired
	private TagIndexDao tagIndexDao;

	@Autowired
	private TagUserRelationDao tagUserRelationDao;

	private List<Long> allTagIds = new ArrayList<Long>();

	@Before
	public void setUp() throws Exception {
		AuthInfoStore.setAuthInfo("TestOwner");
		TagUserRelation relation = new TagUserRelation();
		TagIndex tag = new TagIndex();
		for (int i = 1; i <= 3; ++i) {
			tag.setDisplayName("Tag " + i);
			tag.setTagType(TagType.Custom);
			tag.setDescription("Description");
			tag.setFullTagName(TagType.Custom.getTagName(tag.getDisplayName()));
			allTagIds.add(tagIndexDao.saveOrUpdate(tag));
		}

		relation.setUserId("user1");
		relation.setTagId(this.allTagIds.get(0));
		tagUserRelationDao.saveOrUpdate(relation);
		relation.setTagId(this.allTagIds.get(1));
		tagUserRelationDao.saveOrUpdate(relation);
		relation.setUserId("user2");
		relation.setTagId(this.allTagIds.get(1));
		tagUserRelationDao.saveOrUpdate(relation);
		relation.setTagId(this.allTagIds.get(2));
		tagUserRelationDao.saveOrUpdate(relation);
	}

	@Test
	public void testFindTagIds() throws Exception {
		List<Long> tagIds = tagUserRelationDao.findTagIds("user1").orElse(null);
		assertNotNull("Tag ids should not be null", tagIds);
		assertEquals("There should be two tag ids", 2, tagIds.size());
		assertTrue("The tag ids are incorrect.", new HashSet(tagIds).containsAll(this.allTagIds.subList(0, 2)));

		tagIds = tagUserRelationDao.findTagIds("user2").orElse(null);
		assertNotNull("Tag ids should not be null", tagIds);
		assertEquals("There should be two tag ids", 2, tagIds.size());
		assertTrue("The tag ids are incorrect.", new HashSet(tagIds).containsAll(this.allTagIds.subList(1, 3)));
	}

	@Test
	public void testFindTagIdsFilterByTagType() throws Exception {
		TagIndex tag = new TagIndex();
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
		allTagIds.forEach(id -> {
			TagUserRelation relation = new TagUserRelation();
			relation.setTagId(id);
			relation.setUserId("TestOwner");
			tagUserRelationDao.saveOrUpdate(relation);
		});
		Set<Long> tagIds = tagUserRelationDao.findTagIds("TestOwner", TagType.Location.name(), null).
				orElse(Collections.emptyList()).stream().collect(Collectors.toSet());
		assertEquals("Should have 3 ids", 3, tagIds.size());
		assertTrue("Ids don't match", tagIds.containsAll(allTagIds.subList(3, 6)));

		tagIds = tagUserRelationDao.findTagIds("TestOwner", null, "Location 1").
				orElse(Collections.emptyList()).stream().collect(Collectors.toSet());
		assertEquals("Should have 2 ids", 2, tagIds.size());
		assertTrue("Ids don't match", tagIds.contains(allTagIds.get(3)) && tagIds.contains(allTagIds.get(6)));

		tagIds = tagUserRelationDao.findTagIds("TestOwner", TagType.Location.name(), "Location 1").
				orElse(Collections.emptyList()).stream().collect(Collectors.toSet());
		assertEquals("Should have 1 id", 1, tagIds.size());
		assertEquals("Id doesn't match", allTagIds.get(3), tagIds.iterator().next());

		List<String> tagNameList = new ArrayList<String>();
		tagNameList.add(TagType.Location.getTagName("Location 1"));
		tagIds = tagUserRelationDao.findTagIds("TestOwner", tagNameList).
				orElse(Collections.emptyList()).stream().collect(Collectors.toSet());
		assertEquals("Should have 1 id", 1, tagIds.size());
		assertEquals("Id doesn't match", allTagIds.get(3), tagIds.iterator().next());
	}

	@Test
	public void testFindUserIds() throws Exception {
		List<String> userIds = tagUserRelationDao.findUserIds(this.allTagIds.get(0));
		assertNotNull("User ids should not be null", userIds);
		assertEquals("There should be one user id", 1, userIds.size());
		assertTrue("user id should be user1", "user1".equals(userIds.get(0)));

		userIds = tagUserRelationDao.findUserIds(this.allTagIds.get(1));
		assertNotNull("User ids should not be null", userIds);
		assertEquals("There should be one user id", 2, userIds.size());
		assertTrue("user id should be user1 and user2", userIds.contains("user1") && userIds.contains("user2"));

		userIds = tagUserRelationDao.findUserIds(this.allTagIds.get(2));
		assertNotNull("User ids should not be null", userIds);
		assertEquals("There should be one user id", 1, userIds.size());
		assertTrue("user id should be user1", "user2".equals(userIds.get(0)));

		userIds = tagUserRelationDao.findUserIds(this.allTagIds).orElse(Collections.emptyList());
		assertEquals(2, userIds.size());
		assertTrue(userIds.contains("user1") && userIds.contains("user2"));
	}

	@Test
	public void testFindByTagId() throws Exception {
		List<TagUserRelation> relations = tagUserRelationDao.findByTagId(this.allTagIds.get(0));
		assertNotNull("Relation list should not be null", relations);
		assertEquals("There should be one relation", 1, relations.size());
		assertTrue("User id should be user1", "user1".equals(relations.get(0).getUserId()));

		relations = tagUserRelationDao.findByTagId(this.allTagIds.get(1));
		assertNotNull("Relation list should not be null", relations);
		assertEquals("There should be one relation", 2, relations.size());
		Set<String> userIds = relations.stream().map(TagUserRelation::getUserId).collect(Collectors.toSet());
		assertTrue("User id should be user1 and user2", userIds.contains("user1") && userIds.contains("user2"));

		relations = tagUserRelationDao.findByTagId(this.allTagIds.get(2));
		assertNotNull("Relation list should not be null", relations);
		assertEquals("There should be one relation", 1, relations.size());
		assertTrue("User id should be user1", "user2".equals(relations.get(0).getUserId()));
	}

	@Test
	public void testFindByUserId() throws Exception {
		Optional<List<TagUserRelation>> relations = tagUserRelationDao.findByUserId("user1");
		assertNotNull("Relation list should not be null", relations.orElse(null));
		assertEquals("There should be two relations.", 2, relations.get().size());
		for (TagUserRelation relation : relations.get()) {
			assertTrue("The tag id is not associated with user", this.allTagIds.subList(0, 2).contains(relation.getTagId
					()));
		}

		relations = tagUserRelationDao.findByUserId("user2");
		assertNotNull("Relation list should not be null", relations.orElse(null));
		assertEquals("There should be two relations.", 2, relations.get().size());
		for (TagUserRelation relation : relations.get()) {
			assertTrue("The tag id is not associated with user", this.allTagIds.subList(1, 3).contains(relation
					.getTagId()));
		}
	}

	@Test
	public void testFindByTagIdAndUserId() throws Exception {
		for (Long tagId : this.allTagIds.subList(0, 2)) {
			assertNotNull("Cannot find the relation", tagUserRelationDao.find(tagId, "user1"));
		}

		for (Long tagId : this.allTagIds.subList(1, 3)) {
			assertNotNull("Cannot find the relation", tagUserRelationDao.find(tagId, "user2"));
		}

		assertNull("Should not find the relation", tagUserRelationDao.find(this.allTagIds.get(2), "user1"));
		assertNull("Should not find the relation", tagUserRelationDao.find(this.allTagIds.get(0), "user2"));
	}

	@Test
	public void testDeleteByTagId() throws Exception {
		List<Long> tagIds = tagUserRelationDao.findTagIds("user1").orElse(null);
		assertNotNull("Tag ids should not be null", tagIds);
		assertEquals("There should be two tag ids", 2, tagIds.size());
		tagIds = tagUserRelationDao.findTagIds("user2").orElse(null);
		assertNotNull("Tag ids should not be null", tagIds);
		assertEquals("There should be two tag ids", 2, tagIds.size());
		tagUserRelationDao.deleteByTagId(this.allTagIds.get(1));
		tagIds = tagUserRelationDao.findTagIds("user1").orElse(null);
		assertNotNull("Tag ids should not be null", tagIds);
		assertEquals("There should be one tag id", 1, tagIds.size());
		assertEquals("Tag id doesn't match", this.allTagIds.get(0), tagIds.get(0));
		tagIds = tagUserRelationDao.findTagIds("user2").orElse(null);
		assertNotNull("Tag ids should not be null", tagIds);
		assertEquals("There should be one tag id", 1, tagIds.size());
		assertEquals("Tag id doesn't match", this.allTagIds.get(2), tagIds.get(0));
	}

	@Test
	public void testDeleteByTagIdAndUserId() throws Exception {
		List<Long> tagIds = tagUserRelationDao.findTagIds("user1").orElse(null);
		assertNotNull("Tag ids should not be null", tagIds);
		assertEquals("There should be two tag ids", 2, tagIds.size());
		tagUserRelationDao.deleteByTagIdAndUserId(this.allTagIds.get(1), "user1");
		tagIds = tagUserRelationDao.findTagIds("user1").orElse(null);
		assertNotNull("Tag ids should not be null", tagIds);
		assertEquals("There should be one tag id", 1, tagIds.size());
		assertEquals("Tag id doesn't match", this.allTagIds.get(0), tagIds.get(0));
	}

	@Test
	public void testFindAccessibleTagIds() throws Exception {
		List<Long> tagIds = tagUserRelationDao.findAccessibleTagIds("user1", this.allTagIds).
				orElse(Collections.emptyList());
		assertNotNull("Tag ids should not be null", tagIds);
		assertEquals("There should be two tag ids", 2, tagIds.size());
		assertTrue("The tag ids are incorrect.", new HashSet(tagIds).containsAll(this.allTagIds.subList(0, 2)));

		tagIds = tagUserRelationDao.findAccessibleTagIds("user2", this.allTagIds).
				orElse(Collections.emptyList());
		assertNotNull("Tag ids should not be null", tagIds);
		assertEquals("There should be two tag ids", 2, tagIds.size());
		assertTrue("The tag ids are incorrect.", new HashSet(tagIds).containsAll(this.allTagIds.subList(1, 3)));
	}
}
