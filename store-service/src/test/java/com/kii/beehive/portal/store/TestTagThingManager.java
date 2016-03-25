package com.kii.beehive.portal.store;


import com.kii.beehive.business.manager.TagThingManager;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.jdbc.dao.*;
import com.kii.beehive.portal.jdbc.entity.*;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.anySetOf;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;

@RunWith(JUnit4.class)
public class TestTagThingManager {

	@Autowired
	private TagThingManager thingTagService;

	@Autowired
	private TagIndexDao tagIndexDao;

	@InjectMocks
	private TagThingManager tagThingManager;

	@Spy
	@Autowired
	private TagUserRelationDao tagUserRelationDao;

	@Spy
	@Autowired
	private TagThingRelationDao tagThingRelationDao;

	@Spy
	@Autowired
	private TagGroupRelationDao tagGroupRelationDao;

	@Spy
	@Autowired
	private ThingUserRelationDao thingUserRelationDao;

	@Spy
	@Autowired
	private ThingUserGroupRelationDao thingUserGroupRelationDao;

	@Spy
	@Autowired
	private UserGroupDao userGroupDao;

	@Spy
	@Autowired
	private GlobalThingSpringDao globalThingDao;

	private List<TagIndex> tags;

	private List<BeehiveUser> users;

	private List<UserGroup> userGroups;

	private List<GlobalThingInfo> things;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		Long[] tagIds = new Long[]{100L, 200L};
		tags = new ArrayList<>();
		for (Long id : tagIds) {
			TagIndex tag = new TagIndex();
			tag.setId(id);
			tag.setCreateBy("tag creator");
			tags.add(tag);
		}

		Long[] thingIds = new Long[]{500L, 600L};
		things = new ArrayList();
		for (Long id : thingIds) {
			GlobalThingInfo thing = new GlobalThingInfo();
			thing.setId(id);
			thing.setCreateBy("tag creator");
			things.add(thing);
		}

		String[] userIds = new String[]{"user1", "user2"};
		users = new ArrayList<>();
		for (String id : userIds) {
			BeehiveUser user = new BeehiveUser();
			user.setKiiLoginName(id);
			users.add(user);
		}

