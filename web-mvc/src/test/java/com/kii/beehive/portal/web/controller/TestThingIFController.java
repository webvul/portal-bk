package com.kii.beehive.portal.web.controller;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
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

	private String[] vendorThingIDsForTest = new String[]{"0807W-F00-03-001", "0807W-F00-03-002", "0807W-F00-03-003", "0807W-F00-03-004", "0807W-F00-03-005"};

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
	 * this method only tries to remove the thing, can't ensure the existence of the task thing
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
		assertTrue(thingInfo.getDeleted());

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
	 * this method only tries to remove the tag, can't ensure the existence of the task tag
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
	 * 1. try to send one command to below things, and specify the task thing type "type1" in the command
	 * - vendor thing id "0807W-F00-03-001", thing type "type1"
	 * - vendor thing id "0807W-F00-03-002", thing type "type1"
	 * - vendor thing id "0807W-F00-03-003", thing type "type2"
	 * - vendor thing id "0807W-F00-03-004", thing type "type2"
	 * - vendor thing id "0807W-F00-03-005", thing type "type2"
	 * <p>
	 * 2. actually the command will be send to all things, as thing type in the command will only work while specifying tagList rather than thingList
	 *
	 * @throws Exception
	 */
	@Test
	public void testSendCommandToThingList() throws Exception {

		// 1. try to send one command to below things in one cluster, and specify the task thing type "type1" in the command
		//  - vendor thing id "0807W-F00-03-001", thing type "type1"
		//  - vendor thing id "0807W-F00-03-002", thing type "type1"
		//  - vendor thing id "0807W-F00-03-003", thing type "type2"
		//  - vendor thing id "0807W-F00-03-004", thing type "type2"
		//  - vendor thing id "0807W-F00-03-005", thing type "type2"
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

		// 2. actually the command will be send to all things, as thing type in the command will only work while specifying tagList rather than thingList
		assertTrue(list.size() == 1);

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
	 * 1. try to send one command to below tags with andExpress true, and specify the task thing type "type2"
	 * - tag "A"
	 * - vendor thing id "0807W-F00-03-001", thing type "type1"
	 * - vendor thing id "0807W-F00-03-002", thing type "type1"
	 * - vendor thing id "0807W-F00-03-003", thing type "type2"
	 * - tag "B"
	 * - vendor thing id "0807W-F00-03-003", thing type "type2"
	 * - vendor thing id "0807W-F00-03-004", thing type "type2"
	 * - vendor thing id "0807W-F00-03-005", thing type "type2"
	 * <p>
	 * 2. actually the command will only be send to "0807W-F00-03-003" which is in both tag "A" and "B", and in thing type "type2"
	 *
	 * @throws Exception
	 */
	@Test
	public void testSendCommandToTagListWithAndExpress() throws Exception {

		// 1. try to send one command to below tags with andExpress true, and specify the task thing type "type2"
		//  - tag "A"
		//      - vendor thing id "0807W-F00-03-001", thing type "type1"
		//      - vendor thing id "0807W-F00-03-002", thing type "type1"
		//      - vendor thing id "0807W-F00-03-003", thing type "type2"
		//  - tag "B"
		//      - vendor thing id "0807W-F00-03-003", thing type "type2"
		//      - vendor thing id "0807W-F00-03-004", thing type "type2"
		//      - vendor thing id "0807W-F00-03-005", thing type "type2"

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

		// 2. actually the command will only be send to "0807W-F00-03-003" which is in both tag "A" and "B", and in thing type "type2"
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
	 * 1. try to send one command to below tags with andExpress false, and specify the task thing type "type2"
	 * - tag "A"
	 * - vendor thing id "0807W-F00-03-001", thing type "type1"
	 * - vendor thing id "0807W-F00-03-002", thing type "type1"
	 * - vendor thing id "0807W-F00-03-003", thing type "type2"
	 * - tag "B"
	 * - vendor thing id "0807W-F00-03-003", thing type "type2"
	 * - vendor thing id "0807W-F00-03-004", thing type "type2"
	 * - vendor thing id "0807W-F00-03-005", thing type "type2"
	 * <p>
	 * 2. actually the command will only be send to "0807W-F00-03-003", "0807W-F00-03-004" and "0807W-F00-03-005" which meet both of below conditions:
	 * - in tag "A" or "B"
	 * - in thing type "type2"
	 *
	 * @throws Exception
	 */
	@Test
	public void testSendCommandToTagListWithOrExpress() throws Exception {

		// 1. try to send one command to below tags with andExpress false, and specify the task thing type "type2"
		//  - tag "A"
		//      - vendor thing id "0807W-F00-03-001", thing type "type1"
		//      - vendor thing id "0807W-F00-03-002", thing type "type1"
		//      - vendor thing id "0807W-F00-03-003", thing type "type2"
		//  - tag "B"
		//      - vendor thing id "0807W-F00-03-003", thing type "type2"
		//      - vendor thing id "0807W-F00-03-004", thing type "type2"
		//      - vendor thing id "0807W-F00-03-005", thing type "type2"

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

		// 2. actually the command will only be send to "0807W-F00-03-003", "0807W-F00-03-004" and "0807W-F00-03-005" which meet both of below conditions:
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
	 * - vendor thing id "0807W-F00-03-001", thing type "type1"
	 * - vendor thing id "0807W-F00-03-002", thing type "type1"
	 * - vendor thing id "0807W-F00-03-003", thing type "type2"
	 * - tag "B"
	 * - vendor thing id "0807W-F00-03-003", thing type "type2"
	 * - vendor thing id "0807W-F00-03-004", thing type "type2"
	 * - vendor thing id "0807W-F00-03-005", thing type "type2"
	 * <p>
	 * 2. send below two commands:
	 * command a. task: existing in both tag "A" and "B", thing type: "type2"
	 * command b. task: "0807W-F00-03-001" and "0807W-F00-03-005"
	 * <p>
	 * 3. actually the commands will be send to below things:
	 * command a. "0807W-F00-03-003"
	 * command b. "0807W-F00-03-001" and "0807W-F00-03-005"
	 *
	 * @throws Exception
	 */
	@Test
	public void testSendCommandToThingListAndTagList() throws Exception {

		// 1. construct below relations between tags and things
		//  - tag "A"
		//      - vendor thing id "0807W-F00-03-001", thing type "type1"
		//      - vendor thing id "0807W-F00-03-002", thing type "type1"
		//      - vendor thing id "0807W-F00-03-003", thing type "type2"
		//  - tag "B"
		//      - vendor thing id "0807W-F00-03-003", thing type "type2"
		//      - vendor thing id "0807W-F00-03-004", thing type "type2"
		//      - vendor thing id "0807W-F00-03-005", thing type "type2"

		// bind things to tags
		this.bindThingsToTag(globalThingIDListForTests.subList(0, 3), displayNames[0]);
		this.bindThingsToTag(globalThingIDListForTests.subList(2, 5), displayNames[1]);

		// 2. send below two commands:
		//      command a. task: existing in both tag "A" and "B", thing type: "type2"
		//      command b. task: "0807W-F00-03-001" and "someVendorThingID5, thing type: "type1"
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
		//      command a. "0807W-F00-03-003"
		//      command b. "0807W-F00-03-001" and "0807W-F00-03-005"
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
	 * - vendor thing id "0807W-F00-03-999", thing type "type1", do onboaridng
	 * - vendor thing id "0807W-F00-03-998", thing type "type1", don't do onboarding
	 * <p>
	 * 2. try to send command to above things
	 * <p>
	 * 3. actually the command will be send to "0807W-F00-03-999" only, as "0807W-F00-03-998" is not boarded yet so got skipped
	 *
	 * @throws Exception
	 */
	@Test
	public void testSendCommandToThingWithoutOnboarding() throws Exception {

		// 1. try to create below things
		//  - vendor thing id "0807W-F00-03-999", thing type "type1", do onboaridng
		//  - vendor thing id "0807W-F00-03-998", thing type "type1", don't do onboarding

		// for the clear after test case completed
		this.vendorThingIDsForTest = this.combineArray(this.vendorThingIDsForTest, "0807W-F00-03-999", "0807W-F00-03-998");
		this.thingTypesForTest = this.combineArray(this.thingTypesForTest, "type1", "type2");
		this.kiiAppIDForTest = this.combineArray(this.kiiAppIDForTest, kiiAppIDForTest[0], kiiAppIDForTest[1]);

		// create "0807W-F00-03-999"
		Long globalThingIDX = this.createThing("0807W-F00-03-999", "type1", kiiAppIDForTest[0]);
		this.globalThingIDListForTests.add(globalThingIDX);

		Map<String, Object> onboardingInfo = this.getOnboardingInfo("0807W-F00-03-999");
		this.onboarding("0807W-F00-03-999", onboardingInfo);
		System.out.println("thing with global thing id : someVendorThingIDX is onboarded");

		// create "0807W-F00-03-998"
		Long globalThingIDY = this.createThing("0807W-F00-03-998", "type1", kiiAppIDForTest[0]);
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

		// 3. actually the command will be send to "0807W-F00-03-999" only, as "0807W-F00-03-998" is not boarded yet so got skipped
		assertTrue(list.size() == 1);

		// two things in type "type1"
		List<Map<String, Object>> subList = list.get(0);
		assertTrue(subList.size() == 1);

		Map<String, Object> map = subList.get(0);
		Long globalThingID = Long.valueOf((Integer) map.get("globalThingID"));
		assertEquals(globalThingIDX, globalThingID);


	}

	/**
	 * this test case is based on the task of test case "testSendCommandToThingListAndTagList",
	 * will query the command details sent from test case "testSendCommandToThingListAndTagList"
	 *
	 * 1. the number of command details is expected to be the same with the commands sent in test case
	 * "testSendCommandToThingListAndTagList"
	 * 2. actions and command task of each command are expected to be returned
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

		// 2. actions and command task of each command are expected to be returned
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
	 * 1. send command1 to vendor thing id "0807W-F00-03-001"
	 * 2. sleep 10 seconds (mark the 5th second as timestampA)
	 * 3. send command2 to vendor thing id "0807W-F00-03-002"
	 * 4. send command3 to vendor thing id "0807W-F00-03-001"
	 * 5. sleep 10 seconds (mark the 5th second as timestampB)
	 * 6. send command4 to vendor thing id "0807W-F00-03-001"
	 * 7. when query the commands sent to vendor thing id "0807W-F00-03-001" before timestampB, command1 and
	 * command3 will be returned
	 * 8. when query the commands sent to vendor thing id "0807W-F00-03-001" before timestampB and after
	 * timestampA, command3 will be returned
	 * 9. when query the commands sent to vendor thing id "0807W-F00-03-001" after timestampA, command3 and command4
	 * will be returned
	 *
	 */
	@Test
	public void testGetCommandsOfSingleThing() throws Exception {

		// create command
		Map<String, Object> command = this.createCommand();
		String comandStr = mapper.writeValueAsString(command);
		ThingCommand thingCommand = mapper.readValue(comandStr, ThingCommand.class);

		// get full kii thing ids of "0807W-F00-03-001" and "0807W-F00-03-002"
		GlobalThingInfo thing1 = globalThingDao.getThingByVendorThingID("0807W-F00-03-001");
		GlobalThingInfo thing2 = globalThingDao.getThingByVendorThingID("0807W-F00-03-002");
		long globalThingID1 = thing1.getId();
		long globalThingID2 = thing2.getId();
		String fullKiiThingID1 = thing1.getFullKiiThingID();
		String fullKiiThingID2 = thing2.getFullKiiThingID();
		String userID1 = appInfoManager.getDefaultOwer(thing1.getKiiAppID());
		String userID2 = appInfoManager.getDefaultOwer(thing2.getKiiAppID());

		// 1. send command1 to vendor thing id "0807W-F00-03-001";
		thingCommand.setUserID(userID1);
		String command1 = thingIFInAppService.sendCommand(thingCommand, fullKiiThingID1);

		// 2. sleep 10 seconds (mark the 5th second as timestampA)
		Thread.sleep(5000);
		long timestampA = System.currentTimeMillis();
		Thread.sleep(5000);

		// 3. send command2 to vendor thing id "0807W-F00-03-002"
		thingCommand.setUserID(userID2);
		String command2 = thingIFInAppService.sendCommand(thingCommand, fullKiiThingID2);

		// 4. send command3 to vendor thing id "0807W-F00-03-001"
		thingCommand.setUserID(userID1);
		String command3 = thingIFInAppService.sendCommand(thingCommand, fullKiiThingID1);

		// 5. sleep 10 seconds (mark the 5th second as timestampB)
		Thread.sleep(5000);
		long timestampB = System.currentTimeMillis();
		Thread.sleep(5000);

		// 6. send command4 to vendor thing id "0807W-F00-03-001"
		thingCommand.setUserID(userID1);
		String command4 = thingIFInAppService.sendCommand(thingCommand, fullKiiThingID1);

		// 7. when query the commands sent to vendor thing id "0807W-F00-03-001" before timestampB, command1 and
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


		// 8. when query the commands sent to vendor thing id "0807W-F00-03-001" before timestampB and after
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


		// 9. when query the commands sent to vendor thing id "0807W-F00-03-001" after timestampA, command3 and
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


	/**
	 * below scenario will be tested:
	 * <p>
	 * 1. try to send one command to below things, and specify the task thing type "type1" in the command
	 * - vendor thing id "0807W-F00-03-001", thing type "type1"
	 * - vendor thing id "0807W-F00-03-002", thing type "type1"
	 * - vendor thing id "0807W-F00-03-003", thing type "type2"
	 * - vendor thing id "0807W-F00-03-004", thing type "type2"
	 * - vendor thing id "0807W-F00-03-005", thing type "type2"
	 * <p>
	 * 2. actually the command will be send to all things, as thing type in the command will only work while specifying tagList rather than thingList
	 *
	 * @throws Exception
	 */
	@Test
	public void testSendSingleCommandToThingList() throws Exception {

		// 1. try to send one command to below things, and specify the task thing type "type1" in the command
		//  - vendor thing id "0807W-F00-03-001", thing type "type1"
		//  - vendor thing id "0807W-F00-03-002", thing type "type1"
		//  - vendor thing id "0807W-F00-03-003", thing type "type2"
		//  - vendor thing id "0807W-F00-03-004", thing type "type2"
		//  - vendor thing id "0807W-F00-03-005", thing type "type2"
		HashMap<String, Object> command = new HashMap<>();

		command.put("thingList", globalThingIDListForTests);
		command.put("type", "type1");
		command.put("command", this.createCommand());


		String ctx = mapper.writeValueAsString(command);

		String result = this.mockMvc.perform(
				post("/thing-if/command/single").content(ctx)
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

		// 2. actually the command will be send to all things, as thing type in the command will only work while specifying tagList rather than thingList
		assertTrue(list.size() == 5);

		for (Map<String, Object> map : list) {
			Long globalThingID = Long.valueOf((Integer) map.get("globalThingID"));
			assertTrue(globalThingIDListForTests.contains(globalThingID));

			assertTrue(!Strings.isBlank((String) map.get("commandID")));
		}

	}

	/**
	 * below scenario will be tested:
	 * <p>
	 * 1. construct below relations between tags and things
	 * - tag "A"
	 * - vendor thing id "0807W-F00-03-001", thing type "type1"
	 * - vendor thing id "0807W-F00-03-002", thing type "type1"
	 * - vendor thing id "0807W-F00-03-003", thing type "type2"
	 * - tag "B"
	 * - vendor thing id "0807W-F00-03-003", thing type "type2"
	 * - vendor thing id "0807W-F00-03-004", thing type "type2"
	 * - vendor thing id "0807W-F00-03-005", thing type "type2"
	 * <p>
	 * 2. send below command:
	 * - task: existing in both tag "A" and "B", thing type: "type2"
	 * <p>
	 * 3. actually the commands will be send to below things:
	 * - "0807W-F00-03-003"
	 *
	 * @throws Exception
	 */
	@Test
	public void testSendSingleCommandToTagList() throws Exception {

		// 1. construct below relations between tags and things
		//  - tag "A"
		//      - vendor thing id "0807W-F00-03-001", thing type "type1"
		//      - vendor thing id "0807W-F00-03-002", thing type "type1"
		//      - vendor thing id "0807W-F00-03-003", thing type "type2"
		//  - tag "B"
		//      - vendor thing id "0807W-F00-03-003", thing type "type2"
		//      - vendor thing id "0807W-F00-03-004", thing type "type2"
		//      - vendor thing id "0807W-F00-03-005", thing type "type2"

		// bind things to tags
		this.bindThingsToTag(globalThingIDListForTests.subList(0, 3), displayNames[0]);
		this.bindThingsToTag(globalThingIDListForTests.subList(2, 5), displayNames[1]);

		// 2. send below command:
		// - task: existing in both tag "A" and "B", thing type: "type2"
		HashMap<String, Object> command = new HashMap<>();

		command.put("tagList", new String[]{TagType.Custom + "-" + displayNames[0], TagType.Custom + "-" + displayNames[1]});
		command.put("andExpress", true);
		command.put("type", "type2");
		command.put("command", this.createCommand());

		String ctx = mapper.writeValueAsString(command);

		String result = this.mockMvc.perform(
				post("/thing-if/command/single").content(ctx)
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

		// 3. actually the commands will be send to below things:
		// - "0807W-F00-03-003"
		assertTrue(list.size() == 1);

		Map<String, Object> commandMap = list.get(0);
		Long globalThingID = Long.valueOf((Integer) commandMap.get("globalThingID"));
		assertEquals(globalThingIDListForTests.get(2), globalThingID);
		assertTrue(!Strings.isBlank((String) commandMap.get("commandID")));


	}

	@Test
	public void testSearchThingStates() throws Exception {

		// update state for thing1
		GlobalThingInfo thingInfo1 = globalThingDao.findByID(globalThingIDListForTests.get(0));

		Map<String, Object> states1 = new HashMap<>();
		states1.put("Bri", 80);
		states1.put("Power", 1);
		states1.put("Color", "#111111");

		String stateString1 = mapper.writeValueAsString(states1);

		globalThingDao.updateState(states1, thingInfo1.getFullKiiThingID());


		// update state for thing2
		GlobalThingInfo thingInfo2 = globalThingDao.findByID(globalThingIDListForTests.get(1));

		Map<String, Object> states2 = new HashMap<>();
		states2.put("Bri", 90);
		states2.put("Color", "#222222");

		String stateString2 = mapper.writeValueAsString(states2);

		globalThingDao.updateState(states2, thingInfo2.getFullKiiThingID());


		// update state for thing3
		GlobalThingInfo thingInfo3 = globalThingDao.findByID(globalThingIDListForTests.get(2));

		Map<String, Object> states3 = new HashMap<>();
		states3.put("Bri", 100);
		states3.put("Power", 0);
		states3.put("Color", "#333333");

		String stateString3 = mapper.writeValueAsString(states3);

		globalThingDao.updateState(states3, thingInfo3.getFullKiiThingID());


		// search thing state Bri/Power in thing1/thing2

		HashMap<String, Object> request = new HashMap<>();

		request.put("thingList", new Long[]{thingInfo1.getId(), thingInfo2.getId()});
		request.put("stateList", new String[]{"Bri", "Power"});

		String ctx = mapper.writeValueAsString(request);

		String result = this.mockMvc.perform(
				post("/thing-if/states/search").content(ctx)
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

		// assert
		assertTrue(list.size() == 2);

		list.forEach(e -> {
			long globalThingID = ((Integer)e.get("globalThingID")).longValue();
			String vendorThingID = (String)e.get("vendorThingID");
			Map<String, Object> states = (Map)e.get("states");

			if(globalThingID == thingInfo1.getId()) {
				assertEquals(vendorThingIDsForTest[0], vendorThingID);
				assertTrue(states.keySet().size() == 2);

				assertEquals(states1.get("Bri"), states.get("Bri"));
				assertEquals(states1.get("Power"), states.get("Power"));

			} else if(globalThingID == thingInfo2.getId()) {
				assertEquals(vendorThingIDsForTest[1], vendorThingID);
				assertTrue(states.keySet().size() == 1);

				assertEquals(states2.get("Bri"), states.get("Bri"));

			} else {
				fail();
			}
		});


		// search thing state all in thing1/thing2/some other thing

		request = new HashMap<>();

		request.put("thingList", new Long[]{thingInfo1.getId(), thingInfo2.getId(), globalThingIDListForTests.get(4)});

		ctx = mapper.writeValueAsString(request);

		result = this.mockMvc.perform(
				post("/thing-if/states/search").content(ctx)
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

		// assert
		assertTrue(list.size() == 3);

		list.forEach(e -> {
			long globalThingID = ((Integer)e.get("globalThingID")).longValue();
			String vendorThingID = (String)e.get("vendorThingID");
			Map<String, Object> states = (Map)e.get("states");

			if(globalThingID == thingInfo1.getId()) {
				assertEquals(vendorThingIDsForTest[0], vendorThingID);
				assertTrue(states.keySet().size() == 3);

				assertEquals(states1.get("Bri"), states.get("Bri"));
				assertEquals(states1.get("Power"), states.get("Power"));
				assertEquals(states1.get("Color"), states.get("Color"));

			} else if(globalThingID == thingInfo2.getId()) {
				assertEquals(vendorThingIDsForTest[1], vendorThingID);
				assertTrue(states.keySet().size() == 2);

				assertEquals(states2.get("Bri"), states.get("Bri"));
				assertEquals(states2.get("Color"), states.get("Color"));

			} else if(globalThingID == globalThingIDListForTests.get(4)) {
				assertEquals(vendorThingIDsForTest[4], vendorThingID);
				assertTrue(states.keySet().size() == 0);

			} else {
				fail();
			}
		});




	}

}
