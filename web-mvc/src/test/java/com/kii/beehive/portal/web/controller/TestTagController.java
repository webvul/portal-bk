package com.kii.beehive.portal.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.business.manager.TagThingManager;
import com.kii.beehive.business.manager.UserManager;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.exception.ObjectNotFoundException;
import com.kii.beehive.portal.jdbc.dao.*;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.portal.jdbc.entity.TagUserRelation;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.WebTestTemplate;
import com.kii.beehive.portal.web.constant.Constants;
import com.kii.beehive.portal.web.exception.PortalException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import java.util.*;

import static junit.framework.TestCase.*;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by USER on 12/1/15.
 */
public class TestTagController extends WebTestTemplate {

	private final static String KII_APP_ID = "0af7a7e7";
	@Autowired
	private ObjectMapper mapper;

	@Spy
	@Autowired
	private TagIndexDao tagIndexDao;

	@Spy
	@Autowired
	private TagThingManager tagThingManager;

	@Spy
	@Autowired
	private GlobalThingSpringDao globalThingDao;

	@Spy
	@Autowired
	private TagThingRelationDao tagThingRelationDao;

	@InjectMocks
	private TagController tagController;

	@Spy
	@Autowired
	private TagGroupRelationDao tagGroupRelationDao;

	@Spy
	@Autowired
	private TagUserRelationDao tagUserRelationDao;


	@Spy
	@Autowired
	private UserGroupDao userGroupDao;

	@Spy
	@Autowired
	private TeamTagRelationDao teamTagRelationDao;

	@Spy
	@Autowired
	private UserManager userManager;

	private String displayName = "someDisplayName";

	private Long tagIDForTest;