		Long[] userGroupId = new Long[]{300L, 400L};
		userGroups = new ArrayList<>();
		for (Long id : userGroupId) {
			UserGroup group = new UserGroup();
			group.setId(id);
			userGroups.add(group);
		}
	}

	@Test
	public void testBindTagsToUsers() throws Exception {
		AuthInfoStore.setAuthInfo("someone");
		try {
			tagThingManager.bindTagsToUsers(tags, users);
			fail("Expect an UnauthorizedException");
		} catch (UnauthorizedException e) {
		}

		AuthInfoStore.setAuthInfo("tag creator");
		doReturn(mock(TagUserRelation.class)).when(tagUserRelationDao).find(anyLong(), anyString());
		tagThingManager.bindTagsToUsers(tags, users);
		verify(tagUserRelationDao, Mockito.times(0)).insert(any(TagUserRelation.class));

		Set<String> names = new HashSet<>();
		doReturn(null).when(tagUserRelationDao).find(anyLong(), anyString());
		doAnswer((Answer<Long>) invocation -> {
			TagUserRelation relation = (TagUserRelation) invocation.getArguments()[0];
			names.add(relation.getTagId() + " " + relation.getUserId());
			return null;
		}).when(tagUserRelationDao).insert(any(TagUserRelation.class));
		tagThingManager.bindTagsToUsers(tags, users);
		verify(tagUserRelationDao, Mockito.times(4)).insert(any(TagUserRelation.class));
		tags.forEach(tagIndex -> {
			users.forEach(beehiveUser -> TestCase.assertTrue("Inserted data is incorrect", names.contains(tagIndex.getId() +
					" " + beehiveUser.getKiiLoginName())));
		});
		assertEquals("Should insert 4 entries", 4, names.size());
	}

	@Test
	public void testUnbindTagsFromUsers() throws Exception {
		AuthInfoStore.setAuthInfo("someone");
		try {
			tagThingManager.unbindTagsFromUsers(tags, users);
			fail("Expect an UnauthorizedException");
		} catch (UnauthorizedException e) {
		}

		AuthInfoStore.setAuthInfo("tag creator");

		Set<String> names = new HashSet<>();
		doReturn(null).when(tagUserRelationDao).find(anyLong(), anyString());
		doAnswer((Answer<Long>) invocation -> {
			names.add(invocation.getArguments()[0] + " " + invocation.getArguments()[1]);
			return null;
		}).when(tagUserRelationDao).deleteByTagIdAndUserId(anyLong(), anyString());

		tagThingManager.unbindTagsFromUsers(tags, users);

		verify(tagUserRelationDao, Mockito.times(4)).deleteByTagIdAndUserId(anyLong(), anyString());
		tags.forEach(tagIndex -> {
			users.forEach(beehiveUser -> TestCase.assertTrue("Inserted data is incorrect", names.contains(tagIndex.getId() +
					" " + beehiveUser.getKiiLoginName())));
		});
		assertEquals("Should insert 4 entries", 4, names.size());

	}

	@Test
	public void testBindTagsToUserGroups() throws Exception {
		AuthInfoStore.setAuthInfo("someone");
		try {
			tagThingManager.bindTagsToUserGroups(tags, userGroups);
			fail("Expect an UnauthorizedException");
		} catch (UnauthorizedException e) {
		}

		AuthInfoStore.setAuthInfo("tag creator");

		doReturn(mock(TagGroupRelation.class)).when(tagGroupRelationDao).findByTagIDAndUserGroupID(anyLong(), anyLong
				());
		tagThingManager.bindTagsToUserGroups(tags, userGroups);
		verify(tagGroupRelationDao, times(0)).insert(any(TagGroupRelation.class));

		Set<String> names = new HashSet();
		doReturn(null).when(tagGroupRelationDao).findByTagIDAndUserGroupID(anyLong(), anyLong
				());
		doAnswer((Answer<Long>) invocation -> {
			TagGroupRelation relation = (TagGroupRelation) invocation.getArguments()[0];
			names.add(relation.getTagID() + " " + relation.getUserGroupID());
			return null;
		}).when(tagGroupRelationDao).insert(any(TagGroupRelation.class));
		tagThingManager.bindTagsToUserGroups(tags, userGroups);
		verify(tagGroupRelationDao, Mockito.times(4)).insert(any(TagGroupRelation.class));
		tags.forEach(tagIndex -> {
			userGroups.forEach(group -> TestCase.assertTrue("Inserted data is incorrect", names.contains(tagIndex.getId() +
					" " + group.getId())));
		});
		assertEquals("Should insert 4 entries", 4, names.size());
	}

	@Test
	public void testUnbindTagsFromUserGroups() throws Exception {
		AuthInfoStore.setAuthInfo("someone");
		try {
			tagThingManager.unbindTagsFromUserGroups(tags, userGroups);
			fail("Expect an UnauthorizedException");
		} catch (UnauthorizedException e) {
		}

		AuthInfoStore.setAuthInfo("tag creator");

		Set<String> names = new HashSet();
		doAnswer((Answer<Long>) invocation -> {
			names.add(invocation.getArguments()[0] + " " + invocation.getArguments()[1]);
			return null;
		}).when(tagGroupRelationDao).delete(anyLong(), anyLong());
		tagThingManager.unbindTagsFromUserGroups(tags, userGroups);
		verify(tagGroupRelationDao, Mockito.times(4)).delete(anyLong(), anyLong());
		tags.forEach(tagIndex -> {
			userGroups.forEach(group -> TestCase.assertTrue("Inserted data is incorrect", names.contains(tagIndex.getId() +
					" " + group.getId())));
		});
		assertEquals("Should insert 4 entries", 4, names.size());
	}

	@Test
	public void testBindThingsToUsers() throws Exception {
		AuthInfoStore.setAuthInfo("someone");
		try {
			tagThingManager.bindThingsToUsers(things, users);
			fail("Expect an UnauthorizedException");
		} catch (UnauthorizedException e) {
		}

		AuthInfoStore.setAuthInfo("tag creator");

		doReturn(mock(ThingUserRelation.class)).when(thingUserRelationDao).find(anyLong(), anyString());

		tagThingManager.bindThingsToUsers(things, users);

		verify(thingUserRelationDao, times(0)).insert(any(ThingUserRelation.class));

		Set<String> names = new HashSet();
		doReturn(null).when(thingUserRelationDao).find(anyLong(), anyString());
		doAnswer((Answer<Long>) invocation -> {
			ThingUserRelation relation = (ThingUserRelation) invocation.getArguments()[0];
			names.add(relation.getThingId() + " " + relation.getUserId());
			return null;
		}).when(thingUserRelationDao).insert(any(ThingUserRelation.class));

		tagThingManager.bindThingsToUsers(things, users);

		verify(thingUserRelationDao, Mockito.times(4)).insert(any(ThingUserRelation.class));
		things.forEach(thing -> users.forEach(user -> TestCase.assertTrue("Inserted data is incorrect", names.contains(thing
				.getId() + " " + user.getKiiLoginName()))));
		assertEquals("Should insert 4 entries", 4, names.size());
	}

	@Test
	public void testUnbindThingsFromUsers() throws Exception {
		AuthInfoStore.setAuthInfo("someone");
		try {
			tagThingManager.unbindThingsFromUsers(things, users);
			fail("Expect an UnauthorizedException");
		} catch (UnauthorizedException e) {
		}

		AuthInfoStore.setAuthInfo("tag creator");

		Set<String> names = new HashSet();
		doAnswer((Answer<Long>) invocation -> {
			names.add(invocation.getArguments()[0] + " " + invocation.getArguments()[1]);
			return null;
		}).when(thingUserRelationDao).deleteByThingIdAndUserId(anyLong(), anyString());
		tagThingManager.unbindThingsFromUsers(things, users);
		verify(thingUserRelationDao, times(4)).deleteByThingIdAndUserId(anyLong(), anyString());
		things.forEach(thing -> users.forEach(user -> TestCase.assertTrue("Inserted data is incorrect", names.contains(thing
				.getId() + " " + user.getKiiLoginName()))));
		assertEquals("Should insert 4 entries", 4, names.size());
	}

	@Test
	public void testBindThingsToUserGroups() throws Exception {
		AuthInfoStore.setAuthInfo("someone");
		try {
			tagThingManager.bindThingsToUserGroups(things, userGroups);
			fail("Expect an UnauthorizedException");
		} catch (UnauthorizedException e) {
		}

		AuthInfoStore.setAuthInfo("tag creator");

		doReturn(mock(ThingUserGroupRelation.class)).when(thingUserGroupRelationDao).find(anyLong(), anyLong());

		tagThingManager.bindThingsToUserGroups(things, userGroups);

		verify(thingUserGroupRelationDao, times(0)).insert(any(ThingUserGroupRelation.class));

		Set<String> names = new HashSet();
		doReturn(null).when(thingUserGroupRelationDao).find(anyLong(), anyLong());
		doAnswer((Answer<Long>) invocation -> {
			ThingUserGroupRelation relation = (ThingUserGroupRelation) invocation.getArguments()[0];
			names.add(relation.getThingId() + " " + relation.getUserGroupId());
			return null;
		}).when(thingUserGroupRelationDao).insert(any(ThingUserGroupRelation.class));

		tagThingManager.bindThingsToUserGroups(things, userGroups);

		verify(thingUserGroupRelationDao, Mockito.times(4)).insert(any(ThingUserGroupRelation.class));
		things.forEach(thing -> userGroups.forEach(group -> TestCase.assertTrue("Inserted data is incorrect", names.contains
				(thing.getId() + " " + group.getId()))));
		assertEquals("Should insert 4 entries", 4, names.size());
	}

	@Test
	public void testUnbindThingsFromUserGroups() throws Exception {
		AuthInfoStore.setAuthInfo("someone");
		try {
			tagThingManager.unbindThingsFromUserGroups(things, userGroups);
			fail("Expect an UnauthorizedException");
		} catch (UnauthorizedException e) {
		}

		AuthInfoStore.setAuthInfo("tag creator");

		Set<String> names = new HashSet();
		doAnswer((Answer<Long>) invocation -> {
			names.add(invocation.getArguments()[0] + " " + invocation.getArguments()[1]);
			return null;
		}).when(thingUserGroupRelationDao).deleteByThingIdAndUserGroupId(anyLong(), anyLong());
		tagThingManager.unbindThingsFromUserGroups(things, userGroups);
		verify(thingUserGroupRelationDao, times(4)).deleteByThingIdAndUserGroupId(anyLong(), anyLong());
		things.forEach(thing -> userGroups.forEach(group -> TestCase.assertTrue("Inserted data is incorrect", names.contains
				(thing.getId() + " " + group.getId()))));
		assertEquals("Should insert 4 entries", 4, names.size());
	}

	@Test
	public void testGetAccessibleThingsByType() throws Exception {
		String user = "Someone";

		UserGroup userGroup = new UserGroup();
		userGroup.setId(2200L);
		doReturn(Arrays.asList(userGroup)).when(userGroupDao).findUserGroup(anyString(), any(), any());
		doReturn(Optional.ofNullable(Arrays.asList(11001L))).when(thingUserGroupRelationDao).
				findThingIds(eq(userGroup.getId()));
		doReturn(Optional.ofNullable(Arrays.asList(11002L))).when(thingUserRelationDao).findThingIds(eq(user));

		TagUserRelation relation = new TagUserRelation();
		relation.setTagId(2400L);
		doReturn(Optional.ofNullable(Arrays.asList(relation))).when(tagUserRelationDao).findByUserId(eq(user));
		doReturn(Optional.ofNullable(Arrays.asList(11003L))).when(tagThingRelationDao).findThingIds(eq(relation
				.getTagId()));
		Set<Long> thingIds = new HashSet();
		doAnswer((Answer<Optional<List<GlobalThingInfo>>>) invocation -> {
			thingIds.addAll((Collection<? extends Long>) invocation.getArguments()[0]);
			return Optional.ofNullable(null);
		}).when(globalThingDao).findByIDsAndType(anySetOf(Long.class), anyString());

		List<Long> expected = Arrays.asList(11001L, 11002L, 11003L);
		tagThingManager.getAccessibleThingsByType("some type", user);
		assertTrue("Thing ids don't match", thingIds.containsAll(expected));
		assertEquals("Number of thing ids doesn't match", expected.size(), thingIds.size());
	}

	@Test
	public void testGetTypesOfAccessibleThingsWithCount() throws Exception {
		String user = "Someone";

		UserGroup userGroup = new UserGroup();
		userGroup.setId(2200L);
		doReturn(Arrays.asList(userGroup)).when(userGroupDao).findUserGroup(anyString(), any(), any());
		doReturn(Optional.ofNullable(Arrays.asList(11001L))).when(thingUserGroupRelationDao).
				findThingIds(eq(userGroup.getId()));
		doReturn(Optional.ofNullable(Arrays.asList(11002L))).when(thingUserRelationDao).findThingIds(eq(user));

		TagUserRelation relation = new TagUserRelation();
		relation.setTagId(2400L);
		doReturn(Optional.ofNullable(Arrays.asList(relation))).when(tagUserRelationDao).findByUserId(eq(user));
		doReturn(Optional.ofNullable(Arrays.asList(11003L))).when(tagThingRelationDao).findThingIds(eq(relation
				.getTagId()));
		Set<Long> thingIds = new HashSet();
		doAnswer((Answer<Optional<List<Map<String, Object>>>>) invocation -> {
			thingIds.addAll((Collection<? extends Long>) invocation.getArguments()[0]);
			return Optional.ofNullable(null);
		}).when(globalThingDao).findThingTypesWithThingCount(anySetOf(Long.class));

		List<Long> expected = Arrays.asList(11001L, 11002L, 11003L);
		
		tagThingManager.getTypesOfAccessibleThingsWithCount(user);

		assertTrue("Thing ids don't match", thingIds.containsAll(expected));
		assertEquals("Number of thing ids doesn't match", expected.size(), thingIds.size());
	}

	/*
	@Test
	public void testFindLocations() {

		//
		String location1 = "floor1-room1-counter1";
		TagIndex tag1 = new TagIndex();
		tag1.setTagType(TagType.Location);
		tag1.setDisplayName(location1);

		tagIndexDao.saveOrUpdate(tag1);

		String location2 = "floor1-room1-counter2";
		TagIndex tag2 = new TagIndex();
		tag2.setTagType(TagType.Location);
		tag2.setDisplayName(location2);

		tagIndexDao.saveOrUpdate(tag2);

		String location3 = "floor1-room2-counter1";
		TagIndex tag3 = new TagIndex();
		tag3.setTagType(TagType.Location);
		tag3.setDisplayName(location3);

		tagIndexDao.saveOrUpdate(tag3);

		String location4 = "floor1-room2-counter2";
		TagIndex tag4 = new TagIndex();
		tag4.setTagType(TagType.Location);
		tag4.setDisplayName(location4);

		tagIndexDao.saveOrUpdate(tag4);

		String location5 = "floor2-room1-counter1";
		TagIndex tag5 = new TagIndex();
		tag5.setTagType(TagType.Location);
		tag5.setDisplayName(location5);

		tagIndexDao.saveOrUpdate(tag5);

		//
		List<String> locations = thingTagService.findLocations("");
		assertEquals(5, locations.size());

		locations = thingTagService.findLocations("floor1");
		assertEquals(4, locations.size());
		assertEquals(location1, locations.get(0));
		assertEquals(location2, locations.get(1));
		assertEquals(location3, locations.get(2));
		assertEquals(location4, locations.get(3));

		locations = thingTagService.findLocations("floor1-room2");
		assertEquals(2, locations.size());
		assertEquals(location3, locations.get(0));
		assertEquals(location4, locations.get(1));

		locations = thingTagService.findLocations("floor1-room2-counter1");
		assertEquals(1, locations.size());
		assertEquals(location3, locations.get(0));

	}
*/
}
