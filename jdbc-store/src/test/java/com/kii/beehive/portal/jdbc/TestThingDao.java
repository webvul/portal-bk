package com.kii.beehive.portal.jdbc;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.TagThingRelationDao;
import com.kii.beehive.portal.jdbc.dao.TeamDao;
import com.kii.beehive.portal.jdbc.dao.TeamThingRelationDao;
import com.kii.beehive.portal.jdbc.dao.ThingUserGroupRelationDao;
import com.kii.beehive.portal.jdbc.dao.ThingUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.UserGroupDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagThingRelation;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.portal.jdbc.entity.Team;
import com.kii.beehive.portal.jdbc.entity.TeamThingRelation;
import com.kii.beehive.portal.jdbc.entity.ThingUserGroupRelation;
import com.kii.beehive.portal.jdbc.entity.ThingUserRelation;
import com.kii.beehive.portal.jdbc.entity.UserGroup;

public class TestThingDao extends TestTemplate {

	@Autowired
	private GlobalThingSpringDao globalThingDao;

	@Autowired
	private TagIndexDao tagIndexDao;

	@Autowired
	private TagThingRelationDao tagThingRelationDao;

	@Autowired
	private TeamDao teamDao;

	@Autowired
	private TeamThingRelationDao teamThingRelationDao;

	private GlobalThingInfo thing = new GlobalThingInfo();

	@Autowired
	private UserGroupDao userGroupDao;

	@Autowired
	private ThingUserGroupRelationDao thingUserGroupRelationDao;

	@Autowired
	private ThingUserRelationDao thingUserRelationDao;

	@Before
	public void init() {
		thing.setVendorThingID("demo_vendor_thing_id");
		thing.setKiiAppID("appID");
		thing.setCustom(Collections.singletonMap("custom","val"));
		thing.setType("thingType");
		thing.setStatus(Collections.singletonMap("1","a"));

		String fullKiiThingID = ThingIDTools.joinFullKiiThingID("abcdefghijk", "appID");
		thing.setFullKiiThingID(fullKiiThingID);
		long id = globalThingDao.saveOrUpdate(thing);
		thing.setId(id);

		AuthInfoStore.setTeamID(null);
	}
//
//	@Test
//	public void testFindThingIdsByCreator() throws Exception {
//		List<Long> thingIds = globalThingDao.findThingIdsByCreator(AuthInfoStore.getUserID(), null).
//				orElse(Collections.emptyList());
//		assertEquals(1, thingIds.size());
//		assertEquals(thing.getId(), thingIds.get(0));
//
//		thingIds = globalThingDao.findThingIdsByCreator(AuthInfoStore.getUserID(), Arrays.asList(200L, thing.getId())).
//				orElse(Collections.emptyList());
//		assertEquals(1, thingIds.size());
//		assertEquals(thing.getId(), thingIds.get(0));
//
//		thingIds = globalThingDao.findThingIdsByCreator(AuthInfoStore.getUserID(), Arrays.asList(200L)).
//				orElse(Collections.emptyList());
//		assertEquals(0, thingIds.size());
//	}
//
//	@Test
//	public void testFindByIDsAndType() throws Exception {
//		GlobalThingInfo thingInfo1 = new GlobalThingInfo();
//		thingInfo1.setType("LED");
//		thingInfo1.setVendorThingID("LED123");
//		thingInfo1.setKiiAppID("WhatsApp");
//		Long thingId1 = globalThingDao.saveOrUpdate(thingInfo1);
//
//		GlobalThingInfo thingInfo2 = new GlobalThingInfo();
//		thingInfo2.setType("TV");
//		thingInfo2.setVendorThingID("TV123");
//		thingInfo2.setKiiAppID("WhatsApp");
//		Long thingId2 = globalThingDao.saveOrUpdate(thingInfo2);
//
//		Optional<List<GlobalThingInfo>> result = globalThingDao.findByIDsAndType(Arrays.asList(thingId1, thingId2).stream().
//				collect(Collectors.toSet()), "LED");
//		assertNotNull("Should have a thing", result.get());
//		assertEquals("Number of things is incorrect", 1, result.get().size());
//		assertEquals("Thing id doesn't match", thingId1, result.get().get(0).getId());
//
//		result = globalThingDao.findByIDsAndType(Arrays.asList(thingId1, thingId2).stream().
//				collect(Collectors.toSet()), "TV");
//		assertNotNull("Should have a thing", result.get());
//		assertEquals("Number of things is incorrect", 1, result.get().size());
//		assertEquals("Thing id doesn't match", thingId2, result.get().get(0).getId());
//
//		List<GlobalThingInfo> emptyResult = globalThingDao.findByIDsAndType(Collections.emptySet(), "TV").
//				orElse(Collections.emptyList());
//		assertTrue(emptyResult.isEmpty());
//		emptyResult = globalThingDao.findByIDsAndType(null, "TV").orElse(Collections.emptyList());
//		assertTrue(emptyResult.isEmpty());
//	}
//
//	@Test
//	public void testFindThingTypesWithThingCount() throws Exception {
//		Set<Long> thingIds = new HashSet();
//		for (int i = 0; i < 3; ++i) {
//			GlobalThingInfo thingInfo1 = new GlobalThingInfo();
//			thingInfo1.setType("LED");
//			thingInfo1.setVendorThingID("LED-" + i);
//			thingInfo1.setKiiAppID("WhatsApp");
//			thingIds.add(globalThingDao.saveOrUpdate(thingInfo1));
//		}
//
//		for (int i = 0; i < 2; ++i) {
//			GlobalThingInfo thingInfo2 = new GlobalThingInfo();
//			thingInfo2.setType("TV");
//			thingInfo2.setVendorThingID("TV1-" + i);
//			thingInfo2.setKiiAppID("WhatsApp");
//			thingIds.add(globalThingDao.saveOrUpdate(thingInfo2));
//		}
//
//		List<Map<String, Object>> result = globalThingDao.findThingTypesWithThingCount(thingIds).orElseThrow(() ->
//				new RuntimeException("Test fail. Can't get thingTypesWithThingCount"));
//		assertEquals("There should be two objects", 2, result.size());
//		result.forEach(data -> {
//			if (data.get("type").equals("LED")) {
//				assertEquals("Should have 3 LEDs", "3", data.get("count").toString());
//			} else if (data.get("type").equals("TV")) {
//				assertEquals("Should have 3 TVs", "2", data.get("count").toString());
//			} else {
//				fail("Unexpected data set");
//			}
//		});
//
//		result = globalThingDao.findThingTypesWithThingCount(Collections.emptySet()).orElse(Collections.emptyList());
//		assertTrue(result.isEmpty());
//	}

