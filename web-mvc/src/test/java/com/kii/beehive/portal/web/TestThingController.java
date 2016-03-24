package com.kii.beehive.portal.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.business.manager.TagThingManager;
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.exception.ObjectNotFoundException;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.TagThingRelationDao;
import com.kii.beehive.portal.jdbc.dao.TeamThingRelationDao;
import com.kii.beehive.portal.jdbc.entity.*;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.constant.Constants;
import com.kii.beehive.portal.web.controller.ThingController;
import com.kii.beehive.portal.web.exception.BeehiveUnAuthorizedException;
import com.kii.beehive.portal.web.exception.PortalException;
import com.kii.extension.sdk.entity.thingif.OnBoardingParam;
import com.kii.extension.sdk.entity.thingif.OnBoardingResult;
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

	@Autowired
	private ObjectMapper mapper;
	private Long globalThingIDForTest;

	private String[] vendorThingIDsForTest = new String[]{"someVendorThingID", "someVendorThingID-new"};

	private List<Long> globalThingIDListForTets = new ArrayList<>();

	private String[] displayNames = new String[]{"A", "B"};

	private String tokenForTest = BEARER_SUPER_TOKEN;

	@InjectMocks
	private ThingController thingController;

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

		doReturn(Arrays.asList(mock(TagIndex.class))).when(thingTagManager).getTagIndexes(anyCollectionOf(String.class),
				any(TagType.class));
		doThrow(new UnauthorizedException("test")).when(thingTagManager).unbindThingsFromTags(anyListOf(TagIndex.class)
				, anyListOf(GlobalThingInfo.class));

		try {
			thingController.bindThingsToCustomTags("test", "test");
			fail("Expect a PortalException");
		} catch (PortalException e) {
			assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
		}

		doNothing().when(thingTagManager).unbindThingsFromTags(anyListOf(TagIndex.class),
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
		doNothing().when(thingTagManager).bindTagToThing(anyListOf(TagIndex.class), anyListOf(GlobalThingInfo.class));
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
			assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
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
			assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
		}

		tag.setCreateBy("ThingCreator");
		doNothing().when(thingTagManager).bindTagToThing(anyListOf(TagIndex.class), anyListOf(GlobalThingInfo.class));
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
	public void testCreatThingWithoutLocation() throws Exception {

		// create without tag and custom field
		Map<String, Object> request = new HashMap<>();
		request.put("vendorThingID", vendorThingIDsForTest[0]);
		request.put("kiiAppID", KII_APP_ID);
		request.put("type", "some type");

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

		// query thing
		result = this.mockMvc.perform(
				get("/things/" + globalThingIDForTest)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		map = mapper.readValue(result, Map.class);

		System.out.println(map);

		assertEquals(globalThingIDForTest, Long.valueOf((Integer) map.get("globalThingID")));
		assertEquals(TagThingManager.DEFAULT_LOCATION, map.get("location"));

	}

	@Test
	public void testCreatThingWithTagAndCustomFields() throws Exception {

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
	public void testCreatThingException() throws Exception {

		// no vendorThingID
		Map<String, Object> request = new HashMap<>();
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
				.andExpect(status().isBadRequest())
				.andReturn().getResponse().getContentAsString();

		// invalid vendorThingID
		request = new HashMap<>();
		request.put("vendorThingID", "qwe.rty");
		request.put("kiiAppID", KII_APP_ID);
		request.put("type", "some type");
		request.put("location", "some location");

		ctx = mapper.writeValueAsString(request);

		result = this.mockMvc.perform(
				post("/things").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isBadRequest())
				.andReturn().getResponse().getContentAsString();

		// no kiiAppID
		request = new HashMap<>();
		request.put("vendorThingID", vendorThingIDsForTest[0]);
		request.put("type", "some type");
		request.put("location", "some location");

		ctx = mapper.writeValueAsString(request);

		result = this.mockMvc.perform(
				post("/things").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isBadRequest())
				.andReturn().getResponse().getContentAsString();

		// invalid kiiAppID
		request = new HashMap<>();
		request.put("vendorThingID", vendorThingIDsForTest[0]);
		request.put("kiiAppID", "some_non_existing_kii_app_id");
		request.put("type", "some type");
		request.put("location", "some location");

		ctx = mapper.writeValueAsString(request);

		result = this.mockMvc.perform(
				post("/things").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isNotFound())
				.andReturn().getResponse().getContentAsString();

		// master kiiAppID
		request = new HashMap<>();
		request.put("vendorThingID", vendorThingIDsForTest[0]);
		request.put("kiiAppID", MASTER_KII_APP_ID);
		request.put("type", "some type");
		request.put("location", "some location");

		ctx = mapper.writeValueAsString(request);

		result = this.mockMvc.perform(
				post("/things").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isNotFound())
				.andReturn().getResponse().getContentAsString();


	}

	@Test
	public void testGetThingByGlobalID() throws Exception {

		this.testCreatThingWithTagAndCustomFields();

		String result = this.mockMvc.perform(
				get("/things/" + globalThingIDForTest)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Map<String, Object> map = mapper.readValue(result, Map.class);

		System.out.println(map);

		// assert http return
		assertEquals(this.globalThingIDForTest, Long.valueOf((int) map.get("globalThingID")));
		assertEquals(vendorThingIDsForTest[1], map.get("vendorThingID"));
		assertEquals(KII_APP_ID_NEW, map.get("kiiAppID"));
		assertEquals("some_type_new", map.get("type"));
		assertEquals("some_location_new", map.get("location"));

		// assert status
		Map<String, Object> status = (Map<String, Object>) map.get("status");
		assertEquals(2, status.keySet().size());
		assertEquals("90", status.get("brightness"));
		assertEquals("#123456", status.get("color"));

		// assert custom
		Map<String, Object> custom = (Map<String, Object>) map.get("custom");
		assertEquals(2, custom.keySet().size());
		assertEquals("GMT+8", custom.get("time-zone"));
		assertEquals("20991230", custom.get("produceDate"));

	}

	@Test
	public void testGetThingByGlobalIDException() throws Exception {

		// 999 is not supposed to be existing
		String result = this.mockMvc.perform(
				get("/things/" + "999")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isNotFound())
				.andReturn().getResponse().getContentAsString();

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
	}

	@Test
	public void testRemoveThingAlreadyOnboarding() throws Exception {

		// create thing in global reference table
		this.testCreatThing();

		Map<String, Object> onboardingInfo = this.getOnboardingInfo(vendorThingIDsForTest[0]);

		// create thing in Kii Cloud and do onboarding
		String kiiThingID = this.onboarding(vendorThingIDsForTest[0], onboardingInfo);

		GlobalThingInfo thingInfo = globalThingDao.findByID(globalThingIDForTest);
		assertNotNull(thingInfo);

		// need to check the thing is created in Kii Cloud
		boolean thingExist = this.checkThingExist(vendorThingIDsForTest[0], onboardingInfo);
		assertTrue(thingExist);

		// add the full kii thing id to local table, as the Kii Cloud server code will update the full kii thing id to the table in internal dev
		// this operation is only for unit test, to check the scenario that thing in Kii Cloud will also be removed while deleting thing from Beehive
		globalThingDao.updateKiiThingID(vendorThingIDsForTest[0], onboardingInfo.get("kiiAppID") + "-" + kiiThingID);

		// delete thing
		String result = this.mockMvc.perform(
				delete("/things/" + globalThingIDForTest)
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		thingInfo = globalThingDao.findByID(globalThingIDForTest);
		assertNull(thingInfo);

		// need to check the thing is removed already in Kii Cloud
		thingExist = this.checkThingExist(vendorThingIDsForTest[0], onboardingInfo);
		assertFalse(thingExist);
	}

	@Test
	public void testRemoveThingException() throws Exception {

		// delete thing
		// global thing id 123456789 is not supposed to exist
		String result = this.mockMvc.perform(
				delete("/things/" + "123456789")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isNotFound())
				.andReturn().getResponse().getContentAsString();

	}

	@Test
	public void testOneThingBindMultiTags() throws Exception {

		this.testCreatThing();

		// assert no tag
		String result = this.mockMvc.perform(
				get("/things/" + globalThingIDForTest)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Map<String, Object> map = mapper.readValue(result, Map.class);
		List<String> tagList = (List<String>) map.get("tags");

		assertTrue(tagList == null || tagList.isEmpty());

		// bind tags to thing
		String displayName = displayNames[0] + "," + displayNames[1];

		String url = "/things/" + globalThingIDForTest + "/tags/custom/" + displayName;

		this.mockMvc.perform(post(url).content("{}")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk());

		// assert tags existing
		result = this.mockMvc.perform(
				get("/things/" + globalThingIDForTest)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		map = mapper.readValue(result, Map.class);
		tagList = (List<String>) map.get("tags");

		assertEquals(2, tagList.size());
		assertTrue(tagList.contains(displayNames[0]));
		assertTrue(tagList.contains(displayNames[1]));

		// bind non existing tag to thing
		displayName = "non_existing_tag";

		url = "/things/" + globalThingIDForTest + "/tags/custom/" + displayName;

		this.mockMvc.perform(post(url).content("{}")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk());

		// assert the non existing tag not bound to thing
		result = this.mockMvc.perform(
				get("/things/" + globalThingIDForTest)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		map = mapper.readValue(result, Map.class);
		tagList = (List<String>) map.get("tags");

		assertEquals(2, tagList.size());
		assertTrue(tagList.contains(displayNames[0]));
		assertTrue(tagList.contains(displayNames[1]));

	}

	@Test
	public void testOneThingUnBindMultiTags() throws Exception {

		this.testOneThingBindMultiTags();

		// unbind tags from thing
		String displayName = displayNames[0];

		String url = "/things/" + globalThingIDForTest + "/tags/custom/" + displayName;

		this.mockMvc.perform(delete(url).content("{}")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.header(Constants.ACCESS_TOKEN, tokenForTest)
		)

				.andExpect(status().isOk());

		// assert tags existing
		String result = this.mockMvc.perform(
				get("/things/" + globalThingIDForTest)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Map<String, Object> map = mapper.readValue(result, Map.class);
		List<String> tagList = (List<String>) map.get("tags");

		assertEquals(1, tagList.size());
		assertTrue(tagList.contains(displayNames[1]));

		// unbind non existing tag from thing
		displayName = "non_existing_tag," + displayNames[0];

		url = "/things/" + globalThingIDForTest + "/tags/custom/" + displayName;

		this.mockMvc.perform(delete(url).content("{}")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.header(Constants.ACCESS_TOKEN, tokenForTest)
		)

				.andExpect(status().isOk());

		// assert tags existing
		result = this.mockMvc.perform(
				get("/things/" + globalThingIDForTest)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		map = mapper.readValue(result, Map.class);
		tagList = (List<String>) map.get("tags");

		assertEquals(1, tagList.size());
		assertTrue(tagList.contains(displayNames[1]));

	}

	private List<Long> createThings(String[] vendorThingIDList) throws Exception {

		List<Long> globalThingIDList = new ArrayList<>();

		for (String vendorThingID : vendorThingIDList) {

			Map<String, Object> request = new HashMap<>();
			request.put("vendorThingID", vendorThingID);
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
			long globalThingID = Long.valueOf((int) map.get("globalThingID"));
			assertNotNull(globalThingID);
			assertTrue(globalThingID > 0);

			globalThingIDList.add(globalThingID);
		}

		return globalThingIDList;
	}

	@Test
	public void testMultiThingsBindOneTag() throws Exception {

		globalThingIDListForTets = this.createThings(vendorThingIDsForTest);

		StringBuffer globalThingIDs = new StringBuffer();
		for (Long globalThingID : globalThingIDListForTets) {
			globalThingIDs.append(",").append(globalThingID);
		}
		globalThingIDs.deleteCharAt(0);

		// bind tag to things

		String url = "/things/" + globalThingIDs.toString() + "/tags/custom/" + displayNames[0];

		this.mockMvc.perform(post(url).content("{}")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk());

		// assert things existing
		String result = this.mockMvc.perform(
				get("/things/search?tagType=Custom&displayName=" + displayNames[0])
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<Map<String, Object>> list = mapper.readValue(result, List.class);
		assertEquals(2, list.size());

		for (Map<String, Object> map : list) {
			Long globalThingID = Long.valueOf((int) map.get("globalThingID"));
			assertTrue(globalThingIDListForTets.contains(globalThingID));
		}

		// bind tag to non existing thing
		globalThingIDs.append(",9999");

		url = "/things/" + globalThingIDs.toString() + "/tags/custom/" + displayNames[0];

		this.mockMvc.perform(post(url).content("{}")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk());

		// assert the non existing tag not bound to thing
		result = this.mockMvc.perform(
				get("/things/search?tagType=Custom&displayName=" + displayNames[0])
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		list = mapper.readValue(result, List.class);
		assertEquals(2, list.size());

		for (Map<String, Object> map : list) {
			Long globalThingID = Long.valueOf((int) map.get("globalThingID"));
			assertTrue(globalThingIDListForTets.contains(globalThingID));
		}

	}

	@Test
	public void testMultiThingsUnBindOneTag() throws Exception {

		this.testMultiThingsBindOneTag();

		// unbind thing from tag
		String url = "/things/" + globalThingIDListForTets.get(0) + "/tags/custom/" + displayNames[0];

		this.mockMvc.perform(delete(url).content("{}")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.header(Constants.ACCESS_TOKEN, tokenForTest)
		)

				.andExpect(status().isOk());

		// assert thing existing
		String result = this.mockMvc.perform(
				get("/things/search?tagType=Custom&displayName=" + displayNames[0])
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<Map<String, Object>> list = mapper.readValue(result, List.class);
		assertEquals(1, list.size());

		Map<String, Object> map = list.get(0);
		Long globalThingID = Long.valueOf((int) map.get("globalThingID"));
		assertEquals(globalThingIDListForTets.get(1), globalThingID);


		// unbind non existing tag from thing
		StringBuffer globalThingIDs = new StringBuffer();
		globalThingIDs.append(globalThingIDListForTets.get(0)).append(",").append("9999");
		url = "/things/" + globalThingIDs.toString() + "/tags/custom/" + displayNames[0];

		this.mockMvc.perform(delete(url).content("{}")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.header(Constants.ACCESS_TOKEN, tokenForTest)
		)

				.andExpect(status().isOk());

		// assert thing existing
		result = this.mockMvc.perform(
				get("/things/search?tagType=Custom&displayName=" + displayNames[0])
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		list = mapper.readValue(result, List.class);
		assertEquals(1, list.size());

		map = list.get(0);
		globalThingID = Long.valueOf((int) map.get("globalThingID"));
		assertEquals(globalThingIDListForTets.get(1), globalThingID);

	}

	@Test
	public void testGetThingsByTagExpress() throws Exception {

		// create thing
		String[] vendorThingIDs = new String[]{"vendorThingIDForTest1", "vendorThingIDForTest2", "vendorThingIDForTest3"};
		long[] globalThingIDs = new long[3];

		for (int i = 0; i < vendorThingIDs.length; i++) {
			GlobalThingInfo thingInfo = new GlobalThingInfo();
			thingInfo.setVendorThingID(vendorThingIDs[i]);
			thingInfo.setKiiAppID(KII_APP_ID);
			globalThingIDs[i] = globalThingDao.saveOrUpdate(thingInfo);
		}

		// create tag
		String[] displayNames = new String[]{"displayNameForCustom", "displayNameForLocation"};

		TagIndex tagIndex = new TagIndex(TagType.Custom, displayNames[0], null);
		long tagID1 = tagIndexDao.saveOrUpdate(tagIndex);

		tagIndex = new TagIndex(TagType.Location, displayNames[1], null);
		long tagID2 = tagIndexDao.saveOrUpdate(tagIndex);

		// create relation
		TagThingRelation relation = new TagThingRelation();
		relation.setTagID(tagID1);
		relation.setThingID(globalThingIDs[0]);
		tagThingRelationDao.insert(relation);

		relation = new TagThingRelation();
		relation.setTagID(tagID1);
		relation.setThingID(globalThingIDs[1]);
		tagThingRelationDao.insert(relation);

		relation = new TagThingRelation();
		relation.setTagID(tagID2);
		relation.setThingID(globalThingIDs[2]);
		tagThingRelationDao.insert(relation);


		// search custom tag
		String result = this.mockMvc.perform(
				get("/things/search?" + "tagType=" + TagType.Custom + "&displayName=" + displayNames[0])
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<Map<String, Object>> list = mapper.readValue(result, List.class);

		assertEquals(2, list.size());

		for (Map<String, Object> map : list) {
			System.out.println("response: " + map);
			assertTrue((int) map.get("globalThingID") == globalThingIDs[0] || (int) map.get("globalThingID") == globalThingIDs[1]);
			assertTrue(map.get("vendorThingID").equals(vendorThingIDs[0]) || map.get("vendorThingID").equals(vendorThingIDs[1]));
		}

		// search location tag
		result = this.mockMvc.perform(
				get("/things/search?" + "tagType=" + TagType.Location + "&displayName=" + displayNames[1])
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		list = mapper.readValue(result, List.class);

		assertEquals(1, list.size());

		for (Map<String, Object> map : list) {
			System.out.println("response: " + map);
			assertTrue((int) map.get("globalThingID") == globalThingIDs[2]);
			assertTrue(map.get("vendorThingID").equals(vendorThingIDs[2]));
		}

		// search all things
		result = this.mockMvc.perform(
				get("/things/search")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		list = mapper.readValue(result, List.class);

		assertEquals(3, list.size());

		for (Map<String, Object> map : list) {
			System.out.println("response: " + map);
			assertTrue((int) map.get("globalThingID") == globalThingIDs[0] || (int) map.get("globalThingID") == globalThingIDs[1] || (int) map.get("globalThingID") == globalThingIDs[2]);
			assertTrue(map.get("vendorThingID").equals(vendorThingIDs[0]) || map.get("vendorThingID").equals(vendorThingIDs[1]) || map.get("vendorThingID").equals(vendorThingIDs[2]));
		}

	}

	@Test
	public void testGetThingsByType() throws Exception {

		Long[] thingGroup1 = this.creatThingsForTest(3, "vendorThingIDForTest", KII_APP_ID, "LED");
		Long[] thingGroup2 = this.creatThingsForTest(1, "vendorThingIDForTest", KII_APP_ID, "camera");
		Long[] thingGroup3 = this.creatThingsForTest(1, "vendorThingIDForTest", KII_APP_ID, null);

		// query
		String result = this.mockMvc.perform(
				get("/things/types/LED")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<Map<String, Object>> list = mapper.readValue(result, List.class);

		System.out.println("Response: " + list);

		// assert
		assertEquals(3, list.size());

		List<Long> globalThingIDList = Arrays.asList(thingGroup1);
		for (Map<String, Object> map : list) {
			Long globalThingID = ((Integer) map.get("globalThingID")).longValue();
			globalThingIDList.contains(globalThingID);
		}

		// no result
		result = this.mockMvc.perform(
				get("/things/types/some_non_existing_type")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		list = mapper.readValue(result, List.class);

		System.out.println("Response: " + list);

		// assert
		assertEquals(0, list.size());

	}

	@Test
	public void testGetAllType() throws Exception {

		// no result
		String result = this.mockMvc.perform(
				get("/things/types")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<Map<String, Object>> list = mapper.readValue(result, List.class);

		System.out.println("Response: " + list);

		// assert
		assertEquals(0, list.size());

		Long[] thingGroup1 = this.creatThingsForTest(3, "vendorThingIDForTest", KII_APP_ID, "LED");
		Long[] thingGroup2 = this.creatThingsForTest(1, "vendorThingIDForTest", KII_APP_ID, "camera");
		Long[] thingGroup3 = this.creatThingsForTest(1, "vendorThingIDForTest", KII_APP_ID, null);

		// query
		result = this.mockMvc.perform(
				get("/things/types")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		list = mapper.readValue(result, List.class);

		System.out.println("Response: " + list);

		// assert
		assertEquals(3, list.size());

		for (Map<String, Object> map : list) {
			String type = (String) map.get("type");
			if ("LED".equals(type)) {
				assertEquals(3, map.get("count"));
			} else if ("camera".equals(type)) {
				assertEquals(1, map.get("count"));
			} else if (null == type) {
				assertEquals(1, map.get("count"));
			}
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


	@Test
	public void testGetThingTypeByTagNames() throws Exception {


		// create thing
		String[] vendorThingIDs = new String[]{"vendorThingIDForTest1", "vendorThingIDForTest2", "vendorThingIDForTest3"};
		List<String> types = new ArrayList<>();
		types.add("type1");
		types.add("type2");
		types.add("type3");

		long[] globalThingIDs = new long[3];

		for (int i = 0; i < vendorThingIDs.length; i++) {
			GlobalThingInfo thingInfo = new GlobalThingInfo();
			thingInfo.setVendorThingID(vendorThingIDs[i]);
			thingInfo.setKiiAppID(KII_APP_ID);
			thingInfo.setType(types.get(i));
			globalThingIDs[i] = globalThingDao.saveOrUpdate(thingInfo);
		}

		// create tag
		String[] displayNames = new String[]{"displayNameForCustom", "displayNameForLocation"};

		TagIndex tagIndex = new TagIndex(TagType.Custom, displayNames[0], null);
		long tagID1 = tagIndexDao.saveOrUpdate(tagIndex);

		tagIndex = new TagIndex(TagType.Location, displayNames[1], null);
		long tagID2 = tagIndexDao.saveOrUpdate(tagIndex);

		// create relation
		TagThingRelation relation = new TagThingRelation();
		relation.setTagID(tagID1);
		relation.setThingID(globalThingIDs[0]);
		tagThingRelationDao.insert(relation);

		relation = new TagThingRelation();
		relation.setTagID(tagID1);
		relation.setThingID(globalThingIDs[1]);
		tagThingRelationDao.insert(relation);

		relation = new TagThingRelation();
		relation.setTagID(tagID2);
		relation.setThingID(globalThingIDs[2]);
		tagThingRelationDao.insert(relation);

		String fullTagName = TagType.Custom.getTagName(displayNames[0]) + "," + TagType.Location.getTagName(displayNames[1]);

		// query
		String result = this.mockMvc.perform(
				get("/things/types/fulltagname/" + fullTagName)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<String> list = mapper.readValue(result, List.class);

		System.out.println("Response: " + list);

		// assert
		assertEquals(3, list.size());

		assertTrue(list.containsAll(types));

	}

}
