package com.kii.beehive.portal.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.business.manager.TagThingManager;
import com.kii.beehive.business.service.ThingIFCommandService;
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.exception.ObjectNotFoundException;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.TagThingRelationDao;
import com.kii.beehive.portal.jdbc.dao.TeamThingRelationDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.portal.jdbc.entity.UserGroup;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.constant.Constants;
import com.kii.beehive.portal.web.controller.ThingController;
import com.kii.beehive.portal.web.controller.ThingIFController;
import com.kii.beehive.portal.web.entity.ThingCommandRestBean;
import com.kii.beehive.portal.web.entity.ThingRestBean;
import com.kii.beehive.portal.web.exception.BeehiveUnAuthorizedException;
import com.kii.beehive.portal.web.exception.PortalException;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.TagSelector;
import com.kii.extension.ruleengine.store.trigger.TargetAction;
import com.kii.extension.sdk.entity.thingif.Action;
import com.kii.extension.sdk.entity.thingif.OnBoardingParam;
import com.kii.extension.sdk.entity.thingif.OnBoardingResult;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.Serializable;
import java.util.*;

import static junit.framework.TestCase.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.anyCollectionOf;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anySetOf;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestThingController extends WebTestTemplate {

	private final static String KII_APP_ID = "0af7a7e7";
	private final static String KII_APP_ID_NEW = "c1744915";
	private final static String MASTER_KII_APP_ID = "da0b6a25";

	@Spy
	@Autowired
	private TagThingManager thingTagManager;

	@Spy
	@Autowired
	private TeamThingRelationDao teamThingRelationDao;

	@Spy
	@Autowired
	private TagIndexDao tagIndexDao;

	@Spy
	@Autowired
	private GlobalThingSpringDao globalThingDao;

	@Spy
	@Autowired
	private TagThingRelationDao tagThingRelationDao;

	@Mock
	@Autowired
	private ThingIFInAppService thingIFInAppService;

	@Mock
	@Autowired
	private ThingIFCommandService thingIFCommandService;

	@Autowired
	private ObjectMapper mapper;
	private Long globalThingIDForTest;

	private String[] vendorThingIDsForTest = new String[]{"someVendorThingID", "someVendorThingID-new"};

	private List<Long> globalThingIDListForTets = new ArrayList<>();

	private String[] displayNames = new String[]{"A", "B"};

	private String tokenForTest = BEARER_SUPER_TOKEN;

	@InjectMocks
	private ThingController thingController;

	@InjectMocks
	private ThingIFController thingIFController;

	@Before
	public void before() {
		super.before();

		for (String displayName : displayNames) {
			TagIndex tagIndex = new TagIndex();
			tagIndex.setTagType(TagType.Custom);
			tagIndex.setDisplayName(displayName);
			tagIndex.setFullTagName(TagType.Custom.getTagName(displayName));

			tagIndexDao.saveOrUpdate(tagIndex);
		}

		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetUsersByThing() throws Exception {
		doThrow(new ObjectNotFoundException("test")).when(thingTagManager).getAccessibleThingById(anyString(),
				anyLong());
		try {
			thingController.getUsersByThing(100L);
			fail("Expect an PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
		}

		doReturn(mock(GlobalThingInfo.class)).when(thingTagManager).getAccessibleThingById(anyString(), anyLong());
		doReturn(null).when(thingTagManager).getUsersOfThing(anyLong());

		thingController.getUsersByThing(100L);

		verify(thingTagManager, times(1)).getUsersOfThing(anyLong());
	}

	@Test
	public void testGetThingsByUser() throws Exception {
		doReturn(null).when(thingTagManager).getAccessibleThingsByUserId(anyString());
		thingController.getThingsByUser("someone");

		doReturn(Arrays.asList(mock(GlobalThingInfo.class))).when(thingTagManager).getAccessibleThingsByUserId(anyString());
		thingController.getThingsByUser("someone");
	}

	@Test
	public void testGetThingTypeByTagFullName() throws Exception {
		doThrow(new ObjectNotFoundException("test")).when(thingTagManager).getTypesOfAccessibleThingsByTagFullName(
				anyString(), anySetOf(String.class));
		try {
			thingController.getThingTypeByTagFullName("test");
			fail("Expect a PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
		}

		doReturn(null).when(thingTagManager).getTypesOfAccessibleThingsByTagFullName(
				anyString(), anySetOf(String.class));
		thingController.getThingTypeByTagFullName("test");
	}

	@Test
	public void bindThingsToUserGroups() throws Exception {
		// Error test
		String[] blankUserGroupIds = new String[]{null, " "};
		String[] blankThingIds = new String[]{null, " "};
		for (String userGroupId : blankUserGroupIds) {
			for (String thingIds : blankThingIds) {
				try {
					thingController.bindThingsToUserGroups(thingIds, userGroupId);
					fail("Expect a PortalException");
				} catch (PortalException e) {
					assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
				}
			}
		}

		GlobalThingInfo thingInfo = new GlobalThingInfo();
		thingInfo.setCreateBy("ThingCreator");
		thingInfo.setId(1001L);
		doReturn(Arrays.asList(thingInfo)).when(thingTagManager).getThings(anyListOf(String.class));

		UserGroup userGroup = new UserGroup();
		userGroup.setCreateBy("Someone");
		userGroup.setId(200L);
		doReturn(Arrays.asList(userGroup)).when(thingTagManager).getUserGroups(anyListOf(String.class));

		doThrow(new UnauthorizedException("test")).when(thingTagManager).bindThingsToUserGroups(anyListOf
				(GlobalThingInfo.class), anyListOf(UserGroup.class));
		try {
			thingController.bindThingsToUserGroups("some things", "some userGroups");
			fail("Expect a PortalException");
		} catch (BeehiveUnAuthorizedException e) {
			assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
		}

		doNothing().when(thingTagManager).bindThingsToUserGroups(anyListOf
				(GlobalThingInfo.class), anyListOf(UserGroup.class));
		thingController.bindThingsToUserGroups("some things", "some userGroups");
	}

	@Test
	public void unbindThingsFromUserGroups() throws Exception {
		// Error test
		String[] blankUserGroupIds = new String[]{null, " "};
		String[] blankThingIds = new String[]{null, " "};
		for (String userGroupId : blankUserGroupIds) {
			for (String thingIds : blankThingIds) {
				try {
					thingController.unbindThingsFromUserGroups(thingIds, userGroupId);
					fail("Expect a PortalException");
				} catch (PortalException e) {
					assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
				}
			}
		}

		GlobalThingInfo thingInfo = new GlobalThingInfo();
		thingInfo.setCreateBy("ThingCreator");
		thingInfo.setId(1001L);
		doReturn(Arrays.asList(thingInfo)).when(thingTagManager).getThings(anyListOf(String.class));

		UserGroup userGroup = new UserGroup();
		userGroup.setCreateBy("Someone");
		userGroup.setId(200L);
		doReturn(Arrays.asList(userGroup)).when(thingTagManager).getUserGroups(anyListOf(String.class));

		doThrow(new UnauthorizedException("test")).when(thingTagManager).unbindThingsFromUserGroups(anyListOf
				(GlobalThingInfo.class), anyListOf(UserGroup.class));
		try {
			thingController.unbindThingsFromUserGroups("some things", "some userGroups");
			fail("Expect a PortalException");
		} catch (BeehiveUnAuthorizedException e) {
			assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
		}

		doNothing().when(thingTagManager).unbindThingsFromUserGroups(anyListOf
				(GlobalThingInfo.class), anyListOf(UserGroup.class));
		thingController.unbindThingsFromUserGroups("some things", "some userGroups");
	}

	@Test
	public void testBindThingsToUsers() throws Exception {
		// Error test
		String[] blankUserIds = new String[]{null, " "};
		String[] blankThingIds = new String[]{null, " "};
		for (String userId : blankUserIds) {
			for (String thingIds : blankThingIds) {
				try {
					thingController.bindThingsToUsers(thingIds, userId);
					fail("Expect a PortalException");
				} catch (PortalException e) {
					assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
				}
			}
		}

		GlobalThingInfo thingInfo = new GlobalThingInfo();
		thingInfo.setCreateBy("ThingCreator");
		thingInfo.setId(1001L);
		doReturn(Arrays.asList(thingInfo)).when(thingTagManager).getThings(anyListOf(String.class));

		BeehiveUser user = new BeehiveUser();
		user.setKiiLoginName("Someone");
		doReturn(Arrays.asList(user)).when(thingTagManager).getUsers(anyListOf(String.class));

		doThrow(new UnauthorizedException("test")).when(thingTagManager).bindThingsToUsers(anyListOf
				(GlobalThingInfo.class), anyListOf(BeehiveUser.class));
		try {
			thingController.bindThingsToUsers("some things", "some userGroups");
			fail("Expect a PortalException");
		} catch (BeehiveUnAuthorizedException e) {
			assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
		}

		doNothing().when(thingTagManager).bindThingsToUsers(anyListOf
				(GlobalThingInfo.class), anyListOf(BeehiveUser.class));
		thingController.bindThingsToUsers("some things", "some userGroups");
	}

	@Test
	public void testUnbindThingsFromUsers() throws Exception {
		// Error test
		String[] blankUserIds = new String[]{null, " "};
		String[] blankThingIds = new String[]{null, " "};
		for (String userId : blankUserIds) {
			for (String thingIds : blankThingIds) {
				try {
					thingController.unbindThingsFromUsers(thingIds, userId);
					fail("Expect a PortalException");
				} catch (PortalException e) {
					assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
				}
			}
		}

		GlobalThingInfo thingInfo = new GlobalThingInfo();
		thingInfo.setCreateBy("ThingCreator");
		thingInfo.setId(1001L);
		doReturn(Arrays.asList(thingInfo)).when(thingTagManager).getThings(anyListOf(String.class));

		BeehiveUser user = new BeehiveUser();
		user.setKiiLoginName("Someone");
		doReturn(Arrays.asList(user)).when(thingTagManager).getUsers(anyListOf(String.class));

		doThrow(new UnauthorizedException("test")).when(thingTagManager).unbindThingsFromUsers(anyListOf
				(GlobalThingInfo.class), anyListOf(BeehiveUser.class));
		try {
			thingController.unbindThingsFromUsers("some things", "some userGroups");
			fail("Expect a PortalException");
		} catch (BeehiveUnAuthorizedException e) {
			assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
		}

		doNothing().when(thingTagManager).unbindThingsFromUsers(anyListOf
				(GlobalThingInfo.class), anyListOf(BeehiveUser.class));
		thingController.unbindThingsFromUsers("some things", "some userGroups");
	}

	@Test
	public void testBindThingsToCustomTags() throws Exception {
		// Error test
		String[] blankDisplayNames = new String[]{null, " "};
		String[] blankThingIds = new String[]{null, " "};
		for (String names : blankDisplayNames) {
			for (String thingIds : blankThingIds) {
				try {
					thingController.bindThingsToCustomTags(thingIds, names);
					fail("Expect a PortalException");
				} catch (PortalException e) {
					assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
				}
			}
		}

		doReturn(Arrays.asList(mock(GlobalThingInfo.class))).when(thingTagManager).getThings(anyListOf(String.class));

		doThrow(new ObjectNotFoundException("test")).when(thingTagManager).getTagIndexes(anyCollectionOf(String
				.class), any(TagType.class));
		try {
			thingController.bindThingsToCustomTags("test", "test");
			fail("Expect a PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
		}

		TagIndex tagIndex = new TagIndex();
		tagIndex.setCreateBy("Someone");
		doReturn(Arrays.asList(tagIndex)).when(thingTagManager).getTagIndexes(anyCollectionOf(String.class),
				any(TagType.class));
		doThrow(new UnauthorizedException("test")).when(thingTagManager).unbindThingsFromTags(anyListOf(TagIndex.class)
				, anyListOf(GlobalThingInfo.class));

		AuthInfoStore.setAuthInfo("creator");
		try {
			thingController.bindThingsToCustomTags("test", "test");
			fail("Expect a PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
		}

		doNothing().when(thingTagManager).bindTagsToThings(anyListOf(TagIndex.class),
				anyListOf(GlobalThingInfo.class));
		doNothing().when(thingIFInAppService).onTagIDsChangeFire(anyListOf(Long.class), eq(true));

		thingController.bindThingsToCustomTags("test", "test");

		verify(thingIFInAppService, times(1)).onTagIDsChangeFire(anyListOf(Long.class), eq(true));
	}

	@Test
	public void testUnbindThingsFromCustomTags() throws Exception {
		// Error test
		String[] blankDisplayNames = new String[]{null, " "};
		String[] blankThingIds = new String[]{null, " "};
		for (String names : blankDisplayNames) {
			for (String thingIds : blankThingIds) {
				try {
					thingController.unbindThingsFromCustomTags(thingIds, names);
					fail("Expect a PortalException");
				} catch (PortalException e) {
					assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
				}
			}
		}

		doReturn(Arrays.asList(mock(GlobalThingInfo.class))).when(thingTagManager).getThings(anyListOf(String.class));

		doThrow(new ObjectNotFoundException("test")).when(thingTagManager).getTagIndexes(anyCollectionOf(String
				.class), any(TagType.class));
		try {
			thingController.unbindThingsFromCustomTags("test", "test");
			fail("Expect a PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
		}

		doReturn(Arrays.asList(mock(TagIndex.class))).when(thingTagManager).getTagIndexes(anyCollectionOf(String.class),
				any(TagType.class));
		doThrow(new UnauthorizedException("test")).when(thingTagManager).unbindThingsFromTags(anyListOf(TagIndex.class)
				, anyListOf(GlobalThingInfo.class));

		try {
			thingController.unbindThingsFromCustomTags("test", "test");
			fail("Expect a PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
		}

		doNothing().when(thingTagManager).unbindThingsFromTags(anyListOf(TagIndex.class),
				anyListOf(GlobalThingInfo.class));
		doNothing().when(thingIFInAppService).onTagIDsChangeFire(anyListOf(Long.class), eq(false));

		thingController.unbindThingsFromCustomTags("test", "test");

		verify(thingIFInAppService, times(1)).onTagIDsChangeFire(anyListOf(Long.class), eq(false));
	}

	@Test
	public void testBindThingsToTags() throws Exception {
		// Error test
		String[] blankTagIds = new String[]{null, " "};
		String[] blankThingIds = new String[]{null, " "};
		for (String tagIds : blankTagIds) {
			for (String thingIds : blankThingIds) {
				try {
					thingController.bindThingsToTags(tagIds, thingIds);
					fail("Expect a PortalException");
				} catch (PortalException e) {
					assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
				}
			}
		}

		// Thing not found
		doThrow(new ObjectNotFoundException("test")).when(thingTagManager).getThings(anyListOf(String.class));

		try {
			thingController.bindThingsToTags("thing1", "tag1");
			fail("Expect a PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
		}

		// Not thing creator
		GlobalThingInfo thingInfo = new GlobalThingInfo();
		thingInfo.setCreateBy("ThingCreator");
		doReturn(Arrays.asList(thingInfo)).when(thingTagManager).getThings(anyList());

		TagIndex tagIndex = new TagIndex();
		tagIndex.setCreateBy("ThingCreator");
		doReturn(Arrays.asList(tagIndex)).when(thingTagManager).getTagIndexes(anyListOf(String.class));
		AuthInfoStore.setAuthInfo("Someone");
		try {
			thingController.bindThingsToTags("thing1", "tag1");
			fail("Expect a PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
		}

		// Tag not found
		AuthInfoStore.setAuthInfo("ThingCreator");
		doThrow(new ObjectNotFoundException("test")).when(thingTagManager).getTagIndexes(anyList());

		try {
			thingController.bindThingsToTags("thing1", "tag1");
			fail("Expect a PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
		}

		// Not tag creator
		TagIndex tag = new TagIndex();
		tag.setCreateBy("Someone");
		doReturn(Arrays.asList(tag)).when(thingTagManager).getTagIndexes(anyList());

		try {
			thingController.bindThingsToTags("thing1", "tag1");
			fail("Expect a PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
		}

		tag.setCreateBy("ThingCreator");
		doNothing().when(thingTagManager).bindTagsToThings(anyListOf(TagIndex.class), anyListOf(GlobalThingInfo.class));
		doThrow(new RuntimeException()).when(thingIFInAppService).onTagIDsChangeFire(anyListOf(Long.class), eq(false));
		thingController.bindThingsToTags("thing1", "tag1");
		verify(thingIFInAppService, times(1)).onTagIDsChangeFire(anyListOf(Long.class), eq(true));
	}

	@Test
	public void testUnbindThingsFromTags() throws Exception {
		// Error test
		String[] blankTagIds = new String[]{null, " "};
		String[] blankThingIds = new String[]{null, " "};
		for (String tagIds : blankTagIds) {
			for (String thingIds : blankThingIds) {
				try {
					thingController.unbindThingsFromTags(tagIds, thingIds);
					fail("Expect a PortalException");
				} catch (PortalException e) {
					assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
				}
			}
		}

		// Thing not found
		doThrow(new ObjectNotFoundException("test")).when(thingTagManager).getThings(anyListOf(String.class));

		try {
			thingController.unbindThingsFromTags("thing1", "tag1");
			fail("Expect a PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
		}

		// Not thing creator
		GlobalThingInfo thingInfo = new GlobalThingInfo();
		thingInfo.setCreateBy("ThingCreator");
		doReturn(Arrays.asList(thingInfo)).when(thingTagManager).getThings(anyList());

		TagIndex tagIndex = new TagIndex();
		tagIndex.setCreateBy("ThingCreator");
		doReturn(Arrays.asList(tagIndex)).when(thingTagManager).getTagIndexes(anyListOf(String.class));
		AuthInfoStore.setAuthInfo("Someone");
		try {
			thingController.unbindThingsFromTags("thing1", "tag1");
			fail("Expect a PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
		}

		// Tag not found
		AuthInfoStore.setAuthInfo("ThingCreator");
		doThrow(new ObjectNotFoundException("test")).when(thingTagManager).getTagIndexes(anyList());

		try {
			thingController.unbindThingsFromTags("thing1", "tag1");
			fail("Expect a PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
		}

		// Not tag creator
		TagIndex tag = new TagIndex();
		tag.setCreateBy("Someone");
		doReturn(Arrays.asList(tag)).when(thingTagManager).getTagIndexes(anyList());

		try {
			thingController.unbindThingsFromTags("thing1", "tag1");
			fail("Expect a PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
		}

		tag.setCreateBy("ThingCreator");
		doNothing().when(thingTagManager).bindTagsToThings(anyListOf(TagIndex.class), anyListOf(GlobalThingInfo.class));
		doThrow(new RuntimeException()).when(thingIFInAppService).onTagIDsChangeFire(anyListOf(Long.class), eq(true));
		thingController.unbindThingsFromTags("thing1", "tag1");
		verify(thingIFInAppService, times(1)).onTagIDsChangeFire(anyListOf(Long.class), eq(false));
	}

	@Test
	public void testCreatThing() throws Exception {

		// create without tag and custom field
		Map<String, Object> request = new HashMap<>();
		request.put("vendorThingID", vendorThingIDsForTest[0]);
		request.put("kiiAppID", KII_APP_ID);
		request.put("type", "some type");
		request.put("location", "some location");

		String ctx = mapper.writeValueAsString(request);

		String result = this.mockMvc.perform(
				post("/things").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Map<String, Object> map = mapper.readValue(result, Map.class);

		// assert http reture
		globalThingIDForTest = Long.valueOf((int) map.get("globalThingID"));
		assertNotNull(globalThingIDForTest);
		assertTrue(globalThingIDForTest > 0);

	}

	private Map<String, Object> getOnboardingInfo(String vendorThingID) throws Exception {
		// get onboarding info
		String result = this.mockMvc.perform(
				get("/onboardinghelper/" + vendorThingID)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header("Authorization", super.BEARER_SUPER_TOKEN)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Map<String, Object> map = mapper.readValue(result, Map.class);

		System.out.println(map);

		// assert http return
		assertEquals("f973edcaaec9aeac36dd01ebe1c3bc49", map.get("kiiAppKey"));
		assertEquals("https://api-development-beehivecn3.internal.kii.com", map.get("kiiSiteUrl"));
		assertNotNull(map.get("ownerID"));
		assertNotNull(map.get("ownerToken"));

		return map;
	}

	private String onboarding(String vendorThingID, Map<String, Object> onboardingInfo) throws Exception {

		// do onboarding
		OnBoardingParam param = new OnBoardingParam();

		param.setVendorThingID(vendorThingID);
		param.setThingPassword(vendorThingID);
		param.setUserID((String) onboardingInfo.get("ownerID"));

		OnBoardingResult onBoardingResult = thingIFInAppService.onBoarding(param, (String) onboardingInfo.get("kiiAppID"));
		String kiiThingID = onBoardingResult.getThingID();
		System.out.println("Kii Thing ID: " + kiiThingID);

		assertTrue(kiiThingID.length() > 0);

		return kiiThingID;
	}

	private boolean checkThingExist(String vendorThingID, Map<String, Object> onboardingInfo) throws Exception {

		String kiiAppID = (String) onboardingInfo.get("kiiAppID");

		String url = onboardingInfo.get("kiiSiteUrl") + "/api/apps/" + kiiAppID + "/things/VENDOR_THING_ID:" + vendorThingID;

		HttpGet httpGet = new HttpGet(url);

		httpGet.setHeader("X-Kii-AppID", kiiAppID);
		httpGet.setHeader("X-Kii-AppKey", (String) onboardingInfo.get("kiiAppKey"));
		httpGet.setHeader("Authorization", "Bearer " + onboardingInfo.get("ownerToken"));

		int status = HttpClientBuilder.create().build().execute(httpGet).getStatusLine().getStatusCode();
		System.out.println("status: " + status);

		return status < 400;
	}


	@Test
	public void testCreateThingWithTagAndCustomFields() throws Exception {

		// create without tag and custom field
		Map<String, Object> request = new HashMap<>();
		request.put("vendorThingID", vendorThingIDsForTest[0]);
		request.put("kiiAppID", KII_APP_ID);
		request.put("type", "some type");
		request.put("location", "some location");

		// set status
		Map<String, Object> status = new HashMap<>();
		status.put("brightness", "80");
		status.put("temperature", "90");
		request.put("status", status);

		// set custom
		Map<String, Object> custom = new HashMap<>();
		custom.put("license", "123qwerty");
		custom.put("produceDate", "20001230");
		request.put("custom", custom);

		String ctx = mapper.writeValueAsString(request);

		String result = this.mockMvc.perform(
				post("/things").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Map<String, Object> map = mapper.readValue(result, Map.class);

		// assert http return
		globalThingIDForTest = Long.valueOf((int) map.get("globalThingID"));
		assertNotNull(globalThingIDForTest);
		assertTrue(globalThingIDForTest > 0);

		GlobalThingInfo thingInfo = globalThingDao.getThingByVendorThingID(vendorThingIDsForTest[0]);
		assertTrue(thingInfo != null);

		//error test duplicate venderID
		this.mockMvc.perform(
				post("/things").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isBadRequest());

		// test update
		request = new HashMap<>();
		request.put("globalThingID", globalThingIDForTest);
		request.put("vendorThingID", vendorThingIDsForTest[1]);
		request.put("kiiAppID", KII_APP_ID_NEW);
		request.put("type", "some_type_new");
		request.put("location", "some_location_new");

		// set status
		status = new HashMap<>();
		status.put("brightness", "90");
		status.put("color", "#123456");
		request.put("status", status);

		// set custom
		custom = new HashMap<>();
		custom.put("time-zone", "GMT+8");
		custom.put("produceDate", "20991230");
		request.put("custom", custom);

		ctx = mapper.writeValueAsString(request);

		result = this.mockMvc.perform(
				post("/things").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		map = mapper.readValue(result, Map.class);

		// assert http reture
		Long globalThingIDForTest = Long.valueOf((int) map.get("globalThingID"));
		assertEquals(this.globalThingIDForTest, globalThingIDForTest);

		// assert
		thingInfo = globalThingDao.getThingByVendorThingID(vendorThingIDsForTest[0]);
		assertTrue(thingInfo == null);

		thingInfo = globalThingDao.getThingByVendorThingID(vendorThingIDsForTest[1]);
		assertTrue(thingInfo != null);

	}

	@Test
	public void testCreateThingException() throws Exception {

		// no vendorThingID
		ThingRestBean thingRestBean = new ThingRestBean();
		thingRestBean.setKiiAppID("kiiAppID");
		thingRestBean.setType("Some type");
		thingRestBean.setLocation("Some location");

		try {
			thingController.createThing(thingRestBean);
			fail("Expect an PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
		}


		// invalid vendorThingID
		thingRestBean.setVendorThingID("qwe.rty");
		try {
			thingController.createThing(thingRestBean);
			fail("Expect an PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
		}

		thingRestBean.setKiiAppID(null);
		try {
			thingController.createThing(thingRestBean);
			fail("Expect an PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
		}

		thingRestBean.setKiiAppID("KiiAppId");
		thingRestBean.setVendorThingID("123456");
		doThrow(new UnauthorizedException("test")).when(thingTagManager).createThing(any(GlobalThingInfo.class),
				anyString(), anyListOf(String.class));
		try {
			thingController.createThing(thingRestBean);
			fail("Expect an PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
		}

		doReturn(100L).when(thingTagManager).createThing(any(GlobalThingInfo.class), anyString(), anyCollectionOf
				(String.class));
		Map<String, Long> result = thingController.createThing(thingRestBean);
		assertTrue("Unexpected result", result.containsKey("globalThingID") && result.get("globalThingID").equals
				(100L));


	}

	@Test
	public void testGetThingByGlobalID() throws Exception {
		doThrow(new ObjectNotFoundException("test")).when(thingTagManager).getAccessibleThingById(anyString(),
				anyLong());
		try {
			thingController.getThingByGlobalID(100L);
			fail("Expect an PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
		}

		doReturn(mock(GlobalThingInfo.class)).when(thingTagManager).getAccessibleThingById(anyString(), anyLong());
		TagIndex location = new TagIndex();
		location.setTagType(TagType.Location);
		location.setDisplayName("location");
		TagIndex custom = new TagIndex();
		custom.setTagType(TagType.Custom);
		custom.setDisplayName("Custom Tag");
		doReturn(Arrays.asList(location, custom)).when(thingTagManager).findTagIndexByGlobalThingID(eq(100L));

		ThingRestBean bean = thingController.getThingByGlobalID(100L);
		assertNotNull("Should find the thing", bean);
		assertEquals("Location doesn't match", "location", bean.getLocation());
		assertTrue("Input tag doesn't match", bean.getInputTags().contains("Custom Tag") &&
				1 == bean.getInputTags().size());
	}

	@Test
	public void testRemoveThing() throws Exception {
		doReturn(null).when(globalThingDao).findByID(any(Serializable.class));
		try {
			thingController.removeThing(100L);
			fail("Expect an PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
		}

		doReturn(mock(GlobalThingInfo.class)).when(globalThingDao).findByID(any(Serializable.class));
		doReturn(false).when(thingTagManager).isThingCreator(any(GlobalThingInfo.class));
		doReturn(false).when(thingTagManager).isThingCreator(anyCollectionOf(GlobalThingInfo.class));

		try {
			thingController.removeThing(100L);
			fail("Expect an PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
		}

		doReturn(true).when(thingTagManager).isThingCreator(any(GlobalThingInfo.class));
		doNothing().when(thingTagManager).removeThing(any(GlobalThingInfo.class));

		thingController.removeThing(100L);

		verify(thingTagManager, times(1)).removeThing(any(GlobalThingInfo.class));

		doThrow(new ObjectNotFoundException("test")).when(thingTagManager).removeThing(any(GlobalThingInfo.class));

		try {
			thingController.removeThing(100L);
			fail("Expect an PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
		}
	}


	@Test
	public void testGetThingsByTagExpress() throws Exception {
		doReturn(Collections.emptyList()).when(thingTagManager).getAccessibleTagsByTagTypeAndName(anyString(),
				anyString(), anyString());
		GlobalThingInfo thingInfo = new GlobalThingInfo();
		thingInfo.setKiiAppID("KiiAppID");
		thingInfo.setVendorThingID("123456");
		thingInfo.setCreateBy("Someone");
		doReturn(Arrays.asList(thingInfo)).when(thingTagManager).getThingsByTagIds(anySetOf(Long.class));

		List<ThingRestBean> result = thingController.getThingsByTagExpress("test", "test");
		assertNotNull("Result shouldn't be null", result);
		assertEquals("Number of things doesn't match", 1, result.size());
		assertEquals("Property doesn't match", thingInfo.getKiiAppID(), result.get(0).getKiiAppID());
		assertEquals("Property doesn't match", thingInfo.getVendorThingID(), result.get(0).getVendorThingID());
		assertEquals("Property doesn't match", thingInfo.getCreateBy(), result.get(0).getCreateBy());
	}

	@Test
	public void testGetThingsByType() throws Exception {
		GlobalThingInfo thingInfo = new GlobalThingInfo();
		thingInfo.setId(1001L);
		thingInfo.setCreateBy("Someone");
		thingInfo.setType("LED");

		doReturn(Arrays.asList(thingInfo)).when(thingTagManager).getAccessibleThingsByType(anyString(), anyString());

		List<ThingRestBean> result = thingController.getThingsByType("test");
		assertNotNull("Should not be null", result);
		assertEquals("There should be one thing object", 1, result.size());

		ThingRestBean thing = result.get(0);
		assertEquals("Property doesn't match", (Long) 1001L, thing.getId());
		assertEquals("Property doesn't match", "Someone", thing.getCreateBy());
		assertEquals("Property doesn't match", "LED", thing.getType());
	}

	@Test
	public void testGetAllType() throws Exception {
		doReturn(Collections.emptyList()).when(thingTagManager).getTypesOfAccessibleThingsWithCount(anyString());
		thingController.getAllType();
	}

	@Test
	public void testGetThingTypeByTagIDs() throws Exception {
		doThrow(new ObjectNotFoundException("test")).when(thingTagManager).getThingTypesOfAccessibleThingsByTagIds(
				anyString(), anyCollectionOf(String.class));
		try {
			thingController.getThingTypeByTagIDs("test");
			fail("Expect an PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
		}

		doReturn(Collections.singletonList("testLED")).when(thingTagManager).getThingTypesOfAccessibleThingsByTagIds(
				anyString(), anyCollectionOf(String.class));
		List<String> result = thingController.getThingTypeByTagIDs("test");
		assertTrue("Unexpected data", null != result && result.size() == 1 && result.get(0).equals("testLED"));
	}

	@Test
	public void testSendCommandToThingList() throws Exception {
		Long[] thingGroup1 = this.creatThingsForTest(3, "vendorThingIDForTest", KII_APP_ID, "LED");

		TagSelector ts = new TagSelector();
		ts.setThingList(Arrays.asList(thingGroup1));

		ThingCommand tc = new ThingCommand();
		tc.setSchema("SmartLight");
		tc.setSchemaVersion(0);
		Action action = new Action();
		action.setField("power", "true");
		tc.addAction("turnPower", action);

		TargetAction ta = new TargetAction();
		ta.setCommand(tc);

		ThingCommandRestBean rest = new ThingCommandRestBean();
		rest.setCommand(ta);
		rest.setSelector(ts);

		List<ThingCommandRestBean> restList = new ArrayList<ThingCommandRestBean>();
		restList.add(rest);

		List<Map<Long, String>> commandResultList = new ArrayList<>();

		Map<Long, String> commandResult = new HashMap<>();
		commandResult.put(1L, "commandID");
		commandResultList.add(commandResult);

		doReturn(commandResultList).when(thingIFCommandService).doCommand(anyListOf(ExecuteTarget.class), anyString());

		thingIFController.sendCommand(restList);

		//error test

		try {
			ts.setThingList(new ArrayList<Long>());
			ts.setTagCollect(new ArrayList<String>());
			thingIFController.sendCommand(restList);
			fail("Expect a PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
		}

		try {
			ts.setThingList(null);
			ts.setTagCollect(null);
			thingIFController.sendCommand(restList);
			fail("Expect a PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
		}


		try {
			AuthInfoStore.setAuthInfo("ThingCreator");
			ts.setThingList(Arrays.asList(thingGroup1));
			thingIFController.sendCommand(restList);
			fail("Expect a PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
		}

	}

	private Long[] creatThingsForTest(int creatCount, String prefixVendorThingID, String kiiAppID, String type) {
		// create thing

		Random random = new Random();

		Long[] globalThingIDs = new Long[creatCount];

		for (int i = 0; i < creatCount; i++) {
			GlobalThingInfo thingInfo = new GlobalThingInfo();
			thingInfo.setVendorThingID(prefixVendorThingID + random.nextInt(10000));
			thingInfo.setKiiAppID(kiiAppID);
			thingInfo.setType(type);
			globalThingIDs[i] = globalThingDao.saveOrUpdate(thingInfo);
		}

		return globalThingIDs;
	}

}
