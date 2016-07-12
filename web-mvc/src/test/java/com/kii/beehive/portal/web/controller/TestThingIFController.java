package com.kii.beehive.portal.web.controller;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.util.Strings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.business.manager.AppInfoManager;
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.common.utils.CollectUtils;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.portal.web.WebTestTemplate;
import com.kii.beehive.portal.web.constant.Constants;
import com.kii.extension.sdk.entity.thingif.CommandStateType;
import com.kii.extension.sdk.entity.thingif.OnBoardingParam;
import com.kii.extension.sdk.entity.thingif.OnBoardingResult;
import com.kii.extension.sdk.entity.thingif.ThingCommand;


public class TestThingIFController extends WebTestTemplate {

	@Autowired
	private AppInfoManager appInfoManager;

	@Autowired
	private ThingIFInAppService thingIFInAppService;

	@Autowired
	private GlobalThingSpringDao globalThingDao;

	@Autowired
	private TagIndexDao tagIndexDao;

	@Autowired
	private ObjectMapper mapper;

	private String[] vendorThingIDsForTest = new String[]{"someVendorThingID1", "someVendorThingID2", "someVendorThingID3", "someVendorThingID4", "someVendorThingID5"};

	private String[] thingTypesForTest = new String[]{"type1", "type1", "type2", "type2", "type2"};

	private String[] kiiAppIDForTest = new String[]{"0af7a7e7", "0af7a7e7", "c1744915", "c1744915", "c1744915"};

	private List<Long> globalThingIDListForTests = new ArrayList<>();

	private List<Map<String, Object>> onboardingInfoMap = new ArrayList<>();

	private String[] displayNames = new String[]{"A", "B"};

	private List<Long> tagIDListForTests = new ArrayList<>();

	private String tokenForTest = BEARER_SUPER_TOKEN;

	/**
	 * this variable is used to receive the response of some test cases, so that some other test cases can use these
	 * response if required
	 * the key is used to indicate the response, could be the method name of test case
	 * the value is the response of test case
	 */
	private Map<String, Object> tempResponseMap = new HashMap<>();

	/**
	 * 1. add tags in displayNames
	 * 2. add things in vendorThingIDsForTest
	 */
	@Before
	public void before() {

		System.out.println("=================================================================");
		System.out.println("== Start to run before()");
		System.out.println("=================================================================");

		super.before();

		this.clear();

		// add tags in displayNames
		for (String displayName : displayNames) {
			try {
				this.createTag(displayName);
				System.out.println("tag with display name: " + displayName + " is created");
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}
		}

		// add things in vendorThingIDsForTest
		for (int i = 0; i < vendorThingIDsForTest.length; i++) {
			try {
				Long globalThingID = this.createThing(vendorThingIDsForTest[i], thingTypesForTest[i], kiiAppIDForTest[i]);
				this.globalThingIDListForTests.add(globalThingID);
				System.out.println("thing with global thing id : " + globalThingID + " is created");

				Map<String, Object> onboardingInfo = this.getOnboardingInfo(vendorThingIDsForTest[i]);
				this.onboarding(vendorThingIDsForTest[i], onboardingInfo);
				System.out.println("thing with global thing id : " + globalThingID + " is onboarded");

			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}
		}

		System.out.println("=================================================================");
		System.out.println("== End to run before()");
		System.out.println("=================================================================");

	}

	@After
	public void after() {

		System.out.println("=================================================================");
		System.out.println("== Start to run after()");
		System.out.println("=================================================================");

		this.clear();

		System.out.println("=================================================================");
		System.out.println("== End to run after()");
		System.out.println("=================================================================");
	}

