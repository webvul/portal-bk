package com.kii.beehive.portal.store;


import com.kii.beehive.business.manager.TagThingManager;
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.exception.ObjectNotFoundException;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.jdbc.dao.*;
import com.kii.beehive.portal.jdbc.entity.*;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.service.BeehiveUserDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.store.entity.KiiAppInfo;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.*;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.anyCollectionOf;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.anySetOf;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;

@RunWith(JUnit4.class)
public class TestTagThingManager {

	@Autowired
	private TagThingManager thingTagService;

	@Spy
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
	private BeehiveUserDao userDao;

	@Spy
	@Autowired
	private GlobalThingSpringDao globalThingDao;

	@Spy
	@Autowired
	private AppInfoDao appInfoDao;

	@Spy
	@Autowired
	private GroupUserRelationDao groupUserRelationDao;

	private List<Long> tagIds;

	private List<String> userIds;

	private List<Long> userGroupIds;

	private List<Long> thingIds;

	@Mock
	private ThingIFInAppService thingIFInAppService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		Long[] tagIds = new Long[]{100L, 200L};
		this.tagIds = new ArrayList<>();
		for (Long id : tagIds) {
			TagIndex tag = new TagIndex();
			tag.setId(id);
			tag.setCreateBy("tag creator");
			this.tagIds.add(id);
		}

		Long[] thingIds = new Long[]{500L, 600L};
		this.thingIds = new ArrayList();
		for (Long id : thingIds) {
			GlobalThingInfo thing = new GlobalThingInfo();
			thing.setId(id);
			thing.setCreateBy("tag creator");
			this.thingIds.add(id);
		}

		String[] userIds = new String[]{"user1", "user2"};
		this.userIds = new ArrayList<>();
		for (String id : userIds) {
			BeehiveUser user = new BeehiveUser();
			user.setKiiLoginName(id);
			this.userIds.add(id);
		}

