package com.kii.beehive.portal.jdbc;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.jdbc.dao.*;
import com.kii.beehive.portal.jdbc.entity.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

public class TestTagIndexDao extends TestTemplate {

	@Autowired
	private TagIndexDao tagIndexDao;

	@Autowired
	private GlobalThingSpringDao globalThingDao;

	@Autowired
	private TagThingRelationDao tagThingRelationDao;

	@Autowired
	private TeamDao teamDao;

	@Autowired
	private TeamTagRelationDao teamTagRelationDao;

	private TagIndex tag = new TagIndex();

	@Autowired
	private TagUserRelationDao tagUserRelationDao;

	@Autowired
	private UserGroupDao userGroupDao;

	@Autowired
	private TagGroupRelationDao tagGroupRelationDao;

	@Autowired
	private TeamUserRelationDao teamUserRelationDao;

	@Before
	public void init() {
		tag.setDisplayName("DisplayNameTest");
		tag.setTagType(TagType.Custom);
		tag.setDescription("DescriptionTest");
		tag.setFullTagName(TagType.Custom.getTagName("DisplayNameTest"));
		long id = tagIndexDao.saveOrUpdate(tag);
		tag.setId(id);
		AuthInfoStore.setTeamID(null);
	}

	@Test
	public void testFindTagIdsByCreatorAndFullTagNames() throws Exception {
		List<Long> tagIds = tagIndexDao.findTagIdsByCreatorAndFullTagNames(AuthInfoStore.getUserID(), null).
				orElse(Collections.emptyList());
		assertEquals(1, tagIds.size());
		assertEquals(tag.getId(), tagIds.get(0));

		tagIds = tagIndexDao.findTagIdsByCreatorAndFullTagNames(AuthInfoStore.getUserID(),
				Arrays.asList("test", tag.getFullTagName())).orElse(Collections.emptyList());
		assertEquals(1, tagIds.size());
		assertEquals(tag.getId(), tagIds.get(0));

		tagIds = tagIndexDao.findTagIdsByCreatorAndFullTagNames(AuthInfoStore.getUserID(),
				Arrays.asList("test")).orElse(Collections.emptyList());
		assertEquals(0, tagIds.size());
	}

	@Test
	public void testGetCreatedTagIdsByTypeAndDisplayNames() throws Exception {
		List<Long> tagIds = tagIndexDao.getCreatedTagIdsByTypeAndDisplayNames(AuthInfoStore.getUserID(),
				tag.getTagType(), null).orElse(Collections.emptyList());
		assertEquals(1, tagIds.size());
		assertEquals(tag.getId(), tagIds.get(0));

		tagIds = tagIndexDao.getCreatedTagIdsByTypeAndDisplayNames(AuthInfoStore.getUserID(),
				tag.getTagType(), Arrays.asList("123123", tag.getDisplayName())).orElse(Collections.emptyList());
		assertEquals(1, tagIds.size());
		assertEquals(tag.getId(), tagIds.get(0));

		tagIds = tagIndexDao.getCreatedTagIdsByTypeAndDisplayNames(AuthInfoStore.getUserID(),
				tag.getTagType(), Arrays.asList("123123")).orElse(Collections.emptyList());
		assertEquals(0, tagIds.size());
	}

	@Test
	public void testFindByID() {

		TagIndex entity = tagIndexDao.findByID(tag.getId());
		assertEquals(tag.getDisplayName(), entity.getDisplayName());
		assertEquals(tag.getTagType(), entity.getTagType());
		assertEquals(tag.getDescription(), entity.getDescription());

	}

	@Test
	public void testUpdate() {
		tag.setDisplayName("DisplayNameUpdate");
		tagIndexDao.updateEntityAllByID(tag);
		TagIndex entity = tagIndexDao.findByID(tag.getId());
		assertEquals("DisplayNameUpdate", entity.getDisplayName());
		assertEquals(tag.getTagType(), entity.getTagType());
		assertEquals(tag.getDescription(), entity.getDescription());

	}