	/**
	 * 1. remove things in vendorThingIDsForTest
	 * 2. remove tags in displayNames
	 */
	private void clear() {
		// remove things in vendorThingIDsForTest
		List<GlobalThingInfo> globalThingInfoList = globalThingDao.getThingsByVendorIDArray(
				Arrays.asList(vendorThingIDsForTest)).orElse(Collections.emptyList());
		for (GlobalThingInfo globalThingInfo : globalThingInfoList) {
			try {
				long globalThingID = globalThingInfo.getId();
				this.removeThing(globalThingID);
				System.out.println("thing with global thing id: " + globalThingID + " is deleted");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// remove tags in displayNames
		for (String displayName : displayNames) {
			try {
				this.removeTag(displayName);
				System.out.println("tag with display name: " + displayName + " is deleted");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Long createThing(String vendorThingID, String thingType, String kiiAppID) throws Exception {

		Map<String, Object> request = new HashMap<>();
		request.put("vendorThingID", vendorThingID);
		request.put("kiiAppID", kiiAppID);
		request.put("type", thingType);
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

		return globalThingID;
	}

	/**
	 * this method only tries to remove the thing, can't ensure the existence of the target thing
	 *
	 * @param globalThingID
	 * @throws Exception
	 */
	private void removeThing(Long globalThingID) throws Exception {

		// delete thing
		String result = this.mockMvc.perform(
				delete("/things/" + globalThingID)
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andReturn().getResponse().getContentAsString();

		GlobalThingInfo thingInfo = globalThingDao.findByID(globalThingID);
		assertNull(thingInfo);

	}

	private Long createTag(String displayName) throws Exception {

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

		Long tagID = Long.valueOf((int) map.get("id"));

		// assert http return
		String tagName = (String) map.get("tagName");
		assertEquals(TagType.Custom + "-" + displayName, tagName);

		return tagID;

	}

	/**
	 * this method only tries to remove the tag, can't ensure the existence of the target tag
	 *
	 * @param displayName
	 * @throws Exception
	 */
	private void removeTag(String displayName) throws Exception {

		String result = this.mockMvc.perform(
				delete("/tags/custom/" + displayName)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andReturn().getResponse().getContentAsString();

		List<TagIndex> list = tagIndexDao.findTagByTagTypeAndName(TagType.Custom.name(), displayName);
		assertTrue(list.size() == 0);

	}

	private void bindThingsToTag(List<Long> globalThingIDList, String tagDisplayName) throws Exception {

		StringBuffer globalThingIDs = new StringBuffer();
		for (Long globalThingID : globalThingIDList) {
			globalThingIDs.append(",").append(globalThingID);
		}
		globalThingIDs.deleteCharAt(0);

		// bind tag to things

		String url = "/things/" + globalThingIDs.toString() + "/tags/custom/" + tagDisplayName;

		this.mockMvc.perform(post(url).content("{}")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk());

		// assert things existing
		String result = this.mockMvc.perform(
				get("/things/search?tagType=Custom&displayName=" + tagDisplayName)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<Map<String, Object>> list = mapper.readValue(result, List.class);
		assertEquals(globalThingIDList.size(), list.size());

		for (Map<String, Object> map : list) {
			Long globalThingID = Long.valueOf((int) map.get("globalThingID"));
			assertTrue(globalThingIDList.contains(globalThingID));
		}
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
		assertNotNull(map.get("kiiAppKey"));
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

		String kiiAppID = (String) onboardingInfo.get("kiiAppID");

		OnBoardingResult onBoardingResult = thingIFInAppService.onBoarding(param, kiiAppID);
		String kiiThingID = onBoardingResult.getThingID();
		System.out.println("Kii Thing ID: " + kiiThingID);

		assertTrue(kiiThingID.length() > 0);

		onboardingInfo.put("kiiThingID", kiiThingID);

		// set full kii thing id to local DB for unit testing
		// (because server code in Kii Cloud will only update the full kii thing id into internal dev DB)
		globalThingDao.updateKiiThingID(vendorThingID, kiiAppID + "-" + kiiThingID);

		return kiiThingID;
	}

	private String[] combineArray(String[] oriArr, String... elements) {

		String[] newArr = new String[oriArr.length + elements.length];
		System.arraycopy(oriArr, 0, newArr, 0, oriArr.length);
		System.arraycopy(elements, 0, newArr, oriArr.length, elements.length);

		return newArr;
	}

//    private boolean checkCommandExist(String vendorThingID, Map<String, Object> onboardingInfo) throws IOException {
//
//        String kiiAppID = (String)onboardingInfo.get("kiiAppID");
//        String kiiThingID = (String)onboardingInfo.get("kiiThingID");
//        String kiiSiteUrl = (String)onboardingInfo.get("kiiSiteUrl");
//
//        String url = kiiSiteUrl + "/thing-if/apps/" + kiiAppID + "/targets/thing:" + kiiThingID + "/commands";
//
//        HttpGet httpGet = new HttpGet(url);
//
//        httpGet.setHeader("X-Kii-AppID", kiiAppID);
//        httpGet.setHeader("X-Kii-AppKey", (String)onboardingInfo.get("kiiAppKey"));
//        httpGet.setHeader("Authorization", "Bearer " + onboardingInfo.get("ownerToken"));
//
//        int status = HttpClientBuilder.create().build().execute(httpGet).getEntity()..getStatusLine().getStatusCode();
//        System.out.println("status: " + status);
//
//        return status < 400;
//    }

	private HashMap<String, Object> createMap(String key, Object value) {
		HashMap<String, Object> map = new HashMap<>();
		map.put(key, value);
		return map;
	}

	private HashMap<String, Object> createCommand() {

		HashMap<String, Object> command = new HashMap<>();
		command.put("schema", "some schema");
		command.put("schemaVersion", 1);

		HashMap<String, Object> action1 = this.createMap("turnPower", this.createMap("power", "on"));
		HashMap<String, Object> action2 = this.createMap("changeColor", this.createMap("color", "#123456"));

		command.put("actions", new HashMap[]{action1, action2});

		return command;
	}

	@Test
	public void testBeforeAndAfter() throws Exception {
		System.out.println("=================================================================");
		System.out.println("=================================================================");
		System.out.println("=================================================================");

		this.bindThingsToTag(globalThingIDListForTests.subList(0, 3), displayNames[0]);
		this.bindThingsToTag(globalThingIDListForTests.subList(3, 5), displayNames[1]);

		System.out.println("=================================================================");
		System.out.println("=================================================================");
	}

	/**
	 * below scenario will be tested:
	 * <p>
	 * 1. try to send one command to below things, and specify the target thing type "type1" in the command
	 * - vendor thing id "someVendorThingID1", thing type "type1"
	 * - vendor thing id "someVendorThingID2", thing type "type1"
	 * - vendor thing id "someVendorThingID3", thing type "type2"
	 * - vendor thing id "someVendorThingID4", thing type "type2"
	 * - vendor thing id "someVendorThingID5", thing type "type2"
	 * <p>
	 * 2. actually the command will be send to all things, as thing type in the command will only work while specifying tagList rather than thingList
	 *
	 * @throws Exception
	 */
	@Test
	public void testSendCommandToThingList() throws Exception {

		// 1. try to send one command to below things in one cluster, and specify the target thing type "type1" in the command
		//  - vendor thing id "someVendorThingID1", thing type "type1"
		//  - vendor thing id "someVendorThingID2", thing type "type1"
		//  - vendor thing id "someVendorThingID3", thing type "type2"
		//  - vendor thing id "someVendorThingID4", thing type "type2"
		//  - vendor thing id "someVendorThingID5", thing type "type2"
		List<HashMap<String, Object>> requestList = new ArrayList<>();
		HashMap<String, Object> command = new HashMap<>();

		command.put("thingList", globalThingIDListForTests);
		command.put("type", "type1");
		command.put("command", this.createCommand());

		requestList.add(command);

		String ctx = mapper.writeValueAsString(requestList);

		String result = this.mockMvc.perform(
				post("/thing-if/command").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<List<Map<String, Object>>> list = mapper.readValue(result, List.class);

		System.out.println("========================================================");
		System.out.println("Response: " + result);
		System.out.println("========================================================");

		// 2. actually the command will be send to all things, as all of them are in thing type "type1"
		assertTrue(list.size() == 1);

		// two things in type "type1"
		List<Map<String, Object>> subList = list.get(0);
		assertTrue(subList.size() == 5);

		for (Map<String, Object> map : subList) {
			Long globalThingID = Long.valueOf((Integer) map.get("globalThingID"));
			assertTrue(globalThingIDListForTests.contains(globalThingID));

			assertTrue(!Strings.isBlank((String) map.get("commandID")));
		}

	}

	/**
	 * below scenario will be tested:
	 * <p>
	 * 1. try to send one command to below tags with andExpress true, and specify the target thing type "type2"
	 * - tag "A"
	 * - vendor thing id "someVendorThingID1", thing type "type1"
	 * - vendor thing id "someVendorThingID2", thing type "type1"
	 * - vendor thing id "someVendorThingID3", thing type "type2"
	 * - tag "B"
	 * - vendor thing id "someVendorThingID3", thing type "type2"
	 * - vendor thing id "someVendorThingID4", thing type "type2"
	 * - vendor thing id "someVendorThingID5", thing type "type2"
	 * <p>
	 * 2. actually the command will only be send to "someVendorThingID3" which is in both tag "A" and "B", and in thing type "type2"
	 *
	 * @throws Exception
	 */
	@Test
	public void testSendCommandToTagListWithAndExpress() throws Exception {

		// 1. try to send one command to below tags with andExpress true, and specify the target thing type "type2"
		//  - tag "A"
		//      - vendor thing id "someVendorThingID1", thing type "type1"
		//      - vendor thing id "someVendorThingID2", thing type "type1"
		//      - vendor thing id "someVendorThingID3", thing type "type2"
		//  - tag "B"
		//      - vendor thing id "someVendorThingID3", thing type "type2"
		//      - vendor thing id "someVendorThingID4", thing type "type2"
		//      - vendor thing id "someVendorThingID5", thing type "type2"

		// bind things to tags
		this.bindThingsToTag(globalThingIDListForTests.subList(0, 3), displayNames[0]);
		this.bindThingsToTag(globalThingIDListForTests.subList(2, 5), displayNames[1]);

		List<HashMap<String, Object>> requestList = new ArrayList<>();
		HashMap<String, Object> command = new HashMap<>();

		command.put("tagList", new String[]{TagType.Custom + "-" + displayNames[0], TagType.Custom + "-" + displayNames[1]});
		command.put("andExpress", true);
		command.put("type", "type2");
		command.put("command", this.createCommand());

		requestList.add(command);

		String ctx = mapper.writeValueAsString(requestList);

		String result = this.mockMvc.perform(
				post("/thing-if/command").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<List<Map<String, Object>>> list = mapper.readValue(result, List.class);

		System.out.println("========================================================");
		System.out.println("Response: " + result);
		System.out.println("========================================================");

		// 2. actually the command will only be send to "someVendorThingID3" which is in both tag "A" and "B", and in thing type "type2"
		assertTrue(list.size() == 1);

		// one thing in both tag "A" and "B", and in thing type "type2"
		List<Map<String, Object>> subList = list.get(0);
		assertTrue(subList.size() == 1);

		Map<String, Object> map = subList.get(0);
		Long globalThingID = Long.valueOf((Integer) map.get("globalThingID"));
		assertEquals(globalThingIDListForTests.get(2), globalThingID);

		assertTrue(!Strings.isBlank((String) map.get("commandID")));

	}

	/**
	 * below scenario will be tested:
	 * <p>
	 * 1. try to send one command to below tags with andExpress false, and specify the target thing type "type2"
	 * - tag "A"
	 * - vendor thing id "someVendorThingID1", thing type "type1"
	 * - vendor thing id "someVendorThingID2", thing type "type1"
	 * - vendor thing id "someVendorThingID3", thing type "type2"
	 * - tag "B"
	 * - vendor thing id "someVendorThingID3", thing type "type2"
	 * - vendor thing id "someVendorThingID4", thing type "type2"
	 * - vendor thing id "someVendorThingID5", thing type "type2"
	 * <p>
	 * 2. actually the command will only be send to "someVendorThingID3", "someVendorThingID4" and "someVendorThingID5" which meet both of below conditions:
	 * - in tag "A" or "B"
	 * - in thing type "type2"
	 *
	 * @throws Exception
	 */
	@Test
	public void testSendCommandToTagListWithOrExpress() throws Exception {

		// 1. try to send one command to below tags with andExpress false, and specify the target thing type "type2"
		//  - tag "A"
		//      - vendor thing id "someVendorThingID1", thing type "type1"
		//      - vendor thing id "someVendorThingID2", thing type "type1"
		//      - vendor thing id "someVendorThingID3", thing type "type2"
		//  - tag "B"
		//      - vendor thing id "someVendorThingID3", thing type "type2"
		//      - vendor thing id "someVendorThingID4", thing type "type2"
		//      - vendor thing id "someVendorThingID5", thing type "type2"

		// bind things to tags
		this.bindThingsToTag(globalThingIDListForTests.subList(0, 3), displayNames[0]);
		this.bindThingsToTag(globalThingIDListForTests.subList(2, 5), displayNames[1]);

		List<HashMap<String, Object>> requestList = new ArrayList<>();
		HashMap<String, Object> command = new HashMap<>();

		command.put("tagList", new String[]{TagType.Custom + "-" + displayNames[0], TagType.Custom + "-" + displayNames[1]});
		command.put("andExpress", false);
		command.put("type", "type2");
		command.put("command", this.createCommand());

		requestList.add(command);

		String ctx = mapper.writeValueAsString(requestList);

		String result = this.mockMvc.perform(
				post("/thing-if/command").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<List<Map<String, Object>>> list = mapper.readValue(result, List.class);

		System.out.println("========================================================");
		System.out.println("Response: " + result);
		System.out.println("========================================================");

		// 2. actually the command will only be send to "someVendorThingID3", "someVendorThingID4" and "someVendorThingID5" which meet both of below conditions:
		//  - in tag "A" or "B"
		//  - in thing type "type2"
		assertTrue(list.size() == 1);

		// three things meet the condition
		List<Map<String, Object>> subList = list.get(0);
		assertTrue(subList.size() == 3);

		List<Long> targetGlobalThingIDList = globalThingIDListForTests.subList(2, 5);
		for (Map<String, Object> map : subList) {
			Long globalThingID = Long.valueOf((Integer) map.get("globalThingID"));
			assertTrue(targetGlobalThingIDList.contains(globalThingID));

			assertTrue(!Strings.isBlank((String) map.get("commandID")));
		}

	}

	/**
	 * below scenario will be tested:
	 * <p>
	 * 1. construct below relations between tags and things
	 * - tag "A"
	 * - vendor thing id "someVendorThingID1", thing type "type1"
	 * - vendor thing id "someVendorThingID2", thing type "type1"
	 * - vendor thing id "someVendorThingID3", thing type "type2"
	 * - tag "B"
	 * - vendor thing id "someVendorThingID3", thing type "type2"
	 * - vendor thing id "someVendorThingID4", thing type "type2"
	 * - vendor thing id "someVendorThingID5", thing type "type2"
	 * <p>
	 * 2. send below two commands:
	 * command a. target: existing in both tag "A" and "B", thing type: "type2"
	 * command b. target: "someVendorThingID1" and "someVendorThingID5"
	 * <p>
	 * 3. actually the commands will be send to below things:
	 * command a. "someVendorThingID3"
	 * command b. "someVendorThingID1" and "someVendorThingID5"
	 *
	 * @throws Exception
	 */
	@Test
	public void testSendCommandToThingListAndTagList() throws Exception {

		// 1. construct below relations between tags and things
		//  - tag "A"
		//      - vendor thing id "someVendorThingID1", thing type "type1"
		//      - vendor thing id "someVendorThingID2", thing type "type1"
		//      - vendor thing id "someVendorThingID3", thing type "type2"
		//  - tag "B"
		//      - vendor thing id "someVendorThingID3", thing type "type2"
		//      - vendor thing id "someVendorThingID4", thing type "type2"
		//      - vendor thing id "someVendorThingID5", thing type "type2"

		// bind things to tags
		this.bindThingsToTag(globalThingIDListForTests.subList(0, 3), displayNames[0]);
		this.bindThingsToTag(globalThingIDListForTests.subList(2, 5), displayNames[1]);

		// 2. send below two commands:
		//      command a. target: existing in both tag "A" and "B", thing type: "type2"
		//      command b. target: "someVendorThingID1" and "someVendorThingID5, thing type: "type1"
		List<HashMap<String, Object>> requestList = new ArrayList<>();

		HashMap<String, Object> commandA = new HashMap<>();

		commandA.put("tagList", new String[]{TagType.Custom + "-" + displayNames[0], TagType.Custom + "-" + displayNames[1]});
		commandA.put("andExpress", true);
		commandA.put("type", "type2");
		commandA.put("command", this.createCommand());

		requestList.add(commandA);

		HashMap<String, Object> commandB = new HashMap<>();

		List<Long> globalThingIDList = new ArrayList<>();
		globalThingIDList.add(globalThingIDListForTests.get(0));
		globalThingIDList.add(globalThingIDListForTests.get(4));
		commandB.put("thingList", globalThingIDList);
		commandB.put("type", "type1");
		commandB.put("command", this.createCommand());

		requestList.add(commandB);

		String ctx = mapper.writeValueAsString(requestList);

		String result = this.mockMvc.perform(
				post("/thing-if/command").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<List<Map<String, Object>>> list = mapper.readValue(result, List.class);

		this.tempResponseMap.put("testSendCommandToThingListAndTagList", list);

		System.out.println("========================================================");
		System.out.println("Response: " + result);
		System.out.println("========================================================");

		// 3. actually the commands will only be send to below things:
		//      command a. "someVendorThingID3"
		//      command b. "someVendorThingID1" and "someVendorThingID5"
		assertTrue(list.size() == 2);

		// one thing meet the condition for command a
		List<Map<String, Object>> commandAList = list.get(0);
		assertTrue(commandAList.size() == 1);

		Map<String, Object> commandAMap = commandAList.get(0);
		Long globalThingID = Long.valueOf((Integer) commandAMap.get("globalThingID"));
		assertEquals(globalThingIDListForTests.get(2), globalThingID);
		assertTrue(!Strings.isBlank((String) commandAMap.get("commandID")));

		// two things meet the condition for command b
		List<Map<String, Object>> commandBList = list.get(1);
		assertTrue(commandBList.size() == 2);

		for (Map<String, Object> commandBMap : commandBList) {
			globalThingID = Long.valueOf((Integer) commandBMap.get("globalThingID"));
			assertTrue(globalThingIDList.contains(globalThingID));

			assertTrue(!Strings.isBlank((String) commandBMap.get("commandID")));
		}

	}

	/**
	 * below scenario will be tested:
	 * <p>
	 * 1. try to create below things
	 * - vendor thing id "someVendorThingIDX", thing type "type1", do onboaridng
	 * - vendor thing id "someVendorThingIDY", thing type "type1", don't do onboarding
	 * <p>
	 * 2. try to send command to above things
	 * <p>
	 * 3. actually the command will be send to "someVendorThingIDX" only, as "someVendorThingIDY" is not boarded yet so got skipped
	 *
	 * @throws Exception
	 */
	@Test
	public void testSendCommandToThingWithoutOnboarding() throws Exception {

		// 1. try to create below things
		//  - vendor thing id "someVendorThingIDX", thing type "type1", do onboaridng
		//  - vendor thing id "someVendorThingIDY", thing type "type1", don't do onboarding

		// for the clear after test case completed
		this.vendorThingIDsForTest = this.combineArray(this.vendorThingIDsForTest, "someVendorThingIDX", "someVendorThingIDY");
		this.thingTypesForTest = this.combineArray(this.thingTypesForTest, "type1", "type2");
		this.kiiAppIDForTest = this.combineArray(this.kiiAppIDForTest, kiiAppIDForTest[0], kiiAppIDForTest[1]);

		// create "someVendorThingIDX"
		Long globalThingIDX = this.createThing("someVendorThingIDX", "type1", kiiAppIDForTest[0]);
		this.globalThingIDListForTests.add(globalThingIDX);

		Map<String, Object> onboardingInfo = this.getOnboardingInfo("someVendorThingIDX");
		this.onboarding("someVendorThingIDX", onboardingInfo);
		System.out.println("thing with global thing id : someVendorThingIDX is onboarded");

		// create "someVendorThingIDY"
		Long globalThingIDY = this.createThing("someVendorThingIDY", "type1", kiiAppIDForTest[0]);
		this.globalThingIDListForTests.add(globalThingIDY);

		// 2. try to send command to above things
		List<HashMap<String, Object>> requestList = new ArrayList<>();
		HashMap<String, Object> command = new HashMap<>();

		command.put("thingList", new Long[]{globalThingIDX, globalThingIDY});
		command.put("command", this.createCommand());

		requestList.add(command);

		String ctx = mapper.writeValueAsString(requestList);

		String result = this.mockMvc.perform(
				post("/thing-if/command").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<List<Map<String, Object>>> list = mapper.readValue(result, List.class);

		System.out.println("========================================================");
		System.out.println("Response: " + result);
		System.out.println("========================================================");

		// 3. actually the command will be send to "someVendorThingIDX" only, as "someVendorThingIDY" is not boarded yet so got skipped
		assertTrue(list.size() == 1);

		// two things in type "type1"
		List<Map<String, Object>> subList = list.get(0);
		assertTrue(subList.size() == 1);

		Map<String, Object> map = subList.get(0);
		Long globalThingID = Long.valueOf((Integer) map.get("globalThingID"));
		assertEquals(globalThingIDX, globalThingID);


	}

	/**
	 * this test case is based on the result of test case "testSendCommandToThingListAndTagList",
	 * will query the command details sent from test case "testSendCommandToThingListAndTagList"
	 *
	 * 1. the number of command details is expected to be the same with the commands sent in test case
	 * "testSendCommandToThingListAndTagList"
	 * 2. actions and command result of each command are expected to be returned
	 *
	 */
	@Test
	public void testGetCommand() throws Exception {
		// get commands sent in test case "testSendCommandToThingListAndTagList"
		this.testSendCommandToThingListAndTagList();
		List<List<Map<String, Object>>> commands = (List<List<Map<String, Object>>>)this.tempResponseMap.get("testSendCommandToThingListAndTagList");

		List<Map<String, Object>> request = new ArrayList<>();
		for(List<Map<String, Object>> command : commands) {
			request.addAll(command);
		}

		// query command details
		String ctx = mapper.writeValueAsString(request);

		String result = this.mockMvc.perform(
				post("/thing-if/command/search").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<Map<String, Object>> list = mapper.readValue(result, List.class);

		System.out.println("========================================================");
		System.out.println("Response: " + result);
		System.out.println("========================================================");

		// 1. the number of command details is expected to be the same with the commands sent in test case
		// "testSendCommandToThingListAndTagList"
		assertTrue(list.size() == request.size());

		// 2. actions and command result of each command are expected to be returned
		for(Map<String, Object> commandDetail : list) {

			int globalThingID = (int)commandDetail.get("globalThingID");

			String commandID = (String)request.stream().filter((n) -> (int)n.get("globalThingID") == globalThingID)
					.findFirst().get().get("commandID");

			assertEquals(commandID, (String)commandDetail.get("commandID"));
			assertTrue(CollectUtils.hasElement((List)commandDetail.get("actions")));
			assertEquals(CommandStateType.SENDING.name(), commandDetail.get("commandState"));
		}
	}

	/**
	 * below scenario will be tested:
	 *
	 * 1. send command1 to vendor thing id "someVendorThingID1"
	 * 2. sleep 10 seconds (mark the 5th second as timestampA)
	 * 3. send command2 to vendor thing id "someVendorThingID2"
	 * 4. send command3 to vendor thing id "someVendorThingID1"
	 * 5. sleep 10 seconds (mark the 5th second as timestampB)
	 * 6. send command4 to vendor thing id "someVendorThingID1"
	 * 7. when query the commands sent to vendor thing id "someVendorThingID1" before timestampB, command1 and
	 * command3 will be returned
	 * 8. when query the commands sent to vendor thing id "someVendorThingID1" before timestampB and after
	 * timestampA, command3 will be returned
	 * 9. when query the commands sent to vendor thing id "someVendorThingID1" after timestampA, command3 and command4
	 * will be returned
	 *
	 */
	@Test
	public void testGetCommandsOfSingleThing() throws Exception {

		// create command
		Map<String, Object> command = this.createCommand();
		String comandStr = mapper.writeValueAsString(command);
		ThingCommand thingCommand = mapper.readValue(comandStr, ThingCommand.class);

		// get full kii thing ids of "someVendorThingID1" and "someVendorThingID2"
		GlobalThingInfo thing1 = globalThingDao.getThingByVendorThingID("someVendorThingID1");
		GlobalThingInfo thing2 = globalThingDao.getThingByVendorThingID("someVendorThingID2");
		long globalThingID1 = thing1.getId();
		long globalThingID2 = thing2.getId();
		String fullKiiThingID1 = thing1.getFullKiiThingID();
		String fullKiiThingID2 = thing2.getFullKiiThingID();
		String userID1 = appInfoManager.getDefaultOwer(thing1.getKiiAppID()).getUserID();
		String userID2 = appInfoManager.getDefaultOwer(thing2.getKiiAppID()).getUserID();

		// 1. send command1 to vendor thing id "someVendorThingID1";
		thingCommand.setUserID(userID1);
		String command1 = thingIFInAppService.sendCommand(thingCommand, fullKiiThingID1);

		// 2. sleep 10 seconds (mark the 5th second as timestampA)
		Thread.sleep(5000);
		long timestampA = System.currentTimeMillis();
		Thread.sleep(5000);

		// 3. send command2 to vendor thing id "someVendorThingID2"
		thingCommand.setUserID(userID2);
		String command2 = thingIFInAppService.sendCommand(thingCommand, fullKiiThingID2);

		// 4. send command3 to vendor thing id "someVendorThingID1"
		thingCommand.setUserID(userID1);
		String command3 = thingIFInAppService.sendCommand(thingCommand, fullKiiThingID1);

		// 5. sleep 10 seconds (mark the 5th second as timestampB)
		Thread.sleep(5000);
		long timestampB = System.currentTimeMillis();
		Thread.sleep(5000);

		// 6. send command4 to vendor thing id "someVendorThingID1"
		thingCommand.setUserID(userID1);
		String command4 = thingIFInAppService.sendCommand(thingCommand, fullKiiThingID1);

		// 7. when query the commands sent to vendor thing id "someVendorThingID1" before timestampB, command1 and
		// command3 will be returned
		Map<String, Object> request = new HashMap<>();
		request.put("globalThingID", globalThingID1);
		request.put("end", timestampB);

		String ctx = mapper.writeValueAsString(request);

		String result = this.mockMvc.perform(
				post("/thing-if/command/list").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<Map<String, Object>> list = mapper.readValue(result, List.class);

		System.out.println("========================================================");
		System.out.println("Response: " + result);
		System.out.println("========================================================");

		assertTrue(list.size() == 2);
		for(Map<String, Object> commandDetail : list) {

			int globalThingID = (int)commandDetail.get("globalThingID");
			String commandID = (String)commandDetail.get("commandID");

			assertEquals(globalThingID1, globalThingID);
			assertTrue(command1.equals(commandID) || command3.equals(commandID));
			assertTrue(CollectUtils.hasElement((List)commandDetail.get("actions")));
			assertEquals(CommandStateType.SENDING.name(), commandDetail.get("commandState"));
		}


		// 8. when query the commands sent to vendor thing id "someVendorThingID1" before timestampB and after
		// timestampA, command3 will be returned
		request = new HashMap<>();
		request.put("globalThingID", globalThingID1);
		request.put("start", timestampA);
		request.put("end", timestampB);

		ctx = mapper.writeValueAsString(request);

		result = this.mockMvc.perform(
				post("/thing-if/command/list").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		list = mapper.readValue(result, List.class);

		System.out.println("========================================================");
		System.out.println("Response: " + result);
		System.out.println("========================================================");

		assertTrue(list.size() == 1);

		Map<String, Object> commandDetailMap = list.get(0);
		assertEquals(globalThingID1, (int)commandDetailMap.get("globalThingID"));
		assertEquals(command3, (String)commandDetailMap.get("commandID"));
		assertTrue(CollectUtils.hasElement((List)commandDetailMap.get("actions")));
		assertEquals(CommandStateType.SENDING.name(), commandDetailMap.get("commandState"));


		// 9. when query the commands sent to vendor thing id "someVendorThingID1" after timestampA, command3 and
		// command4 will be returned
		request = new HashMap<>();
		request.put("globalThingID", globalThingID1);
		request.put("start", timestampA);

		ctx = mapper.writeValueAsString(request);

		result = this.mockMvc.perform(
				post("/thing-if/command/list").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		list = mapper.readValue(result, List.class);

		System.out.println("========================================================");
		System.out.println("Response: " + result);
		System.out.println("========================================================");

		assertTrue(list.size() == 2);
		for(Map<String, Object> commandDetail : list) {

			int globalThingID = (int)commandDetail.get("globalThingID");
			String commandID = (String)commandDetail.get("commandID");

			assertEquals(globalThingID1, globalThingID);
			assertTrue(command3.equals(commandID) || command4.equals(commandID));
			assertTrue(CollectUtils.hasElement((List)commandDetail.get("actions")));
			assertEquals(CommandStateType.SENDING.name(), commandDetail.get("commandState"));
		}


	}

}
