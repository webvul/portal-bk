package com.kii.beehive.portal.web.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.service.GlobalThingDao;
import com.kii.beehive.portal.service.TagIndexDao;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.web.WebTestTemplate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.*;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestOnboardingHelperController extends WebTestTemplate {

    @Autowired
    private OnboardingHelperController controller;

    @Autowired
    private GlobalThingDao thingDao;

    @Autowired
    private TagIndexDao indexDao;

    @Autowired
    private ObjectMapper mapper;

    private List<String> vendorThingIDList = new ArrayList<>();

    private List<String> globalThingIDList = new ArrayList<>();

    private List<String> tagNameList = new ArrayList<>();

    @Before
    public void before(){
        vendorThingIDList.add("vendor:id:for:test");
        globalThingIDList.add("global:id:for:test");

        tagNameList.add("location-lobby");
        tagNameList.add("location-baseroom-1F");
    }

    @After
    public void after() {

        for (String s : vendorThingIDList) {
            GlobalThingInfo thingInfo = thingDao.getThingByVendorThingID(s);
            if(thingInfo == null) {
                thingDao.removeGlobalThingByID(thingInfo.getVendorThingID());
            }
        }

        for (String s: globalThingIDList) {
            thingDao.removeGlobalThingByID(s);
        }

        for(String s : tagNameList) {
            indexDao.removeTagByID(s);
        }

    }

    @Test
    public void testSetOnboardingInfo() throws Exception {

        Map<String, Object> request = new HashMap<>();
        request.put("vendorThingID", "vendor:id:for:test");
        request.put("KiiAppID", "123");
        request.put("password", "456");
        request.put("status", "on");
        request.put("type", "LED");

        // set tag
        List<Map> tagList = new ArrayList<>();

        Map<String, Object> tag = new HashMap<>();
        tag.put("tayType", "location");
        tag.put("displayName", "lobby");
        tagList.add(tag);

        tag = new HashMap<>();
        tag.put("tayType", "location");
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
                post("/onboardinghelper/").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String,Object> map=mapper.readValue(result, Map.class);

        // assert http reture
        assertEquals("123-vendor:id:for:test", map.get("globalThingID"));

        // assert DB
        GlobalThingInfo thingInfo = thingDao.getThingInfoByID("123-vendor:id:for:test");
        assertEquals("123-vendor:id:for:test", thingInfo.getGlobalThingID());
        assertEquals("vendor:id:for:test", thingInfo.getVendorThingID());
        assertEquals("123", thingInfo.getKiiAppID());
        assertEquals("456", thingInfo.getPassword());
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
    public void testGetOnboardingInfo1() throws Exception {

        String result=this.mockMvc.perform(
                get("/onboardinghelper/vendor:id:for:test")
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String,Object> map=mapper.readValue(result, Map.class);

        // assert http reture
        assertEquals("123-vendor:id:for:test", map.get("globalThingID"));
        assertEquals("vendor:id:for:test", map.get("vendorThingID"));
        assertEquals("123", map.get("KiiAppID"));
        assertEquals("456", map.get("password"));
        assertEquals("on", map.get("status"));
        assertEquals("LED", map.get("type"));
        assertNotNull(map.get("defaultOwnerID"));

        assertEquals(2, ((Set)map.get("tags")).size());
        assertTrue(((Set)map.get("tags")).contains("location-lobby"));
        assertTrue(((Set)map.get("tags")).contains("location-baseroom-1F"));

        assertEquals(2, ((Map)map.get("custom")).size());
        assertEquals("20001230", ((Map)map.get("custom")).get("dateOfProduce"));
        assertEquals(1234567.89, ((Map)map.get("custom")).get("license"));
    }

}