	@Test
	public void testFindByIDs() {
		TagIndex tag2 = new TagIndex();
		tag2.setDisplayName("DisplayName2");
		tag2.setTagType(TagType.Location);
		tag2.setDescription("Description2");
		tag2.setFullTagName(TagType.Location.getTagName("DisplayName2"));
		long id2 = tagIndexDao.saveOrUpdate(tag2);
		List<Long> ids = new ArrayList<>();
		ids.add(tag.getId());
		ids.add(id2);
		List<TagIndex> list = tagIndexDao.findByIDs(ids);

		assertEquals(2, list.size());
	}

	@Test
	public void testDelete() {
		TagIndex tagIndex = new TagIndex();
		tagIndex.setTagType(TagType.Location);
		tagIndex.setDisplayName("Location");
		Long tagId = tagIndexDao.saveOrUpdate(tagIndex);

		TagUserRelation tagUserRelation = new TagUserRelation();
		tagUserRelation.setUserId("Someone");
		tagUserRelation.setTagId(tagId);
		tagUserRelationDao.saveOrUpdate(tagUserRelation);

		UserGroup userGroup = new UserGroup();
		userGroup.setName("Group");
		Long userGroupId = userGroupDao.saveOrUpdate(userGroup);

		TagGroupRelation tagGroupRelation = new TagGroupRelation();
		tagGroupRelation.setTagID(tagId);
		tagGroupRelation.setUserGroupID(userGroupId);
		tagGroupRelation.setType("Some type");
		tagGroupRelationDao.saveOrUpdate(tagGroupRelation);

		GlobalThingInfo thingInfo = new GlobalThingInfo();
		thingInfo.setVendorThingID("12345");
		thingInfo.setKiiAppID("KiiAppId");
		Long thingId = globalThingDao.saveOrUpdate(thingInfo);

		TagThingRelation tagThingRelation = new TagThingRelation();
		tagThingRelation.setTagID(tagId);
		tagThingRelation.setThingID(thingId);
		tagThingRelationDao.saveOrUpdate(tagThingRelation);

		assertNotNull(tagUserRelationDao.find(tagId, "Someone"));
		assertNotNull(tagGroupRelationDao.findByTagIDAndUserGroupID(tagId, userGroupId));
		assertNotNull(tagThingRelationDao.findByThingIDAndTagID(thingId, tagId));

		tagIndexDao.deleteByID(tagId);

		assertNull(tagIndexDao.findByID(tagId));
		assertNull(tagUserRelationDao.find(tagId, "Someone"));
		assertNull(tagGroupRelationDao.findByTagIDAndUserGroupID(tagId, userGroupId));
		assertNull(tagThingRelationDao.findByThingIDAndTagID(thingId, tagId));
	}

	@Test
	public void testIsIdExist() {
		boolean b = tagIndexDao.IsIdExist(tag.getId());
		assertTrue(b);
	}

	@Test
	public void testFindTagByTagTypeAndName() {
		List<TagIndex> list = tagIndexDao.findTagByTagTypeAndName(null, tag.getDisplayName());
		assertEquals(1, list.size());
		list = tagIndexDao.findTagByTagTypeAndName(TagType.Custom.toString(), tag.getDisplayName());
		assertEquals(1, list.size());
	}

	@Test
	public void testFindTagByTagTypeAndNameWithTeamID() {
		Long teamID = createTeamRel();

		AuthInfoStore.setTeamID(teamID);

		List<TagIndex> list = tagIndexDao.findTagByTagTypeAndName(null, tag.getDisplayName());
		assertEquals(1, list.size());
		list = tagIndexDao.findTagByTagTypeAndName(TagType.Custom.toString(), tag.getDisplayName());
		assertEquals(1, list.size());
	}

