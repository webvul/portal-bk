package com.kii.beehive.portal.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.service.TagIndexDao;
import com.kii.beehive.portal.store.entity.BeehiveUserGroup;
import com.kii.beehive.portal.store.entity.TagType;
import com.kii.beehive.portal.web.WebTestTemplate;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        tagIndexDao.removeTagByID(TagType.Custom + "-" + displayName);

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

}