		Long[] userGroupId = new Long[]{300L, 400L};
		userGroupIds = new ArrayList<>();
		for (Long id : userGroupId) {
			UserGroup group = new UserGroup();
			group.setId(id);
			userGroupIds.add(id);
		}
	}

	@Test
	public void testBindTagsToUsers() throws Exception {
		AuthInfoStore.setAuthInfo("tag creator");
		doReturn(mock(TagUserRelation.class)).when(tagUserRelationDao).find(anyLong(), anyString());
		tagThingManager.bindTagsToUsers(tagIds, userIds);
		verify(tagUserRelationDao, Mockito.times(0)).insert(any(TagUserRelation.class));

		Set<String> names = new HashSet<>();
		doReturn(null).when(tagUserRelationDao).find(anyLong(), anyString());
		doAnswer((Answer<Long>) invocation -> {
			TagUserRelation relation = (TagUserRelation) invocation.getArguments()[0];
			names.add(relation.getTagId() + " " + relation.getUserId());
			return null;
		}).when(tagUserRelationDao).insert(any(TagUserRelation.class));
		tagThingManager.bindTagsToUsers(tagIds, userIds);
		verify(tagUserRelationDao, Mockito.times(4)).insert(any(TagUserRelation.class));
		tagIds.forEach(tagIndex -> userIds.forEach(beehiveUser -> TestCase.assertTrue("Inserted data is incorrect",
				names.contains(tagIndex + " " + beehiveUser))));
		assertEquals("Should insert 4 entries", 4, names.size());
	}

	@Test
	public void testUnbindTagsFromUsers() throws Exception {
		AuthInfoStore.setAuthInfo("tag creator");

		Set<String> names = new HashSet<>();
		doReturn(null).when(tagUserRelationDao).find(anyLong(), anyString());
		doAnswer((Answer<Long>) invocation -> {
			names.add(invocation.getArguments()[0] + " " + invocation.getArguments()[1]);
			return null;
		}).when(tagUserRelationDao).deleteByTagIdAndUserId(anyLong(), anyString());

		tagThingManager.unbindTagsFromUsers(tagIds, userIds);

		verify(tagUserRelationDao, Mockito.times(4)).deleteByTagIdAndUserId(anyLong(), anyString());
		tagIds.forEach(tagIndex -> userIds.forEach(beehiveUser -> TestCase.assertTrue("Inserted data is incorrect",
				names.contains(tagIndex + " " + beehiveUser))));
		assertEquals("Should insert 4 entries", 4, names.size());

	}

	@Test
	public void testBindTagsToUserGroups() throws Exception {
		AuthInfoStore.setAuthInfo("tag creator");

		doReturn(mock(TagGroupRelation.class)).when(tagGroupRelationDao).findByTagIDAndUserGroupID(anyLong(), anyLong
				());
		tagThingManager.bindTagsToUserGroups(tagIds, userGroupIds);
		verify(tagGroupRelationDao, times(0)).insert(any(TagGroupRelation.class));

		Set<String> names = new HashSet();
		doReturn(null).when(tagGroupRelationDao).findByTagIDAndUserGroupID(anyLong(), anyLong
				());
		doAnswer((Answer<Long>) invocation -> {
			TagGroupRelation relation = (TagGroupRelation) invocation.getArguments()[0];
			names.add(relation.getTagID() + " " + relation.getUserGroupID());
			return null;
		}).when(tagGroupRelationDao).insert(any(TagGroupRelation.class));
		tagThingManager.bindTagsToUserGroups(tagIds, userGroupIds);
		verify(tagGroupRelationDao, Mockito.times(4)).insert(any(TagGroupRelation.class));
		tagIds.forEach(tagIndex -> userGroupIds.forEach(group -> TestCase.assertTrue("Inserted data is incorrect",
				names.contains(tagIndex + " " + group))));
		assertEquals("Should insert 4 entries", 4, names.size());
	}

	@Test
	public void testUnbindTagsFromUserGroups() throws Exception {
		AuthInfoStore.setAuthInfo("tag creator");

		Set<String> names = new HashSet();
		doAnswer((Answer<Long>) invocation -> {
			names.add(invocation.getArguments()[0] + " " + invocation.getArguments()[1]);
			return null;
		}).when(tagGroupRelationDao).delete(anyLong(), anyLong());
		tagThingManager.unbindTagsFromUserGroups(tagIds, userGroupIds);
		verify(tagGroupRelationDao, Mockito.times(4)).delete(anyLong(), anyLong());
		tagIds.forEach(tagIndex -> userGroupIds.forEach(group -> TestCase.assertTrue("Inserted data is incorrect",
				names.contains(tagIndex + " " + group))));
		assertEquals("Should insert 4 entries", 4, names.size());
	}

	@Test
	public void testBindThingsToUsers() throws Exception {
		AuthInfoStore.setAuthInfo("tag creator");

		doReturn(mock(ThingUserRelation.class)).when(thingUserRelationDao).find(anyLong(), anyString());

		tagThingManager.bindThingsToUsers(thingIds, userIds);

		verify(thingUserRelationDao, times(0)).insert(any(ThingUserRelation.class));

		Set<String> names = new HashSet();
		doReturn(null).when(thingUserRelationDao).find(anyLong(), anyString());
		doAnswer((Answer<Long>) invocation -> {
			ThingUserRelation relation = (ThingUserRelation) invocation.getArguments()[0];
			names.add(relation.getThingId() + " " + relation.getUserId());
			return null;
		}).when(thingUserRelationDao).insert(any(ThingUserRelation.class));

		tagThingManager.bindThingsToUsers(thingIds, userIds);

		verify(thingUserRelationDao, Mockito.times(4)).insert(any(ThingUserRelation.class));
		thingIds.forEach(thing -> userIds.forEach(user -> TestCase.assertTrue("Inserted data is incorrect",
				names.contains(thing + " " + user))));
		assertEquals("Should insert 4 entries", 4, names.size());
	}

	@Test
	public void testUnbindThingsFromUsers() throws Exception {
		AuthInfoStore.setAuthInfo("tag creator");

		Set<String> names = new HashSet();
		doAnswer((Answer<Long>) invocation -> {
			names.add(invocation.getArguments()[0] + " " + invocation.getArguments()[1]);
			return null;
		}).when(thingUserRelationDao).deleteByThingIdAndUserId(anyLong(), anyString());
		tagThingManager.unbindThingsFromUsers(thingIds, userIds);
		verify(thingUserRelationDao, times(4)).deleteByThingIdAndUserId(anyLong(), anyString());
		thingIds.forEach(thing -> userIds.forEach(user -> TestCase.assertTrue("Inserted data is incorrect",
				names.contains(thing + " " + user))));
		assertEquals("Should insert 4 entries", 4, names.size());
	}

	@Test
	public void testBindThingsToUserGroups() throws Exception {
		AuthInfoStore.setAuthInfo("tag creator");

		doReturn(mock(ThingUserGroupRelation.class)).when(thingUserGroupRelationDao).find(anyLong(), anyLong());

		tagThingManager.bindThingsToUserGroups(thingIds, userGroupIds);

		verify(thingUserGroupRelationDao, times(0)).insert(any(ThingUserGroupRelation.class));

		Set<String> names = new HashSet();
		doReturn(null).when(thingUserGroupRelationDao).find(anyLong(), anyLong());
		doAnswer((Answer<Long>) invocation -> {
			ThingUserGroupRelation relation = (ThingUserGroupRelation) invocation.getArguments()[0];
			names.add(relation.getThingId() + " " + relation.getUserGroupId());
			return null;
		}).when(thingUserGroupRelationDao).insert(any(ThingUserGroupRelation.class));

		tagThingManager.bindThingsToUserGroups(thingIds, userGroupIds);

		verify(thingUserGroupRelationDao, Mockito.times(4)).insert(any(ThingUserGroupRelation.class));
		thingIds.forEach(thing -> userGroupIds.forEach(group -> TestCase.assertTrue("Inserted data is incorrect",
				names.contains(thing + " " + group))));
		assertEquals("Should insert 4 entries", 4, names.size());
	}

	@Test
	public void testUnbindThingsFromUserGroups() throws Exception {
		AuthInfoStore.setAuthInfo("tag creator");

		Set<String> names = new HashSet();
		doAnswer((Answer<Long>) invocation -> {
			names.add(invocation.getArguments()[0] + " " + invocation.getArguments()[1]);
			return null;
		}).when(thingUserGroupRelationDao).deleteByThingIdAndUserGroupId(anyLong(), anyLong());
		tagThingManager.unbindThingsFromUserGroups(thingIds, userGroupIds);
		verify(thingUserGroupRelationDao, times(4)).deleteByThingIdAndUserGroupId(anyLong(), anyLong());
		thingIds.forEach(thing -> userGroupIds.forEach(group -> TestCase.assertTrue("Inserted data is incorrect",
				names.contains(thing + " " + group))));
		assertEquals("Should insert 4 entries", 4, names.size());
	}

	@Test
	public void testGetAccessibleThingsByType() throws Exception {
		String user = "Someone";

		doReturn(Optional.ofNullable(Arrays.asList(2200L))).when(groupUserRelationDao).findUserGroupIds(anyString());

		doAnswer((Answer<Optional<List<Long>>>) invocation -> {
			Collection<Long> tagIds = (Collection<Long>) invocation.getArguments()[0];
			List<Long> thingIds = new ArrayList();
			tagIds.forEach(id -> {
				if (id == 2200L) {
					thingIds.add(11001L);
				}
			});
			return Optional.ofNullable(thingIds);
		}).when(thingUserGroupRelationDao).findThingIds(anyListOf(Long.class));

		doReturn(Optional.ofNullable(Arrays.asList(11002L))).when(thingUserRelationDao).findThingIds(eq(user));

		doReturn(Optional.ofNullable(Arrays.asList(2400L))).when(tagUserRelationDao).findTagIds(eq(user));

		doAnswer((Answer<Optional<List<Long>>>) invocation -> {
			Collection<Long> tagIds = (Collection<Long>) invocation.getArguments()[0];
			List<Long> thingIds = new ArrayList();
			tagIds.forEach(id -> {
				if (id == 2400L) {
					thingIds.add(11003L);
				}
			});
			return Optional.ofNullable(thingIds);
		}).when(tagThingRelationDao).findThingIds(anyCollectionOf(Long.class));

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
	public void testGetThingTypesOfAccessibleThingsByTagIds() throws Exception {
		doReturn(Optional.ofNullable(Collections.emptyList())).when(tagUserRelationDao).
				findAccessibleTagIds(anyString(), anyCollectionOf(Long.class));
		try {
			tagThingManager.getThingTypesOfAccessibleThingsByTagIds("user", Collections.singletonList("100"));
			fail("Expect an ObjectNotFoundException");
		} catch (ObjectNotFoundException e) {
		}

		doReturn(Optional.ofNullable(Arrays.asList(100L, 200L))).when(tagUserRelationDao).
				findAccessibleTagIds(anyString(), anyCollectionOf(Long.class));
		final String[] received = new String[1];
		doAnswer(invocation -> {
			received[0] = invocation.getArguments()[0].toString();
			return Collections.emptyList();
		}).when(globalThingDao).findThingTypeBytagIDs(anyString());
		tagThingManager.getThingTypesOfAccessibleThingsByTagIds("user", Arrays.asList("100", "200"));
		assertEquals("100,200", received[0]);

		try {
			tagThingManager.getThingTypesOfAccessibleThingsByTagIds("user", Arrays.asList("100dsf", "200"));
			fail("Expect an ObjectNotFoundException");
		} catch (ObjectNotFoundException e) {
		}
	}

	@Test
	public void testGetTypesOfAccessibleThingsWithCount() throws Exception {
		String user = "Someone";

		doReturn(Optional.ofNullable(Arrays.asList(2200L))).when(groupUserRelationDao).findUserGroupIds(anyString());

		doAnswer((Answer<Optional<List<Long>>>) invocation -> {
			Collection<Long> tagIds = (Collection<Long>) invocation.getArguments()[0];
			List<Long> thingIds = new ArrayList();
			tagIds.forEach(id -> {
				if (id == 2200L) {
					thingIds.add(11001L);
				}
			});
			return Optional.ofNullable(thingIds);
		}).when(thingUserGroupRelationDao).findThingIds(anyListOf(Long.class));

		doReturn(Optional.ofNullable(Arrays.asList(11002L))).when(thingUserRelationDao).findThingIds(eq(user));

		doReturn(Optional.ofNullable(Arrays.asList(2400L))).when(tagUserRelationDao).findTagIds(eq(user));

		doAnswer((Answer<Optional<List<Long>>>) invocation -> {
			Collection<Long> tagIds = (Collection<Long>) invocation.getArguments()[0];
			List<Long> thingIds = new ArrayList();
			tagIds.forEach(id -> {
				if (id == 2400L) {
					thingIds.add(11003L);
				}
			});
			return Optional.ofNullable(thingIds);
		}).when(tagThingRelationDao).findThingIds(anyCollectionOf(Long.class));

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

	@Test
	public void testGetTypesOfAccessibleThingsByTagFullName() throws Exception {
		List<Long> tagIds = Arrays.asList(1001L, 1002L);
		doReturn(Optional.ofNullable(tagIds)).when(tagUserRelationDao).findTagIds(anyString());
		doReturn(Optional.ofNullable(Collections.emptyList())).when(tagIndexDao).
				findTagIdsByIDsAndFullname(anyListOf(Long.class), anyCollectionOf(String.class));
		try {
			tagThingManager.getTypesOfAccessibleThingsByTagFullName("someone", Arrays.asList("test123").
					stream().collect(Collectors.toSet()));
			fail("Expect an ObjectNotFoundException");
		} catch (ObjectNotFoundException e) {
		}

		Set<Long> received = new HashSet();
		doReturn(Optional.ofNullable(tagIds)).when(tagIndexDao).findTagIdsByIDsAndFullname(anyListOf(Long.class),
				anySetOf(String.class));
		doReturn(Optional.ofNullable(tagIds)).when(tagThingRelationDao).findThingIds(anyListOf(Long.class));
		doAnswer((Answer<List<GlobalThingInfo>>) invocation -> {
			received.addAll((Collection<? extends Long>) invocation.getArguments()[0]);
			return Collections.emptyList();
		}).when(globalThingDao).findByIDs(anyListOf(Long.class));
		tagThingManager.getTypesOfAccessibleThingsByTagFullName("someone", Arrays.asList("test1", "test2").
				stream().collect(Collectors.toSet()));
		assertTrue("Didn't pass correct thing ids", received.containsAll(tagIds));
	}

	@Test
	public void testGetAccessibleThingById() throws Exception {
		doReturn(null).when(thingUserRelationDao).find(anyLong(), anyString());
		doReturn(Collections.emptyList()).when(thingUserGroupRelationDao).findByThingIdAndUserId(anyLong(),
				anyString());
		try {
			tagThingManager.getAccessibleThingById("someone", 100L);
			fail("Expect an ObjectNotFoundException");
		} catch (ObjectNotFoundException e) {
		}

		doReturn(mock(GlobalThingInfo.class)).when(globalThingDao).findByID(any(Serializable.class));
		doReturn(mock(ThingUserRelation.class)).when(thingUserRelationDao).find(anyLong(), anyString());
		tagThingManager.getAccessibleThingById("someone", 100L);

		doReturn(null).when(thingUserRelationDao).find(anyLong(), anyString());
		doReturn(Arrays.asList(mock(ThingUserGroupRelation.class))).when(thingUserGroupRelationDao)
				.findByThingIdAndUserId(anyLong(), anyString());
		tagThingManager.getAccessibleThingById("someone", 100L);
	}

	@Test
	public void testCreateThing() throws Exception {
		GlobalThingInfo thingInfo = new GlobalThingInfo();

		// invalid kiiAppID
		doReturn(null).when(appInfoDao).getAppInfoByID(anyString());
		thingInfo.setKiiAppID("KiiAppId");
		try {
			tagThingManager.createThing(thingInfo, "location", Collections.emptyList());
			fail("Expect an ObjectNotFoundException");
		} catch (ObjectNotFoundException e) {
		}

		KiiAppInfo appInfo = new KiiAppInfo();
		appInfo.setMasterApp(true);
		doReturn(appInfo).when(appInfoDao).getAppInfoByID(anyString());
		try {
			tagThingManager.createThing(thingInfo, "location", Collections.emptyList());
			fail("Expect a UnauthorizedException");
		} catch (UnauthorizedException e) {
		}

		appInfo.setMasterApp(false);
		doReturn(appInfo).when(appInfoDao).getAppInfoByID(anyString());
		doReturn(100L).when(globalThingDao).saveOrUpdate(any(GlobalThingInfo.class));
		doReturn(null).when(tagIndexDao).findTagByTagTypeAndName(anyString(), anyString());
		doAnswer((Answer<Long>) invocation -> {
			if (!((TagIndex) invocation.getArguments()[0]).getDisplayName().equals("location")) {
				fail("Unexpected location");
			}
			return 201L;
		}).when(tagIndexDao).saveOrUpdate(any(TagIndex.class));
		doReturn(null).when(tagThingRelationDao).findByThingIDAndTagID(anyLong(), anyLong());
		doReturn(200L).when(tagThingRelationDao).insert(any(TagThingRelation.class));
		doReturn(0L).when(thingUserRelationDao).saveOrUpdate(any(ThingUserRelation.class));
		doReturn(0L).when(tagUserRelationDao).saveOrUpdate(any(TagUserRelation.class));

		Long thingId = tagThingManager.createThing(thingInfo, "location", Collections.emptyList());
		assertEquals("Unexpected thing id", (Long) 100L, thingId);
		verify(tagThingRelationDao, times(1)).insert(any(TagThingRelation.class));

		doReturn(299L).when(tagIndexDao).saveOrUpdate(any(TagIndex.class));

		final ThingUserRelation[] relation = new ThingUserRelation[1];

		doAnswer((Answer<Long>) invocation -> {
			relation[0] = (ThingUserRelation) invocation.getArguments()[0];
			return 0L;
		}).when(thingUserRelationDao).saveOrUpdate(any(ThingUserRelation.class));

		thingId = tagThingManager.createThing(thingInfo, null, Collections.emptyList());

		verify(tagIndexDao, times(1)).findTagByTagTypeAndName(eq(TagType.Location.name()),
				eq(TagThingManager.DEFAULT_LOCATION));

		assertNotNull(relation[0]);
		assertEquals(thingId, relation[0].getThingId());
		assertEquals(thingInfo.getCreateBy(), relation[0].getUserId());
	}

	@Test
	public void testGetAccessibleTagsByTagTypeAndName() throws Exception {
		doReturn(Optional.ofNullable(Arrays.asList(100L))).when(tagUserRelationDao).findTagIds(anyString(),
				anyString(), anyString());
		doReturn(Optional.ofNullable(Arrays.asList(200L))).when(tagGroupRelationDao).findTagIdsByUserId(anyString(),
				anyString(), anyString());
		Set<Long> tagIds = new HashSet();
		doAnswer((Answer<List<TagIndex>>) invocation -> {
			tagIds.addAll((Collection<? extends Long>) invocation.getArguments()[0]);
			return null;
		}).when(tagIndexDao).findByIDs(anyListOf(Long.class));

		tagThingManager.getAccessibleTagsByTagTypeAndName("someone", null, null);
		assertTrue("Received ids don't match", tagIds.containsAll(Arrays.asList(100L, 200L)) && 2 == tagIds.size());
	}

	@Test
	public void testGetAccessibleTagsByUserId() throws Exception {
		doReturn(Optional.ofNullable(Arrays.asList(100L))).when(tagUserRelationDao).findTagIds(anyString());
		doReturn(Optional.ofNullable(Arrays.asList(200L))).when(tagGroupRelationDao).findTagIdsByUserId(anyString());
		Set<Long> tagIds = new HashSet();
		doAnswer((Answer<List<TagIndex>>) invocation -> {
			tagIds.addAll((Collection<? extends Long>) invocation.getArguments()[0]);
			return null;
		}).when(tagIndexDao).findByIDs(anyListOf(Long.class));

		tagThingManager.getAccessibleTagsByUserId("someone");
		assertTrue("Received ids don't match", tagIds.containsAll(Arrays.asList(100L, 200L)) && 2 == tagIds.size());
	}

	@Test
	public void testGetThingsByTagIds() throws Exception {
		List<GlobalThingInfo> result = tagThingManager.getThingsByTagIds(null);
		assertNotNull("Should not be null", result);
		assertTrue("Should be empty list", result.isEmpty());

		result = tagThingManager.getThingsByTagIds(Collections.emptySet());
		assertNotNull("Should not be null", result);
		assertTrue("Should be empty list", result.isEmpty());

		doReturn(Optional.ofNullable(Arrays.asList(200L))).when(tagThingRelationDao).findThingIds(
				anyCollectionOf(Long.class));
		Set<Long> received = new HashSet();
		doAnswer((Answer<List<GlobalThingInfo>>) invocation -> {
			received.addAll((Collection<? extends Long>) invocation.getArguments()[0]);
			return null;
		}).when(globalThingDao).findByIDs(anyListOf(Long.class));

		tagThingManager.getThingsByTagIds(new HashSet(Arrays.asList(100L)));

		assertTrue("Received ids don't match", 1 == received.size() && received.contains(200L));
	}

	@Test
	public void testGetUsersOfThing() throws Exception {
		doReturn(mock(ThingUserRelation.class)).when(thingUserRelationDao).find(anyLong(), anyString());
		doReturn(mock(GlobalThingInfo.class)).when(globalThingDao).findByID(any(Serializable.class));
		doReturn(Optional.ofNullable(Arrays.asList(100L))).when(tagThingRelationDao).findTagIds(anyLong());
		doReturn(Optional.ofNullable(Arrays.asList(200L))).when(tagGroupRelationDao).findUserGroupIdsByTagIds(
				anyListOf(Long.class));
		doReturn(Optional.ofNullable(Arrays.asList(201L))).when(thingUserGroupRelationDao).findUserGroupIds(anyLong());
		doReturn(Optional.ofNullable(Arrays.asList("user1", "user2"))).when(groupUserRelationDao).
				findUserIds(anyListOf(Long.class));
		doReturn(Arrays.asList("user3")).when(thingUserRelationDao).findUserIds(anyLong());
		doReturn(Optional.ofNullable(Arrays.asList("user4"))).when(tagUserRelationDao).findUserIds(anyListOf(Long
				.class));

		doAnswer((Answer<List<BeehiveUser>>) invocation -> {
			List<String> userIds = (List<String>) invocation.getArguments()[0];
			List<BeehiveUser> users = new ArrayList();
			userIds.forEach(id -> {
				BeehiveUser user = new BeehiveUser();
				user.setKiiLoginName(id);
				users.add(user);
			});
			return users;
		}).when(userDao).getUserByIDs(anyListOf(String.class));

		List<String> userIds = tagThingManager.getUsersOfAccessibleThing("someone", 1000L).stream().map
				(BeehiveUser::getKiiLoginName).collect(Collectors.toList());
		assertEquals(4, userIds.stream().collect(Collectors.toSet()).size());
	}

	@Test
	public void testGetAccessibleThings() throws Exception {
		String user = "Someone";

		doReturn(Optional.ofNullable(Arrays.asList(2200L))).when(groupUserRelationDao).findUserGroupIds(anyString());

		doAnswer((Answer<Optional<List<Long>>>) invocation -> {
			Collection<Long> tagIds = (Collection<Long>) invocation.getArguments()[0];
			List<Long> thingIds = new ArrayList();
			tagIds.forEach(id -> {
				if (id == 2200L) {
					thingIds.add(11001L);
				}
			});
			return Optional.ofNullable(thingIds);
		}).when(thingUserGroupRelationDao).findThingIds(anyListOf(Long.class));

		doReturn(Optional.ofNullable(Arrays.asList(11002L))).when(thingUserRelationDao).findThingIds(eq(user));

		doReturn(Optional.ofNullable(Arrays.asList(2400L))).when(tagUserRelationDao).findTagIds(eq(user));

		doAnswer((Answer<Optional<List<Long>>>) invocation -> {
			Collection<Long> tagIds = (Collection<Long>) invocation.getArguments()[0];
			List<Long> thingIds = new ArrayList();
			tagIds.forEach(id -> {
				if (id == 2400L) {
					thingIds.add(11003L);
				}
			});
			return Optional.ofNullable(thingIds);
		}).when(tagThingRelationDao).findThingIds(anyCollectionOf(Long.class));

		Set<Long> thingIds = new HashSet();
		doAnswer((Answer<List<GlobalThingInfo>>) invocation -> {
			thingIds.addAll((Collection<? extends Long>) invocation.getArguments()[0]);
			return null;
		}).when(globalThingDao).findByIDs(anyCollectionOf(Long.class));

		List<Long> expected = Arrays.asList(11001L, 11002L, 11003L);
		tagThingManager.getAccessibleThingsByUserId("Someone");
		assertTrue("Thing ids don't match", thingIds.containsAll(expected));
		assertEquals("Number of thing ids doesn't match", expected.size(), thingIds.size());
	}

	@Test
	public void testGetUserGroupsOfThing() throws Exception {
		doReturn(mock(ThingUserRelation.class)).when(thingUserRelationDao).find(anyLong(), anyString());
		doReturn(mock(GlobalThingInfo.class)).when(globalThingDao).findByID(any(Serializable.class));
		doReturn(Optional.ofNullable(Arrays.asList(100L))).when(tagThingRelationDao).findTagIds(anyLong());
		doReturn(Optional.ofNullable(Arrays.asList(200L))).when(tagGroupRelationDao).
				findUserGroupIdsByTagIds(anyListOf(Long.class));
		doReturn(Optional.ofNullable(Arrays.asList(300L))).when(thingUserGroupRelationDao).
				findUserGroupIds(anyLong());
		doAnswer((Answer<List<UserGroup>>) invocation -> {
			List<Long> ids = (List<Long>) invocation.getArguments()[0];
			List<UserGroup> groups = new ArrayList();
			ids.forEach(id -> {
				UserGroup g = new UserGroup();
				g.setId(id);
				groups.add(g);
			});
			return groups;
		}).when(userGroupDao).findByIDs(anyCollectionOf(Long.class));
		List<Long> result = tagThingManager.getUserGroupsOfAccessibleThing("someone", 100L).stream().
				map(UserGroup::getId).collect(Collectors.toList());
		assertEquals(2, result.size());
		assertTrue(result.contains(200L) && result.contains(300L));
	}

	@Test
	public void testGetAccessibleThingsByUserGroupId() throws Exception {
		doReturn(Optional.ofNullable(Collections.emptyList())).when(tagGroupRelationDao).
				findTagIdsByUserGroupId(anyLong());
		doReturn(Optional.ofNullable(Arrays.asList(100L))).when(tagThingRelationDao).
				findThingIds(anyCollectionOf(Long.class));
		doReturn(Optional.ofNullable(Arrays.asList(200L))).when(thingUserGroupRelationDao).
				findThingIds(anyLong());
		doReturn(null).when(globalThingDao).findByIDs(anyCollectionOf(Long.class));

		tagThingManager.getAccessibleThingsByUserGroupId(200L);

		verify(globalThingDao, times(1)).findByIDs(anyCollectionOf(Long.class));
	}

	@Test
	public void testGetAccessibleTagsByUserIdAndLocations() throws Exception {
		doReturn(Optional.ofNullable(Arrays.asList(100L))).when(tagUserRelationDao).findTagIds(eq("Someone"));
		doReturn(Optional.ofNullable(Arrays.asList(200L))).when(tagGroupRelationDao).findTagIdsByUserId(eq("Someone"));
		Set<Long> ids = new HashSet();
		doAnswer((Answer<Optional<List<TagIndex>>>) invocation -> {
			ids.addAll((Collection<? extends Long>) invocation.getArguments()[0]);
			return Optional.ofNullable(null);
		}).when(tagIndexDao).findTagsByTagIdsAndLocations(anyCollectionOf(Long.class), anyString());

		tagThingManager.getAccessibleTagsByUserIdAndLocations("Someone", "location");

		assertTrue(ids.contains(100L) && ids.contains(200L) && 2 == ids.size());
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