	@Test
	public void testFindLocations() {
		TagIndex t = new TagIndex();
		t.setDisplayName("LocationTest");
		t.setTagType(TagType.Location);
		t.setDescription("DescriptionTest");
		t.setFullTagName(TagType.Location.getTagName("LocationTest"));
		Long tagId = tagIndexDao.saveOrUpdate(t);

		List<String> list = tagIndexDao.findLocations(t.getDisplayName());
		assertEquals(1, list.size());

		List<TagIndex> result = tagIndexDao.findTagsByTagIdsAndLocations(Arrays.asList(tagId), null).
				orElse(Collections.emptyList());
		assertEquals(1, result.size());
		assertEquals(tagId, result.get(0).getId());

		result = tagIndexDao.findTagsByTagIdsAndLocations(Arrays.asList(tagId), "Loc").orElse(Collections.emptyList());
		assertEquals(1, result.size());
		assertEquals(tagId, result.get(0).getId());

		result = tagIndexDao.findTagsByTagIdsAndLocations(null, "Loc").orElse(Collections.emptyList());
		assertEquals(1, result.size());
		assertEquals(tagId, result.get(0).getId());

		result = tagIndexDao.findTagsByTagIdsAndLocations(null, "Loc1").orElse(Collections.emptyList());
		assertEquals(0, result.size());

		result = tagIndexDao.findTagsByTagIdsAndLocations(Arrays.asList(tagId + 20), "Loc").orElse(Collections
				.emptyList());
		assertEquals(0, result.size());
	}

	@Test
	public void testFindLocationsWithTeamID() {
		Long teamID = createTeamRel();
		AuthInfoStore.setTeamID(teamID);

		TagIndex t = new TagIndex();
		t.setDisplayName("LocationTest");
		t.setTagType(TagType.Location);
		t.setDescription("DescriptionTest");
		t.setFullTagName(TagType.Location.getTagName("LocationTest"));
		Long tID = tagIndexDao.saveOrUpdate(t);

		teamTagRelationDao.saveOrUpdate(new TeamTagRelation(teamID, tID));

		List<String> list = tagIndexDao.findLocations(t.getDisplayName());
		assertEquals(1, list.size());
	}

	@Test
	public void testFindTagByGlobalThingID() {
		GlobalThingInfo thing = new GlobalThingInfo();
		thing.setVendorThingID("demo_vendor_thing_id");
		thing.setKiiAppID("appID");
		thing.setCustom("custom");
		thing.setType("type");
		thing.setStatus("1");
		long thingID = globalThingDao.saveOrUpdate(thing);

		TagThingRelation rel = new TagThingRelation();
		rel.setTagID(tag.getId());
		rel.setThingID(thingID);
		tagThingRelationDao.insert(rel);

		List<TagIndex> list = tagIndexDao.findTagByGlobalThingID(thingID);
		assertEquals(1, list.size());
		assertEquals(tag.getDisplayName(), list.get(0).getDisplayName());
		assertEquals(tag.getTagType(), list.get(0).getTagType());
		assertEquals(tag.getDescription(), list.get(0).getDescription());
	}

	@Test
	public void testFindTagByFullTagName() {
		List<TagIndex> list = tagIndexDao.findTagByFullTagName(tag.getFullTagName());
		assertEquals(1, list.size());
	}


	private Long createTeamRel() {
		Team t = new Team();
		t.setName("TeamTest");
		Long teamID = teamDao.saveOrUpdate(t);

		teamTagRelationDao.saveOrUpdate(new TeamTagRelation(teamID, tag.getId()));
		return teamID;
	}