	@Test
	public void testFindByID() {

		GlobalThingInfo entity = globalThingDao.findByID(thing.getId());
		assertEquals(thing.getVendorThingID(), entity.getVendorThingID());
		assertEquals(thing.getKiiAppID(), entity.getKiiAppID());
		assertEquals(thing.getCustom(), entity.getCustom());
		assertEquals(thing.getType(), entity.getType());
		assertEquals(thing.getStatus(), entity.getStatus());

	}

	@Test
	public void testFindByIDs() {
		GlobalThingInfo thing2 = new GlobalThingInfo();
		thing2.setVendorThingID("demo_vendor_thing_id2");
		thing2.setKiiAppID("appID2");
		thing2.setStatus(Collections.singletonMap("1","a"));

		long id2 = globalThingDao.saveOrUpdate(thing2);
		List<Long> ids = new ArrayList<>();
		ids.add(thing.getId());
		ids.add(id2);
		List<GlobalThingInfo> list = globalThingDao.findByIDs(ids);

		assertEquals(2, list.size());
	}

	@Test
	public void testDelete() {
		globalThingDao.deleteByID(thing.getId());
		GlobalThingInfo entity = globalThingDao.findByID(thing.getId());
		assertNull(entity);

		GlobalThingInfo thingInfo = new GlobalThingInfo();
		thingInfo.setVendorThingID("12345");
		thingInfo.setKiiAppID("KiiAppId");
		Long thingId = globalThingDao.saveOrUpdate(thingInfo);

		TagIndex tagIndex = new TagIndex();
		tagIndex.setTagType(TagType.Location);
		tagIndex.setDisplayName("Location");
		Long tagId = tagIndexDao.saveOrUpdate(tagIndex);

		TagThingRelation tagThingRelation = new TagThingRelation();
		tagThingRelation.setTagID(tagId);
		tagThingRelation.setThingID(thingId);
		tagThingRelationDao.saveOrUpdate(tagThingRelation);

		UserGroup userGroup = new UserGroup();
		userGroup.setName("Group");
		Long userGroupId = userGroupDao.saveOrUpdate(userGroup);

		ThingUserGroupRelation thingUserGroupRelation = new ThingUserGroupRelation();
		thingUserGroupRelation.setUserGroupId(userGroupId);
		thingUserGroupRelation.setThingId(thingId);
		thingUserGroupRelationDao.saveOrUpdate(thingUserGroupRelation);

		ThingUserRelation thingUserRelation = new ThingUserRelation();
		thingUserRelation.setThingId(thingId);
		thingUserRelation.setBeehiveUserID(101L);
		thingUserRelationDao.saveOrUpdate(thingUserRelation);

		assertNotNull(tagThingRelationDao.findByThingIDAndTagID(thingId, tagId));
		assertNotNull(thingUserGroupRelationDao.find(thingId, userGroupId));
		assertNotNull(thingUserRelationDao.find(thingId, 101L));

		globalThingDao.deleteByID(thingId);

		assertNull(tagThingRelationDao.findByThingIDAndTagID(thingId, tagId));
		assertNull(thingUserGroupRelationDao.find(thingId, userGroupId));
		assertNull(thingUserRelationDao.find(thingId, 101L));
		assertNull(globalThingDao.findByID(thingId));
	}


