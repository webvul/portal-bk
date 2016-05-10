package com.kii.beehive.portal.web.controller;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyCollectionOf;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.manager.PortalSyncUserManager;
import com.kii.beehive.business.manager.TagThingManager;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.TagGroupRelationDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.TagThingRelationDao;
import com.kii.beehive.portal.jdbc.dao.TagUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.TeamTagRelationDao;
import com.kii.beehive.portal.jdbc.dao.UserGroupDao;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.portal.jdbc.entity.TagUserRelation;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.WebTestTemplate;
import com.kii.beehive.portal.web.constant.Constants;
import com.kii.beehive.portal.web.exception.PortalException;

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
	private PortalSyncUserManager userManager;

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

		tagController.getTagsByUser();

		verify(tagThingManager, times(1)).getAccessibleTagsByUserId(anyString());
	}

	@Test
	public void testGetUsersByFullTagName() throws Exception {
		doReturn(Collections.singletonList("test")).when(tagThingManager).getUsersOfAccessibleTags(anyString(),
				anyString());

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
	public void testUpdateTag() throws Exception {
		AuthInfoStore.setAuthInfo("Someone");

		String keyId = "id";
		String keyName = "tagName";

		TagIndex tagIndex = new TagIndex();
		tagIndex.setDisplayName("Tag 1");
		tagIndex.setDescription("Tag");
		tagIndex.setTagType(TagType.Custom);
		tagIndex.setId(100L);

		doReturn(Arrays.asList(tagIndex)).when(tagThingManager).getTagIndexes(anyListOf(String.class));
		doReturn(true).when(tagThingManager).isTagCreator(any(TagIndex.class));

		doReturn(tagIndex.getId()).when(tagThingManager).createTag(any(TagIndex.class));

		Map<String, Object> result = tagController.createTag(tagIndex);
		assertEquals(tagIndex.getId(), result.get(keyId));
		assertEquals(tagIndex.getFullTagName(), result.get(keyName));
	}

	@Test
	public void testRemoveTag() throws Exception {
		try {
			tagController.removeTag(null);
			fail("Expect an exception");
		} catch (PortalException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
		}

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
		someone.setId("Someone");
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
		someone.setId("Someone");
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
		doReturn(Collections.emptyList()).when(tagThingManager).getAccessibleTagsByUserIdAndLocations(anyString(),
				anyString());

		verify(tagThingManager, times(0)).getAccessibleTagsByUserIdAndLocations(anyString(),
				anyString());

		tagController.findAllLocations();

		verify(tagThingManager, times(1)).getAccessibleTagsByUserIdAndLocations(anyString(),
				anyString());
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
}