	@Test
	public void testFindTagIdsByIDsAndFullname() throws Exception {
		AuthInfoStore.setAuthInfo("Someone");
		List<Long> tagIds = new ArrayList();
		List<String> names = new ArrayList();
		for (int i = 0; i < 3; ++i) {
			TagIndex tag = new TagIndex();
			tag.setDisplayName("Tag-" + i);
			tag.setTagType(TagType.Location);
			tagIds.add(tagIndexDao.saveOrUpdate(tag));
			names.add(tag.getFullTagName());
			names.add("Test name " + i);
		}
		List<Long> result = tagIndexDao.findTagIdsByIDsAndFullname(tagIds, names).orElse(Collections.emptyList());
		assertEquals("Should find 3 ids", 3, result.size());
		assertTrue("Ids don't match", result.containsAll(tagIds) && tagIds.containsAll(result));

		result = tagIndexDao.findTagIdsByIDsAndFullname(Collections.emptyList(), names).orElse(Collections.emptyList());
		assertTrue(result.isEmpty());
	}

	@Test
	public void testFindTagIdsByTeamAndTagTypeAndName() throws Exception {
		tagIndexDao.deleteByID(tag.getId());

		AuthInfoStore.setAuthInfo("Someone");
		List<Long> tagIds = new ArrayList();
		List<String> names = new ArrayList();
		for (int i = 0; i < 3; ++i) {
			TagIndex tag = new TagIndex();
			tag.setDisplayName("Tag-" + i);
			tag.setTagType(TagType.Location);
			tagIds.add(tagIndexDao.saveOrUpdate(tag));
			names.add(tag.getFullTagName());
		}

		Team team = new Team();
		team.setName("test");

		AuthInfoStore.setAuthInfo("TeamLead");
		AuthInfoStore.setTeamID(teamDao.saveOrUpdate(team));
		for (int i = 0; i < 3; ++i) {
			TagIndex tag = new TagIndex();
			tag.setDisplayName("Tag-Team-" + i);
			tag.setTagType(TagType.Custom);
			tagIds.add(tagIndexDao.saveOrUpdate(tag));
			names.add(tag.getFullTagName());
		}

		TeamUserRelation relation = new TeamUserRelation();
		relation.setTeamID(AuthInfoStore.getTeamID());
		relation.setUserID("TeamLead");
		teamUserRelationDao.saveOrUpdate(relation);

		List<Long> ids = tagIndexDao.findTagIdsByTeamAndTagTypeAndName(null, null, null).
				orElse(Collections.emptyList());
		assertEquals(6, ids.size());
		assertTrue(tagIds.containsAll(ids) && ids.containsAll(tagIds));

		ids = tagIndexDao.findTagIdsByTeamAndTagTypeAndName(AuthInfoStore.getTeamID(), null, null).
				orElse(Collections.emptyList());
		assertEquals(3, ids.size());
		assertTrue(tagIds.subList(3, 6).containsAll(ids) && ids.containsAll(tagIds.subList(3, 6)));

		ids = tagIndexDao.findTagIdsByTeamAndTagTypeAndName(null, TagType.Custom, null).
				orElse(Collections.emptyList());
		assertEquals(3, ids.size());
		assertTrue(tagIds.subList(3, 6).containsAll(ids) && ids.containsAll(tagIds.subList(3, 6)));

		ids = tagIndexDao.findTagIdsByTeamAndTagTypeAndName(null, null, "Tag-Team-2").
				orElse(Collections.emptyList());
		assertEquals(1, ids.size());
		assertTrue(tagIds.get(5).longValue() == ids.get(0).longValue());

		ids = tagIndexDao.findTagIdsByTeamAndTagTypeAndName(null, TagType.Custom, "Tag-Team-2").
				orElse(Collections.emptyList());
		assertEquals(1, ids.size());
		assertTrue(tagIds.get(5).longValue() == ids.get(0).longValue());

		ids = tagIndexDao.findTagIdsByTeamAndTagTypeAndName(AuthInfoStore.getTeamID(), TagType.Custom, "Tag-Team-2").
				orElse(Collections.emptyList());
		assertEquals(1, ids.size());
		assertTrue(tagIds.get(5).longValue() == ids.get(0).longValue());
	}
}