	@Test
	public void testGetThingByVendorThingID() {
		GlobalThingInfo entity = globalThingDao.getThingByVendorThingID(thing.getVendorThingID());
		assertEquals(thing.getVendorThingID(), entity.getVendorThingID());
		assertEquals(thing.getKiiAppID(), entity.getKiiAppID());
		assertEquals(thing.getCustom(), entity.getCustom());
		assertEquals(thing.getType(), entity.getType());
		assertEquals(thing.getStatus(), entity.getStatus());
	}

	@Test
	public void testUpdate() {
		thing.setVendorThingID("demo_vendor_thing_id_update");
		thing.setCustom(Collections.singletonMap("custom","val"));
		thing.setType("type");
		thing.setStatus(Collections.singletonMap("1","power"));
		globalThingDao.updateEntityAllByID(thing);
		GlobalThingInfo entity = globalThingDao.findByID(thing.getId());
		assertEquals(thing.getVendorThingID(), entity.getVendorThingID());
		assertEquals(thing.getKiiAppID(), entity.getKiiAppID());
		assertEquals(thing.getCustom(), entity.getCustom());
		assertEquals(thing.getType(), entity.getType());
		assertEquals(thing.getStatus(), entity.getStatus());

		Map<String, Object> map = new HashMap<>();
		map.put("custom", "updated");
		map.put("status", "newStatus");

		globalThingDao.updateEntityByID(map, thing.getId());

		thing = globalThingDao.findByID(thing.getId());

		assertEquals(thing.getVendorThingID(), entity.getVendorThingID());
		assertEquals(thing.getKiiAppID(), entity.getKiiAppID());
		assertEquals(thing.getCustom(), "updated");
		assertEquals(thing.getStatus(), "newStatus");
	}

	@Test
	public void testFindAllThingTypes() {
		List<String> list = globalThingDao.findAllThingTypes();
		assertTrue(list.size() > 0);
	}

	@Test
	public void testFindAllThingTypesWithTeamID() {
		Long teamID = createTeamRel();

		AuthInfoStore.setTeamID(teamID);
		List<String> list = globalThingDao.findAllThingTypes();
		assertTrue(list.size() > 0);
	}

	@Test
	public void testFindAllThingTypesWithThingCount() {
		List<Map<String, Object>> list = globalThingDao.findAllThingTypesWithThingCount();
		assertTrue(list.size() > 0);
	}

	@Test
	public void testFindAllThingTypesWithThingCountWithTeamID() {
		Long teamID = createTeamRel();

		AuthInfoStore.setTeamID(teamID);

		List<Map<String, Object>> list = globalThingDao.findAllThingTypesWithThingCount();
		assertTrue(list.size() > 0);
	}

	@Test
	public void testGetThingByType() {
		List<GlobalThingInfo> list = globalThingDao.getThingByType(thing.getType());
		GlobalThingInfo entity = list.get(0);
		assertEquals(thing.getVendorThingID(), entity.getVendorThingID());
		assertEquals(thing.getKiiAppID(), entity.getKiiAppID());
		assertEquals(thing.getCustom(), entity.getCustom());
		assertEquals(thing.getType(), entity.getType());
		assertEquals(thing.getStatus(), entity.getStatus());
	}