	private String tokenForTest = BEARER_SUPER_TOKEN;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		List<TagIndex> tags = tagIndexDao.findAll();
		for (TagIndex tag : tags) {
			tagIndexDao.deleteByID(tag.getId());
		}
		AuthInfoStore.setAuthInfo(null);
	}

	@Test
	public void testGetTagsByUser() throws Exception {
		doReturn(null).when(tagThingManager).getAccessibleTagsByUserId(anyString());

		tagController.getTagsByUser("Someone");

		verify(tagThingManager, times(1)).getAccessibleTagsByUserId(anyString());
	}

	@Test
	public void testGetUsersByFullTagName() throws Exception {
		doReturn(Collections.singletonList("test")).when(tagThingManager).getUsersOfAccessibleTags(anyString(),
				anyString());
		doThrow(new ObjectNotFoundException("test")).when(tagThingManager).getUsers(anyListOf(String.class));

		try {
			tagController.getUsersByFullTagName("some tag");
			fail("Expect an PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
		}

		doReturn(null).when(tagThingManager).getUsers(anyListOf(String.class));
		tagController.getUsersByFullTagName("some tag");
	}

	@Test
	public void testGetTagsByUserGroup() throws Exception {
		doReturn(null).when(tagThingManager).getAccessibleTagsByUserGroupId(anyLong());

		tagController.getTagsByUserGroup(100L);

		verify(tagThingManager, times(1)).getAccessibleTagsByUserGroupId(anyLong());
	}

	@Test
	public void testGetUserGroupsByFullTagName() throws Exception {
		doReturn(Arrays.asList(100L)).when(tagThingManager).getUserGroupsOfAccessibleTags(anyString(), anyString());
		doThrow(new ObjectNotFoundException("test")).when(tagThingManager).getUserGroupsByIds(anyListOf(Long.class));

		try {
			tagController.getUserGroupsByFullTagName("some tag");
			fail("Expect a PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
		}

		doReturn(null).when(tagThingManager).getUserGroupsByIds(anyListOf(Long.class));
		tagController.getUserGroupsByFullTagName("some tag");
	}

	@Test
	public void testCreateTag() throws Exception {
		String keyId = "id";
		String keyName = "tagName";

		TagIndex tagIndex = new TagIndex();
		tagIndex.setDisplayName("Tag 1");
		tagIndex.setDescription("Tag");
		tagIndex.setTagType(TagType.Custom);

		AuthInfoStore.setAuthInfo("user1");

		Map<String, Object> result = tagController.createTag(tagIndex);
		assertNotNull("Result of createTag should not be null", result);
		Long id = (Long) result.get(keyId);
		String name = result.get(keyName).toString();
		assertEquals("Name doesn't match", TagType.Custom.getTagName(tagIndex.getDisplayName()), name);

		List<TagUserRelation> relations = tagUserRelationDao.findByUserId("user1").get();
		assertNotNull("Relations should exist.", relations);
		assertEquals("There should be only one relation.", 1, relations.size());
		assertEquals("Tag id doesn't match", id, relations.get(0).getTagId());
		assertEquals("User id doesn't match", "user1", relations.get(0).getUserId());

		TagIndex tag = tagIndexDao.findByID(id);
		assertEquals("Creator doesn't match.", "user1", tag.getCreateBy());
	}

	@Test
	public void testRemoveTag() throws Exception {
		try {
			tagController.removeTag(null);
			fail("Expect an exception");
		} catch (PortalException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
		}

		doThrow(new ObjectNotFoundException("test")).when(tagThingManager).getCreatedTagIdsByTypeAndDisplayNames(
				anyString(), any(TagType.class), anyListOf(String.class));

		try {
			tagController.removeTag("displayName");
			fail("Expect an exception");
		} catch (PortalException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
		}

		doReturn(Arrays.asList(100L)).when(tagThingManager).getCreatedTagIdsByTypeAndDisplayNames(
				anyString(), any(TagType.class), anyListOf(String.class));

		verify(tagThingManager, times(0)).removeTag(eq(100L));

		tagController.removeTag("displayName");

		verify(tagThingManager, times(1)).removeTag(eq(100L));
	}

	@Test
	public void testBindTagToUser() throws Exception {
		doReturn(Arrays.asList(100L)).when(tagThingManager).getCreatedTagIdsByFullTagName(anyString(), anyString());
		BeehiveUser someone = new BeehiveUser();
		someone.setKiiLoginName("Someone");
		doReturn(Arrays.asList(someone)).when(tagThingManager).getUsers(anyListOf(String.class));

		List<String> userIds = new ArrayList();
		doAnswer((Answer<Object>) invocation -> {
			userIds.addAll((Collection<? extends String>) invocation.getArguments()[1]);
			return null;
		}).when(tagThingManager).bindTagsToUsers(anyCollectionOf(Long.class),
				anyCollectionOf(String.class));

		tagController.bindTagToUser("fullname", "Someone");

		assertEquals(1, userIds.size());
		assertEquals("Someone", userIds.get(0));
	}

	@Test
	public void testUnbindTagFromUser() throws Exception {
		doReturn(Arrays.asList(100L)).when(tagThingManager).getCreatedTagIdsByFullTagName(anyString(), anyString());
		BeehiveUser someone = new BeehiveUser();
		someone.setKiiLoginName("Someone");
		doReturn(Arrays.asList(someone)).when(tagThingManager).getUsers(anyListOf(String.class));

		List<String> userIds = new ArrayList();
		doAnswer((Answer<Object>) invocation -> {
			userIds.addAll((Collection<? extends String>) invocation.getArguments()[1]);
			return null;
		}).when(tagThingManager).unbindTagsFromUsers(anyCollectionOf(Long.class),
				anyCollectionOf(String.class));

		tagController.unbindTagFromUser("fullname", "Someone");

		assertEquals(1, userIds.size());
		assertEquals("Someone", userIds.get(0));
	}

	@Test
	public void testBindUserGroupToTag() throws Exception {
		// Error test
		String[] blankTagIds = new String[]{null, " "};
		String[] blankUserGroupIds = new String[]{null, " "};
		for (String tagIds : blankTagIds) {
			for (String userGroupIds : blankUserGroupIds) {
				try {
					tagController.bindTagToUserGroup(tagIds, userGroupIds);
					fail("Expect a PortalException");
				} catch (PortalException e) {
					assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
				}
			}
		}

		doReturn(Arrays.asList(100L)).when(tagThingManager).getCreatedTagIdsByFullTagName(anyString(), anyString());
		doReturn(Arrays.asList(200L)).when(tagThingManager).getUserGroupIds(anyListOf(String.class));
		doNothing().when(tagThingManager).bindTagsToUserGroups(anyListOf(Long.class), anyListOf(Long.class));

		tagController.bindTagToUserGroup("12,34", "56,78");

		verify(tagThingManager, times(1)).bindTagsToUserGroups(anyListOf(Long.class), anyListOf(Long.class));
	}

	@Test
	public void testUnbindUserGroupFromTag() throws Exception {
		// Error test
		String[] blankTagIds = new String[]{null, " "};
		String[] blankUserGroupIds = new String[]{null, " "};
		for (String tagIds : blankTagIds) {
			for (String userGroupIds : blankUserGroupIds) {
				try {
					tagController.unbindTagsFromUserGroups(tagIds, userGroupIds);
					fail("Expect a PortalException");
				} catch (PortalException e) {
					assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
				}
			}
		}

		doReturn(Arrays.asList(100L)).when(tagThingManager).getCreatedTagIdsByFullTagName(anyString(), anyString());
		doReturn(Arrays.asList(200L)).when(tagThingManager).getUserGroupIds(anyListOf(String.class));
		doNothing().when(tagThingManager).unbindTagsFromUserGroups(anyListOf(Long.class), anyListOf(Long.class));

		tagController.unbindTagsFromUserGroups("12,34", "56,78");

		verify(tagThingManager, times(1)).unbindTagsFromUserGroups(anyListOf(Long.class), anyListOf(Long.class));
	}

	@Test
	public void testFindTags() throws Exception {
		tagController.findTags("Test", "123");
		verify(tagThingManager, times(1)).getAccessibleTagsByTagTypeAndName(anyString(),
				eq(StringUtils.capitalize("Test")), eq("123"));
	}

	@Test
	public void testFindLocation() throws Exception {
		Set<String> displayNames = new HashSet<>();
		displayNames.add("floor1-lobby");
		displayNames.add("floor1-room1-counter1");
		displayNames.add("floor1-room1-counter2");
		displayNames.add("floor2-room1");
		displayNames.add("floor2-room2");

		AuthInfoStore.setAuthInfo("TagCreator");
		TagIndex tagIndex = new TagIndex();
		tagIndex.setTagType(TagType.Location);
		displayNames.forEach(name -> {
			tagIndex.setDisplayName(name);
			tagIndexDao.saveOrUpdate(tagIndex);
		});

		List<String> locations = tagController.findAllLocations();
		assertNotNull("Location should not be null", locations);
		assertTrue("Didn't find all locations", displayNames.containsAll(locations) &&
				locations.containsAll(displayNames));

		locations = tagController.findLocations("floor2");
		assertNotNull("Location should not be null", locations);
		locations.forEach(location -> assertTrue("Location should start with floor2", location.startsWith("floor2-")));

		locations = tagController.findLocations("room1");
		assertTrue("Should not find any locations", null == locations || locations.isEmpty());

		AuthInfoStore.setAuthInfo("Someone");
		locations = tagController.findLocations("");
		assertTrue("Should not find any locations", null == locations || locations.isEmpty());
	}

	@Test
	public void testWebCreateTag() throws Exception {

		Map<String, Object> request = new HashMap<>();
		request.put("displayName", displayName);
		request.put("description", "some description");

		String ctx = mapper.writeValueAsString(request);

		String result = this.mockMvc.perform(
				post("/tags/custom").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Map<String, Object> map = mapper.readValue(result, Map.class);

		System.out.println("Response:" + result);

		tagIDForTest = Long.valueOf((int) map.get("id"));

		// assert http return
		String tagName = (String) map.get("tagName");
		assertEquals(TagType.Custom + "-" + displayName, tagName);

	}

	@Test
	public void testWebUpdateTag() throws Exception {

		this.testWebCreateTag();

		// update the tag
		Map<String, Object> request = new HashMap<>();
		request.put("displayName", displayName);
		request.put("description", "some description new");

		String ctx = mapper.writeValueAsString(request);

		String result = this.mockMvc.perform(
				post("/tags/custom").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Map<String, Object> map = mapper.readValue(result, Map.class);

		System.out.println("Response:" + result);

		// assert http return
		Long tagID = Long.valueOf((int) map.get("id"));
		assertEquals(tagIDForTest, tagID);

		String tagName = (String) map.get("tagName");
		assertEquals(TagType.Custom + "-" + displayName, tagName);

		TagIndex tagIndex = tagIndexDao.findByID(tagID);
		assertEquals(tagIDForTest, (Long) tagIndex.getId());
		assertEquals(displayName, tagIndex.getDisplayName());
		assertEquals("some description new", tagIndex.getDescription());


		// create another tag as displayName changed
		request = new HashMap<>();
		request.put("displayName", displayName + "_new");
		request.put("description", "some description new");

		ctx = mapper.writeValueAsString(request);

		result = this.mockMvc.perform(
				post("/tags/custom").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		map = mapper.readValue(result, Map.class);

		System.out.println("Response:" + result);

		// assert http return
		tagID = Long.valueOf((int) map.get("id"));
		assertEquals(Long.valueOf(tagIDForTest + 1), tagID);

		tagName = (String) map.get("tagName");
		assertEquals(TagType.Custom + "-" + displayName + "_new", tagName);

		tagIndex = tagIndexDao.findByID(tagID);
		assertEquals(Long.valueOf(tagIDForTest + 1), (Long) tagIndex.getId());
		assertEquals(displayName + "_new", tagIndex.getDisplayName());
		assertEquals("some description new", tagIndex.getDescription());

	}

	@Test
	public void testWebCreateTagException() throws Exception {

		Map<String, Object> request = new HashMap<>();
		request.put("description", "some description");

		String ctx = mapper.writeValueAsString(request);

		String result = this.mockMvc.perform(
				post("/tags/custom").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isBadRequest())
				.andReturn().getResponse().getContentAsString();

	}

	@Test
	public void testWebRemoveTag() throws Exception {

		this.testWebCreateTag();

		TagIndex tagIndex = tagIndexDao.findByID(tagIDForTest);
		assertNotNull(tagIndex);

		String result = this.mockMvc.perform(
				delete("/tags/custom/" + displayName)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		tagIndex = tagIndexDao.findByID(tagIDForTest);
		assertNull(tagIndex);

	}


	@Test
	public void testWebFindLocations() throws Exception {

		// create location
		List<String> displayNames = new ArrayList<>();
		displayNames.add("floor1-lobby");
		displayNames.add("floor1-room1-counter1");
		displayNames.add("floor1-room1-counter2");
		displayNames.add("floor2-room1");
		displayNames.add("floor2-room2");
		List<Long> tagIDs = new ArrayList<>();

		AuthInfoStore.setAuthInfo("211102");
		// create tag
		for (int i = 0; i < displayNames.size(); i++) {
			TagIndex tagIndex = new TagIndex();
			tagIndex.setTagType(TagType.Location);
			tagIndex.setDisplayName(displayNames.get(i));
			tagIndexDao.saveOrUpdate(tagIndex);
		}

		// find location floor1
		String result = this.mockMvc.perform(
				get("/tags/locations/" + "floor1")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<String> list = mapper.readValue(result, List.class);

		// assert http return
		assertEquals(3, list.size());
		assertEquals(displayNames.get(0), list.get(0));
		assertEquals(displayNames.get(1), list.get(1));
		assertEquals(displayNames.get(2), list.get(2));

		// find location floor1-room1
		result = this.mockMvc.perform(
				get("/tags/locations/" + "floor1-room1")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		list = mapper.readValue(result, List.class);

		// assert http return
		assertEquals(2, list.size());
		assertEquals(displayNames.get(1), list.get(0));
		assertEquals(displayNames.get(2), list.get(1));

		// find all locations
		result = this.mockMvc.perform(
				get("/tags/locations/")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		list = mapper.readValue(result, List.class);

		// assert http return
		assertEquals(5, list.size());
		assertEquals(displayNames.get(0), list.get(0));
		assertEquals(displayNames.get(1), list.get(1));
		assertEquals(displayNames.get(2), list.get(2));
		assertEquals(displayNames.get(3), list.get(3));
		assertEquals(displayNames.get(4), list.get(4));

	}
}
