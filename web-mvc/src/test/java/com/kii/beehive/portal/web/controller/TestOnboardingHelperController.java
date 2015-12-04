package com.kii.beehive.portal.web.controller;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.service.GlobalThingDao;
import com.kii.beehive.portal.service.TagIndexDao;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.web.WebTestTemplate;


public class TestOnboardingHelperController extends WebTestTemplate {

    @Autowired
    private GlobalThingDao thingDao;

    @Autowired
    private TagIndexDao indexDao;

    @Autowired
    private ObjectMapper mapper;

    private static List<String> vendorThingIDList = new ArrayList<>();

    private static List<String> globalThingIDList = new ArrayList<>();

    private static List<String> tagNameList = new ArrayList<>();

    @Before
    public void before(){

        super.before();

        vendorThingIDList.add("vendor.id.for.test");
        globalThingIDList.add("global.id.for.test");

        tagNameList.add("location-lobby");
        tagNameList.add("location-baseroom-1F");
    }

    @After
    public void after() {

        for (String s : vendorThingIDList) {
            GlobalThingInfo thingInfo = thingDao.getThingByVendorThingID(s);
            if(thingInfo == null) {
                try {
                    thingDao.removeGlobalThingByID(thingInfo.getVendorThingID());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        for (String s: globalThingIDList) {
            try {
                thingDao.removeGlobalThingByID(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for(String s : tagNameList) {
            try {
                indexDao.removeTagByID(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    public void testSetOnboardingInfo() throws Exception {

        Map<String, Object> request = new HashMap<>();
        request.put("vendorThingID", "vendor.id.for.test");
        request.put("kiiAppID", "da0b6a25");
        request.put("password", "456");
        request.put("status", "on");
        request.put("type", "LED");

        // set tag
        List<Map> tagList = new ArrayList<>();

        Map<String, Object> tag = new HashMap<>();
        tag.put("tagType", "location");
        tag.put("displayName", "lobby");
        tagList.add(tag);

        tag = new HashMap<>();
        tag.put("tagType", "location");
        tag.put("displayName", "baseroom-1F");
        tagList.add(tag);

        request.put("tags", tagList);

        // set custom
        Map<String, Object> custom = new HashMap<>();
        custom.put("dateOfProduce", "20001230");
        custom.put("license", 1234567.89);
        request.put("custom", custom);

        String ctx= mapper.writeValueAsString(request);

        String result=this.mockMvc.perform(
                post("/onboardinghelper").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String,Object> map=mapper.readValue(result, Map.class);

        // assert http reture
        assertEquals("da0b6a25-vendor.id.for.test", map.get("globalThingID"));

        // assert DB
        GlobalThingInfo thingInfo = thingDao.getThingInfoByID("da0b6a25-vendor.id.for.test");
        assertEquals("da0b6a25-vendor.id.for.test", thingInfo.getGlobalThingID());
        assertEquals("vendor.id.for.test", thingInfo.getVendorThingID());
        assertEquals("da0b6a25", thingInfo.getKiiAppID());
        assertEquals("on", thingInfo.getStatus());
        assertEquals("LED", thingInfo.getType());

        assertEquals(2, thingInfo.getTags().size());
        assertTrue(thingInfo.getTags().contains("location-lobby"));
        assertTrue(thingInfo.getTags().contains("location-baseroom-1F"));

        assertEquals(2, thingInfo.getCustom().size());
        assertEquals("20001230", thingInfo.getCustom().get("dateOfProduce"));
        assertEquals(1234567.89, thingInfo.getCustom().get("license"));

    }

    @Test
    public void testGetOnboardingInfo() throws Exception {

        String result=this.mockMvc.perform(
                get("/onboardinghelper/vendor.id.for.test/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String,Object> map=mapper.readValue(result, Map.class);

        // assert http return
        assertEquals("da0b6a25-vendor.id.for.test", map.get("globalThingID"));
        assertEquals("vendor.id.for.test", map.get("vendorThingID"));
        assertEquals("da0b6a25", map.get("kiiAppID"));
        assertEquals("456", map.get("password"));
        assertEquals("on", map.get("status"));
        assertEquals("LED", map.get("type"));
        assertNotNull(map.get("defaultOwnerID"));

        assertEquals(2, ((List)map.get("tags")).size());
        assertTrue(((List)map.get("tags")).contains("location-lobby"));
        assertTrue(((List)map.get("tags")).contains("location-baseroom-1F"));

        assertEquals(2, ((Map)map.get("custom")).size());
        assertEquals("20001230", ((Map)map.get("custom")).get("dateOfProduce"));
        assertEquals(1234567.89, ((Map)map.get("custom")).get("license"));
    }

}
