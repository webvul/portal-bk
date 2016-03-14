package com.kii.beehive.portal.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.portal.web.WebTestTemplate;
import com.kii.beehive.portal.web.constant.Constants;
import com.kii.extension.sdk.entity.thingif.OnBoardingParam;
import com.kii.extension.sdk.entity.thingif.OnBoardingResult;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class TestThingIFController extends WebTestTemplate {

    @Autowired
    private ThingIFInAppService thingIFInAppService;

    @Autowired
    private GlobalThingSpringDao globalThingDao;

    @Autowired
    private TagIndexDao tagIndexDao;

    @Autowired
    private ObjectMapper mapper;

    private String[] vendorThingIDsForTest = new String[]{"someVendorThingID1", "someVendorThingID2", "someVendorThingID3", "someVendorThingID4", "someVendorThingID5"};

    private String[] thingTypesForTest = new String[] {"type1", "type1", "type2", "type2", "type2"};

    private String[] kiiAppIDForTest = new String[] {"0af7a7e7", "0af7a7e7", "c1744915", "c1744915", "c1744915"};

    private List<Long> globalThingIDListForTests = new ArrayList<>();

    private List<Map<String, Object>> onboardingInfoMap = new ArrayList<>();

    private String[] displayNames = new String[]{"A", "B"};

    private List<Long> tagIDListForTests = new ArrayList<>();

    private String tokenForTest = BEARER_SUPER_TOKEN;

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
        for(String displayName : displayNames) {
            try {
                this.createTag(displayName);
                System.out.println("tag with display name: " + displayName + " is created");
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        }

        // add things in vendorThingIDsForTest
        for (int i=0;i<vendorThingIDsForTest.length;i++){
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
        List<GlobalThingInfo> globalThingInfoList = globalThingDao.getThingsByVendorIDArray(Arrays.asList(vendorThingIDsForTest));
        for(GlobalThingInfo globalThingInfo : globalThingInfoList) {
            try {
                long globalThingID = globalThingInfo.getId();
                this.removeThing(globalThingID);
                System.out.println("thing with global thing id: " + globalThingID + " is deleted");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // remove tags in displayNames
        for(String displayName : displayNames) {
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

        String ctx= mapper.writeValueAsString(request);

        String result=this.mockMvc.perform(
                post("/things").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String,Object> map=mapper.readValue(result, Map.class);

        // assert http reture
        long globalThingID = Long.valueOf((int)map.get("globalThingID"));
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

        String ctx= mapper.writeValueAsString(request);

        String result=this.mockMvc.perform(
                post("/tags/custom").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String,Object> map=mapper.readValue(result, Map.class);

        System.out.println("Response:" + result);

        Long tagID = Long.valueOf((int)map.get("id"));

        // assert http return
        String tagName = (String)map.get("tagName");
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

        String result=this.mockMvc.perform(
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
        for(Long globalThingID : globalThingIDList) {
            globalThingIDs.append(",").append(globalThingID);
        }
        globalThingIDs.deleteCharAt(0);

        // bind tag to things

        String url="/things/"+globalThingIDs.toString()+"/tags/custom/"+tagDisplayName;

        this.mockMvc.perform(post(url).content("{}")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isOk());

        // assert things existing
        String result=this.mockMvc.perform(
                get("/things/search?tagType=Custom&displayName=" + tagDisplayName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Map<String, Object>> list=mapper.readValue(result, List.class);
        assertEquals(globalThingIDList.size(), list.size());

        for(Map<String, Object> map : list) {
            Long globalThingID = Long.valueOf((int)map.get("globalThingID"));
            assertTrue(globalThingIDList.contains(globalThingID));
        }
    }

    private Map<String, Object> getOnboardingInfo(String vendorThingID) throws Exception {
        // get onboarding info
        String result=this.mockMvc.perform(
                get("/onboardinghelper/" + vendorThingID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header("Authorization", super.BEARER_SUPER_TOKEN)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String,Object> map=mapper.readValue(result, Map.class);

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
        OnBoardingParam param=new OnBoardingParam();

        param.setVendorThingID(vendorThingID);
        param.setThingPassword(vendorThingID);
        param.setUserID((String)onboardingInfo.get("ownerID"));

        String kiiAppID = (String)onboardingInfo.get("kiiAppID");

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

        command.put("actions", new HashMap[] {action1, action2});

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
     *
     * 1. try to send one command to below things, and specify the target thing type "type1" in the command
     *  - vendor thing id "someVendorThingID1", thing type "type1"
     *  - vendor thing id "someVendorThingID2", thing type "type1"
     *  - vendor thing id "someVendorThingID3", thing type "type2"
     *  - vendor thing id "someVendorThingID4", thing type "type2"
     *  - vendor thing id "someVendorThingID5", thing type "type2"
     *
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

        String ctx= mapper.writeValueAsString(requestList);

        String result=this.mockMvc.perform(
                post("/thing-if/command").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<List<Map<String, Object>>> list=mapper.readValue(result, List.class);

        System.out.println("========================================================");
        System.out.println("Response: " + result);
        System.out.println("========================================================");

        // 2. actually the command will be send to all things, as all of them are in thing type "type1"
        assertTrue(list.size() == 1);

        // two things in type "type1"
        List<Map<String, Object>> subList = list.get(0);
        assertTrue(subList.size() == 5);

        for(Map<String, Object> map : subList) {
            Long globalThingID = Long.valueOf((Integer)map.get("globalThingID"));
            assertTrue(globalThingIDListForTests.contains(globalThingID));

            assertTrue(!Strings.isBlank((String)map.get("commandID")));
        }

    }

    /**
     * below scenario will be tested:
     *
     * 1. try to send one command to below tags with andExpress true, and specify the target thing type "type2"
     *  - tag "A"
     *      - vendor thing id "someVendorThingID1", thing type "type1"
     *      - vendor thing id "someVendorThingID2", thing type "type1"
     *      - vendor thing id "someVendorThingID3", thing type "type2"
     *  - tag "B"
     *      - vendor thing id "someVendorThingID3", thing type "type2"
     *      - vendor thing id "someVendorThingID4", thing type "type2"
     *      - vendor thing id "someVendorThingID5", thing type "type2"
     *
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

        command.put("tagList", new String[]{TagType.Custom+"-"+displayNames[0], TagType.Custom+"-"+displayNames[1]});
        command.put("andExpress", true);
        command.put("type", "type2");
        command.put("command", this.createCommand());

        requestList.add(command);

        String ctx= mapper.writeValueAsString(requestList);

        String result=this.mockMvc.perform(
                post("/thing-if/command").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<List<Map<String, Object>>> list=mapper.readValue(result, List.class);

        System.out.println("========================================================");
        System.out.println("Response: " + result);
        System.out.println("========================================================");

        // 2. actually the command will only be send to "someVendorThingID3" which is in both tag "A" and "B", and in thing type "type2"
        assertTrue(list.size() == 1);

        // one thing in both tag "A" and "B", and in thing type "type2"
        List<Map<String, Object>> subList = list.get(0);
        assertTrue(subList.size() == 1);

        Map<String, Object> map = subList.get(0);
        Long globalThingID = Long.valueOf((Integer)map.get("globalThingID"));
        assertEquals(globalThingIDListForTests.get(2), globalThingID);

        assertTrue(!Strings.isBlank((String)map.get("commandID")));

    }

    /**
     * below scenario will be tested:
     *
     * 1. try to send one command to below tags with andExpress false, and specify the target thing type "type2"
     *  - tag "A"
     *      - vendor thing id "someVendorThingID1", thing type "type1"
     *      - vendor thing id "someVendorThingID2", thing type "type1"
     *      - vendor thing id "someVendorThingID3", thing type "type2"
     *  - tag "B"
     *      - vendor thing id "someVendorThingID3", thing type "type2"
     *      - vendor thing id "someVendorThingID4", thing type "type2"
     *      - vendor thing id "someVendorThingID5", thing type "type2"
     *
     * 2. actually the command will only be send to "someVendorThingID3", "someVendorThingID4" and "someVendorThingID5" which meet both of below conditions:
     *  - in tag "A" or "B"
     *  - in thing type "type2"
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

        command.put("tagList", new String[]{TagType.Custom+"-"+displayNames[0], TagType.Custom+"-"+displayNames[1]});
        command.put("andExpress", false);
        command.put("type", "type2");
        command.put("command", this.createCommand());

        requestList.add(command);

        String ctx= mapper.writeValueAsString(requestList);

        String result=this.mockMvc.perform(
                post("/thing-if/command").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<List<Map<String, Object>>> list=mapper.readValue(result, List.class);

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
        for(Map<String, Object> map : subList) {
            Long globalThingID = Long.valueOf((Integer)map.get("globalThingID"));
            assertTrue(targetGlobalThingIDList.contains(globalThingID));

            assertTrue(!Strings.isBlank((String)map.get("commandID")));
        }

    }

    /**
     * below scenario will be tested:
     *
     * 1. construct below relations between tags and things
     *  - tag "A"
     *      - vendor thing id "someVendorThingID1", thing type "type1"
     *      - vendor thing id "someVendorThingID2", thing type "type1"
     *      - vendor thing id "someVendorThingID3", thing type "type2"
     *  - tag "B"
     *      - vendor thing id "someVendorThingID3", thing type "type2"
     *      - vendor thing id "someVendorThingID4", thing type "type2"
     *      - vendor thing id "someVendorThingID5", thing type "type2"
     *
     * 2. send below two commands:
     *      command a. target: existing in both tag "A" and "B", thing type: "type2"
     *      command b. target: "someVendorThingID1" and "someVendorThingID5"
     *
     * 3. actually the commands will be send to below things:
     *      command a. "someVendorThingID3"
     *      command b. "someVendorThingID1" and "someVendorThingID5"
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

        commandA.put("tagList", new String[]{TagType.Custom+"-"+displayNames[0], TagType.Custom+"-"+displayNames[1]});
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

        String ctx= mapper.writeValueAsString(requestList);

        String result=this.mockMvc.perform(
                post("/thing-if/command").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<List<Map<String, Object>>> list=mapper.readValue(result, List.class);

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
        Long globalThingID = Long.valueOf((Integer)commandAMap.get("globalThingID"));
        assertEquals(globalThingIDListForTests.get(2), globalThingID);
        assertTrue(!Strings.isBlank((String)commandAMap.get("commandID")));

        // two things meet the condition for command b
        List<Map<String, Object>> commandBList = list.get(1);
        assertTrue(commandBList.size() == 2);

        for(Map<String, Object> commandBMap : commandBList) {
            globalThingID = Long.valueOf((Integer)commandBMap.get("globalThingID"));
            assertTrue(globalThingIDList.contains(globalThingID));

            assertTrue(!Strings.isBlank((String)commandBMap.get("commandID")));
        }

    }

}
