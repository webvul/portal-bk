package com.kii.beehive.portal.web.controller;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
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

    private Long tagIDForTest;

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

        tagIDForTest = Long.valueOf((int)map.get("id"));

        // assert http return
        String tagName = (String)map.get("tagName");
        assertEquals(TagType.Custom + "-" + displayName, tagName);

    }

    @Test
    public void testUpdateTag() throws Exception {

        this.testCreateTag();

        Map<String, Object> request = new HashMap<>();
        request.put("id", tagIDForTest);
        request.put("displayName", displayName+"_new");
        request.put("description", "some description new");

        String ctx= mapper.writeValueAsString(request);

        String result=this.mockMvc.perform(
                post("/tags/custom").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String,Object> map=mapper.readValue(result, Map.class);

        // assert http return
        Long tagID = Long.valueOf((int)map.get("id"));
        assertEquals(tagIDForTest, tagID);

        String tagName = (String)map.get("tagName");
        assertEquals(TagType.Custom + "-" + displayName+"_new", tagName);

        TagIndex tagIndex = tagIndexDao.findByID(tagID);
        assertEquals(tagIDForTest, (Long)tagIndex.getId());
        assertEquals(displayName+"_new", tagIndex.getDisplayName());
        assertEquals("some description new", tagIndex.getDescription());

    }

    @Test
    public void testCreateTagException() throws Exception {

        Map<String, Object> request = new HashMap<>();
        request.put("description", "some description");

        String ctx= mapper.writeValueAsString(request);

        String result=this.mockMvc.perform(
                post("/tags/custom").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

    }

    @Test
    public void testRemoveTag() throws Exception {

        this.testCreateTag();

        TagIndex tagIndex = tagIndexDao.findByID(tagIDForTest);
        assertNotNull(tagIndex);

        String result=this.mockMvc.perform(
                delete("/tags/custom/" + displayName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        tagIndex = tagIndexDao.findByID(tagIDForTest);
        assertNull(tagIndex);

    }

    @Test
    public void testRemoveTagException() throws Exception {

        String result=this.mockMvc.perform(
                delete("/tags/custom/" + "some_non_existing_displayName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

    }

    @Test
    public void testGetAllTag() throws Exception {

        // test no tag
        String result=this.mockMvc.perform(
                get("/tags/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Map<String, Object>> list=mapper.readValue(result, List.class);
        assertEquals(0, list.size());

        // test tag existing

        List<String> displayNames = new ArrayList<>();
        displayNames.add("someDisplayName1");
        displayNames.add("someDisplayName2");
        displayNames.add("someDisplayName3");
        List<Long> tagIDs = new ArrayList<>();

        // create tag
        TagIndex tagIndex = new TagIndex();
        tagIndex.setTagType(com.kii.beehive.portal.jdbc.entity.TagType.Custom);
        tagIndex.setDisplayName(displayNames.get(0));
        long tagID1 = tagIndexDao.saveOrUpdate(tagIndex);
        tagIDs.add(tagID1);

        tagIndex = new TagIndex();
        tagIndex.setTagType(com.kii.beehive.portal.jdbc.entity.TagType.Custom);
        tagIndex.setDisplayName(displayNames.get(1));
        long tagID2 = tagIndexDao.saveOrUpdate(tagIndex);
        tagIDs.add(tagID2);

        tagIndex = new TagIndex();
        tagIndex.setTagType(com.kii.beehive.portal.jdbc.entity.TagType.Location);
        tagIndex.setDisplayName(displayNames.get(2));
        long tagID3 = tagIndexDao.saveOrUpdate(tagIndex);
        tagIDs.add(tagID3);

        result=this.mockMvc.perform(
                get("/tags/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        list=mapper.readValue(result, List.class);

        // assert http return
        assertEquals(3, list.size());

        for(Map<String, Object> map : list) {
            System.out.println("response: " + map);

            assertTrue(tagIDs.contains(Long.valueOf((int)map.get("id"))));
            assertTrue(map.get("tagType").equals(TagType.Custom.toString()) || map.get("tagType").equals(TagType.Location.toString()));
            assertTrue(displayNames.contains(map.get("displayName")));
        }

    }

}
