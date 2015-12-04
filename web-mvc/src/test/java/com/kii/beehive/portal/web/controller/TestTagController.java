package com.kii.beehive.portal.web.controller;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.service.TagIndexDao;
import com.kii.beehive.portal.store.entity.TagType;
import com.kii.beehive.portal.web.WebTestTemplate;

/**
 * Created by USER on 12/1/15.
 */
public class TestTagController extends WebTestTemplate {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private TagIndexDao tagIndexDao;

    private String displayName = "someDisplayName";

    @After
    public void after() {

        try {
            tagIndexDao.removeTagByID(TagType.Custom + "-" + displayName);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateTag() throws Exception {

        Map<String, Object> request = new HashMap<>();
        request.put("displayName", displayName);
        request.put("description", "some description");

        String ctx= mapper.writeValueAsString(request);

        String result=this.mockMvc.perform(
                post("/tags/custom").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String,Object> map=mapper.readValue(result, Map.class);

        // assert http reture
        String tagName = (String)map.get("tagName");
        assertEquals(TagType.Custom + "-" + displayName, tagName);


    }

    @Test
    public void testGetAllTag() throws Exception {

        String ctx= mapper.writeValueAsString(null);

        String result=this.mockMvc.perform(
                get("/tags/all").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Object map=mapper.readValue(result, Object.class);

        // assert http reture
//        String tagName = (String)map.get("tagName");
//        assertEquals(TagType.Custom + "-" + displayName, tagName);

        System.out.println("response: " + map);

        Map<String, Object>[] mapArray = (Map<String, Object>[])map;

        assertTrue(mapArray.length > 0);
        for (Map<String, Object> m : mapArray) {
            Set<String> keys = m.keySet();
            assertTrue(keys.contains("displayName"));
            assertTrue(keys.contains("description"));
            assertTrue(keys.contains("tagName"));
            assertTrue(keys.contains("tagType"));
            assertTrue(keys.contains("things"));
        }

    }

}