	@Test
	public void testGetThingByTypeWithTeamID() {
		List<GlobalThingInfo> list = globalThingDao.getThingByType(thing.getType());
		GlobalThingInfo entity = list.get(0);
		assertEquals(thing.getVendorThingID(), entity.getVendorThingID());
		assertEquals(thing.getKiiAppID(), entity.getKiiAppID());
		assertEquals(thing.getCustom(), entity.getCustom());
		assertEquals(thing.getType(), entity.getType());
		assertEquals(thing.getStatus(), entity.getStatus());
	}

	@Test
	public void testFindThingByTag() {
		TagIndex tag = createTagRel();

		List<GlobalThingInfo> list = globalThingDao.findThingByTag(tag.getFullTagName());
		assertTrue(list.size() > 0);
		GlobalThingInfo entity = list.get(0);
		assertEquals(thing.getVendorThingID(), entity.getVendorThingID());
		assertEquals(thing.getKiiAppID(), entity.getKiiAppID());
		assertEquals(thing.getCustom(), entity.getCustom());
		assertEquals(thing.getType(), entity.getType());
		assertEquals(thing.getStatus(), entity.getStatus());
	}

	@Test
	public void testFindThingByTagWithTeamID() {
		TagIndex tag = createTagRel();

		Long teamID = createTeamRel();

		AuthInfoStore.setTeamID(teamID);
		List<GlobalThingInfo> list = globalThingDao.findThingByTag(tag.getFullTagName());
		assertTrue(list.size() > 0);
		GlobalThingInfo entity = list.get(0);
		assertEquals(thing.getVendorThingID(), entity.getVendorThingID());
		assertEquals(thing.getKiiAppID(), entity.getKiiAppID());
		assertEquals(thing.getCustom(), entity.getCustom());
		assertEquals(thing.getType(), entity.getType());
		assertEquals(thing.getStatus(), entity.getStatus());
	}

	@Test
	public void testFindThingTypeBytagIDs() {
		TagIndex tag = createTagRel();
		TagIndex tag2 = createTagRel();

		List<Map<String, Object>> list = globalThingDao.findThingTypeBytagIDs(tag.getId() + "," + tag2.getId());

		assertTrue(list.size() > 0);
	}

	@Test
	public void testFindThingTypeBytagIDsWithTeamID() {
		TagIndex tag = createTagRel();
		TagIndex tag2 = createTagRel();
		Long teamID = createTeamRel();

		AuthInfoStore.setTeamID(teamID);
		List<Map<String, Object>> list = globalThingDao.findThingTypeBytagIDs(tag.getId() + "," + tag2.getId());

		assertTrue(list.size() > 0);
	}

	@Test
	public void testFindThingTypeByFullTagNames() {
		TagIndex tag = createTagRel();
		TagIndex tag2 = createTagRel();

		List<String> tagCollect = new ArrayList<>();
		tagCollect.add(tag.getFullTagName());
		tagCollect.add(tag2.getFullTagName());

		List<String> list = globalThingDao.findThingTypeByFullTagNames(tagCollect);

		assertTrue(list.size() == 1);
		assertTrue("thingType".equals(list.get(0)));
	}

	@Test
	public void testFindThingTypeByFullTagNamesWithTeamID() {
		TagIndex tag = createTagRel();
		TagIndex tag2 = createTagRel();
		Long teamID = createTeamRel();

		List<String> tagCollect = new ArrayList<>();
		tagCollect.add(tag.getFullTagName());
		tagCollect.add(tag2.getFullTagName());

		AuthInfoStore.setTeamID(teamID);
		List<String> list = globalThingDao.findThingTypeByFullTagNames(tagCollect);

		assertTrue(list.size() == 1);
		assertTrue("thingType".equals(list.get(0)));
	}

	private TagIndex createTagRel() {
		TagIndex tag = new TagIndex();
		tag.setDisplayName("DisplayNameTest");
		tag.setTagType(TagType.Custom);
		tag.setDescription("DescriptionTest");
		tag.setFullTagName(TagType.Custom.getTagName("DisplayNameTest"));
		long id = tagIndexDao.saveOrUpdate(tag);
		tag.setId(id);

		tagThingRelationDao.saveOrUpdate(new TagThingRelation(tag.getId(), thing.getId()));
		return tag;
	}

	private Long createTeamRel() {
		Team t = new Team();
		t.setName("TeamTest");
		Long teamID = teamDao.saveOrUpdate(t);

		teamThingRelationDao.saveOrUpdate(new TeamThingRelation(teamID, thing.getId()));
		return teamID;
	}
}
